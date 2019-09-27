package com.tools.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.tools.string.StringHelper;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @auther: Tang XiaoBai
 * @date: 2018/11/26 15:48
 * @description: 这个类用来执行json的序列化反序列化操作
 */
public final class JsonHelper {
    private static ObjectMapper om;

    static {
        om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 判断是否是有效的json
     *
     * @param json json字符串
     * @return true or false
     */
    public static boolean isJson(String json) {
        if (StringHelper.isNullOrBlank(json)) {
            return false;
        }
        try {
            om.readTree(json);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 判断是否是有效的json对象
     *
     * @param json json字符串
     * @return true or false
     */
    public static boolean isJsonObject(String json) {
        if (StringHelper.isNullOrBlank(json)) {
            return false;
        }
        try {
            JsonNode jsonNode = om.readTree(json);
            JsonNodeType jsonNodeType = jsonNode.getNodeType();
            return jsonNodeType.equals(JsonNodeType.OBJECT);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 判断是否是有效的json数组
     *
     * @param json json字符串
     * @return true or false
     */
    public static boolean isJsonList(String json) {
        if (StringHelper.isNullOrBlank(json)) {
            return false;
        }
        try {
            JsonNode jsonNode = om.readTree(json);
            JsonNodeType jsonNodeType = jsonNode.getNodeType();
            return jsonNodeType.equals(JsonNodeType.ARRAY);
        } catch (IOException e) {
            return false;
        }
    }


    /**
     * 对象转json字符串，如果转换失败则返回null
     *
     * @param object 对象
     * @return String或null
     */
    public static String transToString(Object object) {
        return Optional.ofNullable(object)
                .map(v -> {
                    try {
                        return om.writeValueAsString(v);
                    } catch (JsonProcessingException e) {
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
        if (!isJsonObject(json)) {
            return null;
        }
        try {
            return om.readValue(json, bean);
        } catch (IOException e) {
            return null;
        }
    }


    /**
     * 字符串转List 如果转换失败返回null
     *
     * @param json 字符串
     * @param bean bean
     * @param <T>  泛型
     * @return bean
     */
    public static <T> List<T> transToList(String json, Class<T> bean) {
        if (!isJsonList(json)) {
            return null;
        }
        try {
            return om.readValue(json, om.getTypeFactory().constructCollectionType(List.class, bean));
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 获取json字符串的键值 String
     *
     * @param key
     * @param json
     * @return
     */
    public static String getNodeString(String key, String json) {
        if (!isJsonObject(json)) {
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
