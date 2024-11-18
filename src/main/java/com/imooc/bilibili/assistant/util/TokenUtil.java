package com.imooc.bilibili.assistant.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.imooc.bilibili.assistant.exception.ConditionException;

import java.util.Calendar;
import java.util.Date;

public class TokenUtil {

    public static String generateToken(Long userId) throws Exception {
        // JWT签名的加密算法
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 30); // 30分钟后过期
        return JWT.create()
                .withKeyId(userId.toString())
                .withIssuer("imooc-bilibili")
                .withExpiresAt(calendar.getTime()) // 设置过期时间
                .sign(algorithm);
    }

    public static Long verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
            DecodedJWT jwt = JWT.require(algorithm).build().verify(token); // 解密JWT，得到加密前的原文
            String userId = jwt.getKeyId();
            return Long.parseLong(userId);
        } catch (TokenExpiredException e) {
            throw new ConditionException("555", "Token无效或已失效");
        } catch (Exception e) {
            throw new ConditionException("非法Token");
        }
    }

    public static String generateRefreshToken(Long userId) throws Exception {
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 7); // 7天后过期
        return JWT.create()
                .withKeyId(userId.toString())
                .withIssuer("imooc-bilibili")
                .withExpiresAt(calendar.getTime())
                .sign(algorithm);
    }

}
