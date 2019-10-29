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
//    private final SchemaBuilder VALUE_SCHEMA_BUILDER = SchemaBuilder.struct()
//            .field("jobId", Schema.STRING_SCHEMA)
//            .field("traceId", Schema.STRING_SCHEMA)
//            .field("type", Schema.STRING_SCHEMA)
//            .field("data", Schema.STRING_SCHEMA);
//
//    //线程共享变量，应该只赋值一次
//    private final ObjectMapper mapper = new ObjectMapper();
//    private final Schema VALUE_SCHEMA = VALUE_SCHEMA_BUILDER.build();
//    private final Schema KEY_SCHEMA = Schema.STRING_SCHEMA;
//    private Map<String, List<ExcelTitle>> title = new HashMap<>();

    private InputStream stream;
//    private Iterator<Row> rowsIterator = null;

    private String bucketName;
    //    private Workbook reader = null;
//    private BufferedReader bufferedReader = null;
//    private String topic;
//    private Map<Iterator<Row>, String> jobIDs;
    private String traceID;
    private String fileType;
    //    private Map<String, List<String>> titleMap = new HashMap<>();
//    private int batchSize = OssCsvAndExcelSourceConnector.DEFAULT_TASK_BATCH_SIZE;
    private OSS client = null;
    private KafkaConsumer<String, OssTask> consumer;

    private static final String JOBID_FIELD = "traceID";
    //    private static final String POSITION_FIELD = "position";
    //线程共享会多次赋值变量
//    private Map<String, Long> streamOffset;
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
                traceID = task.getTraceId().toString();
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
                        csvReader.init(stream, traceID, getStreamOffset());
                        break;
                    case "xlsx":
                        excelReader = new ExcelReader(excelReader.getTopic(), excelReader.getBatchSize());
                        excelReader.init(stream, traceID, getStreamOffset());
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

//    private List<SourceRecord> csvPoll(InputStream stream) {
//        String jobID = traceID + 0;
//        int buffSize = 2048;
//        synchronized (this) {
//            if (bufferedReader == null) {
//                bufferedReader = new BufferedReader(new InputStreamReader(stream, Charset.forName("UTF-8")), buffSize);
//                log.info("*********************START!");
//                String[] titleRow = {};
//                try {
//                    titleRow = bufferedReader.readLine().split(",");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                titleMap.clear();
//                title.clear();
//                List<ExcelTitle> sheetTitle = new ArrayList<>();
//                List<String> titleList = new ArrayList<>();
//                for (String value : titleRow) {
//                    titleList.add(value);
//                    sheetTitle.add(new ExcelTitle(value, "String"));
//                }
//                titleMap.put(traceID + 0, titleList);
//                title.put(traceID + 0, sheetTitle);
//                if(!streamOffset.containsKey(jobID)){
//                    streamOffset.put(jobID, 0L);
//                }
//                long rowOffset = streamOffset.get(jobID);
//                while (rowOffset > 0) {
//                    try {
//                        bufferedReader.readLine();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    rowOffset--;
//                }
//            }
//        }
//        try {
//            ArrayList<SourceRecord> records = null;
//            List<String> titleList = titleMap.get(jobID);
//            do {
//                String row = bufferedReader.readLine();
//                synchronized (this) {
//                    if (row == null) {
//                        log.info("读取完成");
//                        log.info(streamOffset.toString());
//                        if (records == null)
//                            records = new ArrayList<>();
//                        Struct headValue = new Struct(VALUE_SCHEMA);
//                        headValue.put("jobId", jobID);
//                        headValue.put("traceId", traceID);
//                        headValue.put("type", "SandBox-Length");
//                        headValue.put("data", "{\"length\": " + (streamOffset.get(jobID)) + " }");
//                        records.add(new SourceRecord(offsetKey(jobID), offsetValueCoding(streamOffset), topic, null,
//                                KEY_SCHEMA, jobID, VALUE_SCHEMA, headValue, System.currentTimeMillis()));
//                        bufferedReader.close();
//                        bufferedReader = null;
//                        stream.close();
//                        this.stream = null;
//                        break;
//                    }
//                }
//
//                String[] r = row.split(",");
//
//                Struct value = new Struct(VALUE_SCHEMA);
//                value.put("jobId", jobID);
//                value.put("traceId", traceID);
//                value.put("type", "SandBox");
//                Map<String, String> rowValue = new LinkedHashMap<>();
//                for (int i = 0; i < titleList.size(); i++) {
//                    String v = i >= r.length ? "" : r[i];
//                    rowValue.put(titleList.get(i), v);
//                }
//                value.put("data", mapper.writeValueAsString(rowValue));
//                log.trace("Read a line from {}", logFilename());
//                if (records == null)
//                    records = new ArrayList<>();
//                if (streamOffset.get(jobID).equals(0L)) {
//                    Struct headValue = new Struct(VALUE_SCHEMA);
//                    headValue.put("jobId", jobID);
//                    headValue.put("traceId", traceID);
//                    headValue.put("type", "SandBox-Schema");
//                    headValue.put("data", mapper.writeValueAsString(title.get(jobID)));
//                    records.add(new SourceRecord(offsetKey(jobID), offsetValueCoding(streamOffset), topic, null,
//                            KEY_SCHEMA, jobID, VALUE_SCHEMA, headValue, System.currentTimeMillis()));
//                }
//                synchronized (this) {
//                    streamOffset.put(jobID, streamOffset.get(jobID) + 1);
//                }
//                records.add(new SourceRecord(offsetKey(jobID), offsetValueCoding(streamOffset), topic, null,
//                        KEY_SCHEMA, jobID, VALUE_SCHEMA, value, System.currentTimeMillis()));
//            } while (records.size() < batchSize);
//            log.info("add:" + records.size() + " jobId:" + jobID + " traceId:" + traceID);
//            log.info("streamOffset:" + streamOffset.get(jobID));
//            return records;
//        } catch (ParseException e) {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            e.printStackTrace(new PrintStream(baos));
//            String exception = baos.toString();
//            log.error("xml读取错误，检查文件格式" + exception);
//            throw e;
//        } catch (IOException e) {
//            e.printStackTrace();
//            log.error("json 解析错误", e);
//            throw new ParseException("json error");
//        }
//    }

