package com.imooc.bilibili.controller;

import com.imooc.bilibili.assistant.domain.*;
import com.imooc.bilibili.controller.support.UserSupport;
import com.imooc.bilibili.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
public class VideoController {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private VideoService videoService;

    /**
     * 视频投稿
     */
    @PostMapping("/videos")
    public JsonResponse<String> addVideo(@RequestBody Video video) {
        Long userId = userSupport.getCurrentUserId();
        video.setUserId(userId);
        videoService.addVideo(video);
        return JsonResponse.success();
    }

    /**
     * 分页查询视频
     */
    @GetMapping("/videos")
    public JsonResponse<PageResult<Video>> getVideos(@RequestParam Integer no, @RequestParam Integer size, @RequestParam(required = false) String area) {
        PageResult<Video> result = videoService.pageListVideos(no, size, area);
        return new JsonResponse<>(result);
    }

    /**
     * 分片下载视频
     *
     * @param path 视频的存储路径
     */
    @GetMapping("/video-slices")
    public void getVideoBySlices(HttpServletRequest request, HttpServletResponse response, @RequestParam String path) throws Exception {
        videoService.getVideoBySlices(request, response, path);
    }

    /**
     * 查询视频标签
     */
    @GetMapping("/video-tags")
    public JsonResponse<List<VideoTag>> getVideoTags(@RequestParam Long videoId) {
        List<VideoTag> videoTagList = videoService.getVideoTags(videoId);
        return new JsonResponse<>(videoTagList);
    }

    /**
     * 添加视频标签
     */
    @PostMapping("/video-tags")
    public JsonResponse<String> addVideoTag(@RequestBody VideoTag videoTag) {
        Long userId = userSupport.getCurrentUserId();
        videoService.addVideoTag(videoTag, userId);
        return JsonResponse.success();
    }

    /**
     * 删除视频标签
     */
    @DeleteMapping("/video-tags")
    public JsonResponse<String> deleteVideoTag(@RequestBody VideoTag videoTag) {
        Long userId = userSupport.getCurrentUserId();
        videoService.deleteVideoTag(videoTag, userId);
        return JsonResponse.success();
    }

    /**
     * 视频点赞
     */
    @PostMapping("/video-likes")
    public JsonResponse<String> addVideoLike(@RequestParam Long videoId) {
        Long userId = userSupport.getCurrentUserId();
        videoService.addVideoLike(videoId, userId);
        return JsonResponse.success();
    }

    /**
     * 取消点赞
     */
    @DeleteMapping("/video-likes")
    public JsonResponse<String> deleteVideoLike(@RequestParam Long videoId) {
        Long userId = userSupport.getCurrentUserId();
        videoService.deleteVideoLike(videoId, userId);
        return JsonResponse.success();
    }

    /**
     * 查询视频点赞数量
     */
    @GetMapping("/video-likes")
    public JsonResponse<Map<String, Object>> countVideoLikes(@RequestParam Long videoId) {
        Long userId = null;
        // 支持游客模式
        try {
            userId = userSupport.getCurrentUserId();
        } catch (Exception ignored) {
            // ignore
        }
        Map<String, Object> result = videoService.countVideoLikes(videoId, userId);
        return new JsonResponse<>(result);
    }

    /**
     * 视频收藏
     */
    @PostMapping("/video-collections")
    public JsonResponse<String> addVideoCollection(@RequestBody VideoCollection videoCollection) {
        Long userId = userSupport.getCurrentUserId();
        videoCollection.setUserId(userId);
        videoService.addVideoCollection(videoCollection);
        return JsonResponse.success();
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/video-collections")
    public JsonResponse<String> deleteVideoCollection(@RequestParam Long videoId) {
        Long userId = userSupport.getCurrentUserId();
        videoService.deleteVideoCollection(videoId, userId);
        return JsonResponse.success();
    }

    /**
     * 查询视频收藏数量
     */
    @GetMapping("/video-collections")
    public JsonResponse<Map<String, Object>> countVideoCollections(@RequestParam Long videoId) {
        Long userId = null;
        try {
            userId = userSupport.getCurrentUserId();
        } catch (Exception ignored) {
            // ignore
        }
        Map<String, Object> result = videoService.countVideoCollections(videoId, userId);
        return new JsonResponse<>(result);
    }

    /**
     * 视频投币
     */
    @PostMapping("/video-coins")
    public JsonResponse<String> addVideoCoins(@RequestBody VideoCoin videoCoin) {
        Long userId = userSupport.getCurrentUserId();
        videoCoin.setUserId(userId);
        videoService.addVideoCoins(videoCoin);
        return JsonResponse.success();
    }

    /**
     * 查询视频投币数量
     */
    @GetMapping("video-coins")
    public JsonResponse<Map<String, Object>> countVideoCoins(@RequestParam Long videoId) {
        Long userId = null;
        try {
            userId = userSupport.getCurrentUserId();
        } catch (Exception ignored) {
            // ignore
        }
        Map<String, Object> result = videoService.countVideoCoins(videoId, userId);
        return new JsonResponse<>(result);
    }

    /**
     * 视频评论
     */
    @PostMapping("/video-comments")
    public JsonResponse<String> addVideoComment(@RequestBody VideoComment videoComment) {
        long userId = userSupport.getCurrentUserId();
        videoComment.setUserId(userId);
        videoService.addVideoComment(videoComment);
        return JsonResponse.success();
    }

    /**
     * 分页查询视频评论
     */
    @GetMapping("/video-comments")
    public JsonResponse<PageResult<VideoComment>> pageListVideoComments(
            @RequestParam Long videoId, @RequestParam Long no, @RequestParam Long size) {
        return new JsonResponse<>(videoService.pageListVideoComments(videoId, no, size));
    }

    /**
     * 查询视频详情
     */
    @GetMapping("/video-details")
    public JsonResponse<Map<String, Object>> getVideoDetails(@RequestParam Long videoId) {
        return new JsonResponse<>(videoService.getVideoDetails(videoId));
    }

}
