package com.murk.commonlib.api;

import com.murk.commonlib.dto.CommentLikeStat;
import com.murk.commonlib.dto.VoteStats;
import com.murk.commonlib.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 供其他服务调用 service-vote 的接口
 */
@FeignClient(name = "service-vote")
public interface VoteFeignClient {

    /**
     * 批量查询多个榜单的 vote/love 统计
     *
     * @param rankIds 逗号分隔的 rankId，例如 "1,2,3"
     */
    @GetMapping("/api/vote/stats")
    Result<List<VoteStats>> getVoteStats(@RequestParam("rankIds") String rankIds);

    /**
     * 投票
     */
    @PostMapping("/api/vote/insertVote")
    Result<Boolean> insertVote(@RequestParam("userId") Integer userId,
                               @RequestParam("rankId") Integer rankId);

    /**
     * 取消投票
     */
    @DeleteMapping("/api/vote/deleteVote")
    Result<Boolean> deleteVote(@RequestParam("userId") Integer userId,
                               @RequestParam("rankId") Integer rankId);

    /**
     * 收藏
     */
    @PostMapping("/api/vote/insertLove")
    Result<Boolean> insertLove(@RequestParam("userId") Integer userId,
                               @RequestParam("rankId") Integer rankId);

    /**
     * 取消收藏
     */
    @DeleteMapping("/api/vote/deleteLove")
    Result<Boolean> deleteLove(@RequestParam("userId") Integer userId,
                               @RequestParam("rankId") Integer rankId);

    /**
     * 评论点赞
     */
    @PostMapping("/api/vote/insertLike")
    Result<Boolean> insertLike(@RequestParam("userId") Integer userId,
                               @RequestParam("comId") Integer comId);

    /**
     * 取消评论点赞
     */
    @DeleteMapping("/api/vote/deleteLike")
    Result<Boolean> deleteLike(@RequestParam("userId") Integer userId,
                               @RequestParam("comId") Integer comId);

    /**
     * 更新热度榜
     */
    @PostMapping("/api/vote/updateHotRank")
    Result<Void> updateHotRank(@RequestParam("rankId") Integer rankId);

    /**
     * 用户是否已对某榜单投票
     */
    @GetMapping("/api/vote/hasVote")
    Result<Boolean> hasVote(@RequestParam("userId") Integer userId,
                            @RequestParam("rankId") Integer rankId);

    /**
     * 用户是否已收藏某榜单
     */
    @GetMapping("/api/vote/hasLove")
    Result<Boolean> hasLove(@RequestParam("userId") Integer userId,
                            @RequestParam("rankId") Integer rankId);

    /**
     * 获取用户收藏的 rankId 列表（分页）
     */
    @GetMapping("/api/vote/lovedRankIds")
    Result<List<Integer>> getLovedRankIds(@RequestParam("userId") Integer userId,
                                          @RequestParam("pageSize") Integer pageSize,
                                          @RequestParam("offset") Integer offset);

    /**
     * 批量查询评论点赞数
     *
     * @param comIds 逗号分隔的 comId，例如 "1,2,3"
     */
    @GetMapping("/api/vote/commentLikes")
    Result<List<CommentLikeStat>> getCommentLikeStats(@RequestParam("comIds") String comIds);

    /**
     * 用户是否已点赞某评论
     */
    @GetMapping("/api/vote/hasLike")
    Result<Boolean> hasLike(@RequestParam("userId") Integer userId,
                            @RequestParam("comId") Integer comId);
}
