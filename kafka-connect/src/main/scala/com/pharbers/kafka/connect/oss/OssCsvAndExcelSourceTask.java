package com.pharbers.kafka.connect.oss;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;
import com.pharbers.kafka.connect.oss.reader.CsvReader;
import com.pharbers.kafka.connect.oss.reader.ExcelReader;
import com.pharbers.kafka.schema.OssTask;
import com.monitorjbl.xlsx.StreamingReader;
import com.monitorjbl.xlsx.exceptions.ParseException;
import com.pharbers.kafka.connect.oss.kafka.ConsumerBuilder;
import com.pharbers.kafka.connect.oss.model.ExcelTitle;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.Duration;
import java.util.*;

/**
 * @author cui
 * @ProjectName micro-service-libs
 * @ClassName OssCsvAndExcelSourceTask
 * @date 19-10-11 下午5:14
 * @Description: OssCsvAndExcelSourceTask
 */
public class OssCsvAndExcelSourceTask extends SourceTask {
    private static final Logger log = LoggerFactory.getLogger(OssCsvAndExcelSourceTask.class);
    private InputStream stream;
    private String bucketName;
    private OSS client = null;
    private KafkaConsumer<String, OssTask> consumer;
    private Iterator<ConsumerRecord<String, OssTask>> ossTasks;
    private CsvReader csvReader = null;
    private ExcelReader excelReader = null;

    @Override
    public String version() {
        return new OssCsvAndExcelSourceConnector().version();
    }

    @Override
    public void start(Map<String, String> props) {
        log.info("start");
        String endpoint = props.get(OssCsvAndExcelSourceConnector.ENDPOINT_CONFIG);
        String accessKeyId = props.get(OssCsvAndExcelSourceConnector.ACCESS_KEY_ID_CONFIG);
        String accessKeySecret = props.get(OssCsvAndExcelSourceConnector.ACCESS_KEY_SECRET_CONFIG);
        bucketName = props.get(OssCsvAndExcelSourceConnector.BUCKET_NAME_CONFIG);
        String ossTaskTopic = props.get(OssCsvAndExcelSourceConnector.OSS_TASK_TOPIC);
        String topic = props.get(OssCsvAndExcelSourceConnector.TOPIC_CONFIG);
        int batchSize = Integer.parseInt(props.get(OssCsvAndExcelSourceConnector.TASK_BATCH_SIZE_CONFIG));
        client = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        consumer = new ConsumerBuilder().build(ossTaskTopic);
        ossTasks = consumer.poll(Duration.ofSeconds(1)).iterator();
        csvReader = new CsvReader(topic, batchSize);
        excelReader = new ExcelReader(topic, batchSize);
    }

    @Override
    public List<SourceRecord> poll() {
        String fileType = "";
        synchronized (this) {
            if (csvReader.isEnd() && excelReader.isEnd()) {
                log.info("准备任务," + Thread.currentThread().getName());
                while (!ossTasks.hasNext()) {
                    log.info("等待kafka任务," + Thread.currentThread().getName());
                    ossTasks = consumer.poll(Duration.ofSeconds(1)).iterator();
                }
                OssTask task = ossTasks.next().value();
                consumer.commitSync();
                String ossKey = task.getOssKey().toString();
                String traceID = task.getTraceId().toString();
                fileType = task.getFileType().toString();
                log.info("ossKey:" + ossKey + " jobID:" + " ," + " traceID:" + traceID);
                try {
                    log.info("Polling object from oss");
                    OSSObject object = client.getObject(bucketName, ossKey);
                    log.info("Contest-Type: " + object.getObjectMetadata().getContentType());
                    stream = object.getObjectContent();
                } catch (OSSException | ClientException oe) {
                    log.error(ossKey, oe);
                }
                switch (fileType) {
                    case "csv":
                        csvReader = new CsvReader(csvReader.getTopic(), csvReader.getBatchSize());
                        csvReader.init(stream, traceID, getStreamOffset(traceID));
                        break;
                    case "xlsx":
                        excelReader = new ExcelReader(excelReader.getTopic(), excelReader.getBatchSize());
                        excelReader.init(stream, traceID, getStreamOffset(traceID));
                }
            }
        }
        List<SourceRecord> record = null;
        if (fileType.equals("csv")) {
            record = csvReader.read();
        } else if (fileType.equals("xlsx")) {
            record = excelReader.read();
        } else {
            log.error("Error Message: fileType missMatch");
        }
        return record;
    }

    @Override
    public void stop() {
        log.trace("Stopping");
        client.shutdown();
        consumer.close();
        synchronized (this) {
            try {
                csvReader.close();
                stream.close();
                log.trace("Closed input stream");
            } catch (IOException e) {
                log.error("Failed to close FileStreamSourceTask stream: ", e);
            }
            this.notify();
        }
    }

    private Map<String, Object> getStreamOffset(String traceID) {
        String offsetKey = "traceID";
        Map<String, Object> offset = context.offsetStorageReader().offset(Collections.singletonMap(offsetKey, traceID));
        if (offset != null) {
            return offset;
        } else {
            return new HashMap<>();
        }
    }
}
