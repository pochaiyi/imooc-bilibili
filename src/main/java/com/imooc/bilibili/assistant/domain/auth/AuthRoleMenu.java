package com.imooc.bilibili.assistant.domain.auth;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class AuthRoleMenu {

    private Long id;

    private Long roleId;

    private Long menuId;

    private Date createTime;

    private AuthMenu authMenu;

}
