package com.pharbers.excel.format.input.reader.common;

import com.pharbers.excel.format.input.writable.phExcelWritable;
import com.pharbers.excel.format.input.writable.common.phXlsCommonWritable;
import org.apache.hadoop.io.NullWritable;

import java.io.IOException;

public class PhExcelXLSReader extends PhExcelXLSCommonReader<NullWritable, phExcelWritable> {
    @Override
    public NullWritable getCurrentKey() throws IOException, InterruptedException {
//        return new LongWritable(parser.currentIndex());
        return NullWritable.get();
    }

    @Override
    public phExcelWritable getCurrentValue() throws IOException, InterruptedException {
        phExcelWritable reVal = new phXlsCommonWritable();
        reVal.setValues(value);
        return reVal;
    }
}
