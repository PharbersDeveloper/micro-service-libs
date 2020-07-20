package com.pharbers.kafka.connect.oss.reader;

import com.pharbers.kafka.connect.oss.exception.TitleLengthException;
import com.pharbers.kafka.connect.oss.handler.OffsetHandler;
import com.pharbers.kafka.connect.oss.handler.SourceRecordHandler;
import com.pharbers.kafka.connect.oss.handler.TitleHandler;
import com.pharbers.kafka.connect.oss.model.CellData;
import com.pharbers.kafka.connect.oss.model.Label;
import com.pharbers.kafka.schema.OssTask;
import com.redis.S;
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
@Deprecated
public class CsvReader implements Reader {
    private static final Logger log = LoggerFactory.getLogger(CsvReader.class);
    private Boolean isEnd = true;
    private BufferedReader bufferedReader = null;
    private String traceId;
    private String jobId;
    private String topic;
    private Label label;
    private TitleHandler titleHandler = null;
    private OffsetHandler offsetHandler = null;
    private int batchSize;
    private final ObjectMapper mapper = new ObjectMapper();
    private SourceRecordHandler sourceRecordHandler;
    private int register = 0;
    public CsvReader(String topic, int batchSize) {
        this.topic = topic;
        this.batchSize = batchSize;
    }

    @Override
    public List<SourceRecord> read() throws TitleLengthException {
        ArrayList<SourceRecord> records = new ArrayList<>();
        synchronized (this){
            register ++;
        }
        do {
            String row = null;
            try {
                row = bufferedReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            synchronized (this) {
                if (row == null) {
                    if(register == 1) {
                        endHandler(records);
                    }
                    break;
                }
            }
            if (offsetHandler.get(jobId) == 0L) {
                records.add(sourceRecordHandler
                        .builder(null, titleHandler.titleBuild(sourceRecordHandler.getValueSchema(), traceId, jobId), offsetHandler.offsetValueCoding()));
                records.add(sourceRecordHandler
                        .builder(null, buildValue(traceId, jobId, "SandBox-Labels", label), offsetHandler.offsetValueCoding()));
            }
            records.add(readLine(row));
            synchronized (this) {
                offsetHandler.add(jobId);
            }
        } while (records.size() < batchSize);
        synchronized (this){
            register --;
        }
        return records;
    }

    @Override
    public void init(InputStream stream, OssTask task, Map<String, Object> streamOffset) throws Exception {
        this.traceId = task.getTraceId().toString();
        label = new Label(task, task.getFileName().toString() + "_0");
        this.offsetHandler = new OffsetHandler(streamOffset);
        log.info("offSet: " + offsetHandler.toString());
        Schema valueSchema = SchemaBuilder.struct()
                .field("jobId", Schema.STRING_SCHEMA)
                .field("traceId", Schema.STRING_SCHEMA)
                .field("type", Schema.STRING_SCHEMA)
                .field("data", Schema.STRING_SCHEMA).build();
        Schema keySchema = SchemaBuilder.string().optional().build();
        sourceRecordHandler = new SourceRecordHandler(OffsetHandler.offsetKey(task.getJobId().toString()), topic, keySchema, valueSchema);
        jobId = UUID.randomUUID().toString();
        //todo: 从task获取title位置
//        int titleIndex = 0;
        isEnd = false;
        int buffSize = 2048;
        bufferedReader = new BufferedReader(new InputStreamReader(stream, Charset.forName("UTF-8")), buffSize);
        log.info("*********************START!");
//        while (titleIndex > 0) {
//            try {
//                bufferedReader.readLine();
//            } catch (IOException e) {
//                throw new Exception("title index 指定错误", e);
//            }
//            titleIndex--;
//        }
        try {
            titleHandler = new TitleHandler(bufferedReader.readLine().split(","), jobId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long rowOffset = offsetHandler.get(jobId);
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
           if(bufferedReader != null) {
               bufferedReader.close();
           }
        } catch (IOException e) {
            log.error(jobId, e);
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
        Struct headValue = new Struct(sourceRecordHandler.getValueSchema());
        headValue.put("jobId", jobId);
        headValue.put("traceId", traceId);
        headValue.put("type", "SandBox-Length");
        headValue.put("data", "{\"length\": " + (offsetHandler.get(jobId)) + " }");
        return sourceRecordHandler.builder(null, headValue, offsetHandler.offsetValueCoding());
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

    private SourceRecord readLine(String row) throws TitleLengthException {
        List<String> titleList = titleHandler.getTitleMap().get(jobId);
        String[] r = row.split(",");
        if (r.length > titleList.size()){
            String value = r[titleList.size()];
            if (value != null && !"".equals(value)){
                throw new TitleLengthException(r);
            }
        }
        Map<String, String> rowValue = new HashMap<>(10);
        for (int i = 0; i < titleList.size(); i++) {
            String v = i >= r.length ? "" : r[i];
            rowValue.put(titleList.get(i), v);
        }
        Struct value = buildValue(traceId, jobId, "SandBox", rowValue);
        return sourceRecordHandler.builder(null, value, offsetHandler.offsetValueCoding());
    }

    private Struct buildValue(String traceId, String jobId, String type, Object data){
        Struct value = new Struct(sourceRecordHandler.getValueSchema());
        value.put("jobId", jobId);
        value.put("traceId", traceId);
        value.put("type", type);
        try {
            value.put("data", mapper.writeValueAsString(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    private void resetSheet(TitleLengthException e){
        //todo：还是需要重新创建迭代器，并且跳过现有的
        String newJobId = UUID.randomUUID().toString();
        titleHandler.resetTitle(e.getErrorLine(), newJobId, jobId);
        jobId = newJobId;
    }
}
