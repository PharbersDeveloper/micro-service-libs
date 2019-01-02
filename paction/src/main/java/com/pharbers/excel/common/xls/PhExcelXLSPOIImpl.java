package com.pharbers.excel.common.xls;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.*;

// 暂时不用
public class PhExcelXLSPOIImpl {

    private Workbook cwb = null;
    private String filePath = "";

    private Workbook openWorkbookInPath(String filePath) {
        InputStream ins = null;
        cwb = null;
        try {
            ins = new FileInputStream(new File(filePath));
            cwb = WorkbookFactory.create(ins);
            this.filePath = filePath;
            return cwb;

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
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

    public Integer querySheetStartRow(Sheet sheet) {
        return sheet.getFirstRowNum();
    }

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
