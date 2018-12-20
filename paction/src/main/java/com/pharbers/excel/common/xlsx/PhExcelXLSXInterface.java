package com.pharbers.excel.common.xlsx;

import javafx.util.Pair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Iterator;

public abstract class PhExcelXLSXInterface {
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

        public datarow queryFirstRow() {
            this.cur_iter = this.value.iterator();
            return queryNextRow();
        }

        public datarow queryNextRow() {
            Row tmp = null;
            if (cur_iter.hasNext())
                tmp = cur_iter.next();

            if (tmp != null)
                return new datarow(this, tmp);
            else return null;
        }

        public Boolean hasNexRow() {
            return cur_iter.hasNext();
        }

        private workbook prev;
        private Sheet value;
        private Iterator<Row> cur_iter;
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

        DecimalFormat decimalFormat = new DecimalFormat("###################.################");
        public String queryCellString() throws Exception {

            if (value == null) {
                return "";
            } else if (value.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                return decimalFormat.format(new BigDecimal(value.getNumericCellValue()));
            } else if (value.getCellType() == Cell.CELL_TYPE_STRING) {
                String temp = value.getStringCellValue();
                try{
                    return new BigDecimal(temp).toString();
                }catch(NumberFormatException e){
                    return temp;
                }
            } else if (value.getCellType() == Cell.CELL_TYPE_BLANK
                    || value.getCellType() == Cell.CELL_TYPE_FORMULA
                    || value.getCellType() == Cell.CELL_TYPE_ERROR) {
                return "";
            } else {
                throw new Exception("not implement : " + value.getCellType());
            }
        }

        public datarow queryWorkRow() {
            return this.prev;
        }
    }

    PhExcelXLSXPOIImpl reader = new PhExcelXLSXPOIImpl();

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
