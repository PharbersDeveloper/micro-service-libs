package com.pharbers.kafka.connect.oss.reader;

import com.monitorjbl.xlsx.StreamingReader;
import com.pharbers.kafka.connect.oss.exception.EmptyRowException;
import com.pharbers.kafka.connect.oss.exception.TitleLengthException;
import com.pharbers.kafka.connect.oss.handler.OffsetHandler;
import com.pharbers.kafka.connect.oss.handler.SourceRecordHandler;
import com.pharbers.kafka.connect.oss.handler.TitleHandler;
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
@Deprecated
public class ExcelReader implements Reader {
    private static final Logger log = LoggerFactory.getLogger(ExcelReader.class);
    private Boolean isEnd = true;
    private Workbook reader = null;
    private String traceId;
    private String topic;
    private OssTask task;
    private Map<Iterator<Row>, String> jobIds = new HashMap<>();
    private Map<String, String> sheetNames = new HashMap<>();
    private Map<String, Integer> registers = new HashMap<>();
    private TitleHandler titleHandler = null;
    private OffsetHandler offsetHandler = null;
    private final ObjectMapper mapper = new ObjectMapper();
    private SourceRecordHandler sourceRecordHandler;
    private int batchSize;
    private boolean reset = false;

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
        String jobId = jobIds.get(rowsIterator);

