package com.pharbers.kafka.connect.oss.readerV2;

import com.monitorjbl.xlsx.StreamingReader;
import com.pharbers.kafka.connect.oss.concurrent.RowData;
import com.pharbers.kafka.connect.oss.exception.TaskConfigException;
import com.pharbers.kafka.schema.OssTask;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

/**
 * 功能描述
 *
 * @author dcs
 * @version 0.0
 * @tparam T 构造泛型参数
 * @note 一些值得注意的地方
 * @since 2019/12/25 16:23
 */
public class ExcelReaderV2 implements ReaderV2 {
    protected Workbook reader;
    protected String jobIdPrefix;
    protected Map<String, Object> metaDate = new HashMap<>();
    protected OssTask task;
    //todo: 和cvsReader统一

    protected final int TITLE_MAX_INDEX = 100;
    protected final String TITLE_TYPE = "SandBox-Schema";
    protected final String LABELS_TYPE = "SandBox-Labels";
    protected final String DATA_TYPE = "SandBox";
    protected final String LENGTH_TYPE = "SandBox-Length";

    public ExcelReaderV2(String jobIdPrefix, OssTask task) {
        this.jobIdPrefix = jobIdPrefix;
        this.task = task;
        metaDate.put("task", task);
    }

    @Override
    public void read(BlockingQueue<RowData> seq) throws Exception {
//        Iterator<Sheet> sheets = reader.sheetIterator();
//        int sheetIndex = 0;
        String sheetName = task.getSheetName().toString();
        Sheet sheet = reader.getSheet(sheetName);
        if (sheet == null) {
            reader.close();
            throw new TaskConfigException("can not found sheet:" + sheetName);
        }


//            Sheet sheet = sheets.next();
//            String jobId = jobIdPrefix + sheetIndex;
        String jobId = jobIdPrefix;
        metaDate.put(jobId, sheet.getSheetName());
        Iterator<Row> rows = sheet.rowIterator();
        List<Row> cacheList = new ArrayList<>();
        for (int i = 0; i < TITLE_MAX_INDEX; i++) {
            if (rows.hasNext()) {
                cacheList.add(rows.next());
            } else {
                break;
            }
        }
        List<Row> titleBeginList;
        titleBeginList = getBeginList(cacheList, seq, jobId);
        seq.put(new RowData(LABELS_TYPE, new String[]{sheet.getSheetName()}, metaDate, jobId, task.getTraceId().toString()));
        Long cacheLength = putRow(seq, titleBeginList.iterator(), jobId, 0);
        Long length = putRow(seq, rows, jobId, cacheLength);
        seq.put(new RowData(LENGTH_TYPE, new String[]{length.toString()}, metaDate, jobId, task.getTraceId().toString()));
        close();
    }

    @Override
    public void init(InputStream stream, String format) {
        reader = StreamingReader.builder().open(stream);
    }

    @Override
    public boolean isEnd() {
        return false;
    }

    @Override
    public void close() {
        try {
            if (null != reader) {
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected List<Row> getBeginList(List<Row> cacheList, BlockingQueue<RowData> seq, String jobId) throws Exception {
        List<String> titleValues = new ArrayList<>();
        int titleIndex = 0;
        for (int i = 0; i < cacheList.size(); i++) {
            List<String> values = new ArrayList<>();
            cacheList.get(i).cellIterator().forEachRemaining(cell -> {
                String value = cell.getStringCellValue();
                if (!"".equals(value)) {
                    values.add(value);
                } else {
                    values.add("null");
                }
            });
            for(int j = values.size() - 1; j >= 0; j --){
                if("null".equals(values.get(j))){
                    values.remove(j);
                } else {
                    break;
                }
            }
            if (values.size() > titleValues.size()) {
                titleValues = values;
                titleIndex = i;
            }
        }
        String regex = "[ ,;{}()\\n\\t=]";
        titleValues = titleValues.stream().map(x -> x.replaceAll(regex, "_")).collect(Collectors.toList());
        String[] schema = titleValues.toArray(new String[0]);
        seq.put(new RowData(TITLE_TYPE, schema, metaDate, jobId, task.getTraceId().toString()));
        return cacheList.subList(titleIndex + 1, cacheList.size());
    }


    private Long putRow(BlockingQueue<RowData> seq, Iterator<Row> rows, String jobId, long beginLength) throws InterruptedException {
        long length = beginLength;
        while (rows.hasNext()) {
            Row row = rows.next();
            List<String> cellValues = new ArrayList<>();
            for(int i = 0; i < row.getLastCellNum(); i++){
                Cell cell = row.getCell(i);
                String value;
                if(cell == null){
                    value = "";
                } else {
                    if ("NUMERIC".equals(cell.getCellType().name())) {
                        value = Double.toString(cell.getNumericCellValue());
                    } else {
                        value = cell.getStringCellValue();
                    }
                }
                cellValues.add(value);
            }
            //row.cellIterator()会跳过空cell
//            row.cellIterator().forEachRemaining(x -> {
//                String value;
//                if ("NUMERIC".equals(x.getCellType().name())) {
//                    value = Double.toString(x.getNumericCellValue());
//                } else {
//                    value = x.getStringCellValue();
//                }
//                cellValues.add(value);
//
//            });
            if (cellValues.stream().allMatch(""::equals)) {
                continue;
            }
            seq.put(new RowData(DATA_TYPE, cellValues.toArray(new String[0]), metaDate, jobId, task.getTraceId().toString()));
            length++;
        }
        return length;
    }

}
