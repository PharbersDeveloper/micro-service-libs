package com.pharbers.excel.format.input.writable.writableStrategy;

public interface phExcelStrategy {
    String richWithInputRow(int index, String value);
    String getRowKey(String flag);
}
