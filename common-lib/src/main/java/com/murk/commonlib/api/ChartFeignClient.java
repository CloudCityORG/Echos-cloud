package com.murk.commonlib.api;

import com.murk.commonlib.dto.CommentVo;
import com.murk.commonlib.dto.MyRankWithSong;
import com.murk.commonlib.dto.PersonalRank;
import com.murk.commonlib.dto.RankTag;
import com.murk.commonlib.pojo.Comment;
import com.murk.commonlib.pojo.RankTags;
import com.murk.commonlib.pojo.Singer;
import com.murk.commonlib.pojo.Song;
import com.murk.commonlib.pojo.Tags;
import com.murk.commonlib.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("service-chart")
public interface ChartFeignClient {

    /**
     * 批量查询榜单
     */
    @PostMapping("/api/chart/rank/batch")
    Result<List<MyRankWithSong>> getRanksByIds(@RequestBody List<Integer> rankIds);

    /**
     * 根据 tagName 查询标签
     */
    @GetMapping("/api/chart/tag/by-name")
    Result<Tags> getTagByName(@RequestParam("tagName") String tagName);

    /**
     * 根据 tagId 查询标签
     */
    @GetMapping("/api/chart/tag/{tagId}")
    Result<Tags> getTagById(@PathVariable("tagId") Integer tagId);

    /**
     * 根据 tagName 查询标签ID
     */
    @GetMapping("/api/chart/tag/id-by-name")
    Result<Integer> getTagIdByName(@RequestParam("tagName") String tagName);

    /**
     * 批量查询标签
     */
    @PostMapping("/api/chart/tag/batch")
    Result<List<Tags>> getTagsByIds(@RequestBody List<Integer> tagIds);

    /**
     * 新增标签，返回生成的主键 tagId
     */
    @PostMapping("/api/chart/tag")
    Result<Integer> addTag(@RequestBody Tags tags);

    /**
     * 标签使用次数 +1
     */
    @PostMapping("/api/chart/tag/{tagId}/increment")
    Result<Void> incrementTagUseCount(@PathVariable("tagId") Integer tagId);

    /**
     * 标签使用次数 -1
     */
    @PostMapping("/api/chart/tag/{tagId}/decrement")
    Result<Void> decrementTagUseCount(@PathVariable("tagId") Integer tagId);

    /**
     * 查询用户某榜单下关联的所有标签ID
     */
    @GetMapping("/api/chart/rank-tag/{rankId}/tag-ids")
    Result<List<Integer>> getTagIdsByRankIdAndUserId(@PathVariable("rankId") Integer rankId,
                                                     @RequestParam("userId") Integer userId);

    /**
     * 查询用户创建的榜单ID列表
     */
    @GetMapping("/api/chart/rank/ids")
    Result<List<Integer>> getRankIdsByUserId(@RequestParam("userId") Integer userId);

    /**
     * 查询榜单标签关联
     */
    @GetMapping("/api/chart/rank-tag/{rankId}/tag/{tagId}")
    Result<RankTag> getRankTag(@PathVariable("rankId") Integer rankId,
                               @PathVariable("tagId") Integer tagId,
                               @RequestParam("userId") Integer userId);

    /**
     * 新增榜单标签关联
     */
    @PostMapping("/api/chart/rank-tag")
    Result<Void> addRankTag(@RequestBody RankTag rankTag);

    /**
     * 删除榜单标签关联（按关联ID）
     */
    @DeleteMapping("/api/chart/rank-tag/{rtId}")
    Result<Void> deleteRankTagById(@PathVariable("rtId") Integer rtId);

    /**
     * 删除用户某标签下的所有榜单标签关联
     */
    @DeleteMapping("/api/chart/rank-tag/by-tag")
    Result<Void> deleteRankTagsByTagIdAndUserId(@RequestParam("tagId") Integer tagId,
                                                @RequestParam("userId") Integer userId);

    /**
     * 删除榜单下的某个标签关联
     */
    @DeleteMapping("/api/chart/rank-tag/{rankId}/tag/{tagId}")
    Result<Void> deleteRankTag(@PathVariable("rankId") Integer rankId,
                               @PathVariable("tagId") Integer tagId,
                               @RequestParam("userId") Integer userId);

    /**
     * 批量更新用户榜单标签关联（将旧标签替换为新标签）
     */
    @PutMapping("/api/chart/rank-tag/association")
    Result<Integer> updateRankTagAssociation(@RequestParam("userId") Integer userId,
                                             @RequestParam("oldTagId") Integer oldTagId,
                                             @RequestParam("targetTagId") Integer targetTagId);

    /**
     * 查询榜单下用户添加的所有标签
     */
    @GetMapping("/api/chart/rank-tag/{rankId}/tags")
    Result<List<Tags>> getTagsByRankIdAndUserId(@PathVariable("rankId") Integer rankId,
                                                @RequestParam("userId") Integer userId);

    /**
     * 查询热门标签（按使用次数倒排）
     */
    @GetMapping("/api/chart/tag/top")
    Result<List<Tags>> getTopTags(@RequestParam("limit") Integer limit);

    /**
     * 新增歌曲
     */
    @PostMapping("/api/chart/song")
    Result<Integer> addSong(@RequestBody Song song);

    /**
     * 新增歌手
     */
    @PostMapping("/api/chart/singer")
    Result<Integer> addSinger(@RequestBody Singer singer);

    /**
     * 根据关键词搜索个人榜单（仅匹配 chart 库内数据）
     */
    @GetMapping("/api/chart/rank/search")
    Result<List<MyRankWithSong>> searchRanks(@RequestParam("keyword") String keyword,
                                             @RequestParam("userId") Integer userId);

    /**
     * 查询所有榜单 ID
     */
    @GetMapping("/api/chart/rank/all-ids")
    Result<List<Integer>> getAllRankIds();

    /**
     * 根据 rankId 查询榜单
     */
    @GetMapping("/api/chart/rank/{rankId}")
    Result<PersonalRank> getRankById(@PathVariable("rankId") Integer rankId);

    /**
     * 查询榜单的评论列表
     */
    @GetMapping("/api/chart/rank/{rankId}/comments")
    Result<List<CommentVo>> getComments(@PathVariable("rankId") Integer rankId,
                                        @RequestParam("userId") Integer userId);

    /**
     * 根据 comId 查询评论
     */
    @GetMapping("/api/chart/comment/{comId}")
    Result<Comment> getCommentById(@PathVariable("comId") Integer comId);

    /**
     * 更新评论删除标记
     */
    @PutMapping("/api/chart/comment/{comId}")
    Result<Void> updateComment(@PathVariable("comId") Integer comId,
                               @RequestParam("idDelete") Integer idDelete);

    /**
     * 查询榜单下的所有标签
     */
    @GetMapping("/api/chart/rank/{rankId}/tags/all")
    Result<List<Tags>> getRankTags(@PathVariable("rankId") Integer rankId);

    /**
     * 查询榜单标签关联列表
     */
    @GetMapping("/api/chart/rank/{rankId}/rank-tags")
    Result<List<RankTags>> getRankTagList(@PathVariable("rankId") Integer rankId);
}
