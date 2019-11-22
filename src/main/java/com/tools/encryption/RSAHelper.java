package com.tools.encryption;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

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
     * 默认公钥
     */
    private static final String DEFAULT_PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJwplYA3Yhc4uQUTyUYHxX1vzIdJPVhmlqHIiOGqnHGdAl5VxAnLhLJjgEXCyl85iUAqP01dnzqFaMaCYzmEhToYDop+7JwmAN+nIaqm+A8SIYnkcXOOuxPvK6t44Wy9MhZ3HoWPVHoZnDACaYnb2hGZb4ISQra6hHV0NkU+1binAgMBAAECgYABnZSEXQ5Quy9+0/OTG+V5JLzy8VkHHxiT1+VCoGc57avmmfCLQWXACrN7BUbesVOwLD+3Zy6MhnDNDPBF2g8exJP6Fgn7lXPpMFcgFjT0FmbVuaIC82k5yhfca6YKz/CFuWjtOHsm8/ozzpJKFf9D2Ie++lchqQlWnlTUL2NucQJBAN4sIb1z/3kxgYOEvYzeSS17eImTUSGFx92LlmApsXBrcsweh/N6Az5/inQ9Y/qdGiXa6Qq5ilXryyzvpnj7DQUCQQCz8IGri6Isn4zdQ6fhEhw5oj588w8CqeTeaAGlwiFA8c9+MxqHUr+uHv5fUOcX2trVJoegyt9NkH0X6Mj63D67AkBerh3+1+VCp6dS/gmtc7lpyZmXv5EuoQ2Iy4jdGEeG6jN063nyd8fUJZRCbzshPTw8b6sqp+FdNmxSjRq7qfllAkA5gF7/weR2XBo4zxkD3LS2Wjmb1lRypnYj+JqmLM5RobSMAKq2meP1MaRaM1FWFzMdMG3hHVOUxtqi3Fn1iJJnAkEAuP20nnCGSAWg6VMJjRkTlz+sp94if7OPXvvNmcBt4xqNpgNKRc6RUYE+oYu4eNZr1VEw3qIay9O8azrBamAKww==";

    /**
     * 默认私钥
     */
    private static final String DEFAULT_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCcKZWAN2IXOLkFE8lGB8V9b8yHST1YZpahyIjhqpxxnQJeVcQJy4SyY4BFwspfOYlAKj9NXZ86hWjGgmM5hIU6GA6KfuycJgDfpyGqpvgPEiGJ5HFzjrsT7yureOFsvTIWdx6Fj1R6GZwwAmmJ29oRmW+CEkK2uoR1dDZFPtW4pwIDAQAB";



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
    public static String encryptByPublic(String content, String publicKeyString) throws InvalidKeySpecException {
        if (content == null || publicKeyString == null) {
            return null;
        }

        byte[] contentByte = Base64.getEncoder().encodeToString(content.getBytes()).getBytes();

        byte[] keyBytes = decoder.decode(publicKeyString);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            //该密钥能够加密的最大字节长度
            int splitLength = ((RSAPublicKey) publicKey).getModulus().bitLength() / 8 - 11;
            byte[][] arrays = splitBytes(contentByte, splitLength);
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
    public static String encryptByPrivate(String content, String privateKeyString) throws InvalidKeySpecException {
        if (content == null || privateKeyString == null) {
            return null;
        }


        byte[] contentByte = Base64.getEncoder().encodeToString(content.getBytes()).getBytes();

        byte[] keyBytes = decoder.decode(privateKeyString);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            //该密钥能够加密的最大字节长度
            int splitLength = ((RSAPrivateKey) privateKey).getModulus().bitLength() / 8 - 11;
            byte[][] arrays = splitBytes(contentByte, splitLength);
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
        if (content == null || privateKeyString == null) {
            return null;
        }

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
            return new String(Base64.getDecoder().decode(stringBuilder.toString()));
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
        if (content == null || publicKeyString == null) {
            return null;
        }

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
            return new String(Base64.getDecoder().decode(stringBuilder.toString()));
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




    /**
     * RSA公钥加密
     * @param content         等待加密的数据
     * @return 加密后的密文(16进制的字符串)
     */
    public static String encryptByPublic(String content) throws InvalidKeySpecException {
        return encryptByPublic(content, DEFAULT_PUBLIC_KEY);
    }



    /**
     * RSA私钥加密
     *
     * @param content  等待加密的数据
     * @return 加密后的密文(16进制的字符串)
     */
    public static String encryptByPrivate(String content) throws InvalidKeySpecException {
        return encryptByPrivate(content, DEFAULT_PRIVATE_KEY);
    }


    /**
     * RSA私钥解密
     *
     * @param content          等待解密的数据
     * @return 解密后的明文
     */
    public static String decryptByPrivate(String content) throws InvalidKeySpecException {
        return decryptByPrivate(content, DEFAULT_PRIVATE_KEY);
    }

    /**
     * RSA公钥解密
     *
     * @param content         等待解密的数据
     * @return 解密后的明文
     */
    public static String decryptByPublic(String content) throws InvalidKeySpecException {
        return decryptByPublic(content, DEFAULT_PUBLIC_KEY);
    }



    private static String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKJHEZLmh5EX0wslfDSW01QdU9Nh8BPmkV3vaR9hZP7IsZxcKDsTyDh9mcIicPTxFUlNTPG00ypQVmRPz/QirHkOVy0hiWPgaC3JLQkOPqJEafumsWGH2GspliQ67rrK0Zzw5i6k4qjxVHv+XiQvtK0imvazv/y5ZMAChw6twtgfAgMBAAECgYBaHlORG/rPRcbK2t9WKShk/wBtVGdJ9fJmkyzy41h98aBI8Bjbbgnz7TR1TkEilbu3gHkphbz7M7A2duhKcPMMR8RVDatl7y6Y9z5n2NyL5xbo69ZAR0KWdvqbaPsZq0VEXrgZHjY5LoHEa+PwNJFcMRASckNSnWHBQfqqsDg5EQJBAODYQxOicBF9Yf/D3jYDJgMW38otr/clO7hYqTg/qxObaAQ7JyGeXrFhF/xAOxySUHEYPvEh1HQErw9hDC5B2gkCQQC4w2l3iqfode7ohrzEzUeEbf/ePI011FotMYGKdpo7eKNCRIvMOjNbM1f7jwW/n5bBa2WQ/O0lVUWLXQKq7MrnAkEAyxRpmT1ZDWL69aC6fnxfg+DQJnMXgRkfFIT36ncPFLmDvHaVBHdfPcTdfhehjE/W2h81EmQk2Nk2KKTV9J3DyQJAMY5oOWH9S0JmhAOvmfGdXw3J9Se0kF2WmkyO/D53e8ANufZ8sJhjsfCBhr2DwzPg0Zwmr87HJJ5MeubFkmbUvQJAH6D/h5YNHtlHIH468/SF1x5ThixZIt2jCEf6ATjZcUqmbAYNY1IO/ITBNqZiXnmjufIChpoK4YkETxWC6PHUBw==";
    private static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCiRxGS5oeRF9MLJXw0ltNUHVPTYfAT5pFd72kfYWT+yLGcXCg7E8g4fZnCInD08RVJTUzxtNMqUFZkT8/0Iqx5DlctIYlj4GgtyS0JDj6iRGn7prFhh9hrKZYkOu66ytGc8OYupOKo8VR7/l4kL7StIpr2s7/8uWTAAocOrcLYHwIDAQAB";


    public static void main(String[] args) throws InvalidKeySpecException, UnsupportedEncodingException {
//        Map<String, Object> map = generateKeyPair();
//        String privateKey = (String) map.get("private");
//        String publicKey = (String) map.get("public");
//        System.out.println(privateKey);
//        System.out.println(publicKey);
//        String s = "cda那UC多送您极道少女敬爱的刹那间你加你家阿森纳达是参da那UC多送您极道少女敬爱的刹那间你加你家阿森纳达是参赛队 农家菜是几年da那UC多送您极道少女敬爱的刹那间你加你家阿森纳达是参赛队 农家菜是几年da那UC多送您极道少女敬爱的刹那间你加你家阿森纳达是参赛队 农家菜是几年da那UC多送您极道少女敬爱的刹那间你加你家阿森纳达是参赛队 农家菜是几年da那UC多送您极道少女敬爱的刹那间你加你家阿森纳达是参赛队 农家菜是几年da那UC多送您极道少女敬爱的刹那间你加你家阿森纳达是参赛队 农家菜是几年da那UC多送您极道少女敬爱的刹那间你加你家阿森纳达是参赛队 农家菜是几年da那UC多送您极道少女敬爱的刹那间你加你家阿森纳达是参赛队 农家菜是几年da那UC多送您极道少女敬爱的刹那间你加你家阿森纳达是参赛队 农家菜是几年da那UC多送您极道少女敬爱的刹那间你加你家阿森纳达是参赛队 农家菜是几年da那UC多送您极道少女敬爱的刹那间你加你家阿森纳达是参赛队 农家菜是几年da那UC多送您极道少女敬爱的刹那间你加你家阿森纳达是参赛队 农家菜是几年da那UC多送您极道少女敬爱的刹那间你加你家阿森纳达是参赛队 农家菜是几年da那UC多送您极道少女敬爱的刹那间你加你家阿森纳达是参赛队 农家菜是几年da那UC多送您极道少女敬爱的刹那间你加你家阿森纳达是参赛队 农家菜是几年da那UC多送您极道少女敬爱的刹那间你加你家阿森纳达是参赛队 农家菜是几年da那UC多送您极道少女敬爱的刹那间你加你家阿森纳达是参赛队 农家菜是几年da那UC多送您极道少女敬爱的刹那间你加你家阿森纳达是参赛队 农家菜是几年da那UC多送您极道少女敬爱的刹那间你加你家阿森纳达是参赛队 农家菜是几年da那UC多送您极道少女敬爱的刹那间你加你家阿森纳达是参赛队 农家菜是几年da那UC多送您极道少女敬爱的刹那间你加你家阿森纳达是参赛队 农家菜是几年da那UC多送您极道少女敬爱的刹那间你加你家阿森纳达是参赛队 农家菜是几年da那UC多送您极道少女敬爱的刹那间你加你家阿森纳达是参赛队 农家菜是几年da那UC多送您极道少女敬爱的刹那间你加你家阿森纳达是参赛队 农家菜是几年da那UC多送您极道少女敬爱的刹那间你加你家阿森纳达是参赛队 农家菜是几年da那UC多送您极道少女敬爱的刹那间你加你家阿森纳达是参赛队 农家菜是几年da那UC多送您极道少女敬爱的刹那间你加你家阿森纳达是参赛队 农家菜是几年da那UC多送您极道少女敬爱的刹那间你加你家阿森纳达是参赛队 农家菜是几年赛队 农家菜是几年";
//        String c1 = RSAHelper.encryptByPublic(s);
//        String m1 = RSAHelper.decryptByPrivate(c1);
//        String c2 = RSAHelper.encryptByPrivate(s);
//        String m2 = RSAHelper.decryptByPublic(c2);
//        System.out.println(c1);
//        System.out.println(m1);
//        System.out.println(c2);
//        System.out.println(m2);

//        JwtBuilder builder= Jwts.builder()
//                .setId("888")
//                .setSubject("小白")
//                .setIssuedAt(new Date())//设置签发时间
//                .setSubject("subject")
//                .setExpiration(new Date(System.currentTimeMillis()+ 86400000))
//                .claim("tanghuachun", "hello")
//                .claim("roles", "admin")
//                .signWith(SignatureAlgorithm.HS256,"xiaocai");//设置签名秘钥
//        String token = builder.compact();
//        System.out.println(token);
//
//        Claims claims = Jwts.parser().setSigningKey("xiaocai").parseClaimsJws(token).getBody();
//        System.out.println(claims);

       // System.out.println("19092313561200bqv8b3tarmos000000".hashCode());
    }


}
