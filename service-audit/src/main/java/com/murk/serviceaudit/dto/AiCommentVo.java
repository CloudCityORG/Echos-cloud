package com.murk.serviceaudit.dto;

import com.murk.commonlib.dto.PersonalRank;
import com.murk.commonlib.dto.RankSong;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor  // 必须：Jackson 需要无参构造
@AllArgsConstructor
public class AiCommentVo {
    private PersonalRank personalRank;
    private com.murk.commonlib.dto.RankTag rankTags;
    private RankSong rankSong;
}
