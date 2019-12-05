package com.tools.string;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Strings;
import java.util.regex.Pattern;
public final class StringHelper {
    /**
     * 去除字符串两边某个字符
     *
     * @param src
     * @param c
     * @return
     */
    public static String trim(String src, char c) {
        if (StrUtil.isBlank(src)) {
            return src;
        }
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
        if (StrUtil.isBlank(src)) {
            return src;
        }
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
        if (StrUtil.isBlank(src)) {
            return src;
        }
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
        if (reg == null || "".equals(reg)) {
            return false;
        }
        if (input == null || "".equals(input)) {
            return false;
        }
        Pattern pattern = Pattern.compile(reg);
        return pattern.matcher(input).matches();
    }


    public static boolean isPhone(String userName) {
        if (Strings.isNullOrEmpty(userName)) {
            return false;
        }
        return StringHelper.regex("0?(13|14|15|17|18|19)[0-9]{9}", userName);
    }

    public static boolean isEmail(String userName)  {
        if (Strings.isNullOrEmpty(userName)) {
           return false;
        }
        return StringHelper.regex("\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}", userName);
    }

    public static boolean isPhoneOrEmail(String username)  {
        return isEmail(username) || isPhone(username);
    }

    /**
     * 判断是否是Ip
     * @param ip
     * @return
     */
    public static boolean isIp(String ip){
        if (Strings.isNullOrEmpty(ip)) {
            return false;
        }
        String reg = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";
        return StringHelper.regex(reg, ip);
    }


}
