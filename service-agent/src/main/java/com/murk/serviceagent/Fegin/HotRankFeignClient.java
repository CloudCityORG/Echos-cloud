package com.murk.serviceagent.Fegin;

import com.murk.commonlib.dto.MyRankWithSong;
import com.murk.commonlib.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="service-vote")
public interface HotRankFeignClient {
    @GetMapping
    Result<List<MyRankWithSong>> getHotRank(@RequestParam("start")Integer start
            ,@RequestParam("end")Integer end);
}
