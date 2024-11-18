package com.imooc.bilibili.assistant.util;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA，非对称加密。外部用公钥加密数据，服务端再用私钥解密数据，安全性很高，但速度较慢。
 */
@Slf4j
public class RSAUtil {

    /**
     * RSA 公钥
     */
    private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCJ+ljqexJRHpfavddenqh3hZTCe26j4RErPKqEP51ykPExymWhhATd/B3JZE817X+vf56pRLn8BD51BNoijf5eplPf6rhzxp4mKd26mlBTySg4SeGhuIqRHrkJjnQBO9FGTR+4SAkynihM16ofzmSDMNL7Wj0qGzkhPUBnRwEIOwIDAQAB";

    /**
     * RSA 私钥
     */
    private static final String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIn6WOp7ElEel9q9116eqHeFlMJ7bqPhESs8qoQ/nXKQ8THKZaGEBN38HclkTzXtf69/nqlEufwEPnUE2iKN/l6mU9/quHPGniYp3bqaUFPJKDhJ4aG4ipEeuQmOdAE70UZNH7hICTKeKEzXqh/OZIMw0vtaPSobOSE9QGdHAQg7AgMBAAECgYBAnGPAwYrtQ5c2jklo/IDFH19uxuAji4Q/q/PpY+S1vlvDxhRbUvf4f1Pvd7KD/UgahXdlqlIxSkd5AqGnJrjjJbelm64vXIkRgt+KnTG2JQ9dNVUn66wSApcas3MmOm+ahkkC7Z609LKKO0r6A2QNMcnONMs23yXaNyd9+jGYOQJBANmBoCeBMq4ZCK1jR8HGP1ocgHKaFfTFdYxNXSBSyyHooN5+J3SbDy3u743RSJYX93Kh9rX2XyeWqRE8GqRCACUCQQCiZZhVIag6KJ8Xrzss2uZ0uOYj4xesi+16b+aAN0UFDCYnlppWrI4RTrhKPuZD72nm24YIsOcTukg3FJTT7sjfAkEAxfY0zb/JYKtoOmch6DuHbXyDa8rSMPWHBytilwjD2RjhUZ+ASaDXL/dIEMtqRTGLBcpjwTl7YYy+U/FgTh/EAQJAa/JSRsxfkYEs90ZgdFlxdMbvG425T9WXXpqBQlr6wvcnaYs/PjvnF3QXJisXTrMW1wC24JIOl9tsY0iIATQpSwJAaRs3lIvsCQHzA6q/2Gt+HInztUJaK4n4Ku2Ll/or3t5P1HSBlT04KrToNXP2eq0/rJJFdqrlWfZUygSA/Jc07Q==";

    public static void main(String[] args) throws Exception {
        // PePBZ5aZZtHrUy4RVGy1JTwlcf2qBn6Ux5YIrq7RZF01/tmp8+cCTAq6oJm8f6K7v6kj2OKPipFU932rDClJ0FL2Xwza+pORuMynIr2kv4HZwyW73edl9bc6MXhOguhYfXteNufmPfUmCuddhVPgRw0qdL4ubrYmHt5PKSGEicw=
        log.info(RSAUtil.encrypt("123456"));
    }

    public static String getPublicKeyStr() {
        return PUBLIC_KEY;
    }

    public static RSAPublicKey getPublicKey() throws Exception {
        byte[] decoded = Base64.decodeBase64(PUBLIC_KEY);
        return (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(decoded));
    }

    public static RSAPrivateKey getPrivateKey() throws Exception {
        byte[] decoded = Base64.decodeBase64(PRIVATE_KEY);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }

    public static RSAKey generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(1024, new SecureRandom());
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));
        String privateKeyString = new String(Base64.encodeBase64(privateKey.getEncoded()));
        return new RSAKey(privateKey, privateKeyString, publicKey, publicKeyString);
    }

    /**
     * 使用公钥加密
     */
    public static String encrypt(String source) throws Exception {
        byte[] decoded = Base64.decodeBase64(PUBLIC_KEY);
        RSAPublicKey rsaPublicKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(decoded));
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(1, rsaPublicKey);
        return Base64.encodeBase64String(cipher.doFinal(source.getBytes(StandardCharsets.UTF_8)));
    }

    public static Cipher getCipher() throws Exception {
        byte[] decoded = Base64.decodeBase64(PRIVATE_KEY);
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(decoded));
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(2, rsaPrivateKey);
        return cipher;
    }

    /**
     * 使用私钥解密
     */
    public static String decrypt(String text) throws Exception {
        Cipher cipher = getCipher();
        byte[] inputByte = Base64.decodeBase64(text.getBytes(StandardCharsets.UTF_8));
        return new String(cipher.doFinal(inputByte));
    }

    @Setter
    @Getter
    public static class RSAKey {
        public String publicKeyString;
        private RSAPrivateKey privateKey;
        private String privateKeyString;
        private RSAPublicKey publicKey;

        public RSAKey(RSAPrivateKey privateKey, String privateKeyString, RSAPublicKey publicKey, String publicKeyString) {
            this.privateKey = privateKey;
            this.privateKeyString = privateKeyString;
            this.publicKey = publicKey;
            this.publicKeyString = publicKeyString;
        }
    }

}
