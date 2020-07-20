package com.pharbers.kafka.connect.oss.concurrent;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSException;
import com.pharbers.kafka.connect.oss.OssCsvAndExcelSourceConnector;
import com.pharbers.kafka.connect.oss.kafka.ConsumerBuilder;
import com.pharbers.kafka.connect.oss.kafka.Producer;
import com.pharbers.kafka.connect.oss.model.BloodMsg;
import com.pharbers.kafka.connect.oss.model.TypeErrorMsg;
import com.pharbers.kafka.connect.oss.readerV2.CsvReaderV2;
import com.pharbers.kafka.connect.oss.readerV2.ExcelReaderForMaxDeliveryData;
import com.pharbers.kafka.connect.oss.readerV2.ExcelReaderV2;
import com.pharbers.kafka.connect.oss.readerV2.ReaderV2;
import com.pharbers.kafka.schema.OssTask;
import org.apache.poi.ooxml.POIXMLException;
import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 功能描述
 *
 * @author dcs
 * @version 0.0
 * @tparam T 构造泛型参数
 * @note 一些值得注意的地方
 * @since 2019/12/25 15:10
 */
public class RowDataProducer implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(RowDataProducer.class);
    private ConsumerBuilder<String, OssTask> kafkaConsumerBuffer;
    private LinkedBlockingQueue<RowData> plate;
    private String endpoint;
    private Map<String, String> props;
    private S3Client client;
    private Boolean isRun;

    public RowDataProducer(ConsumerBuilder<String, OssTask> kafkaConsumerBuffer,
                           LinkedBlockingQueue<RowData> plate,
                           Map<String, String> props) {
        this.kafkaConsumerBuffer = kafkaConsumerBuffer;
        this.plate = plate;
        this.props = props;
        endpoint = props.get(OssCsvAndExcelSourceConnector.ENDPOINT_CONFIG);
        String accessKeyId = props.get(OssCsvAndExcelSourceConnector.ACCESS_KEY_ID_CONFIG);
        String accessKeySecret = props.get(OssCsvAndExcelSourceConnector.ACCESS_KEY_SECRET_CONFIG);
        System.setProperty("aws.accessKeyId", accessKeyId);
        System.setProperty("aws.secretAccessKey", accessKeySecret);

//        client = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    @Override
    public void run() {
        isRun = true;
        while (!Thread.currentThread().isInterrupted()) {
            client = S3Client.builder().region(Region.of(endpoint)).credentialsProvider(SystemPropertyCredentialsProvider.create()).build();
            try {
                runTask();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                client.close();
            }
        }
        isRun = false;
    }

    public boolean isRun() {
        return isRun;
    }

    private void runTask() {
        while (kafkaConsumerBuffer.hasNext()) {
            OssTask task = kafkaConsumerBuffer.next();
            log.info("接收到任务," + Thread.currentThread().getName());
            log.info("ossKey:" + task.getOssKey().toString() + " jobID:" + task.getJobId().toString() + " ," + " traceID:" + task.getTraceId().toString() + " type:" + task.getFileType().toString());
            try {
                pushStatus(task);
                readOss(task);
            } catch (POIXMLException e) {
                log.info("poi异常", e);

                Producer.getIns().pushErr(new TypeErrorMsg(task.getTraceId().toString(), task.getAssetId().toString(), e.getMessage()));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        log.info("等待kafka任务," + Thread.currentThread().getName());
        try {
            Thread.sleep(1000 * 10);
        } catch (InterruptedException e) {
            log.info("Interrupted");
            Thread.currentThread().interrupt();
        }
    }

    protected void readOss(OssTask task) throws Exception {
        InputStream stream = getInputStream(task);
        String format = getFormat(stream, task.getFileType().toString());
        stream.close();
        stream = getInputStream(task);
        ReaderV2 reader = buildReader(task.getFileType().toString(), task, stream, format);
        reader.read(plate);
    }

    protected InputStream getInputStream(OssTask task) throws OSSException, ClientException {
        String ossKey = task.getOssKey().toString();
        String traceId = task.getTraceId().toString();
        String fileType = task.getFileType().toString();
        String bucketName = props.get(OssCsvAndExcelSourceConnector.BUCKET_NAME_CONFIG);
        log.info("ossKey:" + ossKey + " jobID:" + task.getJobId().toString() + " ," + " traceID:" + traceId + " type:" + fileType);
        return client.getObject(GetObjectRequest.builder().bucket(bucketName).key(ossKey).build(), ResponseTransformer.toInputStream());
    }

    protected String getFormat(InputStream stream, String fileType) {
        if ("csv".equals(fileType)) {
            UniversalDetector detector = new UniversalDetector();
            String format = "UTF-8";
            byte[] bytes = new byte[4096];
            try {
                int i = 0;
                while (i < bytes.length) {
                    i += stream.read(bytes, i, bytes.length - i);
                }

                detector.handleData(bytes);
                detector.dataEnd();
                String encode = detector.getDetectedCharset();
                detector.reset();
                format = format.equals(encode) ? format : "GBK";
            } catch (IOException e) {
                e.printStackTrace();
            }
            return format;
        } else {
            return "";
        }
    }

    protected ReaderV2 buildReader(String fileType, OssTask task, InputStream stream, String format) throws Exception {
        ReaderV2 reader;
        String maxDeliveryTag = "delivery";
        switch (fileType) {
            case "csv":
                reader = new CsvReaderV2(UUID.randomUUID().toString(), task);
                reader.init(stream, format);
                break;
            case "xlsx":
                if (task.getProviders().contains(maxDeliveryTag)) {
                    reader = new ExcelReaderForMaxDeliveryData(UUID.randomUUID().toString(), task);
                } else {
                    reader = new ExcelReaderV2(UUID.randomUUID().toString(), task);
                }
                reader.init(stream, format);
                break;
            default:
                log.error("不支持的类型" + fileType);
                Producer.getIns().pushErr(new TypeErrorMsg(task.getTraceId().toString(), task.getAssetId().toString(), fileType));
                throw new Exception("不支持的类型" + fileType);
        }
        return reader;
    }

    protected void pushStatus(OssTask task) {
        BloodMsg msg = new BloodMsg(
                task.getAssetId().toString(),
                new ArrayList<>(),
                task.getJobId().toString(),
                new ArrayList<>(),
                "start",
                "oss source connector"
        );
        Producer.getIns().pushStatus(msg, task.getTraceId().toString());
    }
}
