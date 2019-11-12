package com.pharbers.kafka.connect.oss.reader;

import com.monitorjbl.xlsx.StreamingReader;
import com.pharbers.kafka.connect.oss.handler.OffsetHandler;
import com.pharbers.kafka.connect.oss.handler.TitleHandler;
import com.pharbers.kafka.connect.oss.model.CellData;
import com.pharbers.kafka.connect.oss.model.ExcelTitle;
import com.redis.S;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 功能描述
 *
 * @author dcs
 * @version 0.0
 * @tparam T 构造泛型参数
 * @note 一些值得注意的地方
 * @since 2019/10/29 13:21
 */
public class ExcelReader implements Reader {
    private static final Logger log = LoggerFactory.getLogger(ExcelReader.class);
    private Boolean isEnd = true;
    private Workbook reader = null;
    private String traceID;
    private String topic;
    private Map<Iterator<Row>, String> jobIDs = new HashMap<>();
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

    public ExcelReader(String topic, int batchSize){
        this.topic = topic;
        this.batchSize = batchSize;
    }

    @Override
    public List<SourceRecord> read() {
        if (jobIDs.keySet().size() == 0) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            isEnd = true;
            return new ArrayList<>();
        }
        Iterator<Row> rowsIterator = jobIDs.keySet().iterator().next();
        String jobID = jobIDs.get(rowsIterator);
        return readSheet(rowsIterator, jobID);
    }

    @Override
    public void init(InputStream stream, String traceID, Map<String, Object> streamOffset) {
        this.traceID = traceID;
        this.offsetHandler = new OffsetHandler(streamOffset);
        isEnd = false;
        reader = StreamingReader.builder().open(stream);
        log.info("*********************START!");
        Iterator<Sheet> sheets = reader.sheetIterator();
        int index = 0;
        titleHandler = new TitleHandler();
        while (sheets.hasNext()) {
            Sheet sheet = sheets.next();
            jobIDs.put(sheet.rowIterator(), traceID + index);
            //根据配置文件以第一行为title并且跳过，或者使用指定title不跳过
            if (sheet.rowIterator().hasNext()) {
                Row titleRow = sheet.rowIterator().next();
                List<String> row = new ArrayList<>();
                for (Cell c : titleRow) {
                    row.add(c.getStringCellValue());
                }
                titleHandler.addTitle(row.toArray(new String[0]), traceID, index);
            }
            long rowOffset = offsetHandler.get(traceID + index);
            while (sheet.rowIterator().hasNext() && rowOffset > 0) {
                sheet.rowIterator().next();
                rowOffset--;
            }
            index++;
        }
    }

    @Override
    public boolean isEnd() {
        return isEnd;
    }

    @Override
    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isEnd = true;
    }

    public String getTopic() {
        return topic;
    }

    public int getBatchSize() {
        return batchSize;
    }

    private ArrayList<SourceRecord> readSheet(Iterator<Row> rowsIterator, String jobID){
        ArrayList<SourceRecord> records = new ArrayList<>();
        do{
            synchronized (this) {
                if (!rowsIterator.hasNext()) {
                    if(offsetHandler.get(jobID) > 0) records.add(endBuilder(jobID));
                    jobIDs.remove(rowsIterator);
                    break;
                }
            }
            if (offsetHandler.get(jobID) == 0L) {
                records.add(new SourceRecord(offsetHandler.offsetKey(traceID), offsetHandler.offsetValueCoding(), topic, null,
                        KEY_SCHEMA, jobID, VALUE_SCHEMA, titleHandler.titleBuild(VALUE_SCHEMA, traceID, jobID), System.currentTimeMillis()));
            }
            records.add(readRow(rowsIterator.next(), jobID));
        }while (records.size() < batchSize);
        return records;
    }

    private SourceRecord readRow(Row r, String jobId){
        List<String> titleList = titleHandler.getTitleMap().get(jobId);
        Struct value = new Struct(VALUE_SCHEMA);
        value.put("jobId", jobId);
        value.put("traceId", traceID);
        value.put("type", "SandBox");
        List<Map.Entry<String, String>> rowValue = new ArrayList<>();
        for (int i = 0; i < titleList.size(); i++) {
            String v = r.getCell(i) == null ? "" : r.getCell(i).getStringCellValue();
            if (!"".equals(titleList.get(i))) {
                rowValue.add(new CellData(titleList.get(i), v));
            }
        }
        try {
            value.put("data", mapper.writeValueAsString(rowValue));
        } catch (IOException e) {
            log.error(jobId, e);
        }
        synchronized (this) {
            offsetHandler.add(jobId);
        }
        return new SourceRecord(offsetHandler.offsetKey(traceID), offsetHandler.offsetValueCoding(), topic, null,
                KEY_SCHEMA, jobId, VALUE_SCHEMA, value, System.currentTimeMillis());
    }

    private SourceRecord endBuilder(String jobID){
        log.info("读取完成");
        log.info(offsetHandler.toString());
        Struct headValue = new Struct(VALUE_SCHEMA);
        headValue.put("jobId", jobID);
        headValue.put("traceId", traceID);
        headValue.put("type", "SandBox-Length");
        headValue.put("data", "{\"length\": " + offsetHandler.get(jobID) + " }");
        return new SourceRecord(offsetHandler.offsetKey(traceID), offsetHandler.offsetValueCoding(), topic, null,
                KEY_SCHEMA, jobID, VALUE_SCHEMA, headValue, System.currentTimeMillis());

    }

}
