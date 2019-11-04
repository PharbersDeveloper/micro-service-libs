package com.pharbers.kafka.connect.oss.reader;

import com.pharbers.kafka.connect.oss.handler.OffsetHandler;
import com.pharbers.kafka.connect.oss.handler.TitleHandler;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 功能描述
 *
 * @author dcs
 * @version 0.0
 * @tparam T 构造泛型参数
 * @note 一些值得注意的地方
 * @since 2019/10/28 18:33
 */
public class CsvReader implements Reader {
    private static final Logger log = LoggerFactory.getLogger(CsvReader.class);
    private Boolean isEnd = true;
    private BufferedReader bufferedReader = null;
    private String traceID;
    private String jobID;
    private String topic;
    private TitleHandler titleHandler = null;
    private OffsetHandler offsetHandler = null;
    private final SchemaBuilder VALUE_SCHEMA_BUILDER = SchemaBuilder.struct()
            .field("jobId", Schema.STRING_SCHEMA)
            .field("traceId", Schema.STRING_SCHEMA)
            .field("type", Schema.STRING_SCHEMA)
            .field("data", Schema.STRING_SCHEMA);
    private final ObjectMapper mapper = new ObjectMapper();
    private final Schema VALUE_SCHEMA = VALUE_SCHEMA_BUILDER.build();
    private final Schema KEY_SCHEMA = Schema.STRING_SCHEMA;
    private int batchSize;

    public CsvReader(String topic, int batchSize) {
        this.topic = topic;
        this.batchSize = batchSize;
    }

    @Override
    public List<SourceRecord> read() {
        ArrayList<SourceRecord> records = new ArrayList<>();
        do {
            String row = null;
            try {
                row = bufferedReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            synchronized (this) {
                if (row == null) {
                    endHandler(records);
                    break;
                }
            }
            if (offsetHandler.get(jobID) == 0L) {
                records.add(new SourceRecord(offsetHandler.offsetKey(jobID), offsetHandler.offsetValueCoding(), topic, null,
                        KEY_SCHEMA, jobID, VALUE_SCHEMA, titleHandler.titleBuild(VALUE_SCHEMA, traceID, jobID), System.currentTimeMillis()));
            }
            synchronized (this) {
                offsetHandler.add(jobID);
            }
            records.add(readLine(row));
        } while (records.size() < batchSize);
        return null;
    }

    @Override
    public void init(InputStream stream, String traceID, Map<String, Object> streamOffset) {
        this.traceID = traceID;
        this.offsetHandler = new OffsetHandler(streamOffset);
        log.info("offSet: " + offsetHandler.toString());
        jobID = traceID + 0;
        isEnd = false;
        int buffSize = 2048;
        bufferedReader = new BufferedReader(new InputStreamReader(stream, Charset.forName("UTF-8")), buffSize);
        log.info("*********************START!");
        try {
            titleHandler = new TitleHandler(bufferedReader.readLine().split(","), traceID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long rowOffset = offsetHandler.get(jobID);
        while (rowOffset > 0) {
            try {
                bufferedReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            rowOffset--;
        }
    }

    @Override
    public boolean isEnd() {
        return isEnd;
    }

    @Override
    public void close() {
        try {
            bufferedReader.close();
        } catch (IOException e) {
            log.error(jobID, e);
        }
        isEnd = true;
    }

    public String getTopic() {
        return topic;
    }

    public int getBatchSize() {
        return batchSize;
    }

    private SourceRecord endBuild() {
        Struct headValue = new Struct(VALUE_SCHEMA);
        headValue.put("jobId", jobID);
        headValue.put("traceId", traceID);
        headValue.put("type", "SandBox-Length");
        headValue.put("data", "{\"length\": " + (offsetHandler.get(jobID)) + " }");
        return new SourceRecord(offsetHandler.offsetKey(jobID), offsetHandler.offsetValueCoding(), topic, null,
                KEY_SCHEMA, jobID, VALUE_SCHEMA, headValue, System.currentTimeMillis());
    }

    private void endHandler(ArrayList<SourceRecord> records) {
        log.info("读取完成");
        log.info(offsetHandler.toString());
        records.add(endBuild());
        try {
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isEnd = true;
    }

    private SourceRecord readLine(String row) {
        List<String> titleList = titleHandler.getTitleMap().get(jobID);
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
        try {
            value.put("data", mapper.writeValueAsString(rowValue));
        } catch (IOException e) {
            e.printStackTrace();
        }
        synchronized (this) {
            offsetHandler.add(jobID);
        }
        return new SourceRecord(offsetHandler.offsetKey(jobID), offsetHandler.offsetValueCoding(), topic, null,
                KEY_SCHEMA, jobID, VALUE_SCHEMA, value, System.currentTimeMillis());
    }
}
