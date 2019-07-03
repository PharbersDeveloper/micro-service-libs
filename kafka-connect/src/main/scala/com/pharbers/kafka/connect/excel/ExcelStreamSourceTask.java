package com.pharbers.kafka.connect.excel;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.monitorjbl.xlsx.StreamingReader;
import com.monitorjbl.xlsx.exceptions.ParseException;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;

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
    private static final Schema VALUE_SCHEMA = Schema.STRING_SCHEMA;

    private String filename;
    private InputStream stream;
    private Workbook reader = null;
    private String topic = null;
    private int batchSize = ExcelStreamSourceConnector.DEFAULT_TASK_BATCH_SIZE;

    private Long streamOffset;

    @Override
    public String version() {
        return new ExcelStreamSourceConnector().version();
    }

    @Override
    public void start(Map<String, String> props) {
        filename = props.get(ExcelStreamSourceConnector.FILE_CONFIG);
//        if (filename == null || filename.isEmpty()) {
//            stream = System.in;
//            // Tracking offset for stdin doesn't make sense
//            streamOffset = null;
//            reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
//        }
        // Missing topic or parsing error is not possible because we've parsed the config in the
        // Connector
        topic = props.get(ExcelStreamSourceConnector.TOPIC_CONFIG);
        batchSize = Integer.parseInt(props.get(ExcelStreamSourceConnector.TASK_BATCH_SIZE_CONFIG));
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        log.info("begin poll" + logFilename());
        log.info("batchSize" + batchSize);
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
        // Unfortunately we can't just use readLine() because it blocks in an uninterruptible way.
        // Instead we have to manage splitting lines ourselves, using simple backoff when no new data
        // is available.
        try {
            final Workbook readerCopy;
            synchronized (this) {
                readerCopy = reader;
            }
            if (readerCopy == null)
                return null;

            ArrayList<SourceRecord> records = null;

            long offsetRow = streamOffset;
            Sheet sheet = readerCopy.getSheetAt(0);
            for (Row r : sheet) {
                if (offsetRow > 0) {
                    offsetRow--;
                    continue;
                }
                StringBuilder res = new StringBuilder();
                for (Cell c : r) {
                    res.append(c.getStringCellValue()).append(",");
                }
                if (res.length() > 1) {
                    res.delete(res.length() - 1, res.length());
                    log.trace("Read a line from {}", logFilename());
                    if (records == null)
                        records = new ArrayList<>();
                    streamOffset++;
                    records.add(new SourceRecord(offsetKey(filename), offsetValue(streamOffset), topic, null,
                            null, null, VALUE_SCHEMA, res, System.currentTimeMillis()));

                    if (records.size() >= batchSize) {
                        return records;
                    }
                }
            }
            return records;
        } catch (ParseException e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            String exception = baos.toString();
            log.error("ParseException!!!!" + exception);
            throw e;
        } finally {
            try {
                reader.close();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void stop() {
        log.trace("Stopping");
        synchronized (this) {
            try {
                if (stream != null && stream != System.in) {
                    stream.close();
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


