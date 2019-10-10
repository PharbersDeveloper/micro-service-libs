package com.pharbers.kafka.connect.oss;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;
import com.monitorjbl.xlsx.exceptions.ParseException;
import com.pharbers.kafka.connect.oss.kafka.ConsumerBuilder;
import com.pharbers.kafka.connect.oss.model.ExcelTitle;
import com.pharbers.kafka.schema.OssTask;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.*;

/**
 * 功能描述
 *
 * @author dcs
 * @version 0.0
 * @tparam T 构造泛型参数
 * @note 一些值得注意的地方
 * @since 2019/09/25 10:36
 */
public class OssCsvSourceTask extends SourceTask {
    private static final Logger log = LoggerFactory.getLogger(OssCsvSourceTask.class);

    private final SchemaBuilder VALUE_SCHEMA_BUILDER = SchemaBuilder.struct()
            .field("jobId", Schema.STRING_SCHEMA)
            .field("traceId", Schema.STRING_SCHEMA)
            .field("type", Schema.STRING_SCHEMA)
            .field("data", Schema.STRING_SCHEMA);
    //线程共享变量，应该一次任务只赋值一次
    private final ObjectMapper mapper = new ObjectMapper();
    private final Schema VALUE_SCHEMA = VALUE_SCHEMA_BUILDER.build();
    private final Schema KEY_SCHEMA = Schema.STRING_SCHEMA;
    private List<ExcelTitle> title = new ArrayList<>();

    private String bucketName;
    private String ossTaskTopic;
    private BufferedReader bufferedReader = null;
    private String topic;
    private String jobID;
    private String traceID;
    private boolean autoTitle;
    private List<String> titleList = new ArrayList<>();
    private int batchSize = OssExcelSourceConnector.DEFAULT_TASK_BATCH_SIZE;
    private OSS client = null;
    private KafkaConsumer<String, OssTask> consumer;

    private static final String JOBID_FIELD = "jobID";
    private static final String POSITION_FIELD = "position";
    //线程共享会多次赋值变量
    private Long streamOffset;
    private Iterator<ConsumerRecord<String, OssTask>> ossTasks;
    private String ossKey;

    @Override
    public String version() {
        return new OssCsvSourceConnector().version();
    }

