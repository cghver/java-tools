package com.tools.response;
import com.tools.json.JsonHelper;
import java.io.Serializable;
import java.util.List;

public final class Response<T> implements Serializable {
    private String code;
    private String msg;
    private T data;

    public Response() {
    }

    public Response(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }


    public static <V> Response<V> success() {
        return new Response<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getDesc(), null);
    }

    public static Response success(String msg) {
        return new Response<>(ResponseCode.SUCCESS.getCode(), msg, null);
    }

    public static <V> Response<V> success(V data) {
        return new Response<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getDesc(), data);
    }

    public static <V> Response<V> success(String msg, V data) {
        return new Response<>(ResponseCode.SUCCESS.getCode(), msg, data);
    }

    public static <V> Response<V> failed() {
        return new Response<>(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getDesc(), null);
    }

    public static Response failed(String msg) {
        return new Response<>(ResponseCode.FAILED.getCode(), msg, null);
    }

    public static <V> Response<V> failed(V data) {
        return new Response<>(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getDesc(), data);
    }

    public static <V> Response<V> failed(String msg, V data) {
        return new Response<>(ResponseCode.FAILED.getCode(), msg, data);
    }


    public static <V> Response<V> failed(ResponseCode responseCode) {
        return new Response<>(responseCode.getCode(), responseCode.getDesc(), null);
    }

    public static <V> Response<V> failed(ResponseCode responseCode, V data) {
        return new Response<>(responseCode.getCode(), responseCode.getDesc(), data);
    }

    public static <V> Response<V> failed(String code, String msg, V data) {
        return new Response<>(code, msg, data);
    }

    public static <V> Response<V> failed(String code, String msg) {
        return new Response<>(code, msg, null);
    }

    /**
     * 将Response的json字符串转化为response，data为Object,如果转失败，返回null
     * @param json
     * @param bean
     * @return
     */
    public static <T> Response<T> JsonToResponse(String json, Class<T> bean) {
        String code = JsonHelper.getNodeString("code", json);
        String msg = JsonHelper.getNodeString("msg", json);
        String dataString = JsonHelper.getNodeString("data", json);

        if (code == null || msg == null ){
            return null;
        }
        if (bean == null  || dataString == null){
            return new Response<>(code, msg, null);
        }

        T data = JsonHelper.transToObject(dataString, bean);
        return new Response<>(code, msg, data);
    }

    /**
     * 将Response的json字符串转化为response，data为list,如果转失败，返回null
     * @param json
     * @param bean
     * @return
     */
    public static <T> Response<List<T>> jsonToListResponse(String json, Class<T> bean) {
        String code = JsonHelper.getNodeString("code", json);
        String msg = JsonHelper.getNodeString("msg", json);
        String data = JsonHelper.getNodeString("data", json);
        if (code == null || msg == null){
            return null;
        }
        if (bean == null || data == null || "null".equals(data)){
            return new Response<>(code, msg, null);
        }

        List<T> list = JsonHelper.transToList(data, bean);
        return new Response<>(code, msg, list);
    }
}
