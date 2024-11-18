package com.imooc.bilibili.service;

import com.alibaba.fastjson.JSONObject;
import com.imooc.bilibili.assistant.Constant.RedisConstant;
import com.imooc.bilibili.assistant.Constant.RocketMQConstant;
import com.imooc.bilibili.assistant.domain.UserMoment;
import com.imooc.bilibili.assistant.util.RocketMQUtil;
import com.imooc.bilibili.dao.UserMomentDao;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserMomentService {

    @Autowired
    private UserMomentDao userMomentDao;

    @Autowired
    @Qualifier("momentsProducer")
    private DefaultMQProducer momentsProducer;

    @Autowired
    @Qualifier("userMomentRedisTemplate")
    private RedisTemplate<String, UserMoment> redisTemplate;


    public void addUserMoment(UserMoment userMoment) throws Exception {
        // 存储动态
        userMoment.setCreateTime(new Date());
        userMomentDao.addUserMoment(userMoment);
        // 发布动态
        Message message = new Message(RocketMQConstant.MOMENT_TOPIC,
                JSONObject.toJSONString(userMoment).getBytes(StandardCharsets.UTF_8));
        RocketMQUtil.asyncSendMsg(momentsProducer, message);
    }

    public List<UserMoment> getUserSubscribedMoments(Long userId) {
        List<UserMoment> userMoments = new ArrayList<>();
        String redisKey = RedisConstant.FOLLOWING_SUBSCRIBE_KEY + userId;
        Long size = redisTemplate.opsForList().size(redisKey);
        if (size != null && size > 0) {
            userMoments = redisTemplate.opsForList().leftPop(redisKey, size);
            return userMoments;
        }
        return userMoments;
    }

}
