package com.murk.servicevote.Controller;

import com.murk.commonlib.dto.VoteStats;
import com.murk.commonlib.utils.Result;
import com.murk.servicevote.Mapper.HotRankMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 对外暴露给其它微服务的 vote/love 统计接口
 */
//AIAIAIAIAI
@RestController
@RequestMapping("/api/vote")
public class VoteApiController {

    @Autowired
    private HotRankMapper hotRankMapper;

    @GetMapping("/stats")
    public Result<List<VoteStats>> getVoteStats(@RequestParam("rankIds") String rankIds) {
        if (StringUtils.isBlank(rankIds)) {
            return Result.success(new ArrayList<>());
        }

        String[] ids = rankIds.split(",");
        List<VoteStats> list = new ArrayList<>(ids.length);
        for (String id : ids) {
            Integer rankId = Integer.valueOf(id.trim());
            Integer voteCount = hotRankMapper.CountVote(rankId);
            Integer loveCount = hotRankMapper.CountLove(rankId);
            list.add(new VoteStats(
                    rankId,
                    voteCount == null ? 0 : voteCount,
                    loveCount == null ? 0 : loveCount
            ));
        }
        return Result.success(list);
    }
}
