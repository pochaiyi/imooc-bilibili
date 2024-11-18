package com.imooc.bilibili.dao;

import com.imooc.bilibili.assistant.domain.UserMoment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMomentDao {

    void addUserMoment(UserMoment userMoment);

}
