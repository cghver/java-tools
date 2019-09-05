package com.tools.response;
public enum ResponseCode {
    /**
     * 成功
     */
    SUCCESS("0000", "成功"),
    /**
     * 失败
     */
    FAILED("1001", "失败"),
    /**
     * 参数为空
     */
    PARAM_NULL("1002", "参数为空"),

    /**
     * 参数错误
     */
    PARAM_ERROR("1003", "参数错误"),

    /**
     * 连接超时
     */
    CONNECT_TIMEOUT("1004", "连接超时"),

    /**
     * 读取数据超时
     */
    READ_TIMEOUT("1005", "读取数据超时");


    private String code;
    private String desc;

    public String getCode() {
        return code;
    }


    public String getDesc() {
        return desc;
    }

    ResponseCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public boolean isSuccess() {
        return this.equals(SUCCESS);
    }
}
