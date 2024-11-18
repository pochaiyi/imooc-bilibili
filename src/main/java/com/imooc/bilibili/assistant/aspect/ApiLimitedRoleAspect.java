package com.imooc.bilibili.assistant.aspect;

import com.imooc.bilibili.assistant.aspect.annotation.ApiLimitedRole;
import com.imooc.bilibili.assistant.domain.auth.UserRole;
import com.imooc.bilibili.controller.support.UserSupport;
import com.imooc.bilibili.service.UserRoleService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Aspect
@Component
public class ApiLimitedRoleAspect {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserRoleService userRoleService;

    @Pointcut("@annotation(com.imooc.bilibili.assistant.aspect.annotation.ApiLimitedRole)")
    public void apiLimitedRolePoint() {
    }

    @Before("apiLimitedRolePoint() && @annotation(apiLimitedRole)")
    public void apiLimitedRoleBefore(JoinPoint joinPoint, ApiLimitedRole apiLimitedRole) {
        Long userId = userSupport.getCurrentUserId();
        // 得到用户的角色编码
        List<UserRole> userRoleList = userRoleService.getUserRolesByUserId(userId);
        Set<String> roleCodeSet = new HashSet<>();
        for (UserRole userRole : userRoleList) {
            roleCodeSet.add(userRole.getRoleCode());
        }
        // 限制的角色编码
        Set<String> limitedRoleCodeSet = new HashSet<>();
        Collections.addAll(limitedRoleCodeSet, apiLimitedRole.limitedRoleCodeList());
        // 用户是否所有角色都被限制
        roleCodeSet.removeAll(limitedRoleCodeSet);
        if (roleCodeSet.isEmpty()) {
            throw new RuntimeException("没有权限");
        }
    }

}
