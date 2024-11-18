package com.imooc.bilibili.dao;

import com.imooc.bilibili.assistant.domain.UserFollowingGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface UserFollowingGroupDao {

    UserFollowingGroup getByType(String type);

    UserFollowingGroup getById(Long id);

    ArrayList<UserFollowingGroup> getByUserId(Long userId);

    Integer addUserFollowingGroup(UserFollowingGroup followingGroup);

    List<UserFollowingGroup> getUserFollowingGroups(Long userId);

}
