package com.imooc.bilibili.service;

import com.alibaba.fastjson.JSONObject;
import com.imooc.bilibili.assistant.domain.*;
import com.imooc.bilibili.assistant.exception.ConditionException;
import com.imooc.bilibili.assistant.util.FastDFSUtil;
import com.imooc.bilibili.dao.VideoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VideoService {

    @Autowired
    private VideoDao videoDao;

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Autowired
    private UserCoinService userCoinService;
    @Autowired
    private UserService userService;

    @Transactional
    public void addVideo(Video video) {
        Date now = new Date();
        // 添加视频
        video.setCreateTime(now);
        videoDao.addVideo(video);
        // 添加标签
        Long videoId = video.getId();
        List<VideoTag> videoTagList = video.getVideoTagList();
        videoTagList.forEach(videoTag -> {
            videoTag.setVideoId(videoId);
            videoTag.setCreateTime(now);
        });
        videoDao.batchAddVideoTags(videoTagList);
    }

    public PageResult<Video> pageListVideos(Integer no, Integer size, String area) {
        if (no == null || size == null) {
            throw new ConditionException("参数异常");
        }
        JSONObject params = new JSONObject();
        params.put("start", (no - 1) * size);
        params.put("limit", size);
        params.put("area", area);
        Integer total = videoDao.pageCountVideos(params);

        List<Video> videoList = new ArrayList<>();
        if (total > 0) {
            videoList = videoDao.pageListVideos(params);
        }
        return new PageResult<>(total, videoList);
    }

    public void getVideoBySlices(HttpServletRequest request, HttpServletResponse response, String path) throws Exception {
        fastDFSUtil.getFileSlice(request, response, path);
    }

    public List<VideoTag> getVideoTags(Long videoId) {
        return videoDao.getVideoTags(videoId);
    }

    @Transactional
    public void addVideoTag(VideoTag videoTag, Long userId) {
        Video dbVideo = videoDao.getVideoById(videoTag.getVideoId());
        VideoTag dbVideoTag = videoDao.getTagById(videoTag.getTagId());
        if (dbVideo == null || dbVideoTag == null || !dbVideo.getUserId().equals(userId)) {
            throw new ConditionException("非法操作");
        }
        deleteVideoTag(videoTag, userId);
        videoTag.setCreateTime(new Date());
        videoDao.addVideoTag(videoTag);
    }

    public void deleteVideoTag(VideoTag videoTag, Long userId) {
        Video dbVideo = videoDao.getVideoById(videoTag.getVideoId());
        if (dbVideo == null || !dbVideo.getUserId().equals(userId)) {
            throw new ConditionException("非法操作");
        }
        videoDao.deleteVideo(videoTag.getVideoId(), videoTag.getTagId());
    }

    public void addVideoLike(Long videoId, Long userId) {
        Video dbVideo = videoDao.getVideoById(videoId);
        if (dbVideo == null) {
            throw new ConditionException("非法视频");
        }
        VideoLike dbVideoLike = videoDao.getVideoLikeByVideoIdAndUserId(videoId, userId);
        if (dbVideoLike != null) {
            throw new ConditionException("已经点赞");
        }
        VideoLike videoLike = new VideoLike();
        videoLike.setUserId(userId);
        videoLike.setVideoId(videoId);
        videoLike.setCreateTime(new Date());
        videoDao.addVideoLike(videoLike);
    }

    public void deleteVideoLike(Long videoId, Long userId) {
        videoDao.deleteVideoLike(videoId, userId);
    }

    public Map<String, Object> countVideoLikes(Long videoId, Long userId) {
        Long count = videoDao.countVideoLikesById(videoId);
        VideoLike dbVideoLike = videoDao.getVideoLikeByVideoIdAndUserId(videoId, userId);
        boolean like = dbVideoLike != null;
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("like", like);
        return result;
    }

    public void addVideoCollection(VideoCollection videoCollection) {
        Long userId = videoCollection.getUserId();
        Long videoId = videoCollection.getVideoId();
        Long groupId = videoCollection.getGroupId();
        if (videoId == null || groupId == null) {
            throw new ConditionException("参数异常");
        }
        Video dbVideo = videoDao.getVideoById(videoId);
        if (dbVideo == null) {
            throw new ConditionException("非法视频");
        }
        videoDao.deleteVideoCollection(videoId, userId);
        videoCollection.setCreateTime(new Date());
        videoDao.addVideoCollection(videoCollection);
    }

    public void deleteVideoCollection(Long videoId, Long userId) {
        videoDao.deleteVideoCollection(videoId, userId);
    }

    public Map<String, Object> countVideoCollections(Long videoId, Long userId) {
        Long count = videoDao.countVideoCollectionsById(videoId);
        VideoCollection videoCollection = videoDao.getVideoCollectionByVideoIdAndUserId(videoId, userId);
        boolean collect = videoCollection != null;
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("collect", collect);
        return result;
    }

    public void addVideoCoins(VideoCoin videoCoin) {
        Long userId = videoCoin.getUserId();
        Long videoId = videoCoin.getVideoId();
        Integer amount = videoCoin.getAmount();
        if (userId == null || videoId == null || amount == null) {
            throw new ConditionException("参数异常");
        }
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频");
        }
        Integer userCoinsAmount = userCoinService.getUserCoinsAmount(userId);
        userCoinsAmount = userCoinsAmount != null ? userCoinsAmount : 0;
        if (amount > userCoinsAmount) {
            throw new ConditionException("币量不足");
        }
        VideoCoin dbVideoCoin = videoDao.getVideoCoinByVideoIdAndUserId(videoId, userId);
        if (dbVideoCoin != null) {
            videoCoin.setAmount(dbVideoCoin.getAmount() + amount);
            videoCoin.setUpdateTime(new Date());
            videoDao.updateVideoCoin(videoCoin);
        } else {
            videoCoin.setCreateTime(new Date());
            videoDao.addVideoCoin(videoCoin);
        }
        userCoinService.updateUserCoinsAmount(userId, userCoinsAmount - amount);
    }

    public Map<String, Object> countVideoCoins(Long videoId, Long userId) {
        Long count = videoDao.countVideoCoinsAmount(videoId);
        VideoCoin videoCollection = videoDao.getVideoCoinByVideoIdAndUserId(videoId, userId);
        boolean pay = videoCollection != null;
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("pay", pay);
        return result;
    }

    public void addVideoComment(VideoComment videoComment) {
        Long videoId = videoComment.getVideoId();
        if (videoId == null) {
            throw new ConditionException("参数异常");
        }
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频");
        }
        videoComment.setCreateTime(new Date());
        videoDao.addVideoComment(videoComment);
    }

    public PageResult<VideoComment> pageListVideoComments(Long videoId, Long no, Long size) {
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("videoId", videoId);
        params.put("start", (no - 1) * size);
        params.put("limit", size);
        Integer total = videoDao.countRootVideoComments(params);
        List<VideoComment> rootCommentsList = new ArrayList<>();
        if (total > 0) {
            // 查询一级评论
            rootCommentsList = videoDao.pageListRootVideoComments(params);
            Set<Long> rootIdSet = rootCommentsList.stream().map(VideoComment::getId).collect(Collectors.toSet());
            // 查询二级评论
            List<VideoComment> childCommentsList = videoDao.getChildCommentsByRootIdSet(rootIdSet);
            // 查询用户信息
            Set<Long> userIdSet = rootCommentsList.stream().map(VideoComment::getUserId).collect(Collectors.toSet());
            Set<Long> childUserIdSet = childCommentsList.stream().map(VideoComment::getUserId).collect(Collectors.toSet());
            userIdSet.addAll(childUserIdSet);
            List<UserInfo> userInfoList = userService.getUserInfoByUserIds(userIdSet);
            Map<Long, UserInfo> userInfoMap = userInfoList.stream().collect(Collectors.toMap(UserInfo::getUserId, userInfo -> userInfo));
            // 遍历一级评论，组织评论列表
            rootCommentsList.forEach(root -> {
                Long rootId = root.getId();
                List<VideoComment> childList = new ArrayList<>();
                childCommentsList.forEach(child -> {
                    if (rootId.equals(child.getRootId())) {
                        child.setUserInfo(userInfoMap.get(child.getUserId()));
                        child.setReplyUserInfo(userInfoMap.get(child.getReplyUserId()));
                        childList.add(child);
                    }
                });
                root.setChildList(childList);
                root.setUserInfo(userInfoMap.get(root.getUserId()));
            });
        }
        return new PageResult<>(total, rootCommentsList);
    }

    public Map<String, Object> getVideoDetails(Long videoId) {
        Video video = videoDao.getVideoById(videoId);
        Long userId = video.getUserId();
        User user = userService.getUserById(userId);
        UserInfo userInfo = user.getUserInfo();
        Map<String, Object> result = new HashMap<>();
        result.put("video", video);
        result.put("userInfo", userInfo);
        return result;
    }

}
