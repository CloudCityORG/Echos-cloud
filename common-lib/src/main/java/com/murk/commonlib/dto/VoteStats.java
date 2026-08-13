package com.murk.commonlib.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 某个榜单的投票数、收藏数统计
 */
//嘻嘻我依旧不会依旧助我一臂之力
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteStats {
    private Integer rankId;
    private Integer voteCount;
    private Integer loveCount;
}
