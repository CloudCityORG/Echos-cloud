package com.murk.servicechart.Service.impl;

import com.github.pagehelper.PageInfo;
import com.murk.commonlib.api.AuditFeignClient;
import com.murk.commonlib.api.UserFeginClient;
import com.murk.commonlib.api.VoteFeignClient;
import com.murk.commonlib.dto.CommentLikeStat;
import com.murk.commonlib.dto.UserNameVO;
import com.murk.commonlib.dto.VoteStats;
import com.murk.commonlib.pojo.Comment;
import com.murk.commonlib.pojo.Singer;
import com.murk.commonlib.pojo.Song;
import com.murk.commonlib.utils.Result;
import com.murk.servicechart.Mapper.UserPublishMapper;
import com.murk.servicechart.Service.UserPublishService;
import com.murk.servicechart.dto.CommentVo;
import com.murk.servicechart.dto.MyRankWithSong;
import com.murk.servicechart.dto.RankAddRequest;
import com.murk.commonlib.dto.PersonalRank;
import com.murk.commonlib.dto.RankSong;
import com.murk.commonlib.utils.ThreadLocalUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class UserPublishServiceImpl implements UserPublishService {
    private static final Logger log = LoggerFactory.getLogger(UserPublishServiceImpl.class);
    @Autowired
    private UserPublishMapper userPublishMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private VoteFeignClient voteFeignClient;
    @Autowired
    private UserFeginClient userFeginClient;
    @Autowired
    private AuditFeignClient auditFeignClient;

    private static final String Love_Key="love:user:%d:rank:%d";
    private static final String Vote_Key="vote:user:%d:rank:%d";

    @Override
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public Integer insertRank(Integer categoryId, Integer userId, RankAddRequest rankAddRequestDto) {
        PersonalRank personalRank = new PersonalRank();
        personalRank.setCategoryId(categoryId);
        personalRank.setUserId(userId);
        personalRank.setTargetId(rankAddRequestDto.getTargetId() == null ? 0 : rankAddRequestDto.getTargetId());
        personalRank.setRankName(rankAddRequestDto.getRankName());
        userPublishMapper.insertRank(personalRank);
        Integer rankId = personalRank.getRankId();
        if (rankId == null) {
            System.out.println("rankId为null，直接返回失败");
            return null;
        }

        List<RankSong> ranksongList=new ArrayList<RankSong>();
        rankAddRequestDto.getSongItems().forEach(item->{
            RankSong rankSong=new RankSong();
            rankSong.setRankId(rankId);
            rankSong.setRanking(item.getRanking());
            rankSong.setSongId(item.getSongId());
            ranksongList.add(rankSong);
        });
        int insertCount = userPublishMapper.sixInsert(ranksongList);
        if (insertCount != ranksongList.size()) {
            System.out.println("子表插入行数与预期不符，返回失败");
            return null;
        }
        return rankId;
    }

    @Override
    public List<Singer> selectSinger(Integer categoryId) {
        return userPublishMapper.selectSinger(categoryId);
    }

    @Override
    public List<Song> selectSong(Integer singerId) {
        return userPublishMapper.selectSong(singerId);
    }

    @Override
    public PageInfo<MyRankWithSong> selectPublishRank(Integer category, Integer pageNum, Integer pageSize, Integer offset) {
        Integer userId = ThreadLocalUtil.get();

        List<MyRankWithSong> ranks = userPublishMapper.selectPublishRank(category, pageSize, offset);
        fillUsernames(ranks);
        fillVoteStats(ranks);
        ranks.forEach(rank -> {
            Result<Boolean> followResult = userFeginClient.isFollowed(userId, rank.getUserId());
            rank.setFollowed(followResult != null && followResult.getData() != null && followResult.getData());
        });
        PageInfo<MyRankWithSong> pageInfo = new PageInfo<>();
        Integer total = userPublishMapper.selectTotal(category);
        Integer pages = (total + pageSize - 1) / pageSize;
        pageInfo.setList(ranks);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        pageInfo.setTotal(total);
        pageInfo.setPages(pages);
        return pageInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertVote(Integer userId, Integer rankId) {
        if (userId == null || rankId == null) {
            log.error("【点赞操作】参数为空，userId={}, rankId={}", userId, rankId);
            return false;
        }
        String voteKey = String.format(Vote_Key, userId, rankId);
        log.info("【点赞操作】开始，userId={}, rankId={}, RedisKey={}", userId, rankId, voteKey);

        Boolean isFirstVote = stringRedisTemplate.opsForValue().setIfAbsent(voteKey, "1", 24, TimeUnit.HOURS);

        if (Boolean.FALSE.equals(isFirstVote)) {
            log.info("【取消点赞】Redis已存在记录，开始删除数据库+Redis记录");
            Result<Boolean> deleteResult = voteFeignClient.deleteVote(userId, rankId);
            if (deleteResult.getData() == null || !deleteResult.getData()) {
                log.error("【取消点赞】Feign删除失败，userId={}, rankId={}", userId, rankId);
                return false;
            }
            boolean deleteSuccess = stringRedisTemplate.delete(voteKey);
            if (!deleteSuccess) {
                log.warn("【取消点赞】第一次删除Redis Key失败，重试一次！Key={}", voteKey);
                deleteSuccess = stringRedisTemplate.delete(voteKey);
            }
            return deleteSuccess;
        }

        PersonalRank v = userPublishMapper.selectRankById(rankId);
        if (v == null) {
            log.error("【点赞操作】榜单不存在，rankId={}", rankId);
            stringRedisTemplate.delete(voteKey);
            return false;
        }

        Result<Boolean> result = voteFeignClient.insertVote(userId, rankId);
        if (result.getData() == null || !result.getData()) {
            log.error("【点赞操作】Feign插入失败，userId={}, rankId={}", userId, rankId);
            stringRedisTemplate.delete(voteKey);
            return false;
        }

        log.info("【点赞成功】数据库和Redis均已记录，userId={}, rankId={}", userId, rankId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertLove(Integer userId, Integer rankId) {
        if (userId == null || rankId == null) {
            log.error("【收藏操作】参数为空，userId={}, rankId={}", userId, rankId);
            return false;
        }

        String loveKey = String.format(Love_Key, userId, rankId);
        log.info("【收藏操作】开始，userId={}, rankId={}, RedisKey={}", userId, rankId, loveKey);

        syncLoveStatus(userId, rankId, loveKey);

        Boolean isFirstLove = stringRedisTemplate.opsForValue().setIfAbsent(loveKey, "1", 24, TimeUnit.HOURS);
        if (isFirstLove == null) {
            log.error("【收藏操作】Redis连接失败，无法判断收藏状态");
            return false;
        }

        if (Boolean.FALSE.equals(isFirstLove)) {
            log.info("【取消收藏】Redis已存在记录，开始删除数据库+Redis记录");
            try {
                Result<Boolean> deleteResult = voteFeignClient.deleteLove(userId, rankId);
                boolean success = deleteResult.getData() != null && deleteResult.getData();
                log.info("【取消收藏】Feign删除结果：{}", success);

                if (success) {
                    boolean deleteSuccess = stringRedisTemplate.delete(loveKey);
                    if (!deleteSuccess) {
                        log.warn("【取消收藏】第一次删除Redis Key失败，重试一次！Key={}", loveKey);
                        deleteSuccess = stringRedisTemplate.delete(loveKey);
                    }
                    return deleteSuccess;
                } else {
                    log.error("【取消收藏】Feign删除失败，userId={}, rankId={}", userId, rankId);
                    stringRedisTemplate.delete(loveKey);
                    throw new RuntimeException("取消收藏：数据库无对应记录");
                }
            } catch (Exception e) {
                log.error("【取消收藏】执行失败", e);
                stringRedisTemplate.delete(loveKey);
                throw e;
            }
        }

        try {
            PersonalRank l = userPublishMapper.selectRankById(rankId);
            if (l == null) {
                log.error("【收藏操作】榜单不存在，rankId={}", rankId);
                stringRedisTemplate.delete(loveKey);
                return false;
            }

            Result<Boolean> result = voteFeignClient.insertLove(userId, rankId);
            boolean success = result.getData() != null && result.getData();
            log.info("【收藏操作】Feign插入结果：{}", success);

            if (success) {
                log.info("【收藏成功】Redis已记录，userId={}, rankId={}", userId, rankId);
                return true;
            } else {
                log.error("【收藏失败】Feign插入失败，userId={}, rankId={}", userId, rankId);
                stringRedisTemplate.delete(loveKey);
                throw new RuntimeException("收藏失败：数据库插入失败");
            }
        } catch (Exception e) {
            log.error("【收藏操作】执行失败", e);
            stringRedisTemplate.delete(loveKey);
            throw e;
        }
    }

    private void syncLoveStatus(Integer userId, Integer rankId, String loveKey) {
        Result<Boolean> result = voteFeignClient.hasLove(userId, rankId);
        boolean hasLove = result.getData() != null && result.getData();
        if (hasLove && !stringRedisTemplate.hasKey(loveKey)) {
            stringRedisTemplate.opsForValue().set(loveKey, "1");
            log.info("【收藏缓存同步】数据库有记录，Redis同步成功，userId={}, rankId={}", userId, rankId);
        } else if (!hasLove && stringRedisTemplate.hasKey(loveKey)) {
            stringRedisTemplate.delete(loveKey);
            log.info("【收藏缓存同步】数据库无记录，Redis删除成功，userId={}, rankId={}", userId, rankId);
        }
    }

    @Override
    public PageInfo<MyRankWithSong> selectSearch(Integer category, Integer pageNum,
                                                 Integer offset, Integer pageSize,String keyword) {
        if (keyword != null) {
            keyword = keyword.trim();
        } else {
            keyword = null;
        }
        List<MyRankWithSong> list = userPublishMapper.selectSearch(category, pageNum, offset, pageSize, keyword);
        fillUsernames(list);
        fillVoteStats(list);
        PageInfo<MyRankWithSong> searchRank = new PageInfo<>();
        Integer total = userPublishMapper.selectSearchTotal(category, keyword);
        Integer pages = (total + pageSize - 1) / pageSize;
        searchRank.setList(list);
        searchRank.setPageNum(pageNum);
        searchRank.setPageSize(pageSize);
        searchRank.setTotal(total);
        searchRank.setPages(pages);
        return searchRank;
    }

    @Override
    public Comment insertComment(Integer rankId, Integer userId, String content, Integer parentId) {
        Comment comment = new Comment();
        comment.setRankId(rankId);
        comment.setUserId(userId);
        comment.setComContent(content);
        comment.setParentId(parentId);
        userPublishMapper.insertComment(rankId, userId, content, parentId);
        return comment;
    }

    @Override
    public List<CommentVo> selectComment(Integer rankId, Integer userId) {
        List<CommentVo> list = userPublishMapper.selectComment(rankId, userId);
        fillCommentUsernames(list);
        fillCommentLikes(list, userId);
        System.out.println("查询出的评论数：" + list.size());
        return list;
    }

    @Override
    @Transactional
    public boolean deleteComment(Integer comId,Integer userId) {
        Comment comment = userPublishMapper.selectCommentById(comId);
        if (comment == null) {
            return false;
        }
        if (comment.getUserId() != userId) {
            throw new RuntimeException("只能删除自己的评论");
        }
        int result = userPublishMapper.updateComment(comId, 1);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean insertLike(Integer userId,Integer comId) {
        Comment comment = userPublishMapper.selectCommentById(comId);
        if (comment == null || comment.getIdDelete() == 1) {
            return false;
        }
        Result<Boolean> result = voteFeignClient.insertLike(userId, comId);
        return result.getData() != null && result.getData();
    }

    @Override
    @Transactional
    public boolean deleteLike(Integer comId, Integer userId) {
        Comment comment = userPublishMapper.selectCommentById(comId);
        if (comment == null || comment.getIdDelete() == 1) {
            return false;
        }
        Result<Boolean> result = voteFeignClient.deleteLike(userId, comId);
        return result.getData() != null && result.getData();
    }

    @Override
    @Transactional
    public boolean deleteVote(Integer rankId, Integer userId) {
        PersonalRank personalRank = userPublishMapper.selectRankById(rankId);
        if (personalRank == null) {
            log.warn("【取消点赞】榜单不存在！rankId={}, userId={}", rankId, userId);
            return false;
        }

        log.info("【取消点赞】准备删除点赞记录：rankId={}, userId={}", rankId, userId);
        Result<Boolean> result = voteFeignClient.deleteVote(userId, rankId);
        boolean ok = result.getData() != null && result.getData();
        log.info("【取消点赞】Feign删除结果：{}", ok);
        return ok;
    }

    @Override
    @Transactional
    public boolean deleteLove(Integer rankId, Integer userId) {
        PersonalRank personalRank = userPublishMapper.selectRankById(rankId);
        if (personalRank == null) {
            log.warn("【取消收藏】榜单不存在！rankId={}, userId={}", rankId, userId);
            return false;
        }

        log.info("【取消收藏】准备删除收藏记录：rankId={}, userId={}", rankId, userId);
        Result<Boolean> result = voteFeignClient.deleteLove(userId, rankId);
        boolean ok = result.getData() != null && result.getData();
        log.info("【取消收藏】Feign删除结果：{}", ok);
        return ok;
    }

    @Override
    public boolean insertApply(String singerName, String songName) {
        if (singerName == null || songName == null) {
            return false;
        }
        auditFeignClient.insertSongDemand(songName, singerName);
        return true;
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