    @Override
    public void start(Map<String, String> props) {
        log.info("start com.pharbers.kafka.connect.oss.OssCsvSourceTask");
        String endpoint = props.get(OssCsvSourceConnector.ENDPOINT_CONFIG);
        String accessKeyId = props.get(OssCsvSourceConnector.ACCESS_KEY_ID_CONFIG);
        String accessKeySecret = props.get(OssCsvSourceConnector.ACCESS_KEY_SECRET_CONFIG);
        bucketName = props.get(OssCsvSourceConnector.BUCKET_NAME_CONFIG);
        ossTaskTopic = props.get(OssCsvSourceConnector.OSS_TASK_TOPIC);
        topic = props.get(OssCsvSourceConnector.TOPIC_CONFIG);
        autoTitle = Boolean.parseBoolean(props.get(OssCsvSourceConnector.AUTO_TITLE_CONFIG));
        titleList = new ArrayList<>(Arrays.asList(props.get(OssCsvSourceConnector.TITLES_CONFIG).split(",")));
        batchSize = Integer.parseInt(props.get(OssCsvSourceConnector.TASK_BATCH_SIZE_CONFIG));
        client = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        consumer = new ConsumerBuilder().build(ossTaskTopic);
        ossTasks = consumer.poll(Duration.ofSeconds(1)).iterator();
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
//        log.info("begin poll" + logFilename());
//        log.info("batchSize" + batchSize);

        synchronized (this) {
            if (bufferedReader == null) {
                log.info("准备任务");
                while (!ossTasks.hasNext()) {
                    log.info("等待kafka任务");
                    ossTasks = consumer.poll(Duration.ofSeconds(1)).iterator();
                }
                OssTask task = ossTasks.next().value();
                consumer.commitSync();
                ossKey = task.getOssKey().toString();
                jobID = task.getJobId().toString();
                traceID = task.getTraceId().toString();
                log.info("ossKey:" + ossKey + " jobID:" + jobID + " traceID:" + traceID);
                try {
                    log.info("Polling object from oss");
                    OSSObject object = client.getObject(bucketName, ossKey);
                    log.info("Contest-Type: " + object.getObjectMetadata().getContentType());
                    bufferedReader = new BufferedReader(new InputStreamReader(object.getObjectContent(), Charset.forName("UTF-8")));
                    log.info("*********************START!");
                } catch (OSSException oe) {
                    System.out.println("Caught an OSSException, which means your request made it to OSS, "
                            + "but was rejected with an error response for some reason.");
                    System.out.println("Error Message: " + oe.getErrorCode());
                    System.out.println("Error Code:       " + oe.getErrorCode());
                    System.out.println("Request ID:      " + oe.getRequestId());
                    System.out.println("Host ID:           " + oe.getHostId());
                } catch (ClientException ce) {
                    System.out.println("Caught an ClientException, which means the client encountered "
                            + "a serious internal problem while trying to communicate with OSS, "
                            + "such as not being able to access the network.");
                    System.out.println("Error Message: " + ce.getMessage());
                }

                Map<String, Object> offset = context.offsetStorageReader().offset(Collections.singletonMap(JOBID_FIELD, jobID));
                if (offset != null) {
                    Object lastRecordedOffset = offset.get(POSITION_FIELD);
                    if (lastRecordedOffset != null && !(lastRecordedOffset instanceof Long))
                        throw new ConnectException("Offset position is the incorrect type");
                    streamOffset = (lastRecordedOffset != null) ? (Long) lastRecordedOffset : 0L;
                } else {
                    streamOffset = 0L;
                }
                log.info("offset:" + streamOffset);
                //根据配置文件以第一行为title并且跳过，或者使用指定title不跳过
                String[] titleRow = {};
                try {
                    titleRow = bufferedReader.readLine().split(",");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (autoTitle) {
                    titleList.clear();
                    title.clear();
                    for (String value : titleRow) {
                        titleList.add(value);
                        title.add(new ExcelTitle(value, "String"));
                    }
                } else {
                    title.clear();
                    for (String s : titleList) {
                        title.add(new ExcelTitle(s, "String"));
                    }
                }
                long rowOffset = streamOffset;
                while (rowOffset > 0) {
                    try {
                        bufferedReader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    rowOffset--;
                }
            }
        }
        try {
            ArrayList<SourceRecord> records = null;

            do {
                String row = bufferedReader.readLine();
                synchronized (this) {
                    if (row == null) {
                        log.info("读取完成");
                        log.info(streamOffset.toString());
                        if (records == null)
                            records = new ArrayList<>();
                        Struct headValue = new Struct(VALUE_SCHEMA);
                        headValue.put("jobId", jobID);
                        headValue.put("traceId", traceID);
                        headValue.put("type", "SandBox-Length");
                        headValue.put("data", "{\"length\": " + (streamOffset) + " }");
                        records.add(new SourceRecord(offsetKey(jobID), offsetValue(streamOffset), topic, null,
                                KEY_SCHEMA, jobID, VALUE_SCHEMA, headValue, System.currentTimeMillis()));
                        bufferedReader.close();
                        bufferedReader = null;
                        this.wait(1000);
                        break;
                    }
                }

                String[] r = row.split(",");

                Struct value = new Struct(VALUE_SCHEMA);
                value.put("jobId", jobID);
                value.put("traceId", traceID);
                value.put("type", "SandBox");
                Map<String, String> rowValue = new LinkedHashMap<>();
                for (int i = 0; i < titleList.size(); i++) {
                    String v = i >= r.length ? "" : r[i];
                    rowValue.put(titleList.get(i), v);
                }
                value.put("data", mapper.writeValueAsString(rowValue));
                log.trace("Read a line from {}", logFilename());
                if (records == null)
                    records = new ArrayList<>();
                if (streamOffset.equals(0L)) {
                    Struct headValue = new Struct(VALUE_SCHEMA);
                    headValue.put("jobId", jobID);
                    headValue.put("traceId", traceID);
                    headValue.put("type", "SandBox-Schema");
                    headValue.put("data", mapper.writeValueAsString(title));
                    records.add(new SourceRecord(offsetKey(jobID), offsetValue(streamOffset), topic, null,
                            KEY_SCHEMA, jobID, VALUE_SCHEMA, headValue, System.currentTimeMillis()));
                }
                synchronized (this) {
                    streamOffset++;
                }
                records.add(new SourceRecord(offsetKey(jobID), offsetValue(streamOffset), topic, null,
                        KEY_SCHEMA, jobID, VALUE_SCHEMA, value, System.currentTimeMillis()));
            } while (records.size() < batchSize);
            log.info("add:" + records.size() + " jobId:" + jobID);
            log.info("streamOffset:" + streamOffset);
            return records;
        } catch (ParseException e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            String exception = baos.toString();
            log.error("xml读取错误，检查文件格式" + exception);
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("json 解析错误", e);
            throw new ParseException("json error");
        }
    }

    @Override
    public void stop() {
        log.trace("Stopping");
        client.shutdown();
        consumer.close();
        synchronized (this) {
            try {
                bufferedReader.close();
                log.trace("Closed input stream");
            } catch (IOException e) {
                log.error("Failed to close FileStreamSourceTask stream: ", e);
            }
            this.notify();
        }
    }

    private Map<String, String> offsetKey(String filename) {
        return Collections.singletonMap(JOBID_FIELD, filename);
    }

    private Map<String, Long> offsetValue(Long pos) {
        return Collections.singletonMap(POSITION_FIELD, pos);
    }

    private String logFilename() {
        return ossKey == null ? "stdin" : ossKey;
    }
}
