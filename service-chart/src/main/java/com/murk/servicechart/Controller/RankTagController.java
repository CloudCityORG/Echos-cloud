package com.murk.servicechart.Controller;

import com.murk.commonlib.api.UserFeginClient;
import com.murk.commonlib.dto.RankTag;
import com.murk.commonlib.pojo.Tags;
import com.murk.commonlib.utils.Result;
import com.murk.commonlib.utils.ThreadLocalUtil;
import com.murk.servicechart.Mapper.TagsMapper;
import com.murk.servicechart.Mapper.rankTagRelMapper;
import com.murk.servicechart.Service.RankTagService;
import com.murk.servicechart.dto.TagOptionVO;
import com.murk.commonlib.dto.TagsDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;

@Slf4j
@RestController
@RequestMapping("/api/ranks")
@io.swagger.v3.oas.annotations.tags.Tag(name = "榜单tag管理")
@RequiredArgsConstructor
public class RankTagController {

    private final RankTagService rankTagService;
    private final UserFeginClient userFeginClient;
    private final rankTagRelMapper rankTagRelMapper;
    private final TagsMapper tagsMapper;

    @PostMapping("/{rankId}/tags")
    @Operation(summary = "添加标签到榜单")
    public Result addTagToRank(@PathVariable Integer rankId, @RequestBody @Valid TagsDTO tagDTO) {
        Integer userId = ThreadLocalUtil.get();
        log.info("添加标签到榜单 - 榜单ID: {}, 标签ID: {}", rankId, tagDTO.getTagId());

        List<Integer> tagIds = userFeginClient.getUserTagIds(userId).getData();
        log.info("用户 {} 的标签ID列表: {}", userId, tagIds);
        if (tagIds == null || !tagIds.contains(tagDTO.getTagId())) {
            return Result.error("无权操作：只能给自己的标签添加分类");
        }

        List<Integer> rankIds = rankTagRelMapper.selectRankIdsByUserId(userId);
        log.info("用户 {} 的榜单ID列表: {}", userId, rankIds);
        if (rankIds == null || !rankIds.contains(rankId)) {
            return Result.error("无权操作：只能给自己的榜单添加分类");
        }

        rankTagService.addTag(rankId, tagDTO);
        return Result.success();
    }

    /**
     * 查询回显 - 添加时的下拉标签页
     */
    @GetMapping("/tag-options")
    @Operation(summary = "查询标签选项")
    public Result<List<TagOptionVO>> getTagOptions() {
        Integer currentUserId = ThreadLocalUtil.get();
        if (currentUserId == null) {
            return Result.error("请先登录");
        }

        List<Integer> tagIds = userFeginClient.getUserTagIds(currentUserId).getData();
        if (tagIds == null || tagIds.isEmpty()) {
            return Result.success(List.of());
        }

        String ids = tagIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        List<Tags> myTags = tagsMapper.selectByIds(ids);
        if (myTags == null) {
            return Result.success(List.of());
        }

        List<TagOptionVO> options = myTags.stream()
                .map(tag -> new TagOptionVO(tag.getTagId(), tag.getTagName()))
                .collect(Collectors.toList());

        return Result.success(options);
    }

    /**
     * 获取某榜单的已经有的所有云标签
     */
    @GetMapping("/{rankId}/tags")
    @Operation(summary = "获取榜单标签")
    public Result<List<Tags>> getTagsByRank(@PathVariable Integer rankId) {
        return Result.success(rankTagService.getTagsByRank(rankId));
    }

    /**
     * 取消榜单标签
     */
    @DeleteMapping("/{rankId}/tags/{tagId}")
    @Operation(summary = "取消榜单标签")
    public Result cancelFromRanking(@PathVariable Integer rankId, @PathVariable Integer tagId) {
        Integer userId = ThreadLocalUtil.get();
        log.info("取消榜单关联 - 榜单ID: {}, 标签ID: {}", rankId, tagId);
        if (userId == null) {
            return Result.error("请先登录");
        }

        List<Integer> rankIds = rankTagRelMapper.selectRankIdsByUserId(userId);
        log.info("用户 {} 的榜单ID列表: {}", userId, rankIds);
        if (rankIds == null || !rankIds.contains(rankId)) {
            return Result.error("无权操作：只能取消自己榜单的标签");
        }

        RankTag relation = rankTagRelMapper.selectByRankingIdAndTagId(rankId, tagId, userId);
        if (relation != null) {
            rankTagRelMapper.deleteById(relation.getRTId());
        }
        return Result.success();
    }
}
