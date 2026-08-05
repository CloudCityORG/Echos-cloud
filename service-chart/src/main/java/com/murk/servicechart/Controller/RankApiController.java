package com.murk.servicechart.Controller;

import com.murk.commonlib.api.UserFeginClient;
import com.murk.commonlib.dto.CommentVo;
import com.murk.commonlib.dto.MyRankWithSong;
import com.murk.commonlib.dto.PersonalRank;
import com.murk.commonlib.dto.UserNameVO;
import com.murk.commonlib.pojo.Comment;
import com.murk.commonlib.pojo.RankTags;
import com.murk.commonlib.pojo.Tags;
import com.murk.commonlib.utils.Result;
import com.murk.servicechart.Mapper.UserPublishMapper;
import com.murk.servicechart.Mapper.rankTagRelMapper;
import com.murk.servicechart.Service.MyRankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 供其它微服务调用 service-chart 的榜单接口
 */
@RestController
@RequestMapping("/api/chart")
public class RankApiController {

    @Autowired
    private UserPublishMapper userPublishMapper;
    @Autowired
    private rankTagRelMapper rankTagRelMapper;
    @Autowired
    private UserFeginClient userFeginClient;
    @Autowired
    private MyRankService myRankService;

    @PostMapping("/rank/batch")
    public Result<List<com.murk.commonlib.dto.MyRankWithSong>> getRanksByIds(@RequestBody List<Integer> rankIds) {
        return Result.success(myRankService.getRanksByIds(rankIds).stream()
                .map(this::convert)
                .collect(Collectors.toList()));
    }

    @GetMapping("/rank/search")
    public Result<List<com.murk.commonlib.dto.MyRankWithSong>> searchRanks(@RequestParam("keyword") String keyword,
                                                                           @RequestParam("userId") Integer userId) {
        return Result.success(userPublishMapper.searchRanksByUser(keyword, userId).stream()
                .map(this::convert)
                .collect(Collectors.toList()));
    }

    private com.murk.commonlib.dto.MyRankWithSong convert(com.murk.servicechart.dto.MyRankWithSong source) {
        com.murk.commonlib.dto.MyRankWithSong target = new com.murk.commonlib.dto.MyRankWithSong();
        target.setRankId(source.getRankId());
        target.setRankName(source.getRankName());
        target.setVoteCount(source.getVoteCount());
        target.setUsername(source.getUsername());
        target.setUserId(source.getUserId());
        target.setLoveCount(source.getLoveCount());
        target.setPublishTime(source.getPublishTime());
        target.setRankSongList(source.getRankSongList());
        target.setFollowed(source.isFollowed());
        target.setRankTagVOList(source.getRankTagVOList());
        target.setRankTagsList(source.getRankTagsList());
        target.setUserTagList(source.getUserTagList());
        return target;
    }

    @GetMapping("/rank/all-ids")
    public Result<List<Integer>> getAllRankIds() {
        return Result.success(userPublishMapper.selectAllRankIds());
    }

    @GetMapping("/rank/list")
    public Result<List<com.murk.commonlib.dto.MyRankWithSong>> listRanks(
            @RequestParam(value = "category", required = false) Integer category) {
        List<com.murk.servicechart.dto.MyRankWithSong> source;
        if (category != null) {
            source = userPublishMapper.selectPublishRank(category, Integer.MAX_VALUE, 0);
        } else {
            source = myRankService.getRanksByIds(userPublishMapper.selectAllRankIds());
        }
        return Result.success(source.stream()
                .map(this::convert)
                .collect(Collectors.toList()));
    }

    @GetMapping("/rank/{rankId}")
    public Result<PersonalRank> getRankById(@PathVariable("rankId") Integer rankId) {
        return Result.success(userPublishMapper.selectRankById(rankId));
    }

    @GetMapping("/rank/{rankId}/comments")
    public Result<List<com.murk.commonlib.dto.CommentVo>> getComments(@PathVariable("rankId") Integer rankId,
                                               @RequestParam("userId") Integer userId) {
        List<com.murk.servicechart.dto.CommentVo> list = userPublishMapper.selectComment(rankId, userId);
        List<Integer> userIds = list.stream()
                .map(com.murk.servicechart.dto.CommentVo::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        List<Integer> parentUserIds = list.stream()
                .map(com.murk.servicechart.dto.CommentVo::getParentUserId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        userIds.addAll(parentUserIds);
        if (!userIds.isEmpty()) {
            Result<List<UserNameVO>> nameResult = userFeginClient.getUserByIds(userIds);
            if (nameResult != null && nameResult.getData() != null) {
                Map<Integer, String> nameMap = nameResult.getData().stream()
                        .collect(Collectors.toMap(UserNameVO::getUserId, UserNameVO::getUserName));
                for (com.murk.servicechart.dto.CommentVo vo : list) {
                    vo.setUsername(nameMap.get(vo.getUserId()));
                    vo.setParentUsername(nameMap.get(vo.getParentUserId()));
                }
            }
        }
        return Result.success(list.stream()
                .map(this::convert)
                .collect(Collectors.toList()));
    }

    private com.murk.commonlib.dto.CommentVo convert(com.murk.servicechart.dto.CommentVo vo) {
        com.murk.commonlib.dto.CommentVo target = new com.murk.commonlib.dto.CommentVo();
        target.setComId(vo.getComId());
        target.setComContent(vo.getComContent());
        target.setUserId(vo.getUserId());
        target.setRankId(vo.getRankId());
        target.setUsername(vo.getUsername());
        target.setComTime(vo.getComTime());
        target.setParentId(vo.getParentId());
        target.setLikeCount(vo.getLikeCount());
        target.setIsLiked(vo.getIsLiked());
        target.setParentUsername(vo.getParentUsername());
        return target;
    }

    @GetMapping("/comment/{comId}")
    public Result<Comment> getCommentById(@PathVariable("comId") Integer comId) {
        return Result.success(userPublishMapper.selectCommentById(comId));
    }

    @PutMapping("/comment/{comId}")
    public Result<Void> updateComment(@PathVariable("comId") Integer comId,
                                      @RequestParam("idDelete") Integer idDelete) {
        userPublishMapper.updateComment(comId, idDelete);
        return Result.success();
    }

    @GetMapping("/rank/{rankId}/tags/all")
    public Result<List<Tags>> getRankTags(@PathVariable("rankId") Integer rankId) {
        return Result.success(rankTagRelMapper.selectRankTags(rankId));
    }

    @GetMapping("/rank/{rankId}/rank-tags")
    public Result<List<RankTags>> getRankTagList(@PathVariable("rankId") Integer rankId) {
        return Result.success(rankTagRelMapper.selectRankTagList(rankId));
    }
}
