package com.pharbers.excel.format.input.writable.common;

import com.pharbers.excel.format.input.writable.phExcelWritable;

public class phXlsxCommonWritable extends phExcelWritable {
    private int titleSize = 0;

    @Override
    public String richWithInputRow(int index, String value) {
        if (index == 1) {
            titleSize = value.split(delimiter).length;
            return value;
        } else
            return fullTail(titleSize, value);
    }

    @Override
    public String getRowKey(String flag) {
        return this.getValues();
    }

    private String fullTail(int ts, String value) {

        String[] rLst = value.split(delimiter);
        int rl = rLst.length;

        if(ts > rl){
            String[] resultLst = new String[ts];

            System.arraycopy(rLst, 0, resultLst, 0, rl);

            for (int i = 0; i < ts - rl; i++) {
                resultLst[rl + i] = " ";
            }
            return mkString(resultLst, delimiter);
        }else{
            return value;
        }
    }

    private String mkString(String[] lst, String seq) {
        StringBuilder temp = new StringBuilder();
        for (String i : lst) {
            temp.append(i).append(seq);
        }
        return temp.deleteCharAt(temp.length() - 1).toString();
    }
}
