package com.murk.servicechart.Service;

import com.murk.commonlib.pojo.Song;

import java.io.File;

public interface SongService {
 /**
  * 根据歌曲ID获取歌曲信息和对应的音频文件
  */
 Song getSongById(Integer songId);

 /**
  * 获取歌曲对应的音频文件
  */
 File getSongFile(Song song);
}
