package com.imooc.bilibili.controller;

import com.imooc.bilibili.assistant.domain.JsonResponse;
import com.imooc.bilibili.assistant.domain.UserFollowing;
import com.imooc.bilibili.assistant.domain.UserFollowingGroup;
import com.imooc.bilibili.controller.support.UserSupport;
import com.imooc.bilibili.service.UserFollowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserFollowingController {

    @Autowired
    private UserFollowingService userFollowingService;

    @Autowired
    private UserSupport userSupport;

    /**
     * 关注用户
     */
    @PostMapping("/user-followings")
    public JsonResponse<String> addUserFollowings(@RequestBody UserFollowing userFollowing) {
        Long userId = userSupport.getCurrentUserId();
        userFollowing.setUserId(userId);
        userFollowingService.addUserFollowing(userFollowing);
        return JsonResponse.success();
    }

    /**
     * 查询关注列表
     */
    @GetMapping("/user-followings")
    public JsonResponse<List<UserFollowingGroup>> getUserFollowings() {
        Long userId = userSupport.getCurrentUserId();
        List<UserFollowingGroup> userFollowings = userFollowingService.getUserFollowings(userId);
        return new JsonResponse<>(userFollowings);
    }

    /**
     * 查询粉丝列表
     */
    @GetMapping("/user-fans")
    public JsonResponse<List<UserFollowing>> getUserFans() {
        Long userId = userSupport.getCurrentUserId();
        List<UserFollowing> userFans = userFollowingService.getUserFans(userId);
        return new JsonResponse<>(userFans);
    }

    /**
     * 添加关注分组
     */
    @PostMapping("/user-following-groups")
    public JsonResponse<Long> addUserFollowingGroup(@RequestBody UserFollowingGroup followingGroup) {
        Long userId = userSupport.getCurrentUserId();
        followingGroup.setUserId(userId);
        Long groupId = userFollowingService.addUserFollowingGroup(followingGroup);
        return new JsonResponse<>(groupId);
    }

    /**
     * 查询关注分组
     */
    @GetMapping("/user-following-groups")
    public JsonResponse<List<UserFollowingGroup>> getUserFollowingGroups() {
        Long userId = userSupport.getCurrentUserId();
        return new JsonResponse<>(userFollowingService.getUserFollowingGroups(userId));
    }

}
