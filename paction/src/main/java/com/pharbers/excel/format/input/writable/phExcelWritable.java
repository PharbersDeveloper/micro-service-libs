package com.pharbers.excel.format.input.writable;

import com.pharbers.excel.format.input.writable.writableStrategy.phExcelStrategy;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class phExcelWritable implements WritableComparable<phExcelWritable>, phExcelStrategy, java.io.Serializable {
    protected final String delimiter = String.valueOf((char)31);

    private String values = null; // new ArrayList<String>();

    @Override
    public String toString() {
        String result = values;
        return result;
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        values = in.readUTF();
    }

    @Override
    public void write(DataOutput out) throws IOException {
//        System.out.println(values);
        out.writeUTF(values);
    }

    @Override
    public int compareTo(phExcelWritable o) {
        return this.values.hashCode() - o.values.hashCode();
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }

    /** A Comparator that compares serialized . */
    public static class Comparator extends WritableComparator {
        public Comparator() {
            super(phExcelWritable.class);
        }

        public int compare(byte[] b1, int s1, int l1,
                           byte[] b2, int s2, int l2) {
            return compareBytes(b1, s1, l1, b2, s2, l2);
        }
    }

    static { // register this comparator
        WritableComparator.define(phExcelWritable.class, new Comparator());
    }
}
