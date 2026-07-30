package com.murk.serviceuser.Service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.murk.commonlib.api.ChartFeignClient;
import com.murk.commonlib.dto.RankTag;
import com.murk.commonlib.pojo.Tags;
import com.murk.commonlib.pojo.UserTags;
import com.murk.commonlib.result.PageResult;
import com.murk.commonlib.utils.Result;
import com.murk.commonlib.utils.ThreadLocalUtil;
import com.murk.commonlib.dto.TagsDTO;
import com.murk.commonlib.dto.TagsPageQueryDTO;
import com.murk.serviceuser.Mapper.UserTagsMapper;
import com.murk.serviceuser.Service.TagsService;
import com.murk.serviceuser.pojo.UserTagVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TagsServiceImpl implements TagsService {

    @Autowired
    private UserTagsMapper userTagsMapper;
    @Autowired
    private ChartFeignClient chartFeignClient;

    @Transactional
    public void cancelFromRanking(Integer rankId, Integer tagId) {
        Integer currentUserId = ThreadLocalUtil.get();

        Result<RankTag> relationResult = chartFeignClient.getRankTag(rankId, tagId, currentUserId);
        RankTag relation = relationResult != null ? relationResult.getData() : null;

        if (relation == null) {
            throw new RuntimeException("该榜单下不存在此标签关联");
        }

        if (relation.getUserId() != currentUserId) {
            throw new RuntimeException("无权取消他人添加的标签！");
        }

        chartFeignClient.deleteRankTagById(relation.getRTId());
    }

    @Transactional
    public void add(TagsDTO tagsDTO, Integer userId) {
        String tagName = tagsDTO.getTagName();
        Result<Tags> tagResult = chartFeignClient.getTagByName(tagName);
        Tags tags = tagResult != null ? tagResult.getData() : null;

        if (tags == null) {
            tags = new Tags();
            tags.setTagName(tagName);
            tags.setUseCount(1);
            Result<Integer> addResult = chartFeignClient.addTag(tags);
            Integer tagId = addResult != null ? addResult.getData() : null;
            tags.setTagId(tagId);
            log.info("添加啦 tag ： {}", tagsDTO);
            if (tags.getTagId() == null) {
                log.error("好小子！！阴我");
                throw new RuntimeException("添加标签失败");
            } else {
                log.info("标签 [{}] 新建成功，ID: {}", tagName, tags.getTagId());
            }
        } else {
            log.info("标签 [{}] 已存在，ID: {}", tagName, tags.getTagId());
            chartFeignClient.incrementTagUseCount(tags.getTagId());
        }

        UserTags userTag = userTagsMapper.selectByUserIdAndTagId(userId, tags.getTagId());
        if (userTag == null) {
            userTag = new UserTags();
            userTag.setUserId(userId);
            userTag.setTagId(tags.getTagId());
            userTag.setUseCount(1);
            userTag.setCreateTime(LocalDateTime.now());
            userTag.setStatus(1);
            userTagsMapper.add(userTag);
        } else {
            userTag.setUseCount(userTag.getUseCount() + 1);
            userTagsMapper.update(userTag);
        }
        log.info("添加成功,关联了捏！！");
    }

    public PageResult pageQueryUserTags(TagsPageQueryDTO tagsPageQueryDTO) {
        Integer userId = ThreadLocalUtil.get();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }

        PageHelper.startPage(tagsPageQueryDTO.getPageNum(), tagsPageQueryDTO.getPageSize());
        String tagName = null;
        if (tagsPageQueryDTO != null) {
            tagName = tagsPageQueryDTO.getTagName();
        }
        List<UserTagVO> userTagVOList = userTagsMapper.selectUserTagsWithDetail(userId, tagName);

        Page<UserTagVO> page = (Page<UserTagVO>) userTagVOList;

        long total = page.getTotal();
        List<UserTagVO> records = page.getResult();

        // 批量补充标签名（避免 user_tags 与 tags_dictionary 跨库 JOIN）
        fillTagNames(records);

        return new PageResult(total, records);
    }

    private void fillTagNames(List<UserTagVO> records) {
        if (records == null || records.isEmpty()) {
            return;
        }
        List<Integer> tagIds = records.stream()
                .map(UserTagVO::getTagId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (tagIds.isEmpty()) {
            return;
        }
        Result<List<Tags>> tagsResult = chartFeignClient.getTagsByIds(tagIds);
        if (tagsResult == null || tagsResult.getData() == null) {
            return;
        }
        Map<Integer, String> tagNameMap = tagsResult.getData().stream()
                .collect(Collectors.toMap(Tags::getTagId, Tags::getTagName, (a, b) -> a));
        for (UserTagVO vo : records) {
            vo.setTagName(tagNameMap.get(vo.getTagId()));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Integer tagId) {
        Integer currentUserId = ThreadLocalUtil.get();

        UserTags tag = userTagsMapper.selectByIdinuser(tagId, currentUserId);
        Result<Tags> tagsResult = chartFeignClient.getTagById(tagId);
        Tags tags = tagsResult != null ? tagsResult.getData() : null;
        if (tags == null) {
            log.warn("尝试删除不存在的标签: {}", tagId);
            return;
        }
        if (tags.getUseCount() > 0) {
            tags.setUseCount(tags.getUseCount() - 1);
        }
        log.info("删除标签：{}", tagId);
        chartFeignClient.deleteRankTagsByTagIdAndUserId(tagId, currentUserId);
        userTagsMapper.deleteByTagIdinuser(tagId, currentUserId);
        chartFeignClient.decrementTagUseCount(tagId);
        log.info("删除成功");
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(TagsDTO tagsDTO, Integer userId) {
        Integer targetTagId;
        Integer oldTagId = tagsDTO.getTagId();
        String newTagName = tagsDTO.getTagName();

        Result<Integer> targetTagIdResult = chartFeignClient.getTagIdByName(newTagName);
        targetTagId = targetTagIdResult != null ? targetTagIdResult.getData() : null;

        Tags tag = new Tags();
        tag.setTagName(newTagName);
        tag.setUseCount(1);
        if (targetTagId == null) {
            Result<Integer> addResult = chartFeignClient.addTag(tag);
            targetTagId = addResult != null ? addResult.getData() : null;
            if (targetTagId == null) {
                targetTagIdResult = chartFeignClient.getTagIdByName(newTagName);
                targetTagId = targetTagIdResult != null ? targetTagIdResult.getData() : null;
            }
            log.info("没查到 新标签已添加，ID: {}", targetTagId);
        }
        if (targetTagId == null) {
            throw new RuntimeException("标签创建失败");
        }

        int rows = userTagsMapper.updateUserTagAssociation(userId, oldTagId, targetTagId);
        log.info("用户 [{}] 的标签 [{}] 已更新为 [{}]", userId, oldTagId, targetTagId);
        Result<Integer> rankUpdateResult = chartFeignClient.updateRankTagAssociation(userId, oldTagId, targetTagId);
        int rows2 = rankUpdateResult != null ? rankUpdateResult.getData() : 0;
        log.info("用户 [{}] 的榜单标签 [{}] 已更新为 [{}]", userId, oldTagId, targetTagId);

        if (rows == 0) {
            throw new RuntimeException("用户未拥有该标签");
        }
        if (rows2 == 0) {
            log.warn("用户 [{}] 没有该榜单标签 [{}]", userId, oldTagId);
        }
    }

    public PageResult planetary() {
        Result<List<Tags>> topTags = chartFeignClient.getTopTags(10);
        List<Tags> tags = topTags != null ? topTags.getData() : new ArrayList<>();
        return new PageResult(10, tags);
    }
}
