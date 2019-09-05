package com.tools.response;

public final class ResponseTemplate {
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

}
