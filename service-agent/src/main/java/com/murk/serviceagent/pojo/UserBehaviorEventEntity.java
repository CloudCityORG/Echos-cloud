package com.murk.serviceagent.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户行为事件流水表 user_behavior_event 对应的实体。
 * 数据由 Kafka 消费者写入，供 agent 做兴趣分析与推荐。
 */
@Data
public class UserBehaviorEventEntity {

    private Long eventId;
    private Integer userId;
    private Integer targetType;
    private Integer targetId;
    private Integer actionType;
    private Integer source;
    private String tagIds;
    private Integer score;
    private Integer cancelled;
    private LocalDateTime actionTime;
    private LocalDateTime createTime;
}
