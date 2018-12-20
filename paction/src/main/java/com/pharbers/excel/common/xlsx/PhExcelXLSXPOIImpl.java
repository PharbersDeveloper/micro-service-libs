package com.pharbers.excel.common.xlsx;

import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class PhExcelXLSXPOIImpl {

    private Workbook cwb = null;
    private String filePath = "";

    private Workbook openWorkbookInPath(String filePath) {
        InputStream ins = null;
        cwb = null;
        try {
            ins = new FileInputStream(filePath);
            cwb = StreamingReader.builder().rowCacheSize(100).bufferSize(4096).open(ins);
            this.filePath = filePath;
            return cwb;

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public Workbook queryWorkbook(String filePath) {
        if (!this.filePath.equals(filePath)) {
            openWorkbookInPath(filePath);
        }

        return cwb;
    }

    private Boolean isWorkBookOpened() {
        return cwb != null && !filePath.isEmpty();
    }

    public Integer sheetCount() {
        if (isWorkBookOpened()) {
            return cwb.getNumberOfSheets();

        } else return 0;
    }

    public Sheet querySheetInCurrentWorkbookByIndex(int index) {
        if (isWorkBookOpened()) {
            return cwb.getSheetAt(index);
        } else return null;
    }

    public Sheet querySheetInCurrentWorkbookByName(String name) {
        if (isWorkBookOpened()) {
            return cwb.getSheet(name);
        } else return null;
    }

    /**
     * 流读取不支持
     */
    public Integer querySheetStartRow(Sheet sheet) {
        return sheet.getFirstRowNum();
    }

    /**
     * 流读取不支持
     */
    public Integer querySheetLastRow(Sheet sheet) {
        return sheet.getLastRowNum();
    }

    public Integer queryRowIndex(Row r) {
        return r.getRowNum();
    }

    public Row queryRowByIndex(Sheet sheet, Integer index) {
        return sheet.getRow(index);
    }

    public Integer queryRowStartCol(Row r) {
        return Integer.valueOf(r.getFirstCellNum());
    }

    public Integer queryRowLastCol(Row r) {
        return Integer.valueOf(r.getLastCellNum());
    }

    public Cell queryCellByIndexInRow(Integer index, Row r) {
        return r.getCell(index);
    }
}
