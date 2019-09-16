package com.tools.time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
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
     * yyMMddHHmmss时间格式
     */
    F_yyMMddHHmmss("yyMMddHHmmss", ThreadLocal.withInitial(() -> {
        return new SimpleDateFormat("yyMMddHHmmss");
    })),

    /**
     * yyyy-MM-dd时间格式
     */
    F_yyyy_MM_dd("yyyy-MM-dd", ThreadLocal.withInitial(() -> {
        return new SimpleDateFormat("yyyy-MM-dd");
    })),
    /**
     * yyyyMMdd时间格式
     */
    F_yyyyMMdd("yyyyMMdd", ThreadLocal.withInitial(() -> {
        return new SimpleDateFormat("yyyyMMdd");
    }));
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
    public static String getFormatTime(TimeHelper timeHelper) {
        return timeHelper.getDateFormatThreadLocal().get().format(System.currentTimeMillis());
    }

    /**
     * 获取字符串格式的时间
     *
     * @return string
     */
    public static String getFormatTime(TimeHelper timeHelper, long timestamp) {
        return timeHelper.getDateFormatThreadLocal().get().format(timestamp);
    }

    /**
     * 将字符串的时间转化为时间戳
     * @param timeHelper 时间格式
     * @param formatTime 字符串时间
     * @return
     */
    public static long getTimeStampFromFormatTime(TimeHelper timeHelper, String formatTime){
        try {
            return timeHelper.getDateFormatThreadLocal().get().parse(formatTime).getTime();
        } catch (ParseException e) {
            return 0;
        }
    }

    /**
     * 整数时间戳
     *
     * @param length 长度
     * @return 长整形
     */
    public static long getTimestampLong(int length) {
        if (length > 13) {
            length = 13;
        }
        if (length < 1) {
            length = 1;
        }
        long t = System.currentTimeMillis();
        length = 13 - length;
        // 倍数
        long times = (long) Math.pow(10, length);
        return t / times;
    }

    /**
     * 返回毫秒时间戳
     * @return timestamp
     */
    public Timestamp getTimeStamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static void main(String[] args) {
        System.out.println(TimeHelper.getTimestampLong(10));
    }


}