        try {
            return readSheet(rowsIterator, jobId);
        } catch (TitleLengthException e) {
            log.error("title 错误， 创建新title");
            resetSheet(rowsIterator, e);
            return new ArrayList<>();
        }
    }

    @Override
    public void init(InputStream stream, OssTask task, Map<String, Object> streamOffset) throws Exception{
        this.traceId = task.getTraceId().toString();
        this.task = task;
        this.offsetHandler = new OffsetHandler(streamOffset);
        //todo: 从task获取title位置
//        List<Integer> titleIndexList = task.getTitleIndex();
        isEnd = false;
        reader = StreamingReader.builder().open(stream);
        Schema valueSchema = SchemaBuilder.struct()
                .field("jobId", Schema.STRING_SCHEMA)
                .field("traceId", Schema.STRING_SCHEMA)
                .field("type", Schema.STRING_SCHEMA)
                .field("data", Schema.STRING_SCHEMA).build();
        Schema keySchema = SchemaBuilder.string().optional().build();
        sourceRecordHandler = new SourceRecordHandler(OffsetHandler.offsetKey(task.getJobId().toString()), topic, keySchema, valueSchema);
        log.info("*********************START!");
        Iterator<Sheet> sheets = reader.sheetIterator();
        int index = 0;
        titleHandler = new TitleHandler();
        while (sheets.hasNext()) {
            Sheet sheet = sheets.next();
//            int titleIndex = 0;
            String jobId = UUID.randomUUID().toString() + index;
            jobIds.put(sheet.rowIterator(), jobId);
            sheetNames.put(jobId, sheet.getSheetName());
            if (sheet.rowIterator().hasNext()) {
//                while (sheet.rowIterator().hasNext() && titleIndex > 0) {
//                    sheet.rowIterator().next();
//                    titleIndex--;
//                }
                if(!sheet.rowIterator().hasNext()){
                    throw new Exception("title index 指定错误");
                }
                Row titleRow = sheet.rowIterator().next();
                List<String> row = new ArrayList<>();
                for (Cell c : titleRow) {
                    row.add(c.getStringCellValue());
                }
                titleHandler.addTitle(row.toArray(new String[0]), jobId);
            }
            //todo：这儿每次jobId都是新生成的，根本就取不到offset
            long rowOffset = offsetHandler.get(jobId);
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
            if (reader != null) {
                reader.close();
            }
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

    private ArrayList<SourceRecord> readSheet(Iterator<Row> rowsIterator, String jobId) throws TitleLengthException {
        registers.put(jobId, registers.getOrDefault(jobId, 0) + 1);
        ArrayList<SourceRecord> records = new ArrayList<>();
        do{
            Row r;
            synchronized (this) {
                if (!rowsIterator.hasNext()) {
                    //这儿发送end length，但是可能到这一步的时候，其他还有线程需要修改length
                    if(registers.getOrDefault(jobId, -1) != 1){
                        break;
                    }
                    if(offsetHandler.get(jobId) > 0) {
                        records.add(endBuilder(jobId));
                    }
                    jobIds.remove(rowsIterator);
                    break;
                } else {
                    r = rowsIterator.next();
                }
            }
            if (offsetHandler.get(jobId) == 0L) {
                Struct title = titleHandler.titleBuild(sourceRecordHandler.getValueSchema(), traceId, jobId);
                records.add(sourceRecordHandler.builder(null, title, offsetHandler.offsetValueCoding()));
                Struct labels = buildValue(traceId, jobId, "SandBox-Labels", new Label(task, sheetNames.get(jobId)));
                records.add(sourceRecordHandler.builder(null, labels,
                        offsetHandler.offsetValueCoding()));
            }
            try {
                records.add(readRow(r, jobId));
            } catch (EmptyRowException e) {
                log.debug("empty row");
            }
        }while (records.size() < batchSize);
        registers.put(jobId, registers.get(jobId) - 1);
        return records;
    }

    private SourceRecord readRow(Row r, String jobId) throws TitleLengthException, EmptyRowException {
        List<String> titleList = titleHandler.getTitleMap().get(jobId);
        if (r.getLastCellNum() > titleList.size()){
            String value = null;
            try{
                value = r.getCell(titleList.size()).getStringCellValue();
            } catch (Exception e){
                log.warn(e.getMessage(), e);
            }
            if (value != null && !"".equals(value)){
                //todo: 这个错误不应该多线程抛出
                throw new TitleLengthException(r);
            }
        }
        Map<String, String> rowValue = new HashMap<>(10);
        Set<String> valueSet = new HashSet<>();
        for (int i = 0; i < titleList.size(); i++) {
            String v = r.getCell(i) == null ? "" : r.getCell(i).getStringCellValue();
            if (!"".equals(titleList.get(i))) {
                rowValue.put(titleList.get(i), v);
                valueSet.add(v);
            }
        }
        if((valueSet.size() == 1) && ("".equals(valueSet.iterator().next()))){
            throw new EmptyRowException();
        }
        Struct value = buildValue(traceId, jobId, "SandBox", rowValue);
        synchronized (this) {
            offsetHandler.add(jobId);
        }
        return sourceRecordHandler.builder(null, value, offsetHandler.offsetValueCoding());
    }

    private SourceRecord endBuilder(String jobId){
        log.info("读取完成");
        log.info(offsetHandler.toString());
        Struct headValue = new Struct(sourceRecordHandler.getValueSchema());
        headValue.put("jobId", jobId);
        headValue.put("traceId", traceId);
        headValue.put("type", "SandBox-Length");
        headValue.put("data", "{\"length\": " + offsetHandler.get(jobId) + " }");
        return sourceRecordHandler.builder(null, headValue, offsetHandler.offsetValueCoding());
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

    private void resetSheet(Iterator<Row> rowsIterator, TitleLengthException e){
        if(reset || !jobIds.containsKey(rowsIterator)){
            return;
        }
        reset = true;
        String oldJobId = jobIds.get(rowsIterator);
        String newJobId = UUID.randomUUID().toString();
        Iterator<Row> newRowsIterator =  reader.getSheet(sheetNames.get(oldJobId)).rowIterator();
        for (int i = 0; i <= e.getIndex(); i++){
            //零时方案
            synchronized (this){
                if(newRowsIterator.hasNext()){
                    //todo： 这儿可能会有java.util.ConcurrentModificationException，其他线程在hasNext时可能会修改集合
                    newRowsIterator.next();
                }
            }

        }
        registers.put(oldJobId, 10000);
        titleHandler.resetTitle(e.getErrorLine(), newJobId, oldJobId);
        jobIds.put(newRowsIterator, newJobId);
        jobIds.remove(rowsIterator);
        sheetNames.put(newJobId, sheetNames.get(oldJobId));
        reset = false;
    }
}
