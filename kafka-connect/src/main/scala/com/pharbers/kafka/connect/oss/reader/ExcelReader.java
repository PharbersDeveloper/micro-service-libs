package com.pharbers.kafka.connect.oss.reader;

import com.monitorjbl.xlsx.StreamingReader;
import com.pharbers.kafka.connect.oss.handler.OffsetHandler;
import com.pharbers.kafka.connect.oss.handler.TitleHandler;
import com.pharbers.kafka.connect.oss.model.CellData;
import com.pharbers.kafka.connect.oss.model.Label;
import com.pharbers.kafka.schema.OssTask;
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
    private String traceId;
    private String topic;
    private OssTask task;
    private Map<Iterator<Row>, String> jobIds = new HashMap<>();
    private Map<String, String> SheetNames = new HashMap<>();
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
        if (jobIds.keySet().size() == 0) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            isEnd = true;
            return new ArrayList<>();
        }
        Iterator<Row> rowsIterator = jobIds.keySet().iterator().next();
        String jobID = jobIds.get(rowsIterator);
        return readSheet(rowsIterator, jobID);
    }

    @Override
    public void init(InputStream stream, OssTask task, Map<String, Object> streamOffset) throws Exception{
        this.traceId = task.getTraceId().toString();
        this.task = task;
        this.offsetHandler = new OffsetHandler(streamOffset);
        //todo: 从task获取title位置
        List<Integer> titleIndexList = task.getTitleIndex();
        isEnd = false;
        reader = StreamingReader.builder().open(stream);
        log.info("*********************START!");
        Iterator<Sheet> sheets = reader.sheetIterator();
        int index = 0;
        titleHandler = new TitleHandler();
        while (sheets.hasNext()) {
            Sheet sheet = sheets.next();
            int titleIndex = titleIndexList.size() > index ? titleIndexList.get(index) : 0;
            jobIds.put(sheet.rowIterator(), traceId + index);
            SheetNames.put(traceId + index, sheet.getSheetName());
            if (sheet.rowIterator().hasNext()) {
                while (sheet.rowIterator().hasNext() && titleIndex > 0) {
                    sheet.rowIterator().next();
                    titleIndex--;
                }
                if(!sheet.rowIterator().hasNext()){
                    throw new Exception("title index 指定错误");
                }
                Row titleRow = sheet.rowIterator().next();
                List<String> row = new ArrayList<>();
                for (Cell c : titleRow) {
                    row.add(c.getStringCellValue());
                }
                titleHandler.addTitle(row.toArray(new String[0]), traceId, index);
            }
            long rowOffset = offsetHandler.get(traceId + index);
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

    private ArrayList<SourceRecord> readSheet(Iterator<Row> rowsIterator, String jobId){
        ArrayList<SourceRecord> records = new ArrayList<>();
        do{
            synchronized (this) {
                if (!rowsIterator.hasNext()) {
                    if(offsetHandler.get(jobId) > 0) {
                        records.add(endBuilder(jobId));
                    }
                    jobIds.remove(rowsIterator);
                    break;
                }
            }
            if (offsetHandler.get(jobId) == 0L) {
                records.add(new SourceRecord(offsetHandler.offsetKey(traceId), offsetHandler.offsetValueCoding(), topic, null,
                        KEY_SCHEMA, jobId, VALUE_SCHEMA, titleHandler.titleBuild(VALUE_SCHEMA, traceId, jobId), System.currentTimeMillis()));
                records.add(new SourceRecord(offsetHandler.offsetKey(traceId), offsetHandler.offsetValueCoding(), topic, null,
                        KEY_SCHEMA, jobId, VALUE_SCHEMA, buildValue(traceId, jobId, "SandBox-Labels", new Label(task, SheetNames.get(jobId))), System.currentTimeMillis()));
            }
            records.add(readRow(rowsIterator.next(), jobId));
        }while (records.size() < batchSize);
        return records;
    }

    private SourceRecord readRow(Row r, String jobId){
        List<String> titleList = titleHandler.getTitleMap().get(jobId);

        List<Map.Entry<String, String>> rowValue = new ArrayList<>();
        for (int i = 0; i < titleList.size(); i++) {
            String v = r.getCell(i) == null ? "" : r.getCell(i).getStringCellValue();
            if (!"".equals(titleList.get(i))) {
                rowValue.add(new CellData(titleList.get(i), v));
            }
        }
        Struct value = buildValue(traceId, jobId, "SandBox", rowValue);
        synchronized (this) {
            offsetHandler.add(jobId);
        }
        return new SourceRecord(offsetHandler.offsetKey(traceId), offsetHandler.offsetValueCoding(), topic, null,
                KEY_SCHEMA, jobId, VALUE_SCHEMA, value, System.currentTimeMillis());
    }

    private SourceRecord endBuilder(String jobId){
        log.info("读取完成");
        log.info(offsetHandler.toString());
        Struct headValue = new Struct(VALUE_SCHEMA);
        headValue.put("jobId", jobId);
        headValue.put("traceId", traceId);
        headValue.put("type", "SandBox-Length");
        headValue.put("data", "{\"length\": " + offsetHandler.get(jobId) + " }");
        return new SourceRecord(offsetHandler.offsetKey(traceId), offsetHandler.offsetValueCoding(), topic, null,
                KEY_SCHEMA, jobId, VALUE_SCHEMA, headValue, System.currentTimeMillis());

    }

    private Struct buildValue(String traceId, String jobId, String type, Object data){
        Struct value = new Struct(VALUE_SCHEMA);
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
}
