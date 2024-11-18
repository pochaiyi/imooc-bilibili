package com.imooc.bilibili.assistant.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Setter
@Getter
public class Video {

    private Long id;

    private Long userId;

    private String url;

    private String thumbnail;

    private String title;

    private String type;

    private String duration;

    private String area;

    private String description;

    private Date createTime;

    private Date updateTime;

    private List<VideoTag> videoTagList;

}
