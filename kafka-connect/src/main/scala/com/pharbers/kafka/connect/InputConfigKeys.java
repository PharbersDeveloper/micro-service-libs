package com.pharbers.kafka.connect;

/**
 * 功能描述
 *
 * @author dcs
 * @version 0.0
 * @tparam T 构造泛型参数
 * @note 一些值得注意的地方
 * @since 2019/07/02 10:33
 */
public class InputConfigKeys {
    public static final String JOB_CONFIG = "job";
    public static final String TOPIC_CONFIG = "topic";
    public static final String FILE_CONFIG = "file";
    public static final String TASK_BATCH_SIZE_CONFIG = "batch.size";
    public static final String SEPARATOR_CONFIG = "separator";
    public static final String CHARSET_CONFIG = "charset";
    public static final String TITLE_CONFIG = "title";
    public static final String AUTO_TITLE_CONFIG = "autoTitle";
    public static final String TRANSFORM_CONFIG = "transform";
    //mongodb
    public static final String CONNECTION_CONFIG = "connection";
    public static final String DATABASE_CONFIG = "database";
    public static final String COLLECTION_CONFIG = "collection";
    public static final String FILTER_CONFIG = "filter";
}
