package com.murk.servicechart.Controller;

import com.murk.commonlib.pojo.Singer;
import com.murk.commonlib.pojo.Song;
import com.murk.commonlib.utils.Result;
import com.murk.servicechart.Mapper.SingerMapper;
import com.murk.servicechart.Mapper.SongMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 供其它微服务调用 service-chart 的歌曲/歌手接口
 */
@RestController
@RequestMapping("/api/chart")
public class SongApiController {

    @Autowired
    private SongMapper songMapper;
    @Autowired
    private SingerMapper singerMapper;

    @PostMapping("/song")
    public Result<Integer> addSong(@RequestBody Song song) {
        songMapper.insertSong(song);
        return Result.success(song.getSongId());
    }

    @PostMapping("/singer")
    public Result<Integer> addSinger(@RequestBody Singer singer) {
        singerMapper.insertSinger(singer);
        return Result.success(singer.getSingerId());
    }
}
