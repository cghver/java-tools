package com.tools.string;

import com.google.common.base.Strings;

import java.util.regex.Pattern;

public final class StringHelper {
    /**
     * 检查字符串是否为空或null
     * @param str
     * @return
     */
    public static boolean isNullOrBlank(String str) {
        if (Strings.isNullOrEmpty(str)) {
            return true;
        }
        return Strings.isNullOrEmpty(str.trim());
    }

    /**
     * 去除字符串两边某个字符
     *
     * @param src
     * @param c
     * @return
     */
    public static String trim(String src, char c) {
        if (isNullOrBlank(src)) return src;
        String rex = String.format("^[%c]+|[%c]+$", c, c);
        return src.replaceAll(rex, "");
    }

    /**
     * 去除字符串左边某个字符
     *
     * @param src
     * @param c
     * @return
     */
    public static String trimLeft(String src, char c) {
        if (isNullOrBlank(src)) return src;
        String rex = String.format("^[%c]+", c);
        return src.replaceAll(rex, "");
    }

    /**
     * 去除字符串右边某个字符
     *
     * @param src
     * @param c
     * @return
     */
    public static String trimRight(String src, char c) {
        if (isNullOrBlank(src)) return src;
        String rex = String.format("[%c]+$", c);
        return src.replaceAll(rex, "");
    }

    /**
     * 正则表达式判断
     *
     * @param reg
     * @param input
     * @return
     */
    public static boolean regex(String reg, String input) {
        if (reg == null || reg.equals("")) {
            throw new NullPointerException("正则表达式规则为空");
        }
        if (input == null || input.equals("")) {
            throw new NullPointerException("请传入你要正则表达式内容");
        }
        Pattern pattern = Pattern.compile(reg);
        return pattern.matcher(input).matches();
    }


}
