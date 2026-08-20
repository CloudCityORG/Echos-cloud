package com.murk.servicewebsocket.mq;

import com.alibaba.fastjson.JSON;
import com.murk.commonlib.dto.BattleReport;
import com.murk.commonlib.dto.WebSocketNotifyEvent;
import com.murk.servicewebsocket.config.NativeWebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WebSocketNotifyConsumer {

    @KafkaListener(topics = "${kafka.topic.websocket-notify:websocket-notify}",
            groupId = "${spring.kafka.consumer.group-id:service-websocket-consumer}")
    public void consume(@Payload WebSocketNotifyEvent event) {
        if (event == null) {
            log.warn("收到空的 WebSocket 通知事件");
            return;
        }

        log.info("收到 WebSocket 通知事件, type={}, targetUserId={}, title={}",
                event.getType(), event.getTargetUserId(), event.getTitle());

        // 为了保持前端兼容，仍按原来的 BattleReport 格式推送给客户端
        String reportType = event.getType() == WebSocketNotifyEvent.TYPE_PUBLIC ? "SUCCESS" : "INFO";
        BattleReport report = new BattleReport(reportType, event.getTitle(), event.getContent());
        String jsonMessage = JSON.toJSONString(report);

        if (event.getType() == WebSocketNotifyEvent.TYPE_PUBLIC) {
            NativeWebSocketServer.sendMessageToAll(jsonMessage);
        } else if (event.getType() == WebSocketNotifyEvent.TYPE_PERSONAL) {
            NativeWebSocketServer.sendToUser(String.valueOf(event.getTargetUserId()), jsonMessage);
        } else {
            log.warn("未知的 WebSocket 通知类型: {}", event.getType());
        }
    }
}
