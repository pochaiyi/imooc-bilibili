package com.imooc.bilibili.assistant.aspect;

import com.imooc.bilibili.assistant.Constant.AuthRoleConstant;
import com.imooc.bilibili.assistant.Constant.UserMomentConstant;
import com.imooc.bilibili.assistant.domain.UserMoment;
import com.imooc.bilibili.assistant.domain.auth.UserRole;
import com.imooc.bilibili.assistant.exception.ConditionException;
import com.imooc.bilibili.controller.support.UserSupport;
import com.imooc.bilibili.service.UserRoleService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Aspect
@Component
public class DataLimitedRoleAspect {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserRoleService userRoleService;

    @Pointcut("@annotation(com.imooc.bilibili.assistant.aspect.annotation.DataLimitedRole)")
    public void dataLimitedRolePoint() {
    }

    @Before("dataLimitedRolePoint()")
    public void dataLimitedRoleBefore(JoinPoint joinPoint) {
        Long userId = userSupport.getCurrentUserId();
        // 得到用户的角色编码
        List<UserRole> userRoleList = userRoleService.getUserRolesByUserId(userId);
        Set<String> roleCodeSet = userRoleList.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof UserMoment) {
                String type = ((UserMoment) arg).getType();
                // Lv1用户不允许发布专栏动态
                if (roleCodeSet.contains(AuthRoleConstant.ROLE_CODE_LV1) && UserMomentConstant.MOMENT_TYPE_COLUMN.equals(type)) {
                    throw new ConditionException("没有权限发布专栏类型动态");
                }
                break;
            }
        }
    }

}
