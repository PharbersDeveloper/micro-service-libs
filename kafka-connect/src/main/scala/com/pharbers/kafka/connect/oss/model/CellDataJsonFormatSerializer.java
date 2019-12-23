package com.pharbers.kafka.connect.oss.model;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;

/**
 * 功能描述
 *
 * @author dcs
 * @version 0.0
 * @tparam T 构造泛型参数
 * @note 一些值得注意的地方
 * @since 2019/11/11 18:50
 */
public class CellDataJsonFormatSerializer extends JsonSerializer<CellData> {
    @Override
    public void serialize(CellData value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeStringField(value.key, value.value);
        jgen.writeEndObject();
    }
}
