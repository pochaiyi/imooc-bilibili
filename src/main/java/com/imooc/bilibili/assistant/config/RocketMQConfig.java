package com.imooc.bilibili.assistant.config;

import com.alibaba.fastjson.JSONObject;
import com.imooc.bilibili.assistant.Constant.RedisConstant;
import com.imooc.bilibili.assistant.Constant.RocketMQConstant;
import com.imooc.bilibili.assistant.domain.BulletComment;
import com.imooc.bilibili.assistant.domain.UserFollowing;
import com.imooc.bilibili.assistant.domain.UserMoment;
import com.imooc.bilibili.controller.BulletCommentWebSocket;
import com.imooc.bilibili.service.UserFollowingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

@Configuration
@Slf4j
public class RocketMQConfig {

    @Value("${rocketmq.name-server.address}")
    private String nameServerAddress;

    @Autowired
    private RedisTemplate<String, UserMoment> userMomentRedisTemplate;

    @Autowired
    private UserFollowingService userFollowingService;

    /**
     * 动态生产者
     */
    @Bean
    public DefaultMQProducer momentsProducer() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer(RocketMQConstant.MOMENT_PRODUCER_GROUP);
        producer.setNamesrvAddr(nameServerAddress);
        producer.start();
        return producer;
    }

    /**
     * 动态消费者，push模式
     */
    @Bean
    public DefaultMQPushConsumer momentsConsumer() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(RocketMQConstant.MOMENT_CONSUMER_GROUP);
        consumer.setNamesrvAddr(nameServerAddress);
        consumer.subscribe(RocketMQConstant.MOMENT_TOPIC, "*");
        // 动态消息监听器
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                // 假定每次只有一个动态
                MessageExt msg = list.get(0);
                if (msg == null) {
                    log.debug("收到空动态消息");
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                String body = new String(msg.getBody());
                UserMoment userMoment = JSONObject.parseObject(body, UserMoment.class);
                log.info("收到动态:{}", userMoment.toString());
                // 把新动态插入对应缓存列表
                Long userId = userMoment.getUserId();
                List<UserFollowing> fanList = userFollowingService.getUserFans(userId);
                for (UserFollowing fan : fanList) {
                    String redisKey = RedisConstant.FOLLOWING_SUBSCRIBE_KEY + fan.getUserId();
                    userMomentRedisTemplate.opsForList().rightPush(redisKey, userMoment);
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        return consumer;
    }

    /**
     * 弹幕消息生产者
     */
    @Bean("bulletCommentProducer")
    public DefaultMQProducer bulletCommentProducer() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer(RocketMQConstant.BULLET_COMMENT_PRODUCER_GROUP);
        producer.setNamesrvAddr(nameServerAddress);
        producer.start();
        return producer;
    }

    /**
     * 弹幕消息消费者
     */
    @Bean("bulletCommentConsumer")
    public DefaultMQPushConsumer bulletCommentConsumer() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(RocketMQConstant.MOMENT_COMMENT_CONSUMER_GROUP);
        consumer.setNamesrvAddr(nameServerAddress);
        consumer.subscribe(RocketMQConstant.BULLET_COMMENT_TOPIC, "*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                Message msg = list.get(0);
                if (msg == null) {
                    log.debug("收到空弹幕消息");
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                String msgBody = new String(msg.getBody());
                BulletComment bulletComment = JSONObject.parseObject(msgBody, BulletComment.class);
                String sessionId = bulletComment.getSessionId();
                BulletCommentWebSocket bcServer = BulletCommentWebSocket.WEBSOCKET_MAP.get(sessionId);
                if (bcServer.getSession().isOpen()) {
                    try {
                        bcServer.sendMessage(JSONObject.toJSONString(bulletComment));
                    } catch (Exception e) {
                        log.info("弹幕发送失败:{}", e.getMessage());
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        return consumer;
    }

}
