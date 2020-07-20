package com.pharbers.kafka.connect.oss.exception;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 功能描述
 *
 * @author dcs
 * @version 0.0
 * @tparam T 构造泛型参数
 * @note 一些值得注意的地方
 * @since 2019/12/18 10:45
 */
public class TitleLengthException extends Exception {
    private String[] errorLine;
    private int index;
    public TitleLengthException(String[] line){
        errorLine = line;
    }

    public TitleLengthException(Row r){
        Iterator<Cell> cellIterator =  r.cellIterator();
        List<String> line = new ArrayList<>();
        while (cellIterator.hasNext()){
            String value = cellIterator.next().getStringCellValue();
            if (null != value && !"".equals(value)){
                line.add(value);
            }
        }
        errorLine = line.toArray(new String[0]);
        index = r.getRowNum();
    }

    public String[] getErrorLine() {
        return errorLine;
    }

    public int getIndex() {
        return index;
    }
}
