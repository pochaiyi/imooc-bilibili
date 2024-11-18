package com.imooc.bilibili.dao;

import com.imooc.bilibili.assistant.domain.auth.AuthRoleMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

@Mapper
public interface AuthRoleMenuDao {

    List<AuthRoleMenu> getRoleMenusByRoleIds(Set<Long> roleIdSet);

}
