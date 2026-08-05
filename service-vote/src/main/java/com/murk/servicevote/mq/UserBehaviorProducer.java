package com.murk.servicevote.mq;

import com.murk.commonlib.dto.UserBehaviorEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * 用户行为事件生产者：将点赞、收藏等行为发送到 Kafka，供 agent 服务消费。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserBehaviorProducer {

    private final KafkaTemplate<String, UserBehaviorEvent> kafkaTemplate;

    @Value("${kafka.topic.user-behavior:user-behavior}")
    private String topic;

    /**
     * 发送榜单投票（点赞）事件
     */
    public void sendRankLikeEvent(Integer userId, Integer rankId) {
        sendRankLikeEvent(userId, rankId, false);
    }

    /**
     * 发送榜单投票（点赞/取消点赞）事件
     */
    public void sendRankLikeEvent(Integer userId, Integer rankId, boolean cancelled) {
        send(UserBehaviorEvent.builder()
                .userId(userId)
                .targetType(UserBehaviorEvent.TargetType.RANK)
                .targetId(rankId)
                .actionType(UserBehaviorEvent.ActionType.LIKE)
                .source(1)
                .actionTime(LocalDateTime.now())
                .score(cancelled ? -1 : 1)
                .cancelled(cancelled)
                .build());
    }

    /**
     * 发送榜单收藏（love）事件
     */
    public void sendRankCollectEvent(Integer userId, Integer rankId, boolean cancelled) {
        send(UserBehaviorEvent.builder()
                .userId(userId)
                .targetType(UserBehaviorEvent.TargetType.RANK)
                .targetId(rankId)
                .actionType(UserBehaviorEvent.ActionType.COLLECT)
                .source(1)
                .actionTime(LocalDateTime.now())
                .score(cancelled ? -1 : 1)
                .cancelled(cancelled)
                .build());
    }

    private void send(UserBehaviorEvent event) {
        CompletableFuture<SendResult<String, UserBehaviorEvent>> future =
                kafkaTemplate.send(topic, String.valueOf(event.getUserId()), event);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("发送用户行为事件失败, event={}", event, ex);
            } else {
                log.debug("发送用户行为事件成功, topic={}, partition={}, offset={}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
