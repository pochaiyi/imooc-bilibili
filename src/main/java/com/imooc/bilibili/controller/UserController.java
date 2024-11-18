package com.imooc.bilibili.controller;

import com.alibaba.fastjson.JSONObject;
import com.imooc.bilibili.assistant.domain.JsonResponse;
import com.imooc.bilibili.assistant.domain.PageResult;
import com.imooc.bilibili.assistant.domain.User;
import com.imooc.bilibili.assistant.domain.UserInfo;
import com.imooc.bilibili.assistant.exception.ConditionException;
import com.imooc.bilibili.assistant.util.RSAUtil;
import com.imooc.bilibili.controller.support.UserSupport;
import com.imooc.bilibili.service.UserFollowingService;
import com.imooc.bilibili.service.UserService;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserFollowingService userFollowingService;

    /**
     * 请求RSA公钥，客户端用来加密传输数据
     */
    @GetMapping("/rsa-pks")
    public JsonResponse<String> getRsaPublicKey() {
        return new JsonResponse<>(RSAUtil.getPublicKeyStr());
    }

    /**
     * 用户注册
     */
    @PostMapping("/users")
    public JsonResponse<String> addUser(@RequestBody User user) {
        userService.addUser(user);
        return JsonResponse.success();
    }

    /**
     * 用户登录
     */
    @PostMapping("/user-tokens")
    public JsonResponse<String> login(@RequestBody User user) throws Exception {
        return new JsonResponse<>(userService.login(user));
    }

    /**
     * 更新用户
     */
    @PutMapping("/users")
    public JsonResponse<String> updateUsers(@RequestBody User user) {
        Long userId = userSupport.getCurrentUserId();
        user.setId(userId);
        userService.updateUser(user);
        return JsonResponse.success();
    }

    /**
     * 查询用户信息
     */
    @GetMapping("/users")
    public JsonResponse<User> getUserInfo() {
        Long userId = userSupport.getCurrentUserId();
        return new JsonResponse<>(userService.getUserById(userId));
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/user-infos")
    public JsonResponse<String> updateUserInfos(@RequestBody UserInfo userInfo) {
        Long userId = userSupport.getCurrentUserId();
        userInfo.setUserId(userId);
        userService.updateUserInfo(userInfo);
        return JsonResponse.success();
    }

    /**
     * 分页查询用户信息
     */
    @GetMapping("/user-infos")
    public JsonResponse<PageResult<UserInfo>> getUserInfos(
            @RequestParam Integer no,
            @RequestParam Integer size,
            @RequestParam(required = false) String nick
    ) {
        Long userId = userSupport.getCurrentUserId();
        JSONObject params = new JSONObject();
        params.put("no", no);
        params.put("size", size);
        params.put("nick", nick);
        params.put("userId", userId);
        PageResult<UserInfo> result = userService.pageListUserInfos(params);
        // 检查关注状态
        if (result.getTotal() > 0) {
            userFollowingService.checkedFollowingStatus(result.getList(), userId);
        }
        return new JsonResponse<>(result);
    }

    /**
     * 用户登录（DoubleToken）
     */
    @PostMapping("/user-dts")
    public JsonResponse<Map<String, String>> loginForDts(@RequestBody User user) throws Exception {
        Map<String, String> result = userService.loginByDts(user);
        return new JsonResponse<>(result);
    }

    /**
     * 退出登录（DoubleToken）
     */
    @DeleteMapping("/refresh-tokens")
    public JsonResponse<String> logout(@RequestHeader("refreshToken") String refreshToken) {
        Long userId = userSupport.getCurrentUserId();
        userService.logout(userId, refreshToken);
        return JsonResponse.success();
    }

    /**
     * 刷新AccessToken
     * 前端发现AccessToken过期，然后访问该接口尝试获得新AccessToken
     */
    @PostMapping("/refresh-tokens")
    public JsonResponse<String> refreshAccessToken(@RequestHeader("refreshToken") String refreshToken) throws Exception {
        if (StringUtil.isNullOrEmpty(refreshToken)) {
            throw new ConditionException("555", "RefreshToken为空");
        } else {
            return new JsonResponse<>(userService.refreshAccessToken(refreshToken));
        }
    }

}
