package com.imooc.bilibili.dao;

import com.imooc.bilibili.assistant.domain.File;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileDao {

    File getFileByMD5(String fileMD5);

    Integer addFile(File file);

}
