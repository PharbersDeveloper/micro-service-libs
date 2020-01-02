package com.pharbers.kafka.connect.oss.concurrent;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;
import com.pharbers.kafka.connect.oss.OssCsvAndExcelSourceConnector;
import com.pharbers.kafka.connect.oss.kafka.ConsumerBuilder;
import com.pharbers.kafka.connect.oss.kafka.Producer;
import com.pharbers.kafka.connect.oss.readerV2.CsvReaderV2;
import com.pharbers.kafka.connect.oss.readerV2.ExcelReaderV2;
import com.pharbers.kafka.connect.oss.readerV2.ReaderV2;
import com.pharbers.kafka.schema.OssTask;
import com.pharbers.kafka.schema.PhErrorMsg;
import org.apache.kafka.connect.source.SourceTaskContext;
import org.apache.poi.ooxml.POIXMLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private Map<String, String> props;
    private OSS client;

    public RowDataProducer(ConsumerBuilder<String, OssTask> kafkaConsumerBuffer,
                           LinkedBlockingQueue<RowData> plate,
                           Map<String, String> props) {
        this.kafkaConsumerBuffer = kafkaConsumerBuffer;
        this.plate = plate;
        this.props = props;
        String endpoint = props.get(OssCsvAndExcelSourceConnector.ENDPOINT_CONFIG);
        String accessKeyId = props.get(OssCsvAndExcelSourceConnector.ACCESS_KEY_ID_CONFIG);
        String accessKeySecret = props.get(OssCsvAndExcelSourceConnector.ACCESS_KEY_SECRET_CONFIG);
        client = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    runTask();
                } catch (Exception e){
                    log.error(e.getMessage(), e);
                }
            }
        } finally {
            client.shutdown();
        }

    }

    private void runTask(){
        while (kafkaConsumerBuffer.hasNext()) {
            OssTask task = kafkaConsumerBuffer.next();
            log.info("接收到任务," + Thread.currentThread().getName());
            log.info("ossKey:" + task.getOssKey().toString() + " jobID:" + task.getJobId().toString() + " ," + " traceID:" + task.getTraceId().toString() + " type:" + task.getFileType().toString());
            try {
                InputStream stream = getStream(task);
                ReaderV2 reader = buildReader(task.getFileType().toString(), task, stream);
                reader.read(plate);
            }catch (POIXMLException e) {
                log.info("poi异常", e);
                Producer.getIns().pull(new PhErrorMsg(
                        task.getJobId(), task.getTraceId(),
                        "", "kafka-connector",
                        "ooxml_exception", e.getMessage(), task.getAssetId()));
            }
            catch (Exception e){
                log.error(e.getMessage(), e);
            }
        }
        log.info("等待kafka任务," + Thread.currentThread().getName());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private InputStream getStream(OssTask task) throws OSSException, ClientException {
        String ossKey = task.getOssKey().toString();
        String traceId = task.getTraceId().toString();
        String fileType = task.getFileType().toString();
        String bucketName = props.get(OssCsvAndExcelSourceConnector.BUCKET_NAME_CONFIG);
        log.info("ossKey:" + ossKey + " jobID:" + task.getJobId().toString() + " ," + " traceID:" + traceId + " type:" + fileType);
        OSSObject object = client.getObject(bucketName, ossKey);
        return object.getObjectContent();
    }

    private ReaderV2 buildReader(String fileType, OssTask task, InputStream stream) throws Exception {
        ReaderV2 reader;
        switch (fileType) {
            case "csv":
                reader = new CsvReaderV2(UUID.randomUUID().toString(), task);
                reader.init(stream);
                break;
            case "xlsx":
                reader = new ExcelReaderV2(UUID.randomUUID().toString(), task);
                reader.init(stream);
                break;
            default:
                log.error("不支持的类型" + fileType);
                Producer.getIns().pull(new PhErrorMsg(
                        task.getJobId(), task.getTraceId(),
                        "", "kafka-connector",
                        "type_exception", fileType, task.getAssetId()));
                throw new Exception("不支持的类型" + fileType);
        }
        return reader;
    }
}
