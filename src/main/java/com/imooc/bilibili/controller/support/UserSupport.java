package com.imooc.bilibili.controller.support;

import com.imooc.bilibili.assistant.domain.RefreshToken;
import com.imooc.bilibili.assistant.exception.ConditionException;
import com.imooc.bilibili.assistant.util.TokenUtil;
import com.imooc.bilibili.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class UserSupport {

    @Autowired
    private UserService userService;

    /**
     * 解析JWT的UserId
     */
    public Long getCurrentUserId() {
        // RequestContextHolder是Spring提供的对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        // AccessToken
        String accessToken = attributes.getRequest().getHeader("accessToken");
        Long userId = TokenUtil.verifyToken(accessToken);
        if (userId < 0) {
            throw new ConditionException("非法用户");
        }
        // RefreshToken
        this.verifyRefreshToken(userId);
        return userId;
    }

    public void verifyRefreshToken(Long userId) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        String refreshToken = attributes.getRequest().getHeader("refreshToken");
        Long rUserId = TokenUtil.verifyToken(refreshToken);
        RefreshToken dbRefreshToken = userService.getRefreshToken(refreshToken);
        if (dbRefreshToken == null || !rUserId.equals(userId)) {
            throw new ConditionException("非法用户");
        }
    }

}
