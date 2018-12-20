package com.pharbers.excel.format.input.reader.common;

import com.pharbers.excel.format.input.writable.phExcelWritable;
import com.pharbers.excel.format.input.writable.common.phXlsxCommonWritable;
import org.apache.hadoop.io.NullWritable;

import java.io.IOException;

public class PhExcelXLSXReader extends PhExcelXLSXCommonReader<NullWritable, phExcelWritable> {
    @Override
    public NullWritable getCurrentKey() throws IOException, InterruptedException {
        return NullWritable.get();
    }

    @Override
    public phExcelWritable getCurrentValue() throws IOException, InterruptedException {
        phExcelWritable reVal = new phXlsxCommonWritable();
        reVal.setValues(value);
        return reVal;
    }
}
