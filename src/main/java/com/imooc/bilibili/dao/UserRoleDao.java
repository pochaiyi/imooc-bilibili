package com.imooc.bilibili.dao;

import com.imooc.bilibili.assistant.domain.auth.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserRoleDao {

    List<UserRole> getUserRolesByUserId(Long userId);

    Long getIdByRoleCode(String roleCode);

    Integer addUserRole(UserRole userRole);

}
