package com.imooc.bilibili.service;

import com.imooc.bilibili.assistant.domain.File;
import com.imooc.bilibili.assistant.util.FastDFSUtil;
import com.imooc.bilibili.assistant.util.MD5Util;
import com.imooc.bilibili.dao.FileDao;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

@Service
public class FileService {

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Autowired
    private FileDao fileDao;

    public void converterFileToSlices(MultipartFile file) throws Exception {
        fastDFSUtil.converterFileToSlices(file);
    }

    public String uploadFileBySlices(MultipartFile file, String fileMD5, Integer sliceNo, Integer sliceCount) throws IOException {
        // 秒传：如已存在，直接返回存储路径
        File dbFile = fileDao.getFileByMD5(fileMD5);
        if (dbFile != null) {
            return dbFile.getUrl();
        }
        // 普通上传
        String url = fastDFSUtil.uploadFileBySlice(file, fileMD5, sliceNo, sliceCount);
        if (!StringUtil.isNullOrEmpty(url)) {
            dbFile = new File();
            dbFile.setUrl(url);
            dbFile.setType(fastDFSUtil.getFileType(file));
            dbFile.setMd5(fileMD5);
            dbFile.setCreateTime(new Date());
            fileDao.addFile(dbFile);
        }
        return url;
    }

    public String getFileMD5(MultipartFile file) throws IOException {
        return MD5Util.getMd5Str(file);
    }

}
