package com.imooc.bilibili.dao;

import com.alibaba.fastjson.JSONObject;
import com.imooc.bilibili.assistant.domain.RefreshToken;
import com.imooc.bilibili.assistant.domain.User;
import com.imooc.bilibili.assistant.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper
public interface UserDao {

    User getUserByPhone(String phone);

    Integer addUser(User user);

    Integer addUserInfo(UserInfo userInfo);

    User getUserById(Long id);

    UserInfo getUserInfoById(Long userId);

    Integer updateUserInfo(UserInfo userInfo);

    User getUserByEmail(String email);

    Integer updateUser(User user);

    ArrayList<UserInfo> getUserInfosByFollowingIdSet(HashSet<Long> userIdSet);

    Integer pageCountUserInfos(JSONObject params);

    List<UserInfo> pageListUserInfos(JSONObject params);

    User getUserByPhoneAndEmail(String phone, String email);

    Integer deleteRefreshToken(RefreshToken refreshToken);

    Integer addRefreshToken(RefreshToken refreshToken);

    RefreshToken getRefreshToken(String refreshToken);

    List<UserInfo> getUserInfoByUserIds(Set<Long> userIdSet);

}
