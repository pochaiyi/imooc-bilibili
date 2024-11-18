package com.imooc.bilibili.controller;

import com.imooc.bilibili.assistant.Constant.AuthRoleConstant;
import com.imooc.bilibili.assistant.aspect.annotation.ApiLimitedRole;
import com.imooc.bilibili.assistant.aspect.annotation.DataLimitedRole;
import com.imooc.bilibili.assistant.domain.JsonResponse;
import com.imooc.bilibili.assistant.domain.UserMoment;
import com.imooc.bilibili.controller.support.UserSupport;
import com.imooc.bilibili.service.UserMomentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserMomentController {

    @Autowired
    private UserMomentService userMomentService;

    @Autowired
    private UserSupport userSupport;

    /**
     * 发布动态
     */
    @PostMapping("/user-moments")
    @ApiLimitedRole(limitedRoleCodeList = {AuthRoleConstant.ROLE_CODE_LV0}) // 限制Lv0等级的用户发布动态
    @DataLimitedRole // 限制Lv1等级的用户只能发布专栏动态
    public JsonResponse<String> addUserMoment(@RequestBody UserMoment userMoment) throws Exception {
        Long userId = userSupport.getCurrentUserId();
        userMoment.setUserId(userId);
        userMomentService.addUserMoment(userMoment);
        return JsonResponse.success();
    }

    /**
     * 查询动态
     */
    @GetMapping("/user-subscribed-moments")
    public JsonResponse<List<UserMoment>> getUserSubscribedMoments() {
        Long userId = userSupport.getCurrentUserId();
        List<UserMoment> momentList = userMomentService.getUserSubscribedMoments(userId);
        return new JsonResponse<>(momentList);
    }

}
