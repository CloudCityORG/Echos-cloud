package com.murk.servicechart.Controller;

import com.murk.commonlib.dto.RankTag;
import com.murk.commonlib.pojo.Tags;
import com.murk.commonlib.utils.Result;
import com.murk.servicechart.Mapper.TagsMapper;
import com.murk.servicechart.Mapper.rankTagRelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 供其它微服务调用 service-chart 的 tag/rank_tags 接口
 */
@RestController
@RequestMapping("/api/chart")
public class TagApiController {

    @Autowired
    private TagsMapper tagsMapper;
    @Autowired
    private rankTagRelMapper rankTagRelMapper;

    // ========== tags_dictionary ==========

    @GetMapping("/tag/by-name")
    public Result<Tags> getTagByName(@RequestParam("tagName") String tagName) {
        return Result.success(tagsMapper.selectTagsByName(tagName));
    }

    @GetMapping("/tag/{tagId}")
    public Result<Tags> getTagById(@PathVariable("tagId") Integer tagId) {
        return Result.success(tagsMapper.selectById(tagId));
    }

    @GetMapping("/tag/id-by-name")
    public Result<Integer> getTagIdByName(@RequestParam("tagName") String tagName) {
        return Result.success(tagsMapper.selectTagIdByName(tagName));
    }

    @PostMapping("/tag/batch")
    public Result<List<Tags>> getTagsByIds(@RequestBody List<Integer> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return Result.success(List.of());
        }
        String ids = tagIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        return Result.success(tagsMapper.selectByIds(ids));
    }

    @PostMapping("/tag")
    public Result<Integer> addTag(@RequestBody Tags tags) {
        tagsMapper.add(tags);
        return Result.success(tags.getTagId());
    }

    @PostMapping("/tag/{tagId}/increment")
    public Result<Void> incrementTagUseCount(@PathVariable("tagId") Integer tagId) {
        tagsMapper.incrementUseCount(tagId);
        return Result.success();
    }

    @PostMapping("/tag/{tagId}/decrement")
    public Result<Void> decrementTagUseCount(@PathVariable("tagId") Integer tagId) {
        tagsMapper.updateUseCountfu(tagId);
        return Result.success();
    }

    @GetMapping("/tag/top")
    public Result<List<Tags>> getTopTags(@RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        return Result.success(tagsMapper.planetary(limit));
    }

    // ========== rank_tags ==========

    @GetMapping("/rank/ids")
    public Result<List<Integer>> getRankIdsByUserId(@RequestParam("userId") Integer userId) {
        return Result.success(rankTagRelMapper.selectRankIdsByUserId(userId));
    }

    @GetMapping("/rank-tag/{rankId}/tag-ids")
    public Result<List<Integer>> getTagIdsByRankIdAndUserId(@PathVariable("rankId") Integer rankId,
                                                            @RequestParam("userId") Integer userId) {
        return Result.success(rankTagRelMapper.selectTagIdsByRankIdAndUserId(rankId, userId));
    }

    @GetMapping("/rank-tag/{rankId}/tags")
    public Result<List<Tags>> getTagsByRankIdAndUserId(@PathVariable("rankId") Integer rankId,
                                                       @RequestParam("userId") Integer userId) {
        return Result.success(rankTagRelMapper.selectByRankingIdAndUserId(rankId, userId));
    }

    @GetMapping("/rank-tag/{rankId}/tag/{tagId}")
    public Result<RankTag> getRankTag(@PathVariable("rankId") Integer rankId,
                                      @PathVariable("tagId") Integer tagId,
                                      @RequestParam("userId") Integer userId) {
        return Result.success(rankTagRelMapper.selectByRankingIdAndTagId(rankId, tagId, userId));
    }

    @PostMapping("/rank-tag")
    public Result<Void> addRankTag(@RequestBody RankTag rankTag) {
        rankTagRelMapper.add(rankTag);
        return Result.success();
    }

    @DeleteMapping("/rank-tag/{rtId}")
    public Result<Void> deleteRankTagById(@PathVariable("rtId") Integer rtId) {
        rankTagRelMapper.deleteById(rtId);
        return Result.success();
    }

    @DeleteMapping("/rank-tag/by-tag")
    public Result<Void> deleteRankTagsByTagIdAndUserId(@RequestParam("tagId") Integer tagId,
                                                       @RequestParam("userId") Integer userId) {
        rankTagRelMapper.deleteByTagIdinuser(tagId, userId);
        return Result.success();
    }

    @DeleteMapping("/rank-tag/{rankId}/tag/{tagId}")
    public Result<Void> deleteRankTag(@PathVariable("rankId") Integer rankId,
                                      @PathVariable("tagId") Integer tagId,
                                      @RequestParam("userId") Integer userId) {
        RankTag relation = rankTagRelMapper.selectByRankingIdAndTagId(rankId, tagId, userId);
        if (relation != null) {
            rankTagRelMapper.deleteById(relation.getRTId());
        }
        return Result.success();
    }

    @PutMapping("/rank-tag/association")
    public Result<Integer> updateRankTagAssociation(@RequestParam("userId") Integer userId,
                                                    @RequestParam("oldTagId") Integer oldTagId,
                                                    @RequestParam("targetTagId") Integer targetTagId) {
        return Result.success(rankTagRelMapper.updateRankTagAssociation(userId, oldTagId, targetTagId));
    }
}
