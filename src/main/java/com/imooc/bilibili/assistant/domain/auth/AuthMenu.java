package com.imooc.bilibili.assistant.domain.auth;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 页面访问权限
 */
@Setter
@Getter
public class AuthMenu {

    private Long id;

    private String name;

    private String code;

    private Date createTime;

    private Date updateTime;

}
