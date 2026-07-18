package com.murk.servicechart.Service;

import com.github.pagehelper.PageInfo;
import com.murk.commonlib.pojo.Singer;
import com.murk.commonlib.pojo.Song;
import com.murk.servicechart.dto.CommentVo;
import com.murk.servicechart.dto.EditRank;
import com.murk.commonlib.dto.MyRankWithSong;
import com.murk.commonlib.pojo.UserInfo;

import java.util.List;

public interface MyRankService {

    PageInfo<MyRankWithSong> selectMyrank(Integer pageNum, Integer pageSize,Integer offset, Integer userId);

    MyRankWithSong getRank(Integer rankId);

    List<Singer> selectSinger(Integer categoryId);

    List<Song> selectSong(Integer singerId);


    PageInfo<MyRankWithSong> selectMyLoverank(Integer pageNum, Integer pageSize, Integer offset, Integer userId);



    UserInfo selectByuserId(Integer userId);

    List<CommentVo> selectComment(Integer rankId, Integer userId);

    boolean deleteComment(Integer comId, Integer userId);

    boolean update(EditRank dto, Integer rankId);
}
