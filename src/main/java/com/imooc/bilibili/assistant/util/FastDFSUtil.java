package com.imooc.bilibili.assistant.util;

import com.github.tobato.fastdfs.domain.fdfs.FileInfo;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.imooc.bilibili.assistant.Constant.RedisConstant;
import com.imooc.bilibili.assistant.exception.ConditionException;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

@Component
@Slf4j
public class FastDFSUtil {

    /**
     * 文件默认分组
     */
    public static final String DEFAULT_GROUP = "group1";

    /**
     * 分片大小
     */
    public static final int SLICE_SIZE = 1024 * 1024 * 5;

    @Value("${file.temple-location}")
    private String TMP_STORE_LOCATION;

    @Value("${fdfs.source-address}")
    private String FAST_DFS_STORAGE_HTTP_ADDRESS;

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    /**
     * 支持断点续传
     */
    @Autowired
    private AppendFileStorageClient appendFileStorageClient;

    @Autowired
    private RedisTemplate<String, String> commonRedisTemplate;

    @Autowired
    @Qualifier("commonRedisTemplate")
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 根据文件名分析文件类型
     */
    public String getFileType(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ConditionException("非法文件");
        }
        String filename = file.getOriginalFilename();
        if (filename != null) {
            int index = filename.lastIndexOf(".");
            return filename.substring(index + 1);
        } else {
            throw new ConditionException("非法文件");
        }
    }

    /**
     * 上传文件
     *
     * @return String 文件的存储路径
     */
    public String upload(MultipartFile file) throws IOException {
        StorePath storePath = fastFileStorageClient.uploadFile(
                file.getInputStream(), file.getSize(), this.getFileType(file), new HashSet<>());
        return storePath.getPath();
    }

    /**
     * 删除文件
     */
    public void delete(String filePath) {
        fastFileStorageClient.deleteFile(filePath);
    }

    /**
     * 上传文件的第一个分片
     *
     * @return String 文件的存储路径
     */
    public String upAppenderFile(MultipartFile file) throws IOException {
        StorePath storePath = appendFileStorageClient.uploadAppenderFile(
                DEFAULT_GROUP, file.getInputStream(), file.getSize(), this.getFileType(file));
        return storePath.getPath();
    }

    /**
     * 上传文件的后续分片，使用modify，因为append可能重复上传
     */
    public void modifyFile(MultipartFile file, String filePath, long offset) throws IOException {
        appendFileStorageClient.modifyFile(
                DEFAULT_GROUP, filePath, file.getInputStream(), file.getSize(), offset);
    }

    /**
     * 断点续传
     *
     * @return String 文件上传完毕返回存储路径，否则返回空字符串
     */
    public String uploadFileBySlice(MultipartFile file, String fileMD5, Integer sliceNo, Integer sliceCount) throws IOException {
        if (file == null || StringUtil.isNullOrEmpty(fileMD5) || sliceNo == null || sliceCount == null) {
            throw new ConditionException("参数错误");
        }
        // 相关缓存key
        String pathKey = RedisConstant.FILE_PATH_KEY + fileMD5;
        String offsetKey = RedisConstant.FILE_UPLOADED_SIZE_KEY + fileMD5;
        String noKey = RedisConstant.FILE_UPLOADED_NO_KEY + fileMD5;
        String path;
        long uploadedOffset = 0L;
        int uploadedNo;
        // 上传分片
        if (sliceNo == 1) { // 第一个分片
            uploadedNo = 1;
            path = this.upAppenderFile(file);
            if (StringUtil.isNullOrEmpty(path)) {
                throw new ConditionException("文件上传失败");
            }
            redisTemplate.opsForValue().set(pathKey, path);
            redisTemplate.opsForValue().set(noKey, String.valueOf(uploadedNo));
        } else { // 后面的分片
            String uploadedOffsetStr = commonRedisTemplate.opsForValue().get(offsetKey);
            if (!StringUtil.isNullOrEmpty(uploadedOffsetStr)) {
                uploadedOffset = Long.parseLong(uploadedOffsetStr);
            }
            path = redisTemplate.opsForValue().get(pathKey);
            if (StringUtil.isNullOrEmpty(path)) {
                throw new ConditionException("缺失存储路径");
            }
            this.modifyFile(file, path, uploadedOffset);
            //redisTemplate.opsForValue().increment(noKey);
            String uploadedNoStr = redisTemplate.opsForValue().get(noKey);
            if (StringUtil.isNullOrEmpty(uploadedNoStr)) {
                throw new ConditionException("缺失分片计数");
            }
            uploadedNo = Integer.parseInt(uploadedNoStr) + 1;
            redisTemplate.opsForValue().set(noKey, String.valueOf(uploadedNo));
        }
        // 更新上传大小缓存
        uploadedOffset += file.getSize();
        redisTemplate.opsForValue().set(offsetKey, String.valueOf(uploadedOffset));
        // 判断文件上传完毕
        String accessPath = "";
        if (sliceCount.equals(uploadedNo)) {
            accessPath = path;
            List<String> keyList = Arrays.asList(pathKey, offsetKey, noKey);
            redisTemplate.delete(keyList);
        }
        return accessPath;
    }

    public void converterFileToSlices(MultipartFile multipartFile) throws Exception {
        String fileType = this.getFileType(multipartFile);
        File file = this.converterMultipartFileToFile(multipartFile);
        long size = file.length();
        int count = 1;
        byte[] buffer = new byte[SLICE_SIZE];
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            // 文件分片
            for (int i = 0; i < size; i += SLICE_SIZE) {
                raf.seek(i);
                int len = raf.read(buffer);
                // 保存分片
                String slicePath = TMP_STORE_LOCATION + count + "." + fileType;
                File sliceFile = new File(slicePath);
                try (FileOutputStream fos = new FileOutputStream(sliceFile)) {
                    fos.write(buffer, 0, len);
                }
                count++;
            }
        }
        if (file.delete()) {
            log.info("文件分片成功，删除临时文件");
        }
    }

    public File converterMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        String[] names = Objects.requireNonNull(multipartFile.getOriginalFilename()).split("\\.");
        File file = File.createTempFile(names[0], "." + names[1]);
        multipartFile.transferTo(file);
        return file;
    }

    /**
     * 请求文件分片
     *
     * @param path 文件存储路径
     */
    public void getFileSlice(HttpServletRequest request, HttpServletResponse response, String path) throws Exception {
        FileInfo fileInfo = fastFileStorageClient.queryFileInfo(DEFAULT_GROUP, path);
        long videoSize = fileInfo.getFileSize(); // 文件总大小
        String url = FAST_DFS_STORAGE_HTTP_ADDRESS + path; // 文件访问地址
        // 设置请求头字段
        HashMap<String, Object> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        // 计算文件分片范围
        long begin = 0;
        long end = videoSize - 1;
        String rangeStr = request.getHeader("Range");
        if (!StringUtil.isNullOrEmpty(rangeStr)) {
            String[] split = rangeStr.split("bytes=|-");
            if (split.length >= 2) {
                begin = Long.parseLong(split[1]);
            }
            if (split.length >= 3) {
                end = Long.parseLong(split[2]);
            }
        }
        long contentLength = end - begin + 1;
        // 设置响应头字段
        String contentRange = "bytes " + begin + "-" + end + "/" + videoSize;
        response.setHeader("Content-Range", contentRange);
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Type", "video/mp4");
        response.setContentLength((int) contentLength);
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        // 发出请求
        HttpUtil.get(url, headers, response);
    }

}