//    private List<SourceRecord> excelPoll(InputStream stream) {
//        synchronized (this) {
//            if (reader == null) {
//                try {
//                    reader = StreamingReader.builder().open(stream);
//                    log.info("*********************START!");
//                } catch (OSSException oe) {
//                    System.out.println("Caught an OSSException, which means your request made it to OSS, "
//                            + "but was rejected with an error response for some reason.");
//                    System.out.println("Error Message: " + oe.getErrorCode());
//                    System.out.println("Error Code:       " + oe.getErrorCode());
//                    System.out.println("Request ID:      " + oe.getRequestId());
//                    System.out.println("Host ID:           " + oe.getHostId());
//                } catch (ClientException ce) {
//                    System.out.println("Caught an ClientException, which means the client encountered "
//                            + "a serious internal problem while trying to communicate with OSS, "
//                            + "such as not being able to access the network.");
//                    System.out.println("Error Message: " + ce.getMessage());
//                }
//                Iterator<Sheet> sheets = reader.sheetIterator();
//                int index = 0;
//                title.clear();
//                titleMap.clear();
//                while (sheets.hasNext()) {
//                    List<ExcelTitle> sheetTitle = new ArrayList<>();
//                    List<String> titleList = new ArrayList<>();
//                    Sheet sheet = sheets.next();
//                    jobIDs.put(sheet.rowIterator(), traceID + index);
//                    //根据配置文件以第一行为title并且跳过，或者使用指定title不跳过
//                    if (sheet.rowIterator().hasNext()) {
//                        Row titleRow = sheet.rowIterator().next();
//                        for (Cell c : titleRow) {
//                            String value = c.getStringCellValue();
//                            titleList.add(value);
//                            //todo: 类型判断，现在全是string
//                            if (!value.equals("")) sheetTitle.add(new ExcelTitle(value, "String"));
//                        }
//                    }
//                    titleMap.put(traceID + index, titleList);
//                    title.put(traceID + index, sheetTitle);
//                    if (!streamOffset.containsKey(traceID + index)) {
//                        streamOffset.put(traceID + index, 0L);
//                    }
//                    long rowOffset = Long.parseLong(streamOffset.get(traceID + index).toString());
//                    while (sheet.rowIterator().hasNext() && rowOffset > 0) {
//                        sheet.rowIterator().next();
//                        rowOffset--;
//                    }
//                    index++;
//                }
//            }
//
//        }
//        try {
//            ArrayList<SourceRecord> records = null;
//            if (jobIDs.keySet().size() == 0) {
//                reader.close();
//                stream.close();
//                this.stream = null;
//                reader = null;
//                return new ArrayList<>();
//            }
//            Iterator<Row> rowsIterator = jobIDs.keySet().iterator().next();
//            String jobID = jobIDs.get(rowsIterator);
//            List<String> titleList = titleMap.get(jobID);
//            do {
//                synchronized (this) {
//                    if (!rowsIterator.hasNext()) {
//                        log.info("读取完成");
//                        if (records == null)
//                            records = new ArrayList<>();
//                        Struct headValue = new Struct(VALUE_SCHEMA);
//                        headValue.put("jobId", jobID);
//                        headValue.put("traceId", traceID);
//                        headValue.put("type", "SandBox-Length");
//                        headValue.put("data", "{\"length\": " + streamOffset.get(jobID) + " }");
//                        records.add(new SourceRecord(offsetKey(traceID), offsetValueCoding(streamOffset), topic, null,
//                                KEY_SCHEMA, jobID, VALUE_SCHEMA, headValue, System.currentTimeMillis()));
//                        jobIDs.remove(rowsIterator);
//                        break;
//                    }
//                }
//
//                Row r = rowsIterator.next();
//
//                Struct value = new Struct(VALUE_SCHEMA);
//                value.put("jobId", jobID);
//                value.put("traceId", traceID);
//                value.put("type", "SandBox");
//                Map<String, String> rowValue = new LinkedHashMap<>();
//                for (int i = 0; i < titleList.size(); i++) {
//                    String v = r.getCell(i) == null ? "" : r.getCell(i).getStringCellValue();
//                    if (!titleList.get(i).equals("")) rowValue.put(titleList.get(i), v);
//                }
//                value.put("data", mapper.writeValueAsString(rowValue));
//                log.trace("Read a line from {}", logFilename());
//                if (records == null)
//                    records = new ArrayList<>();
//                if (streamOffset.get(jobID).equals(0L)) {
//                    Struct headValue = new Struct(VALUE_SCHEMA);
//                    headValue.put("jobId", jobID);
//                    headValue.put("traceId", traceID);
//                    headValue.put("type", "SandBox-Schema");
//                    headValue.put("data", mapper.writeValueAsString(title.get(jobID)));
//                    records.add(new SourceRecord(offsetKey(traceID), offsetValueCoding(streamOffset), topic, null,
//                            KEY_SCHEMA, jobID, VALUE_SCHEMA, headValue, System.currentTimeMillis()));
//                }
//                synchronized (this) {
//                    streamOffset.put(jobID, streamOffset.get(jobID) + 1);
//                }
//                records.add(new SourceRecord(offsetKey(traceID), offsetValueCoding(streamOffset), topic, null,
//                        KEY_SCHEMA, jobID, VALUE_SCHEMA, value, System.currentTimeMillis()));
//            } while (records.size() < batchSize);
//            return records;
//        } catch (ParseException e) {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            e.printStackTrace(new PrintStream(baos));
//            String exception = baos.toString();
//            log.error("xml读取错误，检查文件格式" + exception);
//            throw e;
//        } catch (IOException e) {
//            e.printStackTrace();
//            log.error("json 解析错误", e);
//            throw new ParseException("json error");
//        }
//    }

    private Map<String, Object> getStreamOffset() {
        Map<String, Object> offset = context.offsetStorageReader().offset(Collections.singletonMap(JOBID_FIELD, traceID));
        if (offset != null) {
            return offset;
        } else {
            return new HashMap<>();
        }
    }

//    private Map<String, String> offsetKey(String filename) {
//        return Collections.singletonMap(JOBID_FIELD, filename);
//    }
//
//    private Map<String, String> offsetValueCoding(Map<String, Long> pos) throws IOException {
//        String json = new ObjectMapper().writeValueAsString(pos);
//        return Collections.singletonMap(POSITION_FIELD, json);
//    }
//
//    private Map<String, Long> recodeOffset(String offset) throws IOException {
//        return new ObjectMapper().readValue(offset, HashMap.class);
//    }
//
//    private String logFilename() {
//        return ossKey == null ? "stdin" : ossKey;
//    }
}
