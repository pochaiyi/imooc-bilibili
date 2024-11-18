package com.imooc.bilibili.controller;

import com.imooc.bilibili.assistant.domain.BulletComment;
import com.imooc.bilibili.assistant.domain.JsonResponse;
import com.imooc.bilibili.controller.support.UserSupport;
import com.imooc.bilibili.service.BulletCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;

@RestController
public class BulletCommentController {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private BulletCommentService bulletCommentService;

    /**
     * 查询弹幕
     */
    @GetMapping("/bullet-comments")
    public JsonResponse<List<BulletComment>> getBulletComments(
            @RequestParam Long videoId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) throws ParseException {
        List<BulletComment> list;
        try {
            // 登录模式允许时间筛选
            userSupport.getCurrentUserId();
            list = bulletCommentService.getBulletComments(videoId, startTime, endTime);
        } catch (Exception ignored) {
            // 游客模式查询全部弹幕
            list = bulletCommentService.getBulletComments(videoId, null, null);
        }
        return new JsonResponse<>(list);
    }

}
