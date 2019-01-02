package com.pharbers.excel.format.input.writable.common;

import com.pharbers.excel.format.input.writable.phExcelWritable;

public class phXlsCommonWritable extends phExcelWritable {

    @Override
    public String richWithInputRow(int index, String value) {
        return value;
    }

    @Override
    public String getRowKey(String flag) {
        return this.getValues();
    }
}
