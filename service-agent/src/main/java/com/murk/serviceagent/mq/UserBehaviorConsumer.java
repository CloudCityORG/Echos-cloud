package com.murk.serviceagent.mq;

import com.murk.commonlib.dto.UserBehaviorEvent;
import com.murk.serviceagent.Service.UserBehaviorEventService;
import com.murk.serviceagent.pojo.UserBehaviorEventEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 用户行为事件消费者：接收来自 vote/chart/user 等服务的用户行为事件，
 * 并持久化到 agent 库的 user_behavior_event 表，供 AI 智能体分析用户兴趣。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserBehaviorConsumer {

    private final UserBehaviorEventService userBehaviorEventService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "${kafka.topic.user-behavior:user-behavior}",
            groupId = "${spring.kafka.consumer.group-id:service-agent-consumer}")
    public void consume(@Payload UserBehaviorEvent event, Acknowledgment acknowledgment) {
        try {
            log.info("收到用户行为事件: {}", event);
            UserBehaviorEventEntity entity = convert(event);
            userBehaviorEventService.save(entity);
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        } catch (Exception e) {
            log.error("处理用户行为事件失败, event={}", event, e);
            // 不 ack，让消息重试或进入死信队列（后续可扩展）
        }
    }

    private UserBehaviorEventEntity convert(UserBehaviorEvent event) {
        UserBehaviorEventEntity entity = new UserBehaviorEventEntity();
        entity.setUserId(event.getUserId());
        entity.setTargetType(targetTypeToCode(event.getTargetType()));
        entity.setTargetId(event.getTargetId());
        entity.setActionType(actionTypeToCode(event.getActionType()));
        entity.setSource(event.getSource());
        entity.setTagIds(tagIdsToJson(event.getTagIds()));
        entity.setScore(event.getScore() == null ? 1 : event.getScore());
        entity.setCancelled(Boolean.TRUE.equals(event.getCancelled()) ? 1 : 0);
        entity.setActionTime(event.getActionTime() == null ? LocalDateTime.now() : event.getActionTime());
        entity.setCreateTime(LocalDateTime.now());
        return entity;
    }

    private Integer targetTypeToCode(UserBehaviorEvent.TargetType targetType) {
        if (targetType == null) {
            return null;
        }
        return switch (targetType) {
            case RANK -> 1;
            case SONG -> 2;
            case COMMENT -> 3;
            case USER -> 4;
        };
    }

    private Integer actionTypeToCode(UserBehaviorEvent.ActionType actionType) {
        if (actionType == null) {
            return null;
        }
        return switch (actionType) {
            case LIKE -> 1;
            case COLLECT -> 2;
            case BROWSE -> 3;
            case PLAY -> 4;
            case FOLLOW -> 5;
        };
    }

    private String tagIdsToJson(java.util.List<Integer> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(tagIds);
        } catch (JsonProcessingException e) {
            log.error("标签列表序列化失败, tagIds={}", tagIds, e);
            return null;
        }
    }
}
