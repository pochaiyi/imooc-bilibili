package com.imooc.bilibili.assistant.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * MD5，单向加密算法。加密不需密钥，无法还原内容，速度很快，安全性低，通常需要搭配salt随机值来提高安全性。
 */
public class MD5Util {

    /**
     * 加密
     *
     * @param salt    随机盐值
     * @param charset 字符集
     */
    public static String sign(String content, String salt, String charset) {
        content = content + salt;
        return DigestUtils.md5Hex(getContentBytes(content, charset));
    }

    /**
     * 验证 content 加密结果是否与 sign 相等
     *
     * @param salt    随机盐值
     * @param charset 字符集
     */
    public static boolean verify(String content, String sign, String salt, String charset) {
        content = content + salt;
        String mySign = DigestUtils.md5Hex(getContentBytes(content, charset));
        return mySign.equals(sign);
    }

    private static byte[] getContentBytes(String content, String charset) {
        if (!"".equals(charset)) {
            try {
                return content.getBytes(charset);
            } catch (UnsupportedEncodingException var3) {
                throw new RuntimeException("MD5加密过程发生错误，不支持给定的字符集");
            }
        } else {
            return content.getBytes();
        }
    }

    /**
     * 对文件进行MD5加密
     */
    public static String getMd5Str(MultipartFile file) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            int len;
            byte[] buffer = new byte[1024 * 2];
            try (InputStream fis = file.getInputStream()) {
                while ((len = fis.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
            }
            return DigestUtils.md5Hex(baos.toByteArray());
        }
    }

}
