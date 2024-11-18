package com.imooc.bilibili.assistant.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class BulletComment {

    private Long id;

    private Long userId;

    private Long videoId;

    private String content;

    private String bulletCommentTime;

    private Date createTime;

    /**
     * 创建弹幕的会话
     */
    private String sessionId;

}
