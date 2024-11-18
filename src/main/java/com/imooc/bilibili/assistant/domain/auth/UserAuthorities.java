package com.imooc.bilibili.assistant.domain.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 用户权限，包括页面访问权限和元素操作权限
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthorities {

    List<AuthRoleElementOperation> roleElementOperationList;

    List<AuthRoleMenu> roleMenuList;

}
