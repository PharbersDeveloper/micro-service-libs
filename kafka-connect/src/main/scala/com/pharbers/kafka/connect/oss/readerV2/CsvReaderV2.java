package com.pharbers.kafka.connect.oss.readerV2;

import com.pharbers.kafka.connect.oss.concurrent.RowData;

import java.io.InputStream;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

/**
 * 功能描述
 *
 * @author dcs
 * @version 0.0
 * @tparam T 构造泛型参数
 * @note 一些值得注意的地方
 * @since 2019/12/25 16:22
 */
public class CsvReaderV2 implements ReaderV2 {

    public CsvReaderV2(String jobIdPrefix){
    }

    @Override
    public void read(BlockingQueue<RowData> seq){

    }

    @Override
    public void init(InputStream stream) {

    }

    @Override
    public boolean isEnd() {
        return false;
    }

    @Override
    public void close() {

    }
}
