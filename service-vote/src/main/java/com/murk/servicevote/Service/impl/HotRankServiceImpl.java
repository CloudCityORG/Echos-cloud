package com.murk.servicevote.Service.impl;

import com.alibaba.fastjson.JSON;
import com.murk.commonlib.api.ChartFeignClient;
import com.murk.commonlib.api.UserFeginClient;
import com.murk.commonlib.api.WebSocketFeignClient;
import com.murk.commonlib.dto.*;
import com.murk.commonlib.pojo.Comment;
import com.murk.commonlib.utils.Result;
import com.murk.servicevote.Controller.MusicHotRankController;
import com.murk.servicevote.Mapper.HotRankMapper;
import com.murk.servicevote.Service.HotRankService;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HotRankServiceImpl implements HotRankService {
    //设置热门榜单Zset集合的键值
    //Redis核心写在Service层，配和Mapper层的Mysql查询联合完成热门榜单
    /*逻辑：    1.设置榜单排名的分数计算公式，自定义
               2.根据公式调用Redis，根据分数公式对分数进行排名并实时更新
                */


    private static final String Hot_Rank_Key = "music:rank:vote";
    private static final String HOT_RANK_ZSET_KEY = "music:rank:vote";

    // 构造器注入（合并为一个构造函数，确保所有 final 字段都被初始化）
    public HotRankServiceImpl(RedisTemplate<String, Object> redisTemplate) {

        this.redisTemplate = redisTemplate;

    }

    private final RedisTemplate<String, Object> redisTemplate;

    private DefaultRedisScript<List> rankScript;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String Love_Key = "love:user:%d:rank:%d";
    private static final String Vote_Key = "vote:user:%d:rank:%d";
    private static final String Vote_Count_Key = "music:rank:vote:count:%d";

    private final String ZSET_REBUILD_KEY = "MUSIC:RANK:VOTE:REBUILD_LOCK";
    private final long ZSET_LOCK_TIMEOUT = 30L;//互斥锁过期时间
    private static final String DETAIL_LOCK_PREFIX = "rank:detail:lock";
    private static final long DETAIL_LOCK_TIMEOUT = 30L;//互斥锁过期时间
    private static final String RANK_DETAIL_KEY_PREFIX = "rank:detail:";
    private static final long CACHE_EXPIRE_MINUTES=30L;//缓存过期时间

    @Autowired
    private HotRankMapper musicHotRankMapper;
    @Autowired
    private ChartFeignClient chartFeignClient;
    @Autowired
    private UserFeginClient userFeginClient;
    @Autowired
    private WebSocketFeignClient webSocketFeignClient;


    @Override
    public Integer caculateHotRank(Integer rankId) {
        Integer love = getLoveCount(rankId);
        Integer vote = getVoteCount(rankId);
        //防止空指针？
        love = love == null ? 0 : love;
        vote = vote == null ? 0 : vote;
        return love + 2 * vote;
    }

    // 项目启动时加载 Lua 脚本
    @PostConstruct
    public void init() {
        rankScript = new DefaultRedisScript<>();
        rankScript.setResultType(List.class); // 返回值类型：List

        try {
            // 1. 读取 Lua 脚本
            ClassPathResource resource = new ClassPathResource("lua/redis_update.lua");
            // 使用 try-with-resources 自动关闭流，防止资源泄露
            try (InputStream inputStream = resource.getInputStream()) {
                String scriptContent = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));

                rankScript.setScriptText(scriptContent);
                log.info("✅ Lua 战报脚本加载成功！长度: {} 字节", scriptContent.length());
            }
        } catch (IOException e) {
            // 2. 使用 log.error 记录异常堆栈，方便排查
            log.error("❌ Lua 脚本加载失败！请检查 resources/lua/redis_update.lua 是否存在", e);
            // 3. 关键：加载失败直接抛出异常，阻止项目启动
            // 防止项目跑起来了，但一投票就报 NullPointerException
            throw new RuntimeException("Lua 脚本初始化失败", e);
        }
    }

    /**
     * 更新热度并发送战报
     */
    // 1. 加上 @Async，确保不阻塞主线程
    @Async("rankTask")
    @Override
    public void updateHotRank(Integer rankId) {
        if (rankId == null) {
            throw new IllegalArgumentException("rankId不能为空");
        }
        log.info("启用这个方法 ，开始更新热度");


    /*    Integer targetScore = caculateHotRank(rankId);
        Double currentScore = stringRedisTemplate.opsForZSet().score(Hot_Rank_Key, rankId.toString());
        int oldScore = currentScore == null ? 0 : currentScore.intValue();
        int scoreDelta = targetScore - oldScore;别算了会覆盖 次次新上榜 你撑得住？
        */


        //小兔子乖乖 把门开开  爱情像纠结的毛线 ~最后还是失眠 别管我了☺️ ε=(´ο｀*)))唉 现在是2026年 4月25日 00:02:41
        //当世界崩塌  我们 still here
        //发牢骚来了 verdurous mountain 最好听的一集 谁会不喜欢在深夜听上一首纯音乐呢

        Integer increment = caculateHotRank(rankId);
        if (increment <= 0) {
            log.warn("增量小于等于0，不执行更新");
            return;
        }

        long timestamp = System.currentTimeMillis() / 1000;

        double currentScore = stringRedisTemplate.opsForZSet().score(Hot_Rank_Key, rankId.toString());

        final long BASE = 10000000000L;

        long oldScoreValue = currentScore == 0 ? 0L : (long) currentScore;

        long oldRankScore = oldScoreValue / BASE;

        long newRankScore = oldRankScore + increment.longValue();


        long finalScore = newRankScore * BASE + timestamp;

        String scoreStr = String.valueOf(finalScore);

        System.out.println(">>> 最终传给 Redis 的分数: " + scoreStr);
        System.out.println("最终计算出的 Score: " + finalScore);

        List<Object> result = null;
        try {
            result = (List<Object>) stringRedisTemplate.execute(
                    rankScript,
                    Collections.singletonList(Hot_Rank_Key),
                    rankId.toString(),
                    scoreStr // ARGV[2]: 直接传算好的最终分数！

            );
        } catch (Exception e) {
            log.error("执行 Lua 脚本出错：{}", e.getMessage(), e);
            e.printStackTrace();
        }

        if (result != null && result.size() >= 3) {
            Number oldRankNum = (Number) result.get(0);
            Number newRankNum = (Number) result.get(1);

            Long oldRank = oldRankNum == null ? -1L : oldRankNum.longValue();

            Long newRank = newRankNum == null ? -1L : newRankNum.longValue();

            log.info("✅ 热度更新完成，rankId={}, oldRank={}, newRank={}", rankId, oldRank, newRank);
            String currentTopRankIdStr = String.valueOf(result.get(2));

            // 逻辑一：全服广播 (榜首更换) -> 用原生 WebSocket
            if (newRank == 0) {
                String lastTopRankId = (String) redisTemplate.opsForValue().get("rank:last_top_user");

                if (lastTopRankId == null ||
                        !lastTopRankId.equals(currentTopRankIdStr)) {

                    PersonalRank topRank = musicHotRankMapper.selectRankById(Integer.valueOf(currentTopRankIdStr));
                    String displayName = (topRank != null && topRank.getRankName() != null)
                            ? topRank.getRankName() : "榜单#" + currentTopRankIdStr;

                    BattleReport publicReport = new BattleReport(
                            "SUCCESS",
                            "🏆 榜单风云",
                            "恭喜 **" + displayName + "** 登顶热门榜首！"
                    );

                    String jsonPublicMsg = JSON.toJSONString(publicReport);

                    webSocketFeignClient.sendMessageToAll(jsonPublicMsg);

                    log.info("📢 全服广播发送：{}", publicReport.getTitle());

                    redisTemplate.opsForValue().set("rank:last_top_user", currentTopRankIdStr);
                }
            }

//喵

            String personalMsg = null;
            if (oldRank == -1) {
                personalMsg = "🎉 恭喜！你的榜单首次上榜，排名第 " + (newRank + 1) + "！";
            } else if (newRank < oldRank) {
                int diff = oldRank.intValue() - newRank.intValue();
                personalMsg = "🚀 排名上升！你的榜单提升了 " + diff + " 位，当前第 " + (newRank + 1) + "！";
            }

            if (personalMsg != null) {
                PersonalRank currentRank = musicHotRankMapper.selectRankById(Math.toIntExact(rankId));
                if (currentRank != null) {
                    Integer targetUserId = currentRank.getUserId();
                    if (targetUserId != null) {
                        // 【修改点 3】构造私信战报
                        BattleReport personalReport = new BattleReport(
                                "INFO",
                                "排名变动",
                                personalMsg
                        );

                        String jsonPersonalMsg = JSON.toJSONString(personalReport);

                        webSocketFeignClient.sendToUser(targetUserId.toString(), jsonPersonalMsg);

                        log.info("🔒 私信发送给用户 {}: {}", targetUserId, personalMsg);
                    }
                }
            }
        }
    }


    @Override
    public Set<String> getHotRankId(int start, int end) {
        if (end <= 0 || start < 0) {
            return Collections.emptySet();//妙SOS,返回空集合
        }

        return stringRedisTemplate.opsForZSet().reverseRange(Hot_Rank_Key, start, end);
    }

    //有一個严重问题Redis重启那这个Key就丢失，那如果此时有请求来，由于redis挂了，那所有的请求就会直接到DB，导致DB压力过大
    //这就是臭名远扬的缓存击穿，那采取的解决方案是互斥锁去保护，本质：大量请求去抢一个锁，抢到的这个唯一就去调用Mysql方法建立Zset
    //然后释放锁，其他请求发现锁存在，直接返回
    @Override
    public Set<String> getHotRankIdWithMutex(int start, int end) {
        Set<String> result = getHotRankId(start, end);
        if (result != null || !result.isEmpty()) {
            return result;
        }
        log.warn("Zset已经失效，尝试锁竞争mysql重建");
        //利用redis建锁并设置锁过期时间
        Boolean getLock = stringRedisTemplate.opsForValue().setIfAbsent(ZSET_REBUILD_KEY, "1", ZSET_LOCK_TIMEOUT, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(getLock)) {//先双重检查，是否有其他线程已经成功调用mysql完毕
            //没有就马上去抢锁，成功就该线程方法去调用，调用完就删除锁（释放），如果失败就自旋
            try {
                result = getHotRankId(start, end);//注意这里方法调用的是原先
                if (result == null || result.isEmpty()) {
                    log.info("别的线程已经创建完毕，直接返回结果");
                    return result;
                }
                rebuildHotRankZset();
                return getHotRankId(start, end);
            } finally {
                stringRedisTemplate.delete(ZSET_REBUILD_KEY);
            }
        } else {
            //下方是没抢到锁的情况，自旋等待一段时间后再去抢
            try {
                Thread.sleep(100);//自旋等待
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();//恢复中断状态
            }
        }
        return getHotRankIdWithMutex(start, end);//递归重试yeah，操太猎奇了微服务
    }

    //mysql重建Zset排名方法
    private void rebuildHotRankZset() {
        log.info("开始从mysql重建热门榜单");
        long startTime = System.currentTimeMillis();//记录开始时间
        List<PersonalRank> allRanks = musicHotRankMapper.selectAllRanks();
        if (allRanks == null || allRanks.isEmpty()) {
            log.warn("mysql中无数据");
            return;
        }
        long timestamp = System.currentTimeMillis() / 1000;//获取当前时间戳
        final long BASE = 10_000_000_000L;//基数
        for (PersonalRank rank : allRanks) {
            Integer rankId = rank.getRankId();
            Integer hotScore = caculateHotRank(rankId);//计算热度
            long score = timestamp - BASE + hotScore;//计算得分
            stringRedisTemplate.opsForZSet().add(HOT_RANK_ZSET_KEY, rankId.toString(), score);//添加到redis中
        }
        log.info("从mysql重建热门榜单完成，耗时{}ms", System.currentTimeMillis() - startTime);
    }

    @Override
    public List<MyRankWithSong> listById(List<Long> rankId) {
        List<Integer> ids = rankId.stream().map(Long::intValue)
                .collect(Collectors.toList());

        // 1. 调 chart 拿基础信息
        Result<List<MyRankWithSong>> r = chartFeignClient.getRanksByIds(ids);
        List<MyRankWithSong> list = r.getData();
        if (list == null) return Collections.emptyList();

        List<Integer> uids = list.stream().map(MyRankWithSong::getUserId)
                .filter(Objects::nonNull).distinct()
                .collect(Collectors.toList());
        Map<Integer, String> nameMap = userFeginClient.getUserByIds(uids)
                .getData().stream()
                .collect(Collectors.toMap(UserNameVO::getUserId, UserNameVO::getUserName));
        list.forEach(m -> {
            m.setUsername(nameMap.get(m.getUserId()));
            m.setVoteCount(getVoteCount(m.getRankId()));
            m.setLoveCount(getLoveCount(m.getRankId()));
        });
        return list;
    }

    @Override
    public boolean insertVote(Integer userId, Integer rankId) {
        String votekey = String.format(Vote_Key, userId, rankId);
        log.info("【点赞操作】开始，userId={}, rankId={}", userId, rankId);

        // 原子占位：避免并发下重复点赞（先占位成功才允许插库）
        Boolean isFirstVote = stringRedisTemplate.opsForValue().setIfAbsent(votekey, "1", 24, TimeUnit.HOURS);
        if (Boolean.TRUE.equals(isFirstVote)) {
            // --- 分支一：点赞 ---
            PersonalRank v = musicHotRankMapper.selectRankById(rankId);
            if (v == null) {
                stringRedisTemplate.delete(votekey);
                return false;
            }

            try {
                // 写入 DB (利用数据库唯一索引防止重复)
                int rows = musicHotRankMapper.insertVote(rankId, userId);
                if (rows > 0) {
                    increaseVoteCount(rankId);
                    log.info("【投票成功】Redis已记录");
                    return true;
                }
                stringRedisTemplate.delete(votekey);
                return false;
            } catch (Exception e) {
                // 插库失败回滚 Redis 占位，避免脏状态
                stringRedisTemplate.delete(votekey);
                log.warn("【投票异常】可能是重复点赞导致主键冲突: {}", e.getMessage());
                return false;
            }
        }

        // --- 分支二：取消点赞 ---
        log.info("【取消点赞】Redis已存在记录，执行取消");
        int rows = musicHotRankMapper.deleteVote(userId, rankId);
        if (rows > 0) {
            stringRedisTemplate.delete(votekey);
            decreaseVoteCount(rankId);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean insertLove(Integer userId, Integer rankId) {
        //拼接Redis的key值
        String lovekey = String.format(Love_Key, userId, rankId);
        log.info("【收藏操作】开始，userId={}, rankId={}, RedisKey={}", userId, rankId, lovekey);
        // 先以数据库为准判断是否已收藏，避免 Redis key 过期后重复插入
        int existed = musicHotRankMapper.countLove(rankId, userId);
        if (existed > 0) {
            // DB已收藏 -> 本次视为取消收藏（toggle 语义不变）
            stringRedisTemplate.opsForValue().set(lovekey, "1", 24, TimeUnit.HOURS);
            int deletedRows = musicHotRankMapper.deleteLove(userId, rankId);
            if (deletedRows > 0) {
                stringRedisTemplate.delete(lovekey);
                decreaseLoveCount(rankId, deletedRows);
                log.info("【取消收藏】已删除{}条收藏记录，userId={}, rankId={}", deletedRows, userId, rankId);
                return true;
            }
            return false;
        }

        Boolean isFirstLove = stringRedisTemplate.opsForValue().setIfAbsent(lovekey, "1", 24, TimeUnit.HOURS);
        if (Boolean.TRUE.equals(isFirstLove)) {
            // --- 分支一：收藏 ---
            PersonalRank l = musicHotRankMapper.selectRankById(rankId);
            if (l == null) {
                stringRedisTemplate.delete(lovekey);
                return false;
            }
            int row1 = musicHotRankMapper.insertLove(userId, rankId);
            if (row1 > 0) {
                increaseLoveCount(rankId);
                log.info("【收藏成功】Redis已记录，userId={}, rankId={}", userId, rankId);
                return true;
            }
            stringRedisTemplate.delete(lovekey);
            return false;
        }

        // --- 分支二：取消收藏 ---
        log.info("【取消收藏】Redis已存在记录，准备删除数据库记录");
        int deletedRows = musicHotRankMapper.deleteLove(userId, rankId);
        if (deletedRows > 0) {
            stringRedisTemplate.delete(lovekey);
            decreaseLoveCount(rankId, deletedRows);
            log.info("【取消收藏】mysql和Redis记录已删除，deletedRows={}, userId={}, rankId={}", deletedRows, userId, rankId);
            return true;
        }
        return false;
    }


    @Override
    @Transactional
    public boolean deleteVote(Integer rankId, Integer userId) {
        // 原有逻辑
        PersonalRank personalRank = musicHotRankMapper.selectRankById(rankId);
        if (personalRank == null) {
            // 新增日志：打印榜单不存在的提示
            log.warn("【取消点赞】榜单不存在！rankId={}, userId={}", rankId, userId);
            return false;
        }

        // 新增日志：打印要删除的条件
        log.info("【取消点赞】准备删除点赞记录：rankId={}, userId={}", rankId, userId);
        int result = musicHotRankMapper.deleteVote(rankId, userId);

        // 新增日志：打印删除结果（核心！看删了多少行）
        log.info("【取消点赞】删除操作完成，影响行数：{}", result);

        return result > 0;
    }

    // 你的deleteLove方法（同理加日志）
    @Override
    @Transactional
    public boolean deleteLove(Integer rankId, Integer userId) {
        PersonalRank personalRank = musicHotRankMapper.selectRankById(rankId);
        if (personalRank == null) {
            log.warn("【取消收藏】榜单不存在！rankId={}, userId={}", rankId, userId);
            return false;
        }

        log.info("【取消收藏】准备删除收藏记录：rankId={}, userId={}", rankId, userId);
        int result = musicHotRankMapper.deleteLove(rankId, userId);
        log.info("【取消收藏】删除操作完成，影响行数：{}", result);

        return result > 0;
    }

    @Override
    public List<CommentVo> selectComment(Integer rankId, Integer userId) {
        List<CommentVo> list = musicHotRankMapper.selectComment(rankId, userId);
        System.out.println("查询出的评论数：" + list.size());
        return list;
    }

    @Override
    @Transactional
    public boolean deleteComment(Integer comId, Integer userId) {
        Comment comment = musicHotRankMapper.selectCommentById(comId);
        if (comment == null) {
            return false;
        }
        if (comment.getUserId() != userId) {
            throw new RuntimeException("只能删除自己的评论");
        }
        int result = musicHotRankMapper.updateComment(comId, 1);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean insertLike(Integer userId, Integer comId) {
        Comment comment = musicHotRankMapper.selectCommentById(comId);
        if (comment == null || comment.getIdDelete() == 1) {
            return false;
        }
        int result = musicHotRankMapper.insertLike(userId, comId);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean deleteLike(Integer comId, Integer userId) {
        Comment comment = musicHotRankMapper.selectCommentById(comId);
        if (comment == null || comment.getIdDelete() == 1) {
            return false;
        }
        int result = musicHotRankMapper.deleteLike(comId, userId);
        return result > 0;
    }

    //豆包更新之后变权威了
    //下方方法目的：刷新页面之后不会让统统一用户能重复投票，使用redis达到记录用户投票状态的目的
    private void sychorVoteToRedis(Integer rankId, Integer userId, String votekey) {
        //sychronise翻译为同步，有点像锁但不是，这里本质是一个方法，将redis和mysql同步
        int x = musicHotRankMapper.countVote(rankId, userId);
        boolean flag = stringRedisTemplate.hasKey(votekey);
        if (x > 0 || flag == false) {
            stringRedisTemplate.opsForValue().set(votekey, "1");
            log.info("【状态同步】数据库有投票记录，Redis补写Key：{}", votekey);
        }
        if (x <= 0 || flag == true) {
            stringRedisTemplate.delete(votekey);
            log.info("【状态同步】数据库无投票记录，Redis删除Key：{}", votekey);
        }
    }

    private void sychorLoveToRedis(Integer rankId, Integer userId, String lovekey) {
        int y = musicHotRankMapper.countLove(rankId, userId);
        boolean flag = stringRedisTemplate.hasKey(lovekey);
        if (y > 0 || flag == false) {
            stringRedisTemplate.opsForValue().set(lovekey, "1");
            log.info("【状态同步】数据库有投票记录，Redis补写Key：{}", lovekey);
        }
        if (y <= 0 || flag == true) {
            stringRedisTemplate.delete(lovekey);
            log.info("【状态同步】数据库无投票记录，Redis删除Key：{}", lovekey);
        }
    }

    /**
     * 投票数优先读 Redis，未命中再查库并回填 Redis。
     * 保持原业务语义：最终计数和数据库一致。
     */
    private Integer getVoteCount(Integer rankId) {
        String countKey = String.format(Vote_Count_Key, rankId);
        String voteCountStr = stringRedisTemplate.opsForValue().get(countKey);
        if (voteCountStr != null) {
            try {
                return Integer.parseInt(voteCountStr);
            } catch (NumberFormatException e) {
                log.warn("【投票数缓存】格式异常，key={}, value={}", countKey, voteCountStr);
            }
        }
        Integer dbCount = musicHotRankMapper.CountVote(rankId);
        int safeCount = dbCount == null ? 0 : dbCount;
        stringRedisTemplate.opsForValue().set(countKey, String.valueOf(safeCount));
        return safeCount;
    }
    // 1. 定义 ZSet 的 Key


    // 2. 修改 increase 方法
    private void increaseVoteCount(Integer rankId) {
        String countKey = String.format(Vote_Count_Key, rankId);
        String value = stringRedisTemplate.opsForValue().get(countKey);

        if (value == null) {
            Integer dbCount = musicHotRankMapper.CountVote(rankId);
            int safeCount = dbCount == null ? 0 : dbCount;
            stringRedisTemplate.opsForValue().set(countKey, String.valueOf(safeCount));

            // 【新增】同步 ZSet 初始分数
            stringRedisTemplate.opsForZSet().add(HOT_RANK_ZSET_KEY, rankId.toString(), safeCount);
            return;
        }

        // 计数 +1
        stringRedisTemplate.opsForValue().increment(countKey);

        // 【新增】ZSet 分数 +1
        stringRedisTemplate.opsForZSet().incrementScore(HOT_RANK_ZSET_KEY, rankId.toString(), 1);
    }

    // 3. 修改 decrease 方法
    private void decreaseVoteCount(Integer rankId) {
        String countKey = String.format(Vote_Count_Key, rankId);
        String value = stringRedisTemplate.opsForValue().get(countKey);

        if (value == null) {
            Integer dbCount = musicHotRankMapper.CountVote(rankId);
            int safeCount = dbCount == null ? 0 : dbCount;
            stringRedisTemplate.opsForValue().set(countKey, String.valueOf(safeCount));

            // 【新增】同步 ZSet 初始分数
            stringRedisTemplate.opsForZSet().add(HOT_RANK_ZSET_KEY, rankId.toString(), safeCount);
            return;
        }

        Long newValue = stringRedisTemplate.opsForValue().decrement(countKey);

        if (newValue != null && newValue < 0) {
            stringRedisTemplate.opsForValue().set(countKey, "0");
            // 【新增】ZSet 分数修正为 0
            stringRedisTemplate.opsForZSet().add(HOT_RANK_ZSET_KEY, rankId.toString(), 0);
        } else {
            // 【新增】ZSet 分数 -1
            stringRedisTemplate.opsForZSet().incrementScore(HOT_RANK_ZSET_KEY, rankId.toString(), -1);
        }
    }

    private Integer getLoveCount(Integer rankId) {
        String countKey = String.format(Love_Count_Key, rankId);
        String loveCountStr = stringRedisTemplate.opsForValue().get(countKey);
        if (loveCountStr != null) {
            try {
                return Integer.parseInt(loveCountStr);
            } catch (NumberFormatException e) {
                log.warn("【收藏数缓存】格式异常，key={}, value={}", countKey, loveCountStr);
            }
        }
        Integer dbCount = musicHotRankMapper.CountLove(rankId);
        int safeCount = dbCount == null ? 0 : dbCount;
        stringRedisTemplate.opsForValue().set(countKey, String.valueOf(safeCount));
        return safeCount;
    }

    private void increaseLoveCount(Integer rankId) {
        String countKey = String.format(Love_Count_Key, rankId);
        String value = stringRedisTemplate.opsForValue().get(countKey);
        if (value == null) {
            Integer dbCount = musicHotRankMapper.CountLove(rankId);
            int safeCount = dbCount == null ? 0 : dbCount;
            stringRedisTemplate.opsForValue().set(countKey, String.valueOf(safeCount));
            return;
        }
        stringRedisTemplate.opsForValue().increment(countKey);
    }

    private void decreaseLoveCount(Integer rankId, int delta) {
        int step = Math.max(1, delta);
        String countKey = String.format(Love_Count_Key, rankId);
        String value = stringRedisTemplate.opsForValue().get(countKey);
        if (value == null) {
            Integer dbCount = musicHotRankMapper.CountLove(rankId);
            int safeCount = dbCount == null ? 0 : dbCount;
            stringRedisTemplate.opsForValue().set(countKey, String.valueOf(safeCount));
            return;
        }
        Long newValue = stringRedisTemplate.opsForValue().increment(countKey, -step);
        if (newValue != null && newValue < 0) {
            stringRedisTemplate.opsForValue().set(countKey, "0");
        }
    }

    @Override
    public Integer CountVote(Integer rankId) {
        return musicHotRankMapper.CountVote(rankId);
    }

    @Override
    public Integer CountLove(Integer rankId) {
        return musicHotRankMapper.CountLove(rankId);
    }

    @Override
    public int countVote(Integer rankId, Integer userId) {
        return musicHotRankMapper.countVote(rankId, userId);
    }

    @Override
    public int countLove(Integer rankId, Integer userId) {
        return musicHotRankMapper.countLove(rankId, userId);
    }

    @Override
    public List<Integer> getLovedRankIds(Integer userId, Integer pageSize, Integer offset) {
        return musicHotRankMapper.selectLovedRankIds(userId, pageSize, offset);
    }

    @Override
    public int countLike(Integer comId) {
        return musicHotRankMapper.countLike(comId);
    }

    @Override
    public int countLike(Integer comId, Integer userId) {
        return musicHotRankMapper.countLikeByUser(comId, userId);
    }

    //下方依旧互斥锁的逻辑
    //用于应对榜单缓存过期导致大量请求打在DB上
    //当某个key过期时，操作DB去访问，抢那一个锁，抢到后这个请求马上去恢复过期键，其他所有请求都可看见修复成功的键
    @Override
    public List<MyRankWithSong> getHotRankWithCache(List<Long> rankIds) {
        List<MyRankWithSong> finalList = new ArrayList<>();// 🧺 篮子A：装"已经拿到手的资料"
        List<Long> missingIds = new ArrayList<>();//📝 便签：记"档案室还缺谁的资料"
        Map<Long, Boolean> lockedIds = new HashMap<>();// 🔑 钥匙串：记"哪些房间是我亲自开锁进去的"
//        rankIds → 批量读缓存(Map)
//→ 命中的直接装篮
//→ 不命中的抢锁
//→ 抢到：记便签 + 挂钥匙 → 最后批量查MySQL → 回填缓存 → 解钥匙
//→ 没抢到：重读缓存 → 有就装篮 / 没有也记便签
//→ 所有数据到齐 → 按原始顺序排队 → 返回 ✅
        List<String> cashedIsonList = rankIds.stream()
                .map(id -> stringRedisTemplate.opsForValue().get(RANK_DETAIL_KEY_PREFIX + id))
                .toList();
        for (int i = 0; i < cashedIsonList.size(); i++) { //遍历
            String s = cashedIsonList.get(i);
            if (s != null) {
                finalList.add(JSON.parseObject(s, MyRankWithSong.class));
            } else {
                Long rankId = rankIds.get(i);//每个rankId独立加锁，让其他请求去抢锁，恢复数据
                String detailLockKey = DETAIL_LOCK_PREFIX + rankId;
                Boolean gotlock = stringRedisTemplate.opsForValue().setIfAbsent(detailLockKey, "1", DETAIL_LOCK_TIMEOUT, TimeUnit.SECONDS);
                if (Boolean.TRUE.equals(gotlock)) {//抢到锁
                    missingIds.add(rankId);//记便签
                    lockedIds.put(rankId, true);//挂钥匙
                } else {
                    //没抢到锁，重读缓存
                    String s1 = stringRedisTemplate.opsForValue().get(RANK_DETAIL_KEY_PREFIX + rankId);
                    if (s1 != null) {
                        finalList.add(JSON.parseObject(s1, MyRankWithSong.class));
                    } else {
                        missingIds.add(rankId);//记便签
                    }
                }
            }
        }
        if (!missingIds.isEmpty()) {//有便签，去数据库恢复
            List<MyRankWithSong> dbList = musicHotRankMapper.listByIds(missingIds);
            for (MyRankWithSong song : dbList) {
                String key = RANK_DETAIL_KEY_PREFIX + song.getRankId();//缓存key
                redisTemplate.opsForValue().set(key, JSON.toJSONString(song),//缓存写入redis
                        CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
                finalList.add(song);
                // 释放锁
                if (lockedIds.containsKey(song.getRankId().longValue())) {
                    stringRedisTemplate.delete(DETAIL_LOCK_PREFIX + song.getRankId());
                }
            }
        }
        // 排序,按照原始榜单的歌曲排名顺序
        Map<Integer, MyRankWithSong> songMap = finalList.stream()
                .collect(Collectors.toMap(MyRankWithSong::getRankId, s -> s, (a, b) -> a));

        return rankIds.stream()//根据原始顺序返回
                .map(id -> songMap.get(id.intValue()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}