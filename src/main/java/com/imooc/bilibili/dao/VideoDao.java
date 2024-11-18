package com.imooc.bilibili.dao;

import com.alibaba.fastjson.JSONObject;
import com.imooc.bilibili.assistant.domain.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface VideoDao {

    Integer addVideo(Video video);

    Video getVideoById(Long id);

    Integer pageCountVideos(JSONObject params);

    List<Video> pageListVideos(JSONObject params);

    Integer addVideoTag(VideoTag videoTag);

    Integer batchAddVideoTags(List<VideoTag> videoTagList);

    VideoTag getTagById(Long id);

    List<VideoTag> getVideoTags(Long id);

    Integer deleteVideo(Long videoId, Long tagId);

    VideoLike getVideoLikeByVideoIdAndUserId(Long videoId, Long userId);

    Integer addVideoLike(VideoLike videoLike);

    Integer deleteVideoLike(Long videoId, Long userId);

    Long countVideoLikesById(Long videoId);

    VideoCollection getVideoCollectionByVideoIdAndUserId(Long videoId, Long userId);

    Integer addVideoCollection(VideoCollection videoCollection);

    Integer deleteVideoCollection(Long videoId, Long userId);

    Long countVideoCollectionsById(Long videoId);

    VideoCoin getVideoCoinByVideoIdAndUserId(Long videoId, Long userId);

    Integer addVideoCoin(VideoCoin videoCoin);

    Integer updateVideoCoin(VideoCoin videoCoin);

    Long countVideoCoinsAmount(Long videoId);

    Integer addVideoComment(VideoComment videoComment);

    Integer countRootVideoComments(Map<String, Object> params);

    List<VideoComment> pageListRootVideoComments(Map<String, Object> params);

    List<VideoComment> getChildCommentsByRootIdSet(Set<Long> rootIdSet);

}
