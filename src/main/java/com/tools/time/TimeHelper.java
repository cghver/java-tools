package com.tools.time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * 时间相关的函数
 */
public enum TimeHelper {
    /**
     * 标准时间格式
     */
    F_STANDARD("yyyy-MM-dd HH:mm:ss", ThreadLocal.withInitial(() -> {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    })),
    /**
     * yyMMddHHmmssSSS时间格式
     */
    F_yyMMddHHmmssSSS("yyMMddHHmmssSSS", ThreadLocal.withInitial(() -> {
        return new SimpleDateFormat("yyMMddHHmmssSSS");
    })),
    /**
     * 时间格式
     */
    F_yyMMddHHmmss("yyMMddHHmmss", ThreadLocal.withInitial(() -> {
        return new SimpleDateFormat("yyMMddHHmmss");
    })),
    /**
     * FUNCTION用来调用函数
     */
    FUNCTION(null, null);
    private String formatKey;
    private ThreadLocal<DateFormat> dateFormatThreadLocal;

    TimeHelper(String formatKey, ThreadLocal<DateFormat> dateFormatThreadLocal) {
        this.formatKey = formatKey;
        this.dateFormatThreadLocal = dateFormatThreadLocal;
    }

    private String getFormatKey() {
        return formatKey;
    }

    private ThreadLocal<DateFormat> getDateFormatThreadLocal() {
        return dateFormatThreadLocal;
    }

    /**
     * 获取当前字符串格式的时间
     *
     * @return string
     */
    public String getFormatTime(TimeHelper timeHelper) {
        if (timeHelper.equals(TimeHelper.FUNCTION)) return null;
        return timeHelper.getDateFormatThreadLocal().get().format(System.currentTimeMillis());
    }

    /**
     * 获取字符串格式的时间
     *
     * @return string
     */
    public String getFormatTime(TimeHelper timeHelper, long timestamp) {
        if (timeHelper.equals(TimeHelper.FUNCTION)) return null;
        return timeHelper.getDateFormatThreadLocal().get().format(timestamp);
    }

    /**
     * 整数时间戳
     *
     * @param length 长度
     * @return 长整形
     */
    public long getTimestampLong(int length) {
        if (length > 13) length = 13;
        if (length < 1) length = 1;
        long t = System.currentTimeMillis();
        length = 13 - length;
        // 倍数
        long times = (long) Math.pow(10, length);
        return t / times;
    }

    /**
     * 返回毫秒时间戳
     *
     * @return timestamp
     */
    public Timestamp getTimeStamp() {
        return new Timestamp(System.currentTimeMillis());
    }

//    public static void main(String[] args) {
//        System.out.println(TimeHelper.FUNCTION.getFormatTime(TimeHelper.F_STANDARD));
//    }


}
