package com.imooc.bilibili.assistant.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class VideoTag {

    private Long id;

    private Long videoId;

    private Long tagId;

    private Date createTime;

}
