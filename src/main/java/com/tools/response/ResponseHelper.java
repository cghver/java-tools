package com.tools.response;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.tools.json.JsonHelper;

import java.io.IOException;
import java.util.List;

/**
 * 将接口返回的数据按照协议定好的消息格式进行转化
 */
public final class ResponseHelper {
    private static ObjectMapper om;

    static {
        om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    public static <T> Response<T> apiJsonToBean(String result, Class<T> bean) {
        String code = JsonHelper.getNodeString("code", result);
        String msg = JsonHelper.getNodeString("msg", result);
        T data = null;
        try {
            data = om.readValue(JsonHelper.getNodeString("data", result), bean);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Response<>(code, msg, data);
    }

    // 列表数据返回
    public static <T> Response<List<T>> apiJsonToListBean(String result, Class<T> bean) {
        String code = JsonHelper.getNodeString("code", result);
        String msg = JsonHelper.getNodeString("msg", result);
        String data = JsonHelper.getNodeString("data", result);
        TypeFactory typeFactory = om.getTypeFactory();
        List<T> list = null;
        try {
            list = om.readValue(data, typeFactory.constructCollectionType(List.class, bean));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Response<>(code, msg, list);
    }
}
