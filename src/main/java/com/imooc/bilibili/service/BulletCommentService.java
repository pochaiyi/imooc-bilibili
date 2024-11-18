package com.imooc.bilibili.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.imooc.bilibili.assistant.Constant.RedisConstant;
import com.imooc.bilibili.assistant.domain.BulletComment;
import com.imooc.bilibili.dao.BulletCommentDao;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class BulletCommentService {

    @Autowired
    private BulletCommentDao bulletCommentDao;

    @Autowired
    @Qualifier("commonRedisTemplate")
    private RedisTemplate<String, String> redisTemplate;

    @Async
    public void addBulletComment(BulletComment bulletComment) {
        bulletCommentDao.addBulletComment(bulletComment);
    }

    @Async
    public void addBulletCommentToRedis(BulletComment bulletComment) {
        String key = RedisConstant.BULLET_COMMENT_KEY + bulletComment.getVideoId();
        String value = redisTemplate.opsForValue().get(key);
        List<BulletComment> list;
        if (StringUtils.isNullOrEmpty(value)) {
            list = new ArrayList<>();
        } else {
            list = JSONArray.parseArray(value, BulletComment.class);
        }
        list.add(bulletComment);
        redisTemplate.opsForValue().set(key, JSONObject.toJSONString(list));
    }

    public List<BulletComment> getBulletComments(Long videoId, String startTime, String endTime) throws ParseException {
        // 首先尝试查询缓存
        String key = RedisConstant.BULLET_COMMENT_KEY + videoId;
        String value = redisTemplate.opsForValue().get(key);
        List<BulletComment> list;
        if (!StringUtils.isNullOrEmpty(value)) {
            list = JSONArray.parseArray(value, BulletComment.class);
            if (!StringUtils.isNullOrEmpty(startTime) && !StringUtils.isNullOrEmpty(endTime)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date beginDate = sdf.parse(startTime);
                Date endDate = sdf.parse(endTime);
                List<BulletComment> filter = new ArrayList<>();
                if (list != null) {
                    for (BulletComment bulletComment : list) {
                        Date createTime = bulletComment.getCreateTime();
                        if (createTime.after(beginDate) && createTime.before(endDate)) {
                            filter.add(bulletComment);
                        }
                    }
                }
                list = filter;
            }
        } else {
            // 查询数据库，然后回设缓存
            Map<String, Object> params = new HashMap<>();
            params.put("videoId", videoId);
            params.put("startTime", startTime);
            params.put("endTime", endTime);
            list = bulletCommentDao.getBulletComments(params);
            redisTemplate.opsForValue().set(key, JSONObject.toJSONString(list));
        }
        return list;
    }

}
