package com.imooc.bilibili.assistant.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Setter
@Getter
public class UserFollowingGroup {

    private Long id;

    private Long userId;

    private String name;

    private String type;

    private Date createTime;

    private Date updateTime;

    /**
     * 属于该分组的用户信息列表
     */
    private List<UserInfo> followingUserInfoList;

}
