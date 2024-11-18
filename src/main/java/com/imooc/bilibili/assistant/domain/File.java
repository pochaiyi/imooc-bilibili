package com.imooc.bilibili.assistant.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class File {

    private Long id;

    private String url;

    private String type;

    private String md5;

    private Date createTime;

}
