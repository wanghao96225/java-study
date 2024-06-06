package com.wh.sharding.interceptor;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class RSAUtils {

    /** */
    /**
     * 加密算法RSA
     */
    public static final String KEY_ALGORITHM = "RSA";

    /** */
    /**
     * 签名算法
     */
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /** */
    /**
     * 获取公钥的key
     */
    private static final String PUBLIC_KEY = "RSAPublicKey";

    /** */
    /**
     * 获取私钥的key
     */
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    /** */
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /** */
    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    private static final String TYPE = "RSA/ECB/PKCS1Padding";

    /** *//**
     * <p>
     * 生成密钥对(公钥和私钥)
     * </p>
     *
     * @return
     * @throws Exception
     */
    /*public static Map<String, Object> genKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
		 // new String(Base64Util.encode(publicKey.getEncoded()));
		// 得到私钥字符串
		// String privateKeyString = new String(Base64Util.encode((privateKey.getEncoded())));

        System.out.println(publicKeyString);
        System.out.println(privateKeyString);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }*/

    /** */
    /**
     * <p>
     * 用私钥对信息生成数字签名
     * </p>
     *
     * @param data       已加密数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(data);
        return Base64.getEncoder().encodeToString(signature.sign());
    }

    /** */
    /**
     * <p>
     * 校验数字签名
     * </p>
     *
     * @param data      已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign      数字签名
     * @return
     * @throws Exception
     */
    public static boolean verify(byte[] data, String publicKey, String sign)
            throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicK);
        signature.update(data);
        return signature.verify(Base64.getDecoder().decode(sign));
    }

    /** */
    /**
     * <P>
     * 私钥解密
     * </p>
     *
     * @param encryptedData 已加密数据
     * @param privateKey    私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static String decryptByPrivateKey(byte[] encryptedData, String privateKey)
            throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(TYPE);
        cipher.init(Cipher.DECRYPT_MODE, privateK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return Base64.getEncoder().encodeToString(decryptedData);
    }

    /**
     * 公钥解密
     *
     * @param publicKeyText
     * @param text
     * @return
     * @throws Exception
     */
    public static String decryptByPublicKey(String publicKeyText, String text) throws Exception {
        byte[] pkeyBytes = Base64.getDecoder().decode(publicKeyText);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(pkeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] result = cipher.doFinal(Base64.getDecoder().decode(text));
        return new String(result);
    }

    /** */
    /**
     * <p>
     * 公钥解密
     * </p>
     *
     * @param encryptedData 已加密数据
     * @param publicKey     公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey)
            throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(TYPE);
        cipher.init(Cipher.DECRYPT_MODE, publicK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /** */
    /**
     * <p>
     * 公钥加密
     * </p>
     *
     * @param data      源数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static String encryptByPublicKey(byte[] data, String publicKey)
            throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(TYPE);
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        //return Base64Util.encode(encryptedData);
        return bytesToHexString(encryptedData);
    }

    /** */
    /**
     * <p>
     * 公钥加密
     * </p>
     *
     * @param data      源数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static String encryptByPublicKeyByBase(byte[] data, String publicKey)
            throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(TYPE);
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return Base64.getEncoder().encodeToString(encryptedData);
        // return bytesToHexString(encryptedData);
    }

    /**
     * 将字节数组转换成16进制字符串
     *
     * @param bytes 即将转换的数据
     * @return 16进制字符串
     */
    public static String bytesToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length);
        String temp = null;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(0xFF & bytes[i]);
            if (temp.length() < 2) {
                sb.append(0);
            }
            sb.append(temp);
        }
        return sb.toString();
    }

    /** */
    /**
     * <p>
     * 私钥加密
     * </p>
     *
     * @param data       源数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String privateKey)
            throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /** */
    /**
     * <p>
     * 获取私钥
     * </p>
     *
     * @param keyMap 密钥对
     * @return
     * @throws Exception
     */
    public static String getPrivateKey(Map<String, Object> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /** */
    /**
     * <p>
     * 获取公钥
     * </p>
     *
     * @param keyMap 密钥对
     * @return
     * @throws Exception
     */
    public static String getPublicKey(Map<String, Object> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static void main(String[] args) {
        try {
            //生成密钥
            // genKeyPair();
            String public_key_str = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQClFFlth4waf2R34//xspBWkRYtka2gN+iYgc5Fc0UoCXKW6HfP/6AdN1AsU2l/QGttnieSZeX0zauynMcoAxR/a6g4+jjaqwfIgmtCXKWcpV6ogLyiwO8Fhy18dkYz69EzmPcesOKsoMqtdQ4iQzDPKNA/iFp+rcra+KhZTaWo8QIDAQAB";
            String private_key_str = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKUUWW2HjBp/ZHfj//GykFaRFi2RraA36JiBzkVzRSgJcpbod8//oB03UCxTaX9Aa22eJ5Jl5fTNq7KcxygDFH9rqDj6ONqrB8iCa0JcpZylXqiAvKLA7wWHLXx2RjPr0TOY9x6w4qygyq11DiJDMM8o0D+IWn6tytr4qFlNpajxAgMBAAECgYAtqs16gW7+vZM7n3VklbNcX1K1VIGluxG7bt1zVQoio0px+Ol8Pwk6S90ABis/KsENWB02YL4W1PbUuI5XwVJf4sNAgLnY1YQZ9Nr3hrYqMrOetXKtDqMd/ERBMAUngasiYgAVpoejp1Pup2s2SglNObY0ZqPUY/5bqcZ9SRGMKQJBAOv9j16vgT3Uvzg8C3dKqjdlvkLZUbpjoACBwZkEqKhGWcN5YT4YqUQ5DiFAZC4K5u1WU8Ww6j0YmvgMuEuAgNsCQQCzE5YX/MY/A1E7p+Fx2yVc+WALxRBWGHLy8QL1VyBcqti+qpAHbcbxCPBNT21MFON5Pwm2LUdnFX3uj2LFyJEjAkBSiR8Aha9U+bhbKZz0/tcjguEVLj6IStml87vLEbcPC3PPiv84oZVpt+66MHQZKlTzwxKPfL2f2qAwivpV5b6rAkAT059SmJH6eAuOZ0/2oyTH3nRb2T2typcvfq+tKjBTEjddJNU9fVIBbdPtO1YGjmmBCxGwMeMt7ps6xnVRIJuxAkAZv/S9cC+xtZH1jFlQsZatgfxpY3olZfR6NR2oE8nsnyY9NcW/oXuerK9yMIlNnW5ATrYlIgr5u7KWfvQtTEaY";
            // String s = encryptByPublicKeyByBase("王荣昌".getBytes(), public_key_str);
            // System.out.println(s);
            // String res = new String(Base64.getDecoder().decode(RSAUtils.decryptByPrivateKey(Base64.getDecoder().decode(s),private_key_str)));
            // System.out.println(res);

            // String IMAGE_PRIVATEKEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALwNwRLuWJo7gzOIJDQSa2tnsu52bEESmI1yC+/pPEQ4tqXiuyhAqctwFXY97qLakUBdoGs/4CcjU9yTVjFMBE5kyaQncF83TwIoDyZlS5VoYL8gcNXt9RTemlURVYXpu1WxoOD60X6T24uUrko4AiEpu5E2HtmNypELxlJUPWqrAgMBAAECgYA3Mv5adTXyx8dCSBMAaeu4Ei9WidJK44997V0NpfO3vsH2PXBcZ2avvY2/MHystuVt9E2TTaOZOWhzVZg2Ti7w5j/L3P9WpgSB4M4Rpy2WaI31cLLu9cmRgfuQCNHJKwevlp9h144F65XJpHJjkCyP/rW5QvPRtoA/d0ujA4C10QJBAPY+HW5wl139ALfRVJx/GHdxMNLsJskJPwLAT/+vJu5eVerF3d/nQgpuDyEmNN0GvNhrZ/liP1i9IWBsy26TlJMCQQDDgV75r2TEt8wp4mqKP9ufm4tkmidsPlAAsx8G8/ETffekROv9ksWjSdTxvcQ5Z7AFfVq4NFKiXMOG86pwqniJAkAgvMda7HsjUsSqq5jtKXSORK0yDZVmeuU2r2yzWIz7ee1ARkgmQW+lRVabmKHElOW8fCMZo7c6TALI+A3fyJ0jAkEArgw2aUJkC101PPR8tKc09lqbNeymvA6dWoIcVSxGqnuyQ2O4U/6eREgucyfl0NUmNzzpzqOGDVVYHIRAcBzTQQJACt5qNapy/2id4m4KigEcEuNAVmjRL82fXeRdoNbCfch7xWDnszPR1vSXxR1uE2lXD0E2SBbRXOJSwuZpVhDPKw==";
            // String IMAGE_PUBLICKEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC8DcES7liaO4MziCQ0EmtrZ7LudmxBEpiNcgvv6TxEOLal4rsoQKnLcBV2Pe6i2pFAXaBrP+AnI1Pck1YxTAROZMmkJ3BfN08CKA8mZUuVaGC/IHDV7fUU3ppVEVWF6btVsaDg+tF+k9uLlK5KOAIhKbuRNh7ZjcqRC8ZSVD1qqwIDAQAB";
            //
            // String str = "IfdCNJNKQzzG0bspKMPPmFPQ6lC82HeM5+fnBVym+6wJw24T51WgPY81RE4JTmFo95LttN0EBZPSPs2awj1pKYtC4o51vXSOI/mtt0RbUKwcvOXDZBzcHFv/kmYgGK/NakU0k0GCGwagbUnMf7oubiX0STNBOwZ+FNbOsTfxbK4eGGJ5O5447Q38GKvw+YZswwjCwb7h0BgGjwwHy2fRfllbd6c8fP1zV3ENIVBwIK03jjlodNdK7uaSBEo90YSuYVN7t5hmmHEx9XTU1vC7de3oHYXlGS8ospkgo+DPzaNGpmGz940I3jVXzOkhlTJ6c3v4FTH3byl/Novc9EU1409JcKS0sq/eYRSgGtvS8C/baxXknjl3eyoGBK5TfZ+Y32FJqkvof5j+NR98oA4zHClOLRuvqtuVET6kP++kut+JvnRObUJUTuTVn716Jpr50QRAcAbnLEvzyBy5ln20fxNn2dTUokJbyB4oZZFP+oYw61ysrV8pXk+tclT2r/Sns41oXt9oniKsGqIgxDmCQCOg6rghDPKVoYJGVIbCS8O1iB+KQ73B0TCfSlfLpZXbnsK91rAA9t3bOvBc8QQ0HXkFY9bDzOgntf8ftWvl6Eq9wA4Z+o63dCuJXI3fVWv5nrTllK//PQ6wI2od0cVsJ96vXZC1nXlKZZWfQaaceOKf4Wcm6qWIUzG0qy35bZyR1CxWwzMUve1e2dgzeuT23XOsJvCyPMf4JTpkm+Xhr7gpqoRBRsVLi8mU1ObIYCtWbGs87L6FdFrhRljXw9BPOXJXczqXupQFFujlb4vnte8/3JT0v56qh/JKv6R0Fj4MsDieIqE9RKZrLt85ulVwoWTWuhTF1VdtBx0062tcI7/HG539S2X30YGTKpPLWAL/zsmSYoFR6uZm0nM+gwHA8kR+EYK+XAVvPNcb/ULKkhUq0ZaaT3Rbsz04UMmXiVFanOtmDIAsAsnq8fAuuInpzn/vhqEXofGADtrgg5SlMAg5Czw3koycIR1YxuQBxnNjGRpJA9pJR3BMy3/Fyus8GD4mGIwnIwfHKvyuWuYTx2LzvKQRQa/qf3u2w0Wl3mC6is9hubvPg/njv/PiB7mJbz/Ec3+r1nfPXBwAvFM7gj59KEhvJuFo04HwjEaSl7XZGMh3YKlsTZnmRrArBNZzhBGxt3mRUZ0sZ93Yev2QUaw=";
            // String res = new String(Base64.getDecoder().decode(RSAUtils.decryptByPrivateKey(Base64.getDecoder().decode(str), IMAGE_PRIVATEKEY)));
            // System.out.println(res);
            //
            // String ss = RSAUtils.encryptByPublicKey("abcdef".getBytes(), IMAGE_PUBLICKEY);
            // System.out.println("123--  "+ss);
            // byte[] s = decryptByPublicKey(ss.getBytes(),IMAGE_PUBLICKEY);
            // System.out.println(new String(s));
            // String res1 = new String(Base64.getDecoder().decode(RSAUtils.decryptByPrivateKey(ss.getBytes(),private_key_str)));
            // System.out.println(res1);
            List<String> strings = Arrays.asList("leader", "phone", "leaderPhone", "leaderEmail", "companyId", "emails", "phonenumber", "nickName", "password", "vin", "engineCode", "owner", "ownerIdNumber", "ownerPhone", "emergencyContactName", "emergencyContactPhone", "plateNumber", "driveLicenseNumber", "numberPlate", "cellSpecCode", "cellShape", "ratedVoltage", "cellModel", "moduleCode");
            System.out.println(strings.stream().distinct().collect(Collectors.joining("\",\"", "\"", "\"")));
        } catch (Exception e) {

        }
    }

}