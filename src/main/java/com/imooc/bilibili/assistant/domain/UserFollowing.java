package com.imooc.bilibili.assistant.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class UserFollowing {

    private Long id;

    private Long userId;

    private Long followingId;

    private Long groupId;

    private Date createTime;

    /**
     * 被关注者的信息
     */
    private UserInfo userInfo;

}
