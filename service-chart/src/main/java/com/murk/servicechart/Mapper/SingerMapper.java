package com.murk.servicechart.Mapper;

import com.murk.commonlib.pojo.Singer;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface SingerMapper {

    @Insert("INSERT INTO singer(singer_name) VALUES(#{singerName})")
    @Options(useGeneratedKeys = true, keyProperty = "singerId")
    void insertSinger(Singer singer);
}
