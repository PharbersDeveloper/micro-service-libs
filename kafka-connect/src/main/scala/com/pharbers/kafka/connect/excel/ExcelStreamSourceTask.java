package com.pharbers.kafka.connect.excel;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.*;

import com.monitorjbl.xlsx.StreamingReader;
import com.monitorjbl.xlsx.exceptions.ParseException;
import com.pharbers.kafka.connect.csv.InputConfigKeys;
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

/**
 * 功能描述
 *
 * @author dcs
 * @version 0.0
 * @tparam T 构造泛型参数
 * @note 一些值得注意的地方
 * @since 2019/07/03 10:33
 */
public class ExcelStreamSourceTask extends SourceTask {
    private static final Logger log = LoggerFactory.getLogger(ExcelStreamSourceTask.class);
    public static final String FILENAME_FIELD = "filename";
    public static final String POSITION_FIELD = "position";

    private final SchemaBuilder VALUE_SCHEMA_BUILDER = SchemaBuilder.struct();

    //外部参数
    private String filename;
    private Workbook reader = null;
    private String topic = null;
    private int batchSize = ExcelStreamSourceConnector.DEFAULT_TASK_BATCH_SIZE;
    private boolean autoTitle;
    private List<String> titleList = new ArrayList<>();

    //线程共享变量，应该只赋值一次
    private Schema VALUE_SCHEMA;
    private InputStream stream;
    private Iterator<Row> rowsIterator;


    //线程共享会多次赋值变量
    private Long streamOffset;

    @Override
    public String version() {
        return new ExcelStreamSourceConnector().version();
    }

    @Override
    public void start(Map<String, String> props) {
        filename = props.get(InputConfigKeys.FILE_CONFIG);
        topic = props.get(InputConfigKeys.TOPIC_CONFIG);
        batchSize = Integer.parseInt(props.get(InputConfigKeys.TASK_BATCH_SIZE_CONFIG));
        autoTitle = Boolean.parseBoolean(props.get(InputConfigKeys.AUTO_TITLE_CONFIG));
        titleList = new ArrayList<>(Arrays.asList(props.get(InputConfigKeys.TITLE_CONFIG).split(",")));
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        log.info("begin poll" + logFilename());
        log.info("batchSize" + batchSize);
        synchronized (this) {
            if (stream == null) {
                try {
                    stream = Files.newInputStream(Paths.get(filename));
                    log.debug("Opened {} for reading", logFilename());
                } catch (NoSuchFileException e) {
                    log.warn("Couldn't find file {} for FileStreamSourceTask, sleeping to wait for it to be created", logFilename());
                    synchronized (this) {
                        this.wait(1000);
                    }
                    return null;
                } catch (IOException e) {
                    log.error("Error while trying to open file {}: ", filename, e);
                    throw new ConnectException(e);
                }
                reader = StreamingReader.builder().open(stream);
                Map<String, Object> offset = context.offsetStorageReader().offset(Collections.singletonMap(FILENAME_FIELD, filename));
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
                while (rowsIterator.hasNext() && streamOffset > 0){
                    rowsIterator.next();
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
                records.add(new SourceRecord(offsetKey(filename), offsetValue(streamOffset), topic, null,
                        null, null, VALUE_SCHEMA, value, System.currentTimeMillis()));

                //                StringBuilder res = new StringBuilder();
//                for (Cell c : r) {
//                    res.append(c.getStringCellValue()).append(",");
//                }
//                if (res.length() > 1) {
//                    res.delete(res.length() - 1, res.length());
//                    log.trace("Read a line from {}", logFilename());
//                    if (records == null)
//                        records = new ArrayList<>();
//                    synchronized (this) {
//                        streamOffset++;
//                    }
//                    records.add(new SourceRecord(offsetKey(filename), offsetValue(streamOffset), topic, null,
//                            null, null, VALUE_SCHEMA, res, System.currentTimeMillis()));
//
//                    if (records.size() >= batchSize) {
//                        return records;
//                    }
//                }
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
        return Collections.singletonMap(FILENAME_FIELD, filename);
    }

    private Map<String, Long> offsetValue(Long pos) {
        return Collections.singletonMap(POSITION_FIELD, pos);
    }

    private String logFilename() {
        return filename == null ? "stdin" : filename;
    }
}


