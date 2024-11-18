package com.imooc.bilibili.assistant.aspect.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Component
public @interface ApiLimitedRole {

    /**
     * 限制访问的角色编号列表
     */
    String[] limitedRoleCodeList() default {};

}
