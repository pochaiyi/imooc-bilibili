package com.imooc.bilibili.dao;

import com.imooc.bilibili.assistant.domain.BulletComment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface BulletCommentDao {

    Integer addBulletComment(BulletComment bulletComment);

    List<BulletComment> getBulletComments(Map<String, Object> params);

}
