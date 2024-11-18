package com.imooc.bilibili.controller;

import com.imooc.bilibili.assistant.domain.JsonResponse;
import com.imooc.bilibili.assistant.domain.auth.UserAuthorities;
import com.imooc.bilibili.controller.support.UserSupport;
import com.imooc.bilibili.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserAuthController {

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private UserSupport userSupport;

    /**
     * 查询用户权限
     */
    @GetMapping("/user-authorities")
    public JsonResponse<UserAuthorities> getUserAuthorities() {
        Long userId = userSupport.getCurrentUserId();
        UserAuthorities userAuthorities = userAuthService.getUserAuthorities(userId);
        return new JsonResponse<>(userAuthorities);
    }

}
