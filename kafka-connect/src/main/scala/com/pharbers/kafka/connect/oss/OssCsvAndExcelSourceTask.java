package com.pharbers.kafka.connect.oss;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;
import com.pharbers.kafka.connect.oss.exception.TitleLengthException;
import com.pharbers.kafka.connect.oss.kafka.Producer;
import com.pharbers.kafka.connect.oss.reader.CsvReader;
import com.pharbers.kafka.connect.oss.reader.ExcelReader;
import com.pharbers.kafka.schema.OssTask;
import com.pharbers.kafka.connect.oss.kafka.ConsumerBuilder;
import com.pharbers.kafka.schema.PhErrorMsg;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.apache.poi.ooxml.POIXMLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

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
    private ConsumerBuilder<String, OssTask> kafkaConsumerBuffer;
    //todo: plate

    private ExecutorService executorService = null;
    private CsvReader csvReader = null;
    private ExcelReader excelReader = null;
    private boolean stop = false;
    private OssTask task;
    private String fileType = "";

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
        kafkaConsumerBuffer = new ConsumerBuilder<>(ossTaskTopic, OssTask.class);
        ThreadFactory threadFactory = new NameThreadFactory("kafka_listener");
        executorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), threadFactory);
        executorService.execute(kafkaConsumerBuffer);
        csvReader = new CsvReader(topic, batchSize);
        excelReader = new ExcelReader(topic, batchSize);
        stop = false;
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        synchronized (this) {
            if (csvReader.isEnd() && excelReader.isEnd()) {
                log.info("准备任务," + Thread.currentThread().getName());
                while (!kafkaConsumerBuffer.hasNext()) {
                    if (stop) {
                        log.info("结束任务," + Thread.currentThread().getName());
                        return new ArrayList<>();
                    }
                    log.info("等待kafka任务," + Thread.currentThread().getName());
                    Thread.sleep(1000);
                }
                task = kafkaConsumerBuffer.next();
                String ossKey = task.getOssKey().toString();
                String traceId = task.getTraceId().toString();
                fileType = task.getFileType().toString();
                log.info("ossKey:" + ossKey + " jobID:" + task.getJobId().toString() + " ," + " traceID:" + traceId + " type:" + fileType);
                try {
                    OSSObject object = client.getObject(bucketName, ossKey);
                    stream = object.getObjectContent();
                } catch (OSSException | ClientException oe) {
                    log.error(ossKey, oe);
                    return new ArrayList<>();
                }
                try {
                    buildReader(fileType, task);
                } catch (POIXMLException e) {
                    log.info("poi异常", e);
                    Producer.getIns().pull(new PhErrorMsg(
                            task.getJobId(), task.getTraceId(),
                            "", "kafka-connector",
                            "ooxml_exception", e.getMessage(), task.getAssetId()));
                    excelReader.close();
                    csvReader.close();
                    return new ArrayList<>();
                } catch (Exception e) {
                    log.error("构建reader异常", e);
                    excelReader.close();
                    csvReader.close();
                    return new ArrayList<>();
                }
            }
        }
        return read(fileType);
    }

    @Override
    public void stop() {
        log.trace("Stopping");
        executorService.shutdownNow();
        try {
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stop = true;
        client.shutdown();
        synchronized (this) {
            try {
                kafkaConsumerBuffer.close();
                csvReader.close();
                excelReader.close();
                if (stream != null){
                    stream.close();
                }
                log.trace("Closed input stream");
            } catch (IOException e) {
                log.error("Failed to close FileStreamSourceTask stream: ", e);
            } catch (NullPointerException e){
                log.error("NullPointerException", e);
            }
            this.notify();
        }
    }

    private Map<String, Object> getStreamOffset(String jobId) {
        String offsetKey = "jobId";
        Map<String, Object> offset = context.offsetStorageReader().offset(Collections.singletonMap(offsetKey, jobId));
        if (offset != null) {
            return offset;
        } else {
            return new HashMap<>(10);
        }
    }

    private void buildReader(String fileType, OssTask task) throws Exception {
        switch (fileType) {
            case "csv":
                csvReader = new CsvReader(csvReader.getTopic(), csvReader.getBatchSize());
                csvReader.init(stream, task, getStreamOffset(task.getJobId().toString()));
                break;
            case "xlsx":
                excelReader = new ExcelReader(excelReader.getTopic(), excelReader.getBatchSize());
                excelReader.init(stream, task, getStreamOffset(task.getJobId().toString()));
                break;
            default:
                log.error("不支持的类型" + fileType);
                Producer.getIns().pull(new PhErrorMsg(
                        task.getJobId(), task.getTraceId(),
                        "", "kafka-connector",
                        "type_exception", fileType, task.getAssetId()));
        }
    }

    private List<SourceRecord> read(String fileType) {
        switch (fileType) {
            case "csv":
                try {
                    return csvReader.read();
                } catch (TitleLengthException e) {
                    resetCsvRead();
                    log.error(e.getMessage(), e);
                }
            case "xlsx":
                return excelReader.read();
            default:
                log.error(fileType + "is Error Message: fileType missMatch");
                return new ArrayList<>();
        }
    }

    private void resetCsvRead(){
        CsvReader oldRead = csvReader;
        CsvReader reader = new CsvReader(csvReader.getTopic(), csvReader.getBatchSize());
        InputStream newStream = client.getObject(bucketName, task.getOssKey().toString()).getObjectContent();
        List<Integer> titleIndex = new ArrayList<>();
        if(task.getTitleIndex().isEmpty()){
            titleIndex.set(0, 1);
        } else {
            titleIndex.set(0, task.getTitleIndex().get(0) + 1);
        }
        task.setTitleIndex(titleIndex);
        try {
            reader.init(newStream, task, new HashMap<>(0));
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        csvReader = reader;
        oldRead.close();
    }

    static class NameThreadFactory implements ThreadFactory {
        private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        NameThreadFactory(String name) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = name +
                    POOL_NUMBER.getAndIncrement() +
                    "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }
}
