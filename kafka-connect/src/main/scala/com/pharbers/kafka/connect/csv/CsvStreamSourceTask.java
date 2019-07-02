package com.pharbers.kafka.connect.csv;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.*;

/**
 * 功能描述
 *
 * @author dcs
 * @version 0.0
 * @tparam T 构造泛型参数
 * @note 一些值得注意的地方
 * @since 2019/07/02 10:35
 */
public class CsvStreamSourceTask extends SourceTask {
    private static final Logger log = LoggerFactory.getLogger(CsvStreamSourceConnector.class);
    private static final String FILENAME_FIELD = "filename";
    private static final String POSITION_FIELD = "position";
    private static final Schema VALUE_SCHEMA = Schema.STRING_SCHEMA;

    private InputStream stream;
    private BufferedReader reader = null;
    //TODO： 一行如果超过1024会有问题
    private char[] buffer = new char[1024];

    private int batchSize = CsvStreamSourceConnector.DEFAULT_TASK_BATCH_SIZE;
    private String filename;
    private String topic = null;
    private String separator;
    private String charset;
    private String title;

    //并发会怎么样
    private int offset = 0;
    private Long streamOffset;

    @Override
    public String version() {
        return new CsvStreamSourceConnector().version();
    }

    @Override
    public void start(Map<String, String> props) {
        charset = props.get(CsvInputConfigKeys.CHARSET_CONFIG);
        filename = props.get(CsvInputConfigKeys.FILE_CONFIG);
        if (filename == null || filename.isEmpty()) {
            stream = System.in;
            // Tracking offset for stdin doesn't make sense
            streamOffset = null;
            reader = new BufferedReader(new InputStreamReader(stream, Charset.forName(charset)));
        }
        // Missing topic or parsing error is not possible because we've parsed the config in the
        // Connector
        topic = props.get(CsvInputConfigKeys.TOPIC_CONFIG);
        batchSize = Integer.parseInt(props.get(CsvInputConfigKeys.TASK_BATCH_SIZE_CONFIG));
        separator = props.get(CsvInputConfigKeys.SEPARATOR_CONFIG);
        title = props.get(CsvInputConfigKeys.TITLE_CONFIG);
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        if (stream == null) {
            try {
                stream = Files.newInputStream(Paths.get(filename));
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, Charset.forName(charset)));
                Map<String, Object> offset = context.offsetStorageReader().offset(Collections.singletonMap(FILENAME_FIELD, filename));
                // offset 按字节ship， 对于整行读取的方式可能有bug
                //offset记录行的偏移
                if (offset != null) {
                    Object lastRecordedOffset = offset.get(POSITION_FIELD);
                    if (lastRecordedOffset != null && !(lastRecordedOffset instanceof Long))
                        throw new ConnectException("Offset position is the incorrect type");
                    if (lastRecordedOffset != null) {
                        log.debug("Found previous offset, trying to skip to file offset {}", lastRecordedOffset);
                        long skipLeft = (Long) lastRecordedOffset;
//                        while (skipLeft > 0) {
//                            try {
//                                long skipped = stream.skip(skipLeft);
//                                skipLeft -= skipped;
//                            } catch (IOException e) {
//                                log.error("Error while trying to seek to previous offset in file {}: ", filename, e);
//                                throw new ConnectException(e);
//                            }
//                        }
                        for (int i = 0; i < skipLeft; i++){
                            bufferedReader.readLine();
                        }
                        log.debug("Skipped to offset {}", lastRecordedOffset);
                    }
                    streamOffset = (lastRecordedOffset != null) ? (Long) lastRecordedOffset : 0L;
                } else {
                    streamOffset = 0L;
                }
                reader = bufferedReader;
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
        }

        // Unfortunately we can't just use readLine() because it blocks in an uninterruptible way.
        // Instead we have to manage splitting lines ourselves, using simple backoff when no new data
        // is available.
        try {
            final BufferedReader readerCopy;
            synchronized (this) {
                readerCopy = reader;
            }
            if (readerCopy == null)
                return null;

            ArrayList<SourceRecord> records = null;

            int nread = 0;
            while (readerCopy.ready()) {
                nread = readerCopy.read(buffer, offset, buffer.length - offset);
                log.trace("Read {} bytes from {}", nread, logFilename());

                if (nread > 0) {
                    offset += nread;
                    if (offset == buffer.length) {
                        char[] newbuf = new char[buffer.length * 2];
                        System.arraycopy(buffer, 0, newbuf, 0, buffer.length);
                        buffer = newbuf;
                    }

                    String line;
                    do {
                        line = extractLine();
                        if (line != null) {
                            log.trace("Read a line from {}", logFilename());
                            if (records == null)
                                records = new ArrayList<>();
                            records.add(new SourceRecord(offsetKey(filename), offsetValue(streamOffset), topic, null,
                                    null, null, VALUE_SCHEMA, transform(line), System.currentTimeMillis()));

                            if (records.size() >= batchSize) {
                                return records;
                            }
                        }
                    } while (line != null);
                }
            }

            if (nread <= 0)
                synchronized (this) {
                    this.wait(1000);
                }

            return records;
        } catch (IOException e) {
            // Underlying stream was killed, probably as a result of calling stop. Allow to return
            // null, and driving thread will handle any shutdown if necessary.
        }
        return null;
    }

    private String extractLine() {
        int until = -1, newStart = -1;
        for (int i = 0; i < offset; i++) {
            if (buffer[i] == '\n') {
                until = i;
                newStart = i + 1;
                break;
            } else if (buffer[i] == '\r') {
                // We need to check for \r\n, so we must skip this if we can't check the next char
                if (i + 1 >= offset)
                    return null;

                until = i;
                newStart = (buffer[i + 1] == '\n') ? i + 2 : i + 1;
                break;
            }
        }

        if (until != -1) {
            String result = new String(buffer, 0, until);
            System.arraycopy(buffer, newStart, buffer, 0, buffer.length - newStart);
            offset = offset - newStart;
            if (streamOffset != null)
//                streamOffset += newStart;
                //记录行偏移
                streamOffset ++;
            return result;
        } else {
            return null;
        }
    }

    private String transform(String line){
        Iterator<String> rows = Arrays.asList(line.split(separator)).iterator();
        Iterator<String> titles = Arrays.asList(title.split(separator)).iterator();
        List<String[]> rowWithTitles = new ArrayList<>();
        while (rows.hasNext() && titles.hasNext()){
            String[] a = {rows.next(), titles.next()};
            rowWithTitles.add(a);
        }

        return line;
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
