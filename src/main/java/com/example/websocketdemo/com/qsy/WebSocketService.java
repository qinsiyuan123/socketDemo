package com.example.websocketdemo.com.qsy;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.logging.log4j.message.MapMessage.MapFormat.JSON;

/**
 * @Author: qsy
 * @Date: 2023/12/25  15:23
 */
@Component
@ServerEndpoint("/myWebSocket/{sid}")
@Slf4j
public class WebSocketService {
    //消息存储
    private static Map<String, Session> onlineSessions = new ConcurrentHashMap<>();
    //连接sid和连接会话
    private String sid;
    private Session session;

    @OnOpen
    public void onOpen(@PathParam("sid") String sid, Session session) {
        onlineSessions.put(sid, session);
        this.sid = sid;
        this.session = session;
        sendToOne(sid, "连接成功");
    }

    @OnClose
    public void onClose(@PathParam("sid") String sid, Session session) {
        onlineSessions.remove(sid);
        log.info("关闭成功");
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        JSONObject jsonObject = JSONObject.parseObject(message);
        String toSid = jsonObject.getString("sid");
        String msg = jsonObject.getString("message");
        log.info("服务端收到消息 ==> formSid={}, toSid={}, message={}", sid, toSid, message);

        if (toSid == null || toSid == "" || "".equalsIgnoreCase(toSid)) {
            sendToAll(msg);
        } else {
            sendToOne(toSid, msg);
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket发生错误，错误信息为：" + error.getMessage());
        error.printStackTrace();
    }

    private void sendToAll(String message) {
        onlineSessions.forEach((onlineSid, toSession) -> {
            if (!sid.equalsIgnoreCase(onlineSid)) {
                toSession.getAsyncRemote().sendText(message);
            }
        });
    }

    private void sendToOne(String sid, String message) {
        Session toSession = onlineSessions.get(sid);
        if (session == null) {
            log.error("服务端给客户端发送消息 ==> toSid ={}不存在, message={}", sid, message);
        }
        log.info("发送消息，sid->{} ,message->{}", sid, message);
        toSession.getAsyncRemote().sendText(message);
    }
}
