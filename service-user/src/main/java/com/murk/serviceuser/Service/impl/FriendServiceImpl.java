package com.murk.serviceuser.Service.impl;

import com.murk.commonlib.api.VoteFeignClient;
import com.murk.commonlib.dto.CommonLoveVO;
import com.murk.commonlib.utils.Result;
import com.murk.serviceuser.Mapper.FriendMapper;
import com.murk.serviceuser.Mapper.UserMapper;
import com.murk.serviceuser.Service.FriendService;
import com.murk.serviceuser.dto.UserSimpleVO;
import com.murk.serviceuser.pojo.UserFollow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FriendServiceImpl implements FriendService {

    @Autowired
    private FriendMapper friendMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private VoteFeignClient voteFeignClient;

    @Override
    public CommonLoveVO getCommonLoveRank(Integer userId, Integer rankId, Integer sampleLimit) {
        Integer limit = (sampleLimit == null || sampleLimit <= 0) ? 5 : Math.min(sampleLimit, 50);

        // 1. 从 user 库查询互关好友ID
        List<Integer> mutualFriendIds = friendMapper.selectMutualFriendIds(userId, limit);
        if (mutualFriendIds == null || mutualFriendIds.isEmpty()) {
            CommonLoveVO vo = new CommonLoveVO();
            vo.setCommonLoveCount(0);
            vo.setUsernames(Collections.emptyList());
            return vo;
        }

        // 2. 调用 vote 服务逐个判断好友是否收藏该榜单
        List<Integer> loverIds = mutualFriendIds.stream()
                .filter(Objects::nonNull)
                .filter(friendId -> {
                    try {
                        Result<Boolean> result = voteFeignClient.hasLove(friendId, rankId);
                        return result != null && result.getData() != null && Boolean.TRUE.equals(result.getData());
                    } catch (Exception e) {
                        log.warn("调用 vote 服务查询好友收藏状态失败，friendId={}，rankId={}", friendId, rankId, e);
                        return false;
                    }
                })
                .collect(Collectors.toList());

        int count = loverIds.size();
        List<String> usernames = loverIds.stream()
                .map(friendId -> userMapper.getUsernameById(friendId))
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        CommonLoveVO vo = new CommonLoveVO();
        vo.setCommonLoveCount(count);
        vo.setUsernames(usernames);
        return vo;
    }

    @Override
    @Transactional
    public boolean isFollowed(Integer UserId, Integer targetUserId) {
        UserFollow userFollow = friendMapper.isFollowed(UserId, targetUserId);
        if(userFollow == null){
            UserFollow userFollow1 = new UserFollow();
            userFollow1.setUserId(UserId);
            userFollow1.setFollowId(targetUserId);
            userFollow1.setIsMutual(0);
            userFollow1.setCreateTime(LocalDateTime.now());
            friendMapper.add(userFollow1);

            UserFollow userFollow2 = friendMapper.isFollowed(targetUserId, UserId);
            if(userFollow2 != null){
                userFollow2.setIsMutual(1);
                friendMapper.update(userFollow2);
                userFollow1.setIsMutual(1);
                friendMapper.update(userFollow1);
            }
            return true;

        } else {
            friendMapper.delete(UserId, targetUserId);
            return false;
        }
    }

    @Override
    public UserSimpleVO getMymaster(Integer currentUserId) {
        UserSimpleVO userSimpleVO = new UserSimpleVO();
        userSimpleVO.setUserId(currentUserId);
        userSimpleVO.setUsername(userMapper.getUsernameById(currentUserId));
        userSimpleVO.setMasterList(friendMapper.selectFollowList(currentUserId, currentUserId)
                .stream()
                .map(userFollow -> userMapper.getUsernameById(userFollow.getFollowId()))
                .collect(Collectors.toList()));
        return userSimpleVO;
    }

     @Override
    public UserSimpleVO getMyFans(Integer currentUserId) {
        UserSimpleVO userSimpleVO = new UserSimpleVO();
        userSimpleVO.setUserId(currentUserId);
        userSimpleVO.setUsername(userMapper.getUsernameById(currentUserId));
        userSimpleVO.setFanList(
                friendMapper.selectFanList(currentUserId,currentUserId).
                        stream().
                        map(
                                userFollow -> userMapper.getUsernameById(userFollow.getUserId())
                        ).collect(Collectors.toList()));
        return userSimpleVO;
    }

}
