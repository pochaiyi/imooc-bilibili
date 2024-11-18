package com.imooc.bilibili.assistant.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class VideoLike {

    private Long id;

    private Long userId;

    private Long videoId;

    private Date createTime;

}