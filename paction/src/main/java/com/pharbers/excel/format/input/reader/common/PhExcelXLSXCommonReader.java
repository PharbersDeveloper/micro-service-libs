package com.pharbers.excel.format.input.reader.common;

import java.util.List;
import java.io.IOException;
import scala.collection.JavaConversions;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import com.pharbers.pactions.excel.input.PhExcelXLSX;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

/**
 * Created by clock on 18-3-24.
 */
public abstract class PhExcelXLSXCommonReader<KEYIN, VALUEIN> extends RecordReader<KEYIN, VALUEIN> {
    protected FileSplit fileSplit;
    protected Configuration conf;
    protected String value = null;
    protected Boolean processed = false;

    protected PhExcelXLSX parser = null;
    protected String delimiter = String.valueOf((char)31);

    public int getSheetindex() {
        return sheetindex;
    }

    public void setSheetindex(int sheetindex) {
        this.sheetindex = sheetindex;
    }

    protected int sheetindex = 0;

    @Override
    public void initialize(InputSplit inputSplit,
                           TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {

        this.fileSplit = (FileSplit)inputSplit;
        this.conf = taskAttemptContext.getConfiguration();

        parser = PhExcelXLSX.apply(((FileSplit) inputSplit).getPath().toUri().getPath(), sheetindex);
    }

    @Override
    public boolean nextKeyValue() throws IOException, InterruptedException {

        try {
            value = "";
            List<String> tmp = JavaConversions.seqAsJavaList(parser.currentRowData());
            for (String iter : tmp) {
                value += iter + delimiter;
            }

            if (!value.isEmpty())
                value = value.substring(0, value.length() - 1);

            parser.nextRow();
            return true;

        } catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public float getProgress() throws IOException, InterruptedException {
        return 0;
    }

    @Override
    public void close() throws IOException {

    }
}
