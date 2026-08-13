package com.murk.servicechart.Mapper;

import com.murk.commonlib.pojo.Song;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface SongMapper {

    Song selectSongById(Integer songId);

    @Insert("INSERT INTO song(song_name, song_url) VALUES(#{songName}, #{songurl})")
    @Options(useGeneratedKeys = true, keyProperty = "songId")
    void insertSong(Song song);
}
