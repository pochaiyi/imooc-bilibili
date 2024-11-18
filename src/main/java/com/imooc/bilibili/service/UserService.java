package com.imooc.bilibili.service;

import com.alibaba.fastjson.JSONObject;
import com.imooc.bilibili.assistant.Constant.AuthRoleConstant;
import com.imooc.bilibili.assistant.Constant.UserConstant;
import com.imooc.bilibili.assistant.domain.PageResult;
import com.imooc.bilibili.assistant.domain.RefreshToken;
import com.imooc.bilibili.assistant.domain.User;
import com.imooc.bilibili.assistant.domain.UserInfo;
import com.imooc.bilibili.assistant.exception.ConditionException;
import com.imooc.bilibili.assistant.util.MD5Util;
import com.imooc.bilibili.assistant.util.RSAUtil;
import com.imooc.bilibili.assistant.util.TokenUtil;
import com.imooc.bilibili.dao.UserDao;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserRoleService userRoleService;

    /**
     * 检查手机号，解密密码，然后保存user和userInfo
     */
    public void addUser(User user) {
        // 检查手机号
        String phone = user.getPhone();
        String email = user.getEmail();
        if (StringUtil.isNullOrEmpty(phone) && StringUtil.isNullOrEmpty(email)) {
            throw new ConditionException("手机号或邮箱不能为空");
        }
        User dbUser = getUserByPhoneAndEmail(phone, email);
        if (dbUser != null) {
            throw new ConditionException("手机号和邮箱已经注册");
        }
        // 处理密码
        String password = user.getPassword(); // 加密后的密码
        String rawPassword; // 密码原文
        try {
            rawPassword = RSAUtil.decrypt(password); // 解密密码
        } catch (Exception e) {
            throw new ConditionException("密码解密失败");
        }
        Date now = new Date();
        String salt = String.valueOf(now.getTime());
        String MD5Password = MD5Util.sign(rawPassword, user.getSalt(), "UTF-8"); // 密码存库前先MD5加密
        // 添加 User
        user.setPassword(MD5Password);
        user.setSalt(salt);
        user.setCreateTime(now);
        userDao.addUser(user);
        // 添加 UserInfo
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setNick(UserConstant.DEFAULT_NICK + "_" + phone);
        userInfo.setGender(UserConstant.GENDER_UNKNOWN);
        userInfo.setBirth(UserConstant.DEFAULT_BIRTH);
        userInfo.setCreateTime(now);
        userDao.addUserInfo(userInfo);
        // 设置用户默认角色
        userRoleService.addUserDefaultRole(user.getId(), AuthRoleConstant.ROLE_CODE_LV0);
    }

    private User getUserByPhoneAndEmail(String phone, String email) {
        return userDao.getUserByPhoneAndEmail(phone, email);
    }

    public String login(User user) throws Exception {
        User dbUser = checkPhoneEmailPassword(user);
        // 返回令牌
        return TokenUtil.generateToken(dbUser.getId());
    }

    public User getUserById(Long userId) {
        User user = userDao.getUserById(userId);
        UserInfo userInfo = userDao.getUserInfoById(userId);
        user.setUserInfo(userInfo);
        return user;
    }

    public void updateUserInfo(UserInfo userInfo) {
        userInfo.setUpdateTime(new Date());
        userDao.updateUserInfo(userInfo);
    }

    public void updateUser(User user) {
        user.setUpdateTime(new Date());
        userDao.updateUser(user);
    }

    public ArrayList<UserInfo> getUserInfosByUserIds(HashSet<Long> userIdSet) {
        return userDao.getUserInfosByFollowingIdSet(userIdSet);
    }

    public PageResult<UserInfo> pageListUserInfos(JSONObject params) {
        Integer no = params.getInteger("no");
        Integer size = params.getInteger("size");
        params.put("start", (no - 1) * size);
        params.put("limit", size);
        // 满足条件的总数
        Integer total = userDao.pageCountUserInfos(params);
        params.put("total", total);
        List<UserInfo> userInfoList = new ArrayList<>();
        if (total > 0) {
            // 查询用户信息
            userInfoList = userDao.pageListUserInfos(params);
        }
        return new PageResult<>(total, userInfoList);
    }

    public Map<String, String> loginByDts(User user) throws Exception {
        User dbUser = checkPhoneEmailPassword(user);
        // 生成双令牌
        String accessToken = TokenUtil.generateToken(dbUser.getId());
        RefreshToken refreshToken = new RefreshToken(
                dbUser.getId(), TokenUtil.generateRefreshToken(dbUser.getId()));
        // 更新数据库
        userDao.deleteRefreshToken(refreshToken);
        userDao.addRefreshToken(refreshToken);
        // 生成结果集
        Map<String, String> result = new HashMap<>();
        result.put("accessToken", accessToken);
        result.put("refreshToken", refreshToken.getRefreshToken());
        return result;
    }

    private User checkPhoneEmailPassword(User user) {
        // 检查手机号和邮箱
        String phone = user.getPhone();
        String email = user.getEmail();
        if (StringUtil.isNullOrEmpty(phone) && StringUtil.isNullOrEmpty(email)) {
            throw new ConditionException("手机号和邮箱不能都为空");
        }
        // 通过手机号或邮箱查询用户
        User dbUser;
        dbUser = phone != null ? userDao.getUserByPhone(phone) : userDao.getUserByEmail(email);
        if (dbUser == null) {
            throw new ConditionException("用户不存在");
        }
        // 解密密码
        String rawPassword;
        try {
            rawPassword = RSAUtil.decrypt(user.getPassword()); // 密码原文
        } catch (Exception e) {
            throw new ConditionException("密码解析失败");
        }
        // 验证密码
        if (MD5Util.verify(rawPassword, dbUser.getPassword(), dbUser.getSalt(), "UTF-8")) {
            throw new ConditionException("密码错误");
        }
        return dbUser;
    }

    public void logout(Long userId, String refreshToken) {
        userDao.deleteRefreshToken(new RefreshToken(userId, refreshToken));
    }

    public String refreshAccessToken(String refreshToken) throws Exception {
        RefreshToken dbRefreshToken = userDao.getRefreshToken(refreshToken);
        // 验证RefreshToken
        if (dbRefreshToken != null) {
            Long dbTokenUserId = TokenUtil.verifyToken(dbRefreshToken.getRefreshToken());
            return TokenUtil.generateToken(dbTokenUserId);
        } else {
            throw new ConditionException("555", "RefreshToken无效");
        }
    }

    public List<UserInfo> getUserInfoByUserIds(Collection<Long> userIds) {
        HashSet<Long> userIdSet = new HashSet<>(userIds);
        return userDao.getUserInfoByUserIds(userIdSet);
    }

    public RefreshToken getRefreshToken(String refreshToken) {
        return userDao.getRefreshToken(refreshToken);
    }

}
