package com.imooc.bilibili.service;

import com.imooc.bilibili.assistant.domain.auth.AuthRoleElementOperation;
import com.imooc.bilibili.assistant.domain.auth.AuthRoleMenu;
import com.imooc.bilibili.assistant.domain.auth.UserRole;
import com.imooc.bilibili.dao.UserRoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class UserRoleService {

    @Autowired
    private UserRoleDao userRoleDao;

    @Autowired
    private AuthRoleElementOperationService authRoleElementOperation;

    @Autowired
    private AuthRoleMenuService authRoleMenuService;

    public List<UserRole> getUserRolesByUserId(Long userId) {
        return userRoleDao.getUserRolesByUserId(userId);
    }

    public List<AuthRoleElementOperation> getRoleElementOperationsByRoleIds(Set<Long> roleIdSet) {
        return authRoleElementOperation.getRoleElementOperationsByRoleIds(roleIdSet);
    }

    public List<AuthRoleMenu> getRoleMenusByRoleIds(Set<Long> roleIdSet) {
        return authRoleMenuService.getRoleMenusByRoleIds(roleIdSet);
    }

    public void addUserDefaultRole(Long userId, String roleCode) {
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setCreateTime(new Date());
        userRole.setRoleId(userRoleDao.getIdByRoleCode(roleCode));
        userRoleDao.addUserRole(userRole);
    }

}
