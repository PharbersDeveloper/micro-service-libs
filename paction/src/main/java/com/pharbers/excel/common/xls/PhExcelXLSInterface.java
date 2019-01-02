package com.pharbers.excel.common.xls;

import javafx.util.Pair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.math.BigDecimal;
import java.text.DecimalFormat;

// 暂时不用
public abstract class PhExcelXLSInterface {
    public static final class workbook {
        workbook(Workbook b) {
            this.value = b;
        }

        private Workbook value;
    }

    public static final class worksheet {
        worksheet(workbook b, Sheet s) {
            this.prev = b;
            this.value = s;
        }

        private workbook prev;
        private Sheet value;
    }

    public static final class datarow {
        datarow(worksheet s, Row r ) {
            this.prev = s;
            this.value = r;
        }

        private worksheet prev;
        private Row value;

        public worksheet queryWorksheet() {
            return this.prev;
        }

        public Integer queryCurrentRowIndex() {
            return this.value.getRowNum();
        }
    }

    public static final class datacell {
        datacell(datarow r, Cell c) {
            this.prev = r;
            this.value = c;
        }

        private datarow prev;
        private Cell value;

        public Pair<Integer, Integer> queryCellPosition() {
            return new Pair(prev.value.getRowNum(), value.getColumnIndex());
        }

        DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
        public String queryCellString() throws Exception {
            if (value.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                return String.valueOf(decimalFormat.format(value.getNumericCellValue()));
            } else if (value.getCellType() == Cell.CELL_TYPE_STRING) {
                String temp = value.getStringCellValue();
                try{
                    return new BigDecimal(temp).toString();
                }catch(NumberFormatException e){
                    return temp;
                }
            } else {
                throw new Exception("not implement");
            }
        }

        public datarow queryWorkRow() {
            return this.prev;
        }
    }

    PhExcelXLSPOIImpl reader = new PhExcelXLSPOIImpl();

    protected workbook openExcelFile(String filePath) {
        return new workbook(reader.queryWorkbook(filePath));
    }

    protected worksheet openExcelSheet(String sheetName, workbook book) {
        return new worksheet(book, reader.querySheetInCurrentWorkbookByName(sheetName));
    }

    protected worksheet openExcelSheet(Integer sheetIndex, workbook book) {
        return new worksheet(book, reader.querySheetInCurrentWorkbookByIndex(sheetIndex));
    }

    protected Pair<Integer, Integer> rowsInSheet(worksheet s) {
        return new Pair(reader.querySheetStartRow(s.value), reader.querySheetLastRow(s.value));
    }

    protected datarow queryRowByIndex(worksheet s, Integer index) {
        return new datarow(s, reader.queryRowByIndex(s.value, index));
    }

    protected Pair<Integer, Integer> colsInRow(datarow r) {
        return new Pair(reader.queryRowStartCol(r.value), reader.queryRowLastCol(r.value));
    }

    protected datacell queryCellByIndex(datarow r, Integer index) {
        return new datacell(r, reader.queryCellByIndexInRow(index, r.value));
    }
}
