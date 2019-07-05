package com.pharbers.kafka.connect.excel;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.*;

import com.monitorjbl.xlsx.StreamingReader;
import com.monitorjbl.xlsx.exceptions.ParseException;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
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

    //TODO: 改成可配置化的传参，可筛选列的形式。
    public static final String[] LIST_TITLE = {"YEAR", "MONTH", "HOSP_ID", "MOLE_NAME", "PRODUCT_NAME",
            "PACK_DES", "PACK_NUMBER", "VALUE", "STANDARD_UNIT", "DOSAGE", "DELIVERY_WAY", "CORP_NAME"};

    private SchemaBuilder VALUE_SCHEMA_BUILDER = SchemaBuilder.struct();
    private Schema VALUE_SCHEMA = null;

    private String filename;
    private InputStream stream;
    private Workbook reader = null;
    private Iterator<Row> rowsIterator = null;
    private String topic = null;
    private int batchSize = ExcelStreamSourceConnector.DEFAULT_TASK_BATCH_SIZE;

    private Long streamOffset;
    private long offsetRow = 0L;

    @Override
    public String version() {
        return new ExcelStreamSourceConnector().version();
    }

    @Override
    public void start(Map<String, String> props) {
        filename = props.get(ExcelStreamSourceConnector.FILE_CONFIG);
        topic = props.get(ExcelStreamSourceConnector.TOPIC_CONFIG);
        batchSize = Integer.parseInt(props.get(ExcelStreamSourceConnector.TASK_BATCH_SIZE_CONFIG));
        for (int i = 0; i < LIST_TITLE.length; i++) {
            VALUE_SCHEMA_BUILDER.field(LIST_TITLE[i], Schema.STRING_SCHEMA);
        }
        VALUE_SCHEMA = VALUE_SCHEMA_BUILDER.build();

    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        log.info("begin poll" + logFilename());
        log.info("batchSize" + batchSize);
        synchronized (this) {
            if (stream == null){
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
                //todo: 根据配置读取表,不要第一行.
                rowsIterator =  sheet.iterator();
                while (rowsIterator.hasNext() && streamOffset > 0){
                    rowsIterator.next();
                }
            }
        }

        try {
            ArrayList<SourceRecord> records = null;

            while (true){
                synchronized (this) {
                    if (!rowsIterator.hasNext()){
                        synchronized (this) {
                            log.info("读取完成");
                            this.wait(1000);
                        }
                        break;
                    }
                }

                Row r = rowsIterator.next();

                Struct value = new Struct(VALUE_SCHEMA);
                //TODO:做异常处理
                for (int i = 0; i < LIST_TITLE.length; i++) {
                    value.put(LIST_TITLE[i], r.getCell(i).getStringCellValue());
                }
                log.trace("Read a line from {}", logFilename());
                if (records == null)
                    records = new ArrayList<>();
                synchronized (this) {
                    streamOffset++;
                }
                records.add(new SourceRecord(offsetKey(filename), offsetValue(streamOffset), topic, null,
                        null, null, VALUE_SCHEMA, value, System.currentTimeMillis()));

                if (records.size() >= batchSize) {
                    return records;
                }
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
            }
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


