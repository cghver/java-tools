package com.tools.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Optional;

/**
 * @Auther: Tang XiaoBai
 * @Date: 2018/11/26 15:48
 * @Description: 这个类用来执行json的序列化非反序列化操作
 */
public final class JsonHelper {
    private static ObjectMapper om;

    static {
        om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 对象转json字符串，如果转换失败则返回null
     *
     * @param object
     * @return
     */
    public static String transToString(Object object) {
        return Optional.ofNullable(object)
                .map(v -> {
                    try {
                        return om.writeValueAsString(v);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        return null;
                    }
                }).orElse(null);
    }

    /**
     * 字符串转对象 如果转换失败返回null
     *
     * @param json 字符串
     * @param bean bean
     * @param <T>  泛型
     * @return bean
     */
    public static <T> T transToObject(String json, Class<T> bean) {
        return Optional.ofNullable(json)
                .map(v -> {
                    try {
                        return om.readValue(v, bean);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .orElse(null);
    }

    /**
     * 获取json字符串的键值 String
     *
     * @param key
     * @param json
     * @return
     */
    public static String getNodeString(String key, String json) {
        if (key == null || json == null) {
            return null;
        }
        String result = null;
        try {
            JsonNode jsonNode = om.readTree(json);
            Object o = jsonNode.get(key);
            if (o == null) {
                return null;
            }
            result = String.valueOf(o).replaceAll("(^\")|(\"$)", "").trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取json字符串的键值 Long
     *
     * @param key
     * @param json
     * @return
     */
    public static Long getNodeLong(String key, String json) {
        return Optional.ofNullable(getNodeString(key, json)).map(Long::valueOf).orElse(null);
    }

    /**
     * 获取json字符串的键值 double
     *
     * @param key
     * @param json
     * @return
     */
    public static Double getNodeDouble(String key, String json) {
        return Optional.ofNullable(getNodeString(key, json)).map(Double::valueOf).orElse(null);
    }

    /**
     * 获取json字符串的键值 int
     *
     * @param key
     * @param json
     * @return
     */
    public static Integer getNodeInteger(String key, String json) {
        return Optional.ofNullable(getNodeString(key, json)).map(Integer::valueOf).orElse(null);
    }

}
