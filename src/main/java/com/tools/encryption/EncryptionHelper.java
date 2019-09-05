package com.tools.encryption;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * @Auther: Tang XiaoBai
 * @Date: 2018/11/15 13:23
 * @Description: 加密函数类，提供了MD5、SHA-256加密算法
 */
public class EncryptionHelper {

    private static String process(String algorithm, String s) {
        if (null == s) {
            throw new NullPointerException("非法字符串");
        }
        if (null == algorithm) {
            throw new NullPointerException("算法类型有误");
        }
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(algorithm);
            md.update(s.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                // 10进制转16进制，X 表示以十六进制形式输出，02 表示不足两位前面补0输出
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String md5(String s) {
        return process("MD5", s);
    }

    public static String sha256(String s) {
        return process("SHA-256", s);
    }

}
