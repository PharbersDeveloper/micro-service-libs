package com.pharbers.kafka.connect.oss;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;
import com.pharbers.kafka.schema.OssTask;
import com.monitorjbl.xlsx.StreamingReader;
import com.monitorjbl.xlsx.exceptions.ParseException;
import com.pharbers.kafka.connect.oss.kafka.ConsumerBuilder;
import com.pharbers.kafka.connect.oss.model.ExcelTitle;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
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
    private final SchemaBuilder VALUE_SCHEMA_BUILDER = SchemaBuilder.struct()
            .field("jobId", Schema.STRING_SCHEMA)
            .field("traceId", Schema.STRING_SCHEMA)
            .field("type", Schema.STRING_SCHEMA)
            .field("data", Schema.STRING_SCHEMA);

    //线程共享变量，应该只赋值一次
    private final ObjectMapper mapper = new ObjectMapper();
    private final Schema VALUE_SCHEMA = VALUE_SCHEMA_BUILDER.build();
    private final Schema KEY_SCHEMA = Schema.STRING_SCHEMA;
    private List<ExcelTitle> title = new ArrayList<>();

    private InputStream stream;
    private Iterator<Row> rowsIterator;

    private String bucketName;
    private String ossTaskTopic;
    private Workbook reader = null;
    private BufferedReader bufferedReader = null;
    private String topic;
    private String jobID;
    private String traceID;
    private String fileType;
    private boolean autoTitle;
    private List<String> titleList = new ArrayList<>();
    private int batchSize = OssCsvAndExcelSourceConnector.DEFAULT_TASK_BATCH_SIZE;
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
        return new OssCsvAndExcelSourceConnector().version();
    }

    @Override
    public void start(Map<String, String> props) {
        log.info("start com.pharbers.kafka.connect.oss.OssCsvAndExcelSourceTask");
        String endpoint = props.get(OssCsvAndExcelSourceConnector.ENDPOINT_CONFIG);
        String accessKeyId = props.get(OssCsvAndExcelSourceConnector.ACCESS_KEY_ID_CONFIG);
        String accessKeySecret = props.get(OssCsvAndExcelSourceConnector.ACCESS_KEY_SECRET_CONFIG);
        bucketName = props.get(OssCsvAndExcelSourceConnector.BUCKET_NAME_CONFIG);
        ossTaskTopic = props.get(OssCsvAndExcelSourceConnector.OSS_TASK_TOPIC);
        topic = props.get(OssCsvAndExcelSourceConnector.TOPIC_CONFIG);
        autoTitle = Boolean.parseBoolean(props.get(OssCsvAndExcelSourceConnector.AUTO_TITLE_CONFIG));
        titleList = new ArrayList<>(Arrays.asList(props.get(OssCsvAndExcelSourceConnector.TITLES_CONFIG).split(",")));
        batchSize = Integer.parseInt(props.get(OssCsvAndExcelSourceConnector.TASK_BATCH_SIZE_CONFIG));
        client = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        consumer = new ConsumerBuilder().build(ossTaskTopic);
        ossTasks = consumer.poll(Duration.ofSeconds(1)).iterator();
    }

    public List<SourceRecord> csvPoll(InputStream stream) throws InterruptedException {
        synchronized (this) {
            if (bufferedReader == null) {
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(stream, Charset.forName("UTF-8")));
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

    public List<SourceRecord> excelPoll(InputStream stream) throws InterruptedException {
        synchronized (this) {
            if (reader == null) {
                try {
                    reader = StreamingReader.builder().open(stream);
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

                Sheet sheet = reader.getSheetAt(0);
                //根据配置文件以第一行为title并且跳过，或者使用指定title不跳过
                rowsIterator = sheet.iterator();
                title.clear();
                if (autoTitle && rowsIterator.hasNext()) {
                    titleList.clear();
                    Row titleRow = rowsIterator.next();
                    for (Cell c : titleRow) {
                        String value = c.getStringCellValue();
                        titleList.add(value);
                        title.add(new ExcelTitle(value, "String"));
//                        VALUE_SCHEMA_BUILDER.field(value, Schema.STRING_SCHEMA);
                    }
//                    VALUE_SCHEMA = VALUE_SCHEMA_BUILDER.build();
                } else {
                    for (String s : titleList) {
                        title.add(new ExcelTitle(s, "String"));
                    }
//                    VALUE_SCHEMA = VALUE_SCHEMA_BUILDER.build();
                }
                long rowOffset = streamOffset;
                while (rowsIterator.hasNext() && rowOffset > 0) {
                    rowsIterator.next();
                    rowOffset--;
                }
            }
        }
        try {
            ArrayList<SourceRecord> records = null;

            do {
                synchronized (this) {
                    if (!rowsIterator.hasNext()) {
                        log.info("读取完成");
                        if (records == null)
                            records = new ArrayList<>();
                        Struct headValue = new Struct(VALUE_SCHEMA);
                        headValue.put("jobId", jobID);
                        headValue.put("traceId", traceID);
                        headValue.put("type", "SandBox-Length");
                        headValue.put("data", "{\"length\": " + streamOffset + " }");
                        records.add(new SourceRecord(offsetKey(jobID), offsetValue(streamOffset), topic, null,
                                KEY_SCHEMA, jobID, VALUE_SCHEMA, headValue, System.currentTimeMillis()));
                        reader.close();
                        reader = null;
                        this.wait(1000);
                        break;
                    }
                }

                Row r = rowsIterator.next();

                Struct value = new Struct(VALUE_SCHEMA);
                value.put("jobId", jobID);
                value.put("traceId", traceID);
                value.put("type", "SandBox");
                Map<String, String> rowValue = new LinkedHashMap<>();
                for (int i = 0; i < titleList.size(); i++) {
                    String v = r.getCell(i) == null ? "" : r.getCell(i).getStringCellValue();
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

    public Long getStreamOffset() {
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
        return streamOffset;
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
//        log.info("begin poll" + logFilename());
//        log.info("batchSize" + batchSize);

        synchronized (this) {
            if (bufferedReader == null && reader == null) {
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
                fileType = task.getFileType().toString();
                log.info("ossKey:" + ossKey + " jobID:" + jobID + " traceID:" + traceID);
                try {
                    log.info("Polling object from oss");
                    OSSObject object = client.getObject(bucketName, ossKey);
                    log.info("Contest-Type: " + object.getObjectMetadata().getContentType());
                    stream = object.getObjectContent();
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
                streamOffset = getStreamOffset();
            }
        }
        List<SourceRecord> record = null;
        if (fileType.equals("csv")) {
            record = csvPoll(stream);
        } else if (fileType.equals("xlsx")) {
            record = excelPoll(stream);
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
                bufferedReader.close();
                reader.close();
                stream.close();
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
