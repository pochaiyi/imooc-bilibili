package com.imooc.bilibili.service;

import com.imooc.bilibili.assistant.domain.UserFollowingGroup;
import com.imooc.bilibili.dao.UserFollowingGroupDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserFollowingGroupService {

    @Autowired
    private UserFollowingGroupDao userFollowingGroupDao;

    public UserFollowingGroup getByType(String type) {
        return userFollowingGroupDao.getByType(type);
    }

    public UserFollowingGroup getById(Long id) {
        return userFollowingGroupDao.getById(id);
    }

    public ArrayList<UserFollowingGroup> getByUserId(Long userId) {
        return userFollowingGroupDao.getByUserId(userId);
    }

    public void addUserFollowingGroup(UserFollowingGroup followingGroup) {
        userFollowingGroupDao.addUserFollowingGroup(followingGroup);
    }

    public List<UserFollowingGroup> getUserFollowingGroups(Long userId) {
        return userFollowingGroupDao.getUserFollowingGroups(userId);
    }

}
