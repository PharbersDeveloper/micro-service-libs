package com.pharbers.kafka.connect.oss.readerV2;

import com.pharbers.kafka.connect.oss.concurrent.RowData;
import com.pharbers.kafka.schema.OssTask;
import java.io.InputStream;
import java.util.Collection;
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
 * @since 2019/12/25 15:20
 */
public interface ReaderV2 {
    /**
     * 将数据存入seq
     * seq最好是阻塞队列，已保证不会oom，以及并发消费此seq的安全
     * @param seq 读取的数据存入此集合
     */
    void read(BlockingQueue<RowData> seq) throws InterruptedException;

    /**
     * 初始化
     * @param stream 需要读的流
     * @param streamOffset 需要跳过的部分
     */
    void init(InputStream stream);

    /**
     * 是否完成
     * @return 是否完成
     */
    boolean isEnd();

    /**
     * 关闭流
     */
    void close();
}
