package com.tools.encryption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * RSA算法加密/解密工具类
 */
public class RSAHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(RSAHelper.class);
    /**
     * 算法名称
     */
    private static final String ALGORITHM = "RSA";
    /**
     * 默认密钥大小
     */
    private static final int KEY_SIZE = 1024;

    /**
     * 密钥对生成器
     */
    private static KeyPairGenerator keyPairGenerator = null;
    private static KeyFactory keyFactory = null;
    /**
     * 缓存的密钥对
     */
    private static KeyPair keyPair = null;
    /**
     * Base64 编码/解码器 JDK1.8
     */
    private static Base64.Decoder decoder = Base64.getDecoder();
    private static Base64.Encoder encoder = Base64.getEncoder();

    // 初始化密钥工厂
    static {
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyFactory = KeyFactory.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * 私有构造器
     */
    private RSAHelper() {
    }

    /**
     * 生成密钥对
     * 将密钥分别用Base64编码保存到#publicKey.properties#和#privateKey.properties#文件中
     * 保存的默认名称分别为publicKey和privateKey
     */
    private static synchronized Map<String, Object> generateKeyPair() {
        try {
            keyPairGenerator.initialize(KEY_SIZE, new SecureRandom(UUID.randomUUID().toString().replaceAll("-", "").getBytes()));
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (InvalidParameterException e) {
            LOGGER.error("KeyPairGenerator does not support a key length of " + KEY_SIZE + ".", e);
        } catch (NullPointerException e) {
            LOGGER.error("RSAHelper#key_pair_gen is null,can not generate KeyPairGenerator instance.", e);
        }
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        String publicKeyString = encoder.encodeToString(rsaPublicKey.getEncoded());
        String privateKeyString = encoder.encodeToString(rsaPrivateKey.getEncoded());
        Map<String, Object> keyPair = new HashMap<>();
        keyPair.put("public", publicKeyString);
        keyPair.put("private", privateKeyString);
        return keyPair;
    }


    /**
     * RSA公钥加密
     *
     * @param content         等待加密的数据
     * @param publicKeyString RSA 公钥 if null then getPublicKey()
     * @return 加密后的密文(16进制的字符串)
     */
    public static String encryptByPublic(byte[] content, String publicKeyString) throws InvalidKeySpecException {
        if (publicKeyString == null) return null;

        byte[] keyBytes = decoder.decode(publicKeyString);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            //该密钥能够加密的最大字节长度
            int splitLength = ((RSAPublicKey) publicKey).getModulus().bitLength() / 8 - 11;
            byte[][] arrays = splitBytes(content, splitLength);
            StringBuilder stringBuilder = new StringBuilder();
            for (byte[] array : arrays) {
                stringBuilder.append(bytesToHexString(cipher.doFinal(array)));
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("encrypt()#NoSuchAlgorithmException", e);
        } catch (NoSuchPaddingException e) {
            LOGGER.error("encrypt()#NoSuchPaddingException", e);
        } catch (InvalidKeyException e) {
            LOGGER.error("encrypt()#InvalidKeyException", e);
        } catch (BadPaddingException e) {
            LOGGER.error("encrypt()#BadPaddingException", e);
        } catch (IllegalBlockSizeException e) {
            LOGGER.error("encrypt()#IllegalBlockSizeException", e);
        }
        return null;
    }

    /**
     * RSA私钥加密
     *
     * @param content          等待加密的数据
     * @param privateKeyString RSA 私钥(base64)
     * @return 加密后的密文(16进制的字符串)
     */
    public static String encryptByPrivate(byte[] content, String privateKeyString) throws InvalidKeySpecException {
        if (privateKeyString == null) return null;

        byte[] keyBytes = decoder.decode(privateKeyString);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            //该密钥能够加密的最大字节长度
            int splitLength = ((RSAPrivateKey) privateKey).getModulus().bitLength() / 8 - 11;
            byte[][] arrays = splitBytes(content, splitLength);
            StringBuilder stringBuilder = new StringBuilder();
            for (byte[] array : arrays) {
                stringBuilder.append(bytesToHexString(cipher.doFinal(array)));
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("encrypt()#NoSuchAlgorithmException", e);
        } catch (NoSuchPaddingException e) {
            LOGGER.error("encrypt()#NoSuchPaddingException", e);
        } catch (InvalidKeyException e) {
            LOGGER.error("encrypt()#InvalidKeyException", e);
        } catch (BadPaddingException e) {
            LOGGER.error("encrypt()#BadPaddingException", e);
        } catch (IllegalBlockSizeException e) {
            LOGGER.error("encrypt()#IllegalBlockSizeException", e);
        }
        return null;
    }


    /**
     * RSA私钥解密
     *
     * @param content          等待解密的数据
     * @param privateKeyString RSA 私钥
     * @return 解密后的明文
     */
    public static String decryptByPrivate(String content, String privateKeyString) throws InvalidKeySpecException {
        if (privateKeyString == null) return null;


        byte[] keyBytes = decoder.decode(privateKeyString);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);


        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            //该密钥能够加密的最大字节长度
            int splitLength = ((RSAPrivateKey) privateKey).getModulus().bitLength() / 8;
            byte[] contentBytes = hexStringToBytes(content);
            byte[][] arrays = splitBytes(contentBytes, splitLength);
            StringBuilder stringBuilder = new StringBuilder();
            for (byte[] array : arrays) {
                stringBuilder.append(new String(cipher.doFinal(array)));
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("encrypt()#NoSuchAlgorithmException", e);
        } catch (NoSuchPaddingException e) {
            LOGGER.error("encrypt()#NoSuchPaddingException", e);
        } catch (InvalidKeyException e) {
            LOGGER.error("encrypt()#InvalidKeyException", e);
        } catch (BadPaddingException e) {
            LOGGER.error("encrypt()#BadPaddingException", e);
        } catch (IllegalBlockSizeException e) {
            LOGGER.error("encrypt()#IllegalBlockSizeException", e);
        }
        return null;
    }

    /**
     * RSA公钥解密
     *
     * @param content         等待解密的数据
     * @param publicKeyString RSA 公钥
     * @return 解密后的明文
     */
    public static String decryptByPublic(String content, String publicKeyString) throws InvalidKeySpecException {
        if (publicKeyString == null) return null;

        byte[] keyBytes = decoder.decode(publicKeyString);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            //该密钥能够加密的最大字节长度
            int splitLength = ((RSAPublicKey) publicKey).getModulus().bitLength() / 8;
            byte[] contentBytes = hexStringToBytes(content);
            byte[][] arrays = splitBytes(contentBytes, splitLength);
            StringBuilder stringBuilder = new StringBuilder();
            for (byte[] array : arrays) {
                stringBuilder.append(new String(cipher.doFinal(array)));
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("encrypt()#NoSuchAlgorithmException", e);
        } catch (NoSuchPaddingException e) {
            LOGGER.error("encrypt()#NoSuchPaddingException", e);
        } catch (InvalidKeyException e) {
            LOGGER.error("encrypt()#InvalidKeyException", e);
        } catch (BadPaddingException e) {
            LOGGER.error("encrypt()#BadPaddingException", e);
        } catch (IllegalBlockSizeException e) {
            LOGGER.error("encrypt()#IllegalBlockSizeException", e);
        }
        return null;
    }


    /**
     * 根据限定的每组字节长度，将字节数组分组
     *
     * @param bytes       等待分组的字节组
     * @param splitLength 每组长度
     * @return 分组后的字节组
     */
    private static byte[][] splitBytes(byte[] bytes, int splitLength) {
        //bytes与splitLength的余数
        int remainder = bytes.length % splitLength;
        //数据拆分后的组数，余数不为0时加1
        int quotient = remainder != 0 ? bytes.length / splitLength + 1 : bytes.length / splitLength;
        byte[][] arrays = new byte[quotient][];
        byte[] array;
        for (int i = 0; i < quotient; i++) {
            //如果是最后一组（quotient-1）,同时余数不等于0，就将最后一组设置为remainder的长度
            if (i == quotient - 1 && remainder != 0) {
                array = new byte[remainder];
                System.arraycopy(bytes, i * splitLength, array, 0, remainder);
            } else {
                array = new byte[splitLength];
                System.arraycopy(bytes, i * splitLength, array, 0, splitLength);
            }
            arrays[i] = array;
        }
        return arrays;
    }

    /**
     * 将字节数组转换成16进制字符串
     *
     * @param bytes 即将转换的数据
     * @return 16进制字符串
     */
    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length);
        String temp;
        for (byte aByte : bytes) {
            temp = Integer.toHexString(0xFF & aByte);
            if (temp.length() < 2) {
                sb.append(0);
            }
            sb.append(temp);
        }
        return sb.toString();
    }

    /**
     * 将16进制字符串转换成字节数组
     *
     * @param hex 16进制字符串
     * @return byte[]
     */
    private static byte[] hexStringToBytes(String hex) {
        int len = (hex.length() / 2);
        hex = hex.toUpperCase();
        byte[] result = new byte[len];
        char[] chars = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(chars[pos]) << 4 | toByte(chars[pos + 1]));
        }
        return result;
    }

    /**
     * 将char转换为byte
     *
     * @param c char
     * @return byte
     */
    private static byte toByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }


//    public static void main(String[] args) throws InvalidKeySpecException {
//        Map<String, Object> map = generateKeyPair();
//        String privateKey = (String) map.get("private");
//        String publicKey = (String) map.get("public");
//        String s = "Hello This is My House";
//        String c1 = RSAHelper.encryptByPublic(s.getBytes(), publicKey);
//        String m1 = RSAHelper.decryptByPrivate(c1, privateKey);
//        String c2 = RSAHelper.encryptByPrivate(s.getBytes(), privateKey);
//        String m2 = RSAHelper.decryptByPublic(c2, publicKey);
//        System.out.println(c1);
//        System.out.println(m1);
//        System.out.println(c2);
//        System.out.println(m2);
//    }


}
