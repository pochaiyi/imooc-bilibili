package com.imooc.bilibili.service;

import com.imooc.bilibili.assistant.Constant.UserConstant;
import com.imooc.bilibili.assistant.domain.User;
import com.imooc.bilibili.assistant.domain.UserFollowing;
import com.imooc.bilibili.assistant.domain.UserFollowingGroup;
import com.imooc.bilibili.assistant.domain.UserInfo;
import com.imooc.bilibili.assistant.exception.ConditionException;
import com.imooc.bilibili.dao.UserFollowingDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Service
public class UserFollowingService {

    @Autowired
    private UserFollowingDao userFollowingDao;

    @Autowired
    private UserService userService;

    @Autowired
    private UserFollowingGroupService userFollowingGroupService;

    /**
     * 关注用户
     */
    @Transactional
    public void addUserFollowing(UserFollowing userFollowing) {
        // 关注分组
        Long groupId = userFollowing.getGroupId();
        if (groupId == null) {
            UserFollowingGroup followingGroup = userFollowingGroupService.getByType(UserConstant.DEFAULT_USER_FOLLOWING_TYPE);
            userFollowing.setGroupId(followingGroup.getId());
        } else {
            UserFollowingGroup userFollowingGroup = userFollowingGroupService.getById(groupId);
            if (userFollowingGroup == null) {
                throw new ConditionException("关注分组不存在");
            }
        }
        // 关注用户
        Long followingId = userFollowing.getFollowingId();
        User followingUser = userService.getUserById(followingId);
        if (followingUser == null) {
            throw new ConditionException("关注用户不存在");
        }
        // 先删再加，包含更新功能
        userFollowing.setCreateTime(new Date());
        userFollowingDao.deleteUserFollowing(userFollowing.getUserId(), followingId);
        userFollowingDao.addUserFollowing(userFollowing);
    }

    /**
     * 查询关注列表
     */
    public List<UserFollowingGroup> getUserFollowings(Long userId) {
        // 查询关注列表
        List<UserFollowing> followingList = userFollowingDao.getUserFollowings(userId);
        // 查询用户信息
        HashSet<Long> followingIdSet = new HashSet<>();
        for (UserFollowing userFollowing : followingList) {
            followingIdSet.add(userFollowing.getFollowingId());
        }
        List<UserInfo> userInfoList = new ArrayList<>();
        if (!followingIdSet.isEmpty()) {
            userInfoList = userService.getUserInfosByUserIds(followingIdSet);
        }
        // 设置用户信息
        for (UserFollowing userFollowing : followingList) {
            for (UserInfo userInfo : userInfoList) {
                if (userInfo.getUserId().equals(userFollowing.getFollowingId())) {
                    userFollowing.setUserInfo(userInfo);
                }
            }
        }
        // 查询分组列表
        ArrayList<UserFollowingGroup> groupList = userFollowingGroupService.getByUserId(userId);
        // 全部关注分组
        UserFollowingGroup allGroup = new UserFollowingGroup();
        allGroup.setName(UserConstant.USER_FOLLOWING_GROUP_ALL_NAME);
        allGroup.setFollowingUserInfoList(userInfoList);
        // 按分组包装结果
        ArrayList<UserFollowingGroup> result = new ArrayList<>();
        result.add(allGroup);
        for (UserFollowingGroup group : groupList) {
            ArrayList<UserInfo> groupUserInfoList = new ArrayList<>();
            for (UserFollowing userFollowing : followingList) {
                if (userFollowing.getGroupId().equals(group.getId())) {
                    groupUserInfoList.add(userFollowing.getUserInfo());
                }
            }
            group.setFollowingUserInfoList(groupUserInfoList);
            result.add(group);
        }
        return result;
    }

    /**
     * 查询粉丝列表
     */
    public List<UserFollowing> getUserFans(Long userId) {
        // 查询粉丝列表
        List<UserFollowing> fanList = userFollowingDao.getUserFans(userId);
        // 查询粉丝信息
        HashSet<Long> fanIdSet = new HashSet<>();
        for (UserFollowing fan : fanList) {
            fanIdSet.add(fan.getUserId());
        }
        List<UserInfo> userInfoList = new ArrayList<>();
        if (!fanIdSet.isEmpty()) {
            userInfoList = userService.getUserInfosByUserIds(fanIdSet);
        }
        // 查询关注列表
        List<UserFollowing> followingList = userFollowingDao.getUserFollowings(userId);

        for (UserFollowing fan : fanList) {
            // 设置粉丝信息
            for (UserInfo userInfo : userInfoList) {
                if (userInfo.getUserId().equals(fan.getUserId())) {
                    fan.setUserInfo(userInfo);
                }
            }
            // 设置互粉状态
            for (UserFollowing following : followingList) {
                if (following.getFollowingId().equals(fan.getUserId()))
                    fan.getUserInfo().setFollowed(true);
            }
        }
        return fanList;
    }

    public Long addUserFollowingGroup(UserFollowingGroup followingGroup) {
        followingGroup.setCreateTime(new Date());
        followingGroup.setType(UserConstant.DEFINE_USER_FOLLOWING_TYPE);
        userFollowingGroupService.addUserFollowingGroup(followingGroup);
        return followingGroup.getId();
    }

    public List<UserFollowingGroup> getUserFollowingGroups(Long userId) {
        return userFollowingGroupService.getUserFollowingGroups(userId);
    }

    public void checkedFollowingStatus(List<UserInfo> userInfoList, Long userId) {
        List<UserFollowing> userFollowingList = userFollowingDao.getUserFollowings(userId);
        for (UserInfo userInfo : userInfoList) {
            for (UserFollowing userFollowing : userFollowingList) {
                if (userFollowing.getFollowingId().equals(userInfo.getUserId())) {
                    userInfo.setFollowed(true);
                }
            }
        }
    }

}