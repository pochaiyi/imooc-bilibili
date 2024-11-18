package com.imooc.bilibili.service;

import com.imooc.bilibili.assistant.domain.auth.AuthRoleElementOperation;
import com.imooc.bilibili.assistant.domain.auth.AuthRoleMenu;
import com.imooc.bilibili.assistant.domain.auth.UserAuthorities;
import com.imooc.bilibili.assistant.domain.auth.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserAuthService {

    @Autowired
    UserRoleService userRoleService;

    public UserAuthorities getUserAuthorities(Long userId) {
        // 查询所有角色
        List<UserRole> roleIdList = userRoleService.getUserRolesByUserId(userId);
        Set<Long> roleIdSet = new HashSet<Long>();
        for (UserRole userRole : roleIdList) {
            roleIdSet.add(userRole.getRoleId());
        }
        // 查询页面元素操作权限
        List<AuthRoleElementOperation> roleElementOperationList = userRoleService.getRoleElementOperationsByRoleIds(roleIdSet);
        // 查询页面访问权限
        List<AuthRoleMenu> roleMenuList = userRoleService.getRoleMenusByRoleIds(roleIdSet);
        return new UserAuthorities(roleElementOperationList, roleMenuList);
    }

}
