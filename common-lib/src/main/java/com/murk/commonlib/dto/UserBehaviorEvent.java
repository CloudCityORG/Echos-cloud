package com.murk.commonlib.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户行为事件，通过 Kafka 在微服务间传递。
 * 用于记录：点赞榜单、收藏榜单、浏览榜单、播放歌曲等行为，供 agent 服务持久化并用于智能推荐。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBehaviorEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 行为发生用户
     */
    private Integer userId;

    /**
     * 行为目标类型：RANK(榜单)、SONG(歌曲)、COMMENT(评论)、USER(用户)
     */
    private TargetType targetType;

    /**
     * 行为目标 ID，如 rank_id / song_id / com_id / user_id
     */
    private Integer targetId;

    /**
     * 行为类型：LIKE(点赞)、COLLECT(收藏)、BROWSE(浏览)、PLAY(播放)、FOLLOW(关注)
     */
    private ActionType actionType;

    /**
     * 行为来源：1-vote, 2-chart, 3-user, 4-agent 等
     */
    private Integer source;

    /**
     * 关联标签 ID 列表（如榜单标签、歌曲风格标签），用于兴趣向量计算
     */
    private List<Integer> tagIds;

    /**
     * 行为发生时间
     */
    private LocalDateTime actionTime;

    /**
     * 额外扩展字段，可用于存储分值、时长等
     */
    private Integer score;

    /**
     * 是否取消行为（如取消点赞、取消收藏）
     */
    private Boolean cancelled;

    public enum TargetType {
        RANK, SONG, COMMENT, USER
    }

    public enum ActionType {
        LIKE, COLLECT, BROWSE, PLAY, FOLLOW
    }
}
