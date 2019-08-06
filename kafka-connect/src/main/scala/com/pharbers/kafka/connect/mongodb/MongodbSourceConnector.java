package com.pharbers.kafka.connect.mongodb;

import com.pharbers.kafka.connect.InputConfigKeys;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;

import java.util.*;

/**
 * 功能描述
 *
 * @author dcs
 * @version 0.0
 * @tparam T 构造泛型参数
 * @note 一些值得注意的地方
 * @since 2019/08/05 14:53
 */
public class MongodbSourceConnector extends SourceConnector {
    static final int DEFAULT_TASK_BATCH_SIZE = 2000;

    private static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(InputConfigKeys.CONNECTION_CONFIG, Type.STRING, Importance.HIGH, "mongodb path like mongodb://192.168.100.176:27017")
            .define(InputConfigKeys.TOPIC_CONFIG, Type.LIST, Importance.HIGH, "The topic to publish data to")
            .define(InputConfigKeys.TASK_BATCH_SIZE_CONFIG, Type.INT, DEFAULT_TASK_BATCH_SIZE, Importance.LOW,
                    "The maximum number of records the Source task can read from file one time")
            .define(InputConfigKeys.DATABASE_CONFIG, Type.STRING, Importance.HIGH, "database")
            .define(InputConfigKeys.COLLECTION_CONFIG, Type.STRING, Importance.HIGH, "collection")
            .define(InputConfigKeys.FILTER_CONFIG, Type.STRING, Importance.MEDIUM, "过滤")
            .define(InputConfigKeys.JOB_CONFIG, Type.STRING,  UUID.randomUUID().toString(), Importance.HIGH, "配置job id");

    private String jobId;
    private String connection;
    private String topic;
    private int batchSize;
    private String database;
    private String collection;
    private String filter;

    @Override
    public String version() {
        return AppInfoParser.getVersion();
    }

    @Override
    public void start(Map<String, String> props) {
        AbstractConfig parsedConfig = new AbstractConfig(CONFIG_DEF, props);
        jobId = parsedConfig.getString(InputConfigKeys.JOB_CONFIG);
        connection = parsedConfig.getString(InputConfigKeys.CONNECTION_CONFIG);
        List<String> topics = parsedConfig.getList(InputConfigKeys.TOPIC_CONFIG);
        if (topics.size() != 1) {
            throw new ConfigException("'topic' in FileStreamSourceConnector configuration requires definition of a single topic");
        }
        topic = topics.get(0);
        database = parsedConfig.getString(InputConfigKeys.DATABASE_CONFIG);
        collection = parsedConfig.getString(InputConfigKeys.COLLECTION_CONFIG);
        batchSize = parsedConfig.getInt(InputConfigKeys.TASK_BATCH_SIZE_CONFIG);
        filter = parsedConfig.getString(InputConfigKeys.FILTER_CONFIG);
    }

    @Override
    public Class<? extends Task> taskClass() {
        return MongodbSourceTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        ArrayList<Map<String, String>> configs = new ArrayList<>();
        // Only one input stream makes sense.
        Map<String, String> config = new HashMap<>();
        config.put(InputConfigKeys.JOB_CONFIG, jobId);
        config.put(InputConfigKeys.CONNECTION_CONFIG, connection);
        config.put(InputConfigKeys.TOPIC_CONFIG, topic);
        config.put(InputConfigKeys.TASK_BATCH_SIZE_CONFIG, String.valueOf(batchSize));
        config.put(InputConfigKeys.DATABASE_CONFIG, database);
        config.put(InputConfigKeys.COLLECTION_CONFIG, collection);
        config.put(InputConfigKeys.FILTER_CONFIG, filter);
        configs.add(config);
        return configs;
    }

    @Override
    public void stop() {

    }

    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }
}
