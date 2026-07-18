package com.murk.serviceagent.Service;

import com.murk.commonlib.dto.MyRankWithSong;

import java.util.List;

public interface MusicChartService {
    List<MyRankWithSong> getMusicChart(String keyword,int userId);
    String getHotChart();
}
