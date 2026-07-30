package com.murk.commonlib.api;

import com.murk.commonlib.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 供其它微服务调用 service-audit 的接口
 */
@FeignClient("service-audit")
public interface AuditFeignClient {

    /**
     * 提交歌曲申请（写入 song_demand）
     */
    @PostMapping("/api/audit/song-demand")
    Result<Void> insertSongDemand(@RequestParam("songName") String songName,
                                  @RequestParam("singerName") String singerName);
}
