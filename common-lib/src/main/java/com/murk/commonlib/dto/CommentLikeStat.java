package com.murk.commonlib.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 评论点赞统计
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentLikeStat {
    private Integer comId;
    private Integer likeCount;
}
