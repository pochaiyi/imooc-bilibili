package com.imooc.bilibili.dao;

import com.imooc.bilibili.assistant.domain.UserFollowing;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserFollowingDao {

    Integer deleteUserFollowing(Long userId, Long followingId);

    Integer addUserFollowing(UserFollowing userFollowing);

    List<UserFollowing> getUserFollowings(Long userId);

    List<UserFollowing> getUserFans(Long userId);

}
