package com.imooc.bilibili.assistant.util;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * AES，对称加密算法。加密和解密使用相同的密钥，速度很快，适合数据传输频繁的场合。
 */
public class AESUtil {

    private static final String KEY_ALGORITHM = "AES";

    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private final Cipher decryptCipher;

    private final Cipher encryptCipher;

    private final String seed;

    public AESUtil(String seed) throws Exception {
        this.seed = seed;
        decryptCipher = Cipher.getInstance(KEY_ALGORITHM);
        encryptCipher = Cipher.getInstance(KEY_ALGORITHM);
        decryptCipher.init(Cipher.DECRYPT_MODE, this.getSecretKey());
        encryptCipher.init(Cipher.ENCRYPT_MODE, this.getSecretKey());
    }

    /**
     * 解密
     */
    public String decrypt(String content) throws Exception {
        byte[] bytes = Base64.decodeBase64(content);
        byte[] result = decryptCipher.doFinal(bytes);
        return new String(result, StandardCharsets.UTF_8);
    }

    /**
     * 加密
     */
    public String encrypt(String content) throws Exception {
        byte[] result = encryptCipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeBase64String(result);
    }

    public SecretKey getSecretKey() throws Exception {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(seed.getBytes());
        KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
        kg.init(128, random);
        return kg.generateKey();
    }

}