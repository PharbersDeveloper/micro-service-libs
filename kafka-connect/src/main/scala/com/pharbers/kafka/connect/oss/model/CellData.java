package com.pharbers.kafka.connect.oss.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Map;

/**
 * 功能描述
 *
 * @author dcs
 * @version 0.0
 * @tparam T 构造泛型参数
 * @note 一些值得注意的地方
 * @since 2019/11/11 18:33
 */

@JsonSerialize(using = CellDataJsonFormatSerializer.class)
public class CellData implements Map.Entry<String, String> {
    String key;
    String value;

    public CellData(String key, String value){
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String setValue(String value) {
        String old = this.value;
        this.value = value;
        return old;
    }
}
