package com.imooc.bilibili.assistant.domain.auth;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 页面元素操作权限
 */
@Setter
@Getter
public class AuthElementOperation {

    private Long id;

    private String elementName;

    private String elementCode;

    private String operationType;

    private Date createTime;

    private Date updateTime;

}
