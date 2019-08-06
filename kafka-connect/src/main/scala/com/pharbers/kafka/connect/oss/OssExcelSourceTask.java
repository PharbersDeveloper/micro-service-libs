package com.pharbers.kafka.connect.oss;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;
import com.monitorjbl.xlsx.StreamingReader;
import com.monitorjbl.xlsx.exceptions.ParseException;
import com.monitorjbl.xlsx.impl.TempFileUtil;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author jeorch
 * @ProjectName micro-service-libs
 * @ClassName OssSourceTask
 * @date 19-7-1下午7:46
 * @Description: TODO
 */
public class OssExcelSourceTask extends SourceTask {

    private static final Logger log = LoggerFactory.getLogger(OssExcelSourceTask.class);

    private final SchemaBuilder VALUE_SCHEMA_BUILDER = SchemaBuilder.struct().field("jobId", Schema.STRING_SCHEMA);
    //线程共享变量，应该只赋值一次
    private Schema VALUE_SCHEMA;
    private final Schema KEY_SCHEMA = Schema.STRING_SCHEMA;

    private InputStream stream;
    private Iterator<Row> rowsIterator;

    private String bucketName;
    private String ossKey;
    private Workbook reader = null;
    private String topic = null;
    private String jobId;
    private boolean autoTitle;
    private List<String> titleList = new ArrayList<>();
    private int batchSize = OssExcelSourceConnector.DEFAULT_TASK_BATCH_SIZE;
    private OSS client = null;

    public static final String JOBID_FIELD = "jobId";
    public static final String POSITION_FIELD = "position";
    //线程共享会多次赋值变量
    private Long streamOffset;

    @Override
    public String version() {
        return new OssExcelSourceConnector().version();
    }

    @Override
    public void start(Map<String, String> props) {
        String endpoint = props.get(OssExcelSourceConnector.ENDPOINT_CONFIG);
        String accessKeyId = props.get(OssExcelSourceConnector.ACCESS_KEY_ID_CONFIG);
        String accessKeySecret = props.get(OssExcelSourceConnector.ACCESS_KEY_SECRET_CONFIG);
        bucketName = props.get(OssExcelSourceConnector.BUCKET_NAME_CONFIG);
        ossKey = props.get(OssExcelSourceConnector.KEY_CONFIG);
        if (ossKey == null || ossKey.isEmpty()) {
            stream = System.in;
            // Tracking offset for stdin doesn't make sense
            streamOffset = null;
        }
        // Missing topic or parsing error is not possible because we've parsed the config in the
        // Connector
        topic = props.get(OssExcelSourceConnector.TOPIC_CONFIG);
        jobId = props.get(OssExcelSourceConnector.JOB_ID_CONFIG);
        autoTitle = Boolean.parseBoolean(props.get(OssExcelSourceConnector.AUTO_TITLE_CONFIG));
        titleList = new ArrayList<>(Arrays.asList(props.get(OssExcelSourceConnector.TITLES_CONFIG).split(",")));
        batchSize = Integer.parseInt(props.get(OssExcelSourceConnector.TASK_BATCH_SIZE_CONFIG));
        client = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        log.info("begin poll" + logFilename());
        log.info("batchSize" + batchSize);
        synchronized (this) {
            if (stream == null) {
                try {
                    log.info("Polling object from oss");
                    OSSObject object = client.getObject(bucketName, ossKey);
                    log.info("Contest-Type: " + object.getObjectMetadata().getContentType());
                    stream = object.getObjectContent();
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
                } finally {
                    /*
                     * Do not forget to shut down the client finally to release all allocated resources.
                     */
                    client.shutdown();
                }

                Map<String, Object> offset = context.offsetStorageReader().offset(Collections.singletonMap(JOBID_FIELD, jobId));
                if (offset != null) {
                    Object lastRecordedOffset = offset.get(POSITION_FIELD);
                    if (lastRecordedOffset != null && !(lastRecordedOffset instanceof Long))
                        throw new ConnectException("Offset position is the incorrect type");
                    streamOffset = (lastRecordedOffset != null) ? (Long) lastRecordedOffset : 0L;
                } else {
                    streamOffset = 0L;
                }
                log.info("offset:" + streamOffset);
                Sheet sheet = reader.getSheetAt(0);
                //根据配置文件以第一行为title并且跳过，或者使用指定title不跳过
                rowsIterator =  sheet.iterator();
                if(autoTitle && rowsIterator.hasNext()){
                    titleList.clear();
                    Row titleRow = rowsIterator.next();
                    for (Cell c : titleRow) {
                        String value = c.getStringCellValue();
                        titleList.add(value);
                        VALUE_SCHEMA_BUILDER.field(value, Schema.STRING_SCHEMA);
                    }
                    VALUE_SCHEMA = VALUE_SCHEMA_BUILDER.build();
                } else {
                    for(String s : titleList){
                        VALUE_SCHEMA_BUILDER.field(s, Schema.STRING_SCHEMA);
                    }
                    VALUE_SCHEMA = VALUE_SCHEMA_BUILDER.build();
                }
                long rowOffset = streamOffset;
                while (rowsIterator.hasNext() && rowOffset > 0){
                    rowsIterator.next();
                    rowOffset --;
                }
            }
        }
        try {
            ArrayList<SourceRecord> records = null;

            do {
                synchronized (this) {
                    if (!rowsIterator.hasNext()) {
                        log.info("读取完成");
                        this.wait(1000);
                        break;
                    }
                }

                Row r = rowsIterator.next();

                Struct value = new Struct(VALUE_SCHEMA);
                value.put("jobId", jobId);
                for (int i = 0; i < titleList.size(); i++) {
                    String v = r.getCell(i) == null ? "" : r.getCell(i).getStringCellValue();
                    value.put(titleList.get(i), v);
                }
                log.trace("Read a line from {}", logFilename());
                if (records == null)
                    records = new ArrayList<>();
                synchronized (this) {
                    streamOffset++;
                }
                records.add(new SourceRecord(offsetKey(jobId), offsetValue(streamOffset), topic, null,
                        KEY_SCHEMA, jobId, VALUE_SCHEMA, value, System.currentTimeMillis()));
            } while (records.size() < batchSize);
            return records;
        } catch (ParseException e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            String exception = baos.toString();
            log.error("xml读取错误，检查文件格式" + exception);
            throw e;
        }
    }

    @Override
    public void stop() {
        log.trace("Stopping");
        synchronized (this) {
            try {
                if (stream != null && stream != System.in) {
                    stream.close();
                    reader.close();
                    log.trace("Closed input stream");
                }
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
