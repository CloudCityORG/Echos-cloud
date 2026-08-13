package com.murk.servicechart.Service;

import com.github.pagehelper.PageInfo;
import com.murk.commonlib.pojo.Comment;
import com.murk.commonlib.pojo.Singer;
import com.murk.commonlib.pojo.Song;
import com.murk.servicechart.dto.CommentVo;
import com.murk.servicechart.dto.MyRankWithSong;
import com.murk.servicechart.dto.RankAddRequest;

import java.util.List;


public interface UserPublishService {
    public Integer insertRank(Integer categoryId, Integer userId, RankAddRequest rankAddRequestDto);

    List<Singer> selectSinger(Integer categoryId);

    List<Song> selectSong(Integer singerId);

    PageInfo<MyRankWithSong> selectPublishRank(Integer category, Integer pageNum, Integer pageSize, Integer offset);

    boolean insertVote(Integer userId, Integer rankId);

    boolean insertLove(Integer userId, Integer rankId);

    PageInfo<MyRankWithSong> selectSearch(Integer category, Integer pageNum, Integer offset, Integer pageSize,String keyword);

    Comment insertComment(Integer rankId, Integer userId, String content, Integer parentId);

    List<CommentVo> selectComment(Integer rankId, Integer userId);

    boolean deleteComment(Integer comId,Integer userId);

    boolean insertLike(Integer userId,Integer comId);

    boolean deleteLike(Integer comId,Integer userId);

    boolean deleteVote(Integer rankId, Integer userId);

    boolean deleteLove(Integer rankId, Integer userId);

    boolean insertApply(String singerName, String songName);

}
