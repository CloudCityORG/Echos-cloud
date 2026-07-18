package com.murk.serviceaudit.Service;

import com.murk.commonlib.dto.MyRankWithSong;
import com.murk.commonlib.dto.PersonalRank;
import com.murk.commonlib.dto.RankSong;
import com.murk.commonlib.dto.RankTags;

public interface AiCommentService {
    String getAiComment(MyRankWithSong myRankWithSong);
}
