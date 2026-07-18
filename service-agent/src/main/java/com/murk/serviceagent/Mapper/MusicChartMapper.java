package com.murk.serviceagent.Mapper;

import com.murk.commonlib.dto.MyRankWithSong;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MusicChartMapper {
    List<MyRankWithSong> selectChart(String keyword,int userId);
}
