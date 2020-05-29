package com.pharbers.kafka.connect.oss.readerV2;

import com.pharbers.kafka.connect.oss.concurrent.RowData;
import com.pharbers.kafka.schema.OssTask;
import com.redis.S;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * 功能描述
 *
 * @author dcs
 * @version 0.0
 * @tparam T 构造泛型参数
 * @note 一些值得注意的地方
 * @since 2020/05/29 15:07
 */
public class ExcelReaderForMaxDeliveryData extends ExcelReaderV2 {
    public ExcelReaderForMaxDeliveryData(String jobIdPrefix, OssTask task) {
        super(jobIdPrefix, task);
    }

    @Override
    protected List<Row> getBeginList(List<Row> cacheList, BlockingQueue<RowData> seq, String jobId) throws Exception {
        List<String> unitTitle = new ArrayList<>();
        List<String> categoryTitle = new ArrayList<>();
        List<String> enTitle = new ArrayList<>();
        List<String> cnTitle = new ArrayList<>();
        List<List<String>> titles = new ArrayList<>();
        titles.add(unitTitle);
        titles.add(categoryTitle);
        titles.add(enTitle);
        titles.add(cnTitle);
        for (int i = 0; i < titles.size(); i++){
            Iterator<Cell> cells = cacheList.get(i).cellIterator();
            int cellIndex = 0;
            while (cells.hasNext()){
                Cell cell = cells.next();
                String value = cell.getStringCellValue();
                if("".equals(value) && cellIndex != 0 && i < 2) {
                    value = titles.get(i).get(cellIndex - 1);
                }
                titles.get(i).add(value);
                cellIndex ++;
            }
        }
        if(unitTitle.size() < enTitle.size() || categoryTitle.size() < enTitle.size() || cnTitle.size() < enTitle.size()){
            throw new Exception("title format error");
        }
        String[] schema = new String[enTitle.size()];

        char separator = (char)31;
        for(int i = 0; i < enTitle.size(); i ++){
            StringBuilder values = new StringBuilder();
            boolean first = true;
            for(List<String> title: titles){
                String value = title.get(i);
                if(!"".equals(value)){
                    if(first){
                        values.append(value);
                        first = false;
                    } else {
                        values.append(separator);
                        values.append(value);
                    }
                }
            }
            schema[i] = values.toString();
        }

        seq.put(new RowData(TITLE_TYPE, schema, metaDate, jobId, task.getTraceId().toString()));
        return cacheList.subList(titles.size(), cacheList.size());
    }
}
