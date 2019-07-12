package com.pharbers.kafka.connect.excel;

import com.pharbers.kafka.connect.csv.InputConfigKeys;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;

import java.util.*;

/**
 * 功能描述
 *
 * @author dcs
 * @version 0.0
 * @tparam T 构造泛型参数
 * @note 一些值得注意的地方
 * @since 2019/07/03 10:31
 */
public class ExcelStreamSourceConnector extends SourceConnector {


    static final int DEFAULT_TASK_BATCH_SIZE = 2000;

    private static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(InputConfigKeys.FILE_CONFIG, Type.STRING, null, Importance.HIGH, "Source filename. If not specified, the standard input will be used")
            .define(InputConfigKeys.TOPIC_CONFIG, Type.LIST, Importance.HIGH, "The topic to publish data to")
            .define(InputConfigKeys.TASK_BATCH_SIZE_CONFIG, Type.INT, DEFAULT_TASK_BATCH_SIZE, Importance.LOW,
                    "The maximum number of records the Source task can read from file one time")
            .define(InputConfigKeys.AUTO_TITLE_CONFIG, Type.BOOLEAN, true, Importance.LOW, "是否使用第一行为标题")
            .define(InputConfigKeys.TITLE_CONFIG, Type.LIST, new ArrayList<String>(), Importance.LOW, "配置title， autoTitle为false才能生效")
            .define(InputConfigKeys.JOB_CONFIG, Type.STRING,  UUID.randomUUID().toString(), Importance.HIGH, "配置job id");


    private String jobId;
    private String filename;
    private String topic;
    private int batchSize;
    private boolean autoTitle;
    private List<String> titleList = new ArrayList<>();

    @Override
    public String version() {
        return AppInfoParser.getVersion();
    }

    @Override
    public void start(Map<String, String> props) {
        AbstractConfig parsedConfig = new AbstractConfig(CONFIG_DEF, props);
        jobId = parsedConfig.getString(InputConfigKeys.JOB_CONFIG);
        filename = parsedConfig.getString(InputConfigKeys.FILE_CONFIG);
        List<String> topics = parsedConfig.getList(InputConfigKeys.TOPIC_CONFIG);
        if (topics.size() != 1) {
            throw new ConfigException("'topic' in FileStreamSourceConnector configuration requires definition of a single topic");
        }
        topic = topics.get(0);
        autoTitle = parsedConfig.getBoolean(InputConfigKeys.AUTO_TITLE_CONFIG);
        titleList = parsedConfig.getList(InputConfigKeys.TITLE_CONFIG);
        batchSize = parsedConfig.getInt(InputConfigKeys.TASK_BATCH_SIZE_CONFIG);
    }

    @Override
    public Class<? extends Task> taskClass() {
        return ExcelStreamSourceTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        ArrayList<Map<String, String>> configs = new ArrayList<>();
        // Only one input stream makes sense.
        Map<String, String> config = new HashMap<>();
        config.put(InputConfigKeys.JOB_CONFIG, jobId);
        if (filename != null)
            config.put(InputConfigKeys.FILE_CONFIG, filename);
        config.put(InputConfigKeys.TOPIC_CONFIG, topic);
        config.put(InputConfigKeys.TASK_BATCH_SIZE_CONFIG, String.valueOf(batchSize));
        config.put(InputConfigKeys.AUTO_TITLE_CONFIG, String.valueOf(autoTitle));
        config.put(InputConfigKeys.TITLE_CONFIG, String.join(",", titleList));
        configs.add(config);
        return configs;
    }

    @Override
    public void stop() {
        // Nothing to do since FileStreamSourceConnector has no background monitoring.
    }

    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }
}
