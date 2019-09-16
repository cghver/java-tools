package com.tools.string;

import java.util.concurrent.ThreadLocalRandom;

public final class RandomHelper {

    /**
     * 随机字符串
     *
     * @param length 需要生成随机字符串的长度
     * @return string
     */
    public static String getRandomString(int length) {
        //length表示生成字符串的长度
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = ThreadLocalRandom.current().nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 随机数(只包含数字)
     *
     * @param length 需要生成随机字符串的长度
     * @return string
     */
    public static String getRandomNumber(int length) {
        //length表示生成数字的长度
        String base = "0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = ThreadLocalRandom.current().nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 生成某个范围的随机数
     *
     * @param min
     * @param max
     * @return
     */
    public static int getRandom(int min, int max) {
        return ThreadLocalRandom.current().nextInt(max) % (max - min + 1) + min;
    }


}
