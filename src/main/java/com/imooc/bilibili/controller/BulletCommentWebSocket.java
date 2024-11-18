package com.imooc.bilibili.controller;

import com.alibaba.fastjson.JSONObject;
import com.imooc.bilibili.assistant.Constant.RocketMQConstant;
import com.imooc.bilibili.assistant.domain.BulletComment;
import com.imooc.bilibili.assistant.util.RocketMQUtil;
import com.imooc.bilibili.assistant.util.TokenUtil;
import com.imooc.bilibili.service.BulletCommentService;
import io.netty.util.internal.StringUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ServerEndpoint("/bullet-comment/{accessToken}")
@Controller
@Getter
@Slf4j
public class BulletCommentWebSocket {

    /**
     * <SessionId,BulletCommentWebSocket>
     */
    public static final ConcurrentHashMap<String, BulletCommentWebSocket> WEBSOCKET_MAP = new ConcurrentHashMap<>();

    private static final AtomicInteger ONLINE_COUNT = new AtomicInteger(0);

    private static DefaultMQProducer bcProducer;

    private static BulletCommentService bulletCommentService;

    private Session session;

    private String sessionId;

    private Long userId;

    @Autowired
    public void setBcProducer(@Qualifier("bulletCommentProducer") DefaultMQProducer bcProducer) {
        BulletCommentWebSocket.bcProducer = bcProducer;
    }

    @Autowired
    public void setBulletCommentService(BulletCommentService bulletCommentService) {
        BulletCommentWebSocket.bulletCommentService = bulletCommentService;
    }

    /**
     * 建立连接触发
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("accessToken") String accessToken) {
        try {
            this.userId = TokenUtil.verifyToken(accessToken);
        } catch (Exception e) {
            // ignore
        }
        this.session = session;
        this.sessionId = session.getId();
        if (!WEBSOCKET_MAP.containsKey(this.sessionId)) {
            ONLINE_COUNT.getAndIncrement();
        }
        WEBSOCKET_MAP.put(this.sessionId, this);
        log.info("[用户{}]连接成功，在线人数:{}", this.userId, ONLINE_COUNT.get());
        try {
            this.sendMessage("0");
        } catch (Exception e) {
            log.error("连接异常");
        }
    }

    /**
     * 关闭连接时发
     */
    @OnClose
    public void onClose() {
        if (WEBSOCKET_MAP.containsKey(this.sessionId)) {
            WEBSOCKET_MAP.remove(this.sessionId);
            ONLINE_COUNT.decrementAndGet();
        }
        log.info("[用户{}]退出成功，在线人数:{}", this.userId, ONLINE_COUNT.get());
    }

    /**
     * 收到客户端消息触发
     */
    @OnMessage
    public void onMessage(String message) {
        log.info("[用户{}]发送消息:{}", this.userId, message);
        if (!StringUtil.isNullOrEmpty(message)) {
            try {
                // 弹幕持久化和缓存
                BulletComment bulletComment = JSONObject.parseObject(message, BulletComment.class);
                bulletComment.setUserId(this.userId);
                bulletComment.setCreateTime(new Date());
                bulletCommentService.addBulletComment(bulletComment);
                bulletCommentService.addBulletCommentToRedis(bulletComment);
                // 广播推送弹幕消息
                for (Map.Entry<String, BulletCommentWebSocket> entry : WEBSOCKET_MAP.entrySet()) {
                    BulletCommentWebSocket bcServer = entry.getValue();
                    bulletComment.setSessionId(bcServer.getSessionId());
                    Message msg = new Message(RocketMQConstant.BULLET_COMMENT_TOPIC, JSONObject.toJSONString(bulletComment).getBytes(StandardCharsets.UTF_8));
                    RocketMQUtil.syncSendMsg(bcProducer, msg);
                }
            } catch (Exception e) {
                log.error("弹幕接收异常");
            }
        }
    }

    /**
     * 向客户端发送信息
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 定时任务，告知客户端当前在线人数
     * <p>
     * TODO 没有区分不同视频的在线人数
     */
    @Scheduled(fixedRate = 5000)
    public void noticeOnlineCount() throws Exception {
        for (Map.Entry<String, BulletCommentWebSocket> entry : WEBSOCKET_MAP.entrySet()) {
            BulletCommentWebSocket bcServer = entry.getValue();
            if (bcServer.session.isOpen()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("onlineCount", ONLINE_COUNT.get());
                jsonObject.put("msg", "在线人数：" + ONLINE_COUNT.get());
                bcServer.sendMessage(jsonObject.toJSONString());
            }
        }
    }

}
