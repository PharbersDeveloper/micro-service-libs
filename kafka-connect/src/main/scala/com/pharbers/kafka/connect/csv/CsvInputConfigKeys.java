package com.pharbers.kafka.connect.csv;

/**
 * 功能描述
 *
 * @author dcs
 * @version 0.0
 * @tparam T 构造泛型参数
 * @note 一些值得注意的地方
 * @since 2019/07/02 10:33
 */
class CsvInputConfigKeys {
    static final String TOPIC_CONFIG = "topic";
    static final String FILE_CONFIG = "file";
    static final String TASK_BATCH_SIZE_CONFIG = "batch.size";
    static final String SEPARATOR_CONFIG = "separator";
    static final String CHARSET_CONFIG = "charset";
    static final String TITLE_CONFIG = "title";
    static final String TRANSFORM_CONFIG = "transforms";
}
