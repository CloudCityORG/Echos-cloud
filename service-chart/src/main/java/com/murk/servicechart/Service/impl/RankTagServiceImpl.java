package com.murk.servicechart.Service.impl;

import com.murk.commonlib.api.UserFeginClient;
import com.murk.commonlib.dto.RankTag;
import com.murk.commonlib.pojo.Tags;
import com.murk.commonlib.pojo.UserTags;
import com.murk.commonlib.utils.Result;
import com.murk.commonlib.utils.ThreadLocalUtil;
import com.murk.servicechart.Mapper.MyRankMapper;
import com.murk.servicechart.Mapper.rankTagRelMapper;
import com.murk.servicechart.Service.RankTagService;
import com.murk.commonlib.dto.TagsDTO;
import com.murk.commonlib.dto.PersonalRank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
public class RankTagServiceImpl implements RankTagService {

    @Autowired
    private rankTagRelMapper rankTagRelMapper;
    @Autowired
    private MyRankMapper myRankMapper;
    @Autowired
    private UserFeginClient userFeginClient;

    /**
     * 添加标签到榜单
     */
    @Override
    public void addTag(Integer rankId, TagsDTO tagDTO) {
        Integer userId = ThreadLocalUtil.get();

        // 更新 user 库中 user_tags 的使用次数
        Result<UserTags> userTagResult = userFeginClient.getUserTag(userId, tagDTO.getTagId());
        UserTags userTag = userTagResult != null ? userTagResult.getData() : null;
        if (userTag != null) {
            userTag.setUseCount(userTag.getUseCount() == null ? 1 : userTag.getUseCount() + 1);
            userFeginClient.updateUserTag(userTag);
        }

        RankTag rankTag = new RankTag();
        rankTag.setTagId(tagDTO.getTagId());
        rankTag.setRankId(rankId);
        rankTag.setUserId(userId);
        rankTag.setCreateTime(LocalDateTime.now());
        rankTagRelMapper.add(rankTag);
    }

    /**
     * 获取某榜单下用户添加的所有标签
     */
    @Override
    public List<Tags> getTagsByRank(Integer rankId) {
        PersonalRank personalRank = myRankMapper.selectRankById(rankId);
        if (personalRank == null) {
            log.warn("【获取标签】榜单不存在！rankId={}", rankId);
            return null;
        }
        Integer creatorId = myRankMapper.selectCreatorIdByRankId(rankId);
        return rankTagRelMapper.selectByRankingIdAndUserId(rankId, creatorId);
    }
}
