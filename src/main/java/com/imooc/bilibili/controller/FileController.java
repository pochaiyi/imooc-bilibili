package com.imooc.bilibili.controller;

import com.imooc.bilibili.assistant.domain.JsonResponse;
import com.imooc.bilibili.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 文件分片
     */
    @GetMapping("/file-slices")
    public JsonResponse<String> slices(MultipartFile file) throws Exception {
        fileService.converterFileToSlices(file);
        return JsonResponse.success();
    }

    /**
     * 文件内容MD5加密
     */
    @GetMapping("/md5files")
    public JsonResponse<String> getFileMD5(MultipartFile file) throws IOException {
        return new JsonResponse<>(fileService.getFileMD5(file));
    }

    /**
     * 分片上传
     */
    @PutMapping("/file-slices")
    public JsonResponse<String> fileSlices(
            MultipartFile slice,
            String fileMD5,
            Integer sliceNo,
            Integer sliceCount) throws Exception {
        String accessPath = fileService.uploadFileBySlices(slice, fileMD5, sliceNo, sliceCount);
        return new JsonResponse<>(accessPath);
    }

}
