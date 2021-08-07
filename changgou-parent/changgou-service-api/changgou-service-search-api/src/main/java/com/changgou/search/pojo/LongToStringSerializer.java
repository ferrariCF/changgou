package com.changgou.search.pojo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author: lee
 * @date: 2021-08-07
 **/
public class LongToStringSerializer extends JsonSerializer<Long> {

    @Override
    public void serialize(Long aLong, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        //将Long类型数据转换成字符串 避免精度丢失的问题
        jsonGenerator.writeString(aLong.toString());
    }
}
