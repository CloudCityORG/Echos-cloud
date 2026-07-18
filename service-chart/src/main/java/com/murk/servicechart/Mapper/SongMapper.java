package com.murk.servicechart.Mapper;

import com.murk.commonlib.pojo.Song;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SongMapper {

    Song selectSongById(Integer songId);
}
