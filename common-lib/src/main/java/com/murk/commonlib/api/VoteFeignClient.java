package com.murk.commonlib.api;

import com.murk.commonlib.dto.VoteStats;
import com.murk.commonlib.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}
