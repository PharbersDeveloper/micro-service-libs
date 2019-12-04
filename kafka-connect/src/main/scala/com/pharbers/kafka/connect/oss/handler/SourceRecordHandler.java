package com.pharbers.kafka.connect.oss.handler;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;

import java.util.Map;

/**
 * 功能描述
 *
 * @author dcs
 * @version 0.0
 * @tparam T 构造泛型参数
 * @note 一些值得注意的地方
 * @since 2019/12/04 11:43
 */
public class SourceRecordHandler {
    private Map<String, ?> sourceOffsetKey;
    private String topic;
    private Schema keySchema;
    private Schema valueSchema;

    public SourceRecordHandler(Map<String, String> sourceOffsetKey, String topic, Schema keySchema, Schema valueSchema) {
        this.sourceOffsetKey = sourceOffsetKey;
        this.topic = topic;
        this.keySchema = keySchema;
        this.valueSchema = valueSchema;
    }

    public SourceRecord builder(Object key, Object value, Map<String, ?> sourceOffset){
        return new SourceRecord(sourceOffsetKey, sourceOffset, topic, null,
                keySchema, key, valueSchema, value, System.currentTimeMillis());
    }

    public Map<String, ?> getSourceOffsetKey() {
        return sourceOffsetKey;
    }

    public String getTopic() {
        return topic;
    }

    public Schema getKeySchema() {
        return keySchema;
    }

    public Schema getValueSchema() {
        return valueSchema;
    }
}
