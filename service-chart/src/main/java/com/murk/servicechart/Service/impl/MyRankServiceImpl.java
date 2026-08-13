package com.murk.servicechart.Service.impl;

import com.github.pagehelper.PageInfo;
import com.murk.commonlib.api.UserFeginClient;
import com.murk.commonlib.api.VoteFeignClient;
import com.murk.commonlib.dto.CommentLikeStat;
import com.murk.commonlib.dto.UserNameVO;
import com.murk.commonlib.dto.VoteStats;
import com.murk.commonlib.pojo.Comment;
import com.murk.commonlib.pojo.Singer;
import com.murk.commonlib.pojo.Song;
import com.murk.commonlib.pojo.Tags;
import com.murk.commonlib.pojo.UserInfo;
import com.murk.commonlib.utils.Result;
import com.murk.commonlib.utils.ThreadLocalUtil;
import com.murk.servicechart.Mapper.MyRankMapper;
import com.murk.servicechart.Service.MyRankService;
import com.murk.servicechart.dto.CommentVo;
import com.murk.servicechart.dto.EditRank;
import com.murk.servicechart.dto.MyRankWithSong;
import com.murk.commonlib.dto.RankTagVO;
import com.murk.servicechart.dto.RankAddRequest;
import com.murk.commonlib.dto.RankSong;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MyRankServiceImpl implements MyRankService {
    @Autowired
    private MyRankMapper myRankMapper;
    @Autowired
    private VoteFeignClient voteFeignClient;
    @Autowired
    private UserFeginClient userFeginClient;

    @Override
    public PageInfo<MyRankWithSong> selectMyrank(Integer pageNum, Integer pageSize, Integer offset, Integer userId) {
        List<MyRankWithSong> list = myRankMapper.selectMyRank(userId, pageSize, offset);
        fillUsernames(list);
        fillVoteStats(list);
        Integer total = myRankMapper.selectMyRankTotal(userId);
        Integer pages = (total + pageSize - 1) / pageSize;
        PageInfo<MyRankWithSong> pageInfo = new PageInfo<>();
        pageInfo.setList(list);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        pageInfo.setTotal(total);
        pageInfo.setPages(pages);
        return pageInfo;
    }

    @Override
    public List<Singer> selectSinger(Integer categoryId) {
        return myRankMapper.selectSinger(categoryId);
    }

    @Override
    public List<Song> selectSong(Integer singerId) {
        return myRankMapper.selectSong(singerId);
    }

    @Override
    public MyRankWithSong getRank(Integer rankId) {
        System.out.println("看榜单详情榜单：" + rankId);
        Integer userId = ThreadLocalUtil.get();
        System.out.println("用户id：" + userId);

        MyRankWithSong myRankWithSong = myRankMapper.getRank(rankId);
        if (myRankWithSong != null) {
            List<MyRankWithSong> temp = Collections.singletonList(myRankWithSong);
            fillUsernames(temp);
            fillVoteStats(temp);
        }
        return myRankWithSong;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(EditRank dto, Integer rankId) {
        String rankName = dto.getRankName();
        myRankMapper.insertNewRankname(rankId, rankName);
        int deleteCount = myRankMapper.deleteOldRankname(rankId);
        List<RankAddRequest.RankSongItem> items = dto.getSongItems();
        List<RankSong> newSongs = new ArrayList<>();
        if (!CollectionUtils.isEmpty(items)) {
            for (RankAddRequest.RankSongItem item : items) {
                RankSong newSong = new RankSong();
                newSong.setSongId(item.getSongId());
                newSong.setRanking(item.getRanking());
                newSong.setRankId(rankId);
                newSongs.add(newSong);
            }
        }
        if (!CollectionUtils.isEmpty(newSongs)) {
            myRankMapper.insertNewRank(newSongs);
        }
        return true;
    }

    @Override
    public List<MyRankWithSong> getRanksByIds(List<Integer> rankIds) {
        if (rankIds == null || rankIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<MyRankWithSong> list = myRankMapper.getRankByIds(rankIds);
        fillUsernames(list);
        fillVoteStats(list);
        return list;
    }

    @Override
    public PageInfo<MyRankWithSong> selectMyLoverank(Integer pageNum, Integer pageSize, Integer offset, Integer userId) {
        Result<List<Integer>> idsResult = voteFeignClient.getLovedRankIds(userId, pageSize, offset);
        List<Integer> lovedRankIds = idsResult.getData();
        if (lovedRankIds == null || lovedRankIds.isEmpty()) {
            PageInfo<MyRankWithSong> empty = new PageInfo<>();
            empty.setList(Collections.emptyList());
            empty.setPageNum(pageNum);
            empty.setPageSize(pageSize);
            empty.setTotal(0);
            empty.setPages(0);
            return empty;
        }
        List<MyRankWithSong> myLoveSong = myRankMapper.selectLoveSong(lovedRankIds);
        fillUsernames(myLoveSong);
        fillVoteStats(myLoveSong);
        PageInfo<MyRankWithSong> pageInfo = new PageInfo<>();
        pageInfo.setList(myLoveSong);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        pageInfo.setTotal(myLoveSong.size());
        pageInfo.setPages(1);
        System.out.println("查询出的喜欢的榜单数：" + pageInfo.getList().size());
        return pageInfo;
    }

    @Override
    public UserInfo selectByuserId(Integer userId) {
        Result<String> result = userFeginClient.getUsernameById(userId);
        if (result == null || result.getData() == null) {
            return null;
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setId(userId);
        userInfo.setUsername(result.getData());
        return userInfo;
    }

    @Override
    public List<CommentVo> selectComment(Integer rankId, Integer userId) {
        List<CommentVo> list = myRankMapper.selectComment(rankId, userId);
        fillCommentUsernames(list);
        fillCommentLikes(list, userId);
        System.out.println("查询出的评论数：" + list.size());
        return list;
    }

    @Override
    @Transactional
    public boolean deleteComment(Integer comId, Integer userId) {
        Comment comment = myRankMapper.selectCommentById(comId);
        if (comment == null) {
            return false;
        }
        if (comment.getUserId() != userId) {
            throw new RuntimeException("只能删除自己的评论");
        }
        int result = myRankMapper.updateComment(comId, 1);
        return result > 0;
    }

    private void fillVoteStats(List<MyRankWithSong> ranks) {
        if (ranks == null || ranks.isEmpty()) {
            return;
        }
        String rankIds = ranks.stream()
                .map(MyRankWithSong::getRankId)
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        if (rankIds.isEmpty()) {
            return;
        }
        Result<List<VoteStats>> result = voteFeignClient.getVoteStats(rankIds);
        if (result.getData() == null) {
            return;
        }
        Map<Integer, VoteStats> statsMap = result.getData().stream()
                .collect(Collectors.toMap(VoteStats::getRankId, s -> s, (a, b) -> a));
        ranks.forEach(rank -> {
            VoteStats stats = statsMap.get(rank.getRankId());
            if (stats != null) {
                rank.setVoteCount(stats.getVoteCount());
                rank.setLoveCount(stats.getLoveCount());
            }
        });
    }

    private void fillCommentLikes(List<CommentVo> comments, Integer userId) {
        if (comments == null || comments.isEmpty()) {
            return;
        }
        String comIds = comments.stream()
                .map(CommentVo::getComId)
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        if (!comIds.isEmpty()) {
            Result<List<CommentLikeStat>> likeResult = voteFeignClient.getCommentLikeStats(comIds);
            if (likeResult.getData() != null) {
                Map<Integer, Integer> likeMap = likeResult.getData().stream()
                        .collect(Collectors.toMap(CommentLikeStat::getComId, CommentLikeStat::getLikeCount, (a, b) -> a));
                comments.forEach(c -> c.setLikeCount(likeMap.getOrDefault(c.getComId(), 0)));
            }
        }
        if (userId != null) {
            for (CommentVo c : comments) {
                Result<Boolean> result = voteFeignClient.hasLike(userId, c.getComId());
                c.setIsLiked((result.getData() != null && result.getData()) ? 1 : 0);
            }
        }
    }

    private void fillUsernames(List<MyRankWithSong> ranks) {
        if (ranks == null || ranks.isEmpty()) {
            return;
        }
        List<Integer> userIds = ranks.stream()
                .map(MyRankWithSong::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (userIds.isEmpty()) {
            return;
        }
        Map<Integer, String> usernameMap = batchGetUsernames(userIds);
        ranks.forEach(rank -> rank.setUsername(usernameMap.get(rank.getUserId())));
    }

    private void fillCommentUsernames(List<CommentVo> comments) {
        if (comments == null || comments.isEmpty()) {
            return;
        }
        Set<Integer> userIdSet = new HashSet<>();
        for (CommentVo c : comments) {
            if (c.getUserId() != null) {
                userIdSet.add(c.getUserId());
            }
            if (c.getParentUserId() != null) {
                userIdSet.add(c.getParentUserId());
            }
        }
        if (userIdSet.isEmpty()) {
            return;
        }
        Map<Integer, String> usernameMap = batchGetUsernames(new ArrayList<>(userIdSet));
        for (CommentVo c : comments) {
            c.setUsername(usernameMap.get(c.getUserId()));
            c.setParentUsername(usernameMap.get(c.getParentUserId()));
        }
    }

    private Map<Integer, String> batchGetUsernames(List<Integer> userIds) {
        Result<List<UserNameVO>> result = userFeginClient.getUserByIds(userIds);
        if (result == null || result.getData() == null) {
            return Collections.emptyMap();
        }
        return result.getData().stream()
                .collect(Collectors.toMap(UserNameVO::getUserId, UserNameVO::getUserName, (a, b) -> a));
    }
}
