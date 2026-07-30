package com.murk.serviceuser.Service;

import com.murk.commonlib.dto.CommonLoveVO;
import com.murk.serviceuser.dto.UserSimpleVO;

public interface FriendService {

    /**
     * 查询：互关好友共同收藏（收藏同一个榜单）
     */
    CommonLoveVO getCommonLoveRank(Integer userId, Integer rankId, Integer sampleLimit);

    /**
     * 操作：关注/取消关注
     */
    boolean isFollowed(Integer currentUserId, Integer targetUserId);

    UserSimpleVO getMyFans(Integer currentUserId);

    UserSimpleVO getMymaster(Integer currentUserId);
}

