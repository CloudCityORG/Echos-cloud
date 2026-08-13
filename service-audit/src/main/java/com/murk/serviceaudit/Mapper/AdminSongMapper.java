package com.murk.serviceaudit.Mapper;

import com.murk.serviceaudit.dto.ApplySongVo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminSongMapper {
    List<ApplySongVo> selectAllApply();

    @Insert("insert into song_demand(demand_songname,demand_singername) values (#{songName},#{singerName})")
    void insertSongDemand(@Param("songName") String songName, @Param("singerName") String singerName);
}
