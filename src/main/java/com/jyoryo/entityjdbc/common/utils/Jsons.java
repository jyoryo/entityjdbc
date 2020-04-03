package com.jyoryo.entityjdbc.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jyoryo.entityjdbc.common.Strings;
import com.jyoryo.entityjdbc.common.exception.JsonException;

/**
 * Json 工具类
 * @author jyoryo
 *
 */
public class Jsons {
    private static ObjectMapper DEFAULT_MAPPER = new ObjectMapper();
    
    static {
        // 对象的所有字段全部列入序列化
        DEFAULT_MAPPER.setDefaultPropertyInclusion(JsonInclude.Include.ALWAYS);
        // 忽略空Bean转json的错误
        DEFAULT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
        // 忽略 在json字符串中存在，但是在java对象中不存在对应属性的情况。防止错误
        DEFAULT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    /**
     * 将对象转换为Json字符串
     * @param obj
     * @return
     */
    public static <T> String toJson(T obj) {
        if(null == obj) {
            return null;
        }
        try {
            return obj instanceof String ? (String)obj : DEFAULT_MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            throw new JsonException("obj can not convert json value!", e);
        }
    }
    
    /**
     * 将Json字符串转换为目标对象
     * @param json
     * @param clazz
     * @return
     */
    public static <T> T toObject(String json, Class<T> clazz) {
        if(Strings.isBlank(json) || null == clazz) {
            return null;
        }
        try {
            return DEFAULT_MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            throw new JsonException("json value can not convert obj!", e);
        }
    }
}
