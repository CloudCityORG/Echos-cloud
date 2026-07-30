package com.murk.serviceaudit.Controller;

import com.murk.commonlib.utils.Result;
import com.murk.serviceaudit.Mapper.AdminSongMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 供其它微服务调用 service-audit 的接口
 */
@RestController
@RequestMapping("/api/audit")
public class SongDemandApiController {

    @Autowired
    private AdminSongMapper adminSongMapper;

    @PostMapping("/song-demand")
    public Result<Void> insertSongDemand(@RequestParam("songName") String songName,
                                         @RequestParam("singerName") String singerName) {
        adminSongMapper.insertSongDemand(songName, singerName);
        return Result.success();
    }
}
