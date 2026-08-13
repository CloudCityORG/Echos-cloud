package com.murk.servicevote.Mapper;

import com.murk.commonlib.dto.CommentVo;
import com.murk.commonlib.dto.MyRankWithSong;
import com.murk.commonlib.dto.PersonalRank;
import com.murk.commonlib.pojo.Comment;
import com.murk.commonlib.pojo.RankTags;
import com.murk.commonlib.pojo.Tags;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


import java.util.List;

public interface HotRankMapper {
    Integer CountVote(@Param("rankId") Integer rankId);
    Integer CountLove(@Param("rankId") Integer rankId);

    @Delete("delete from vote_record where rank_id=#{rankId} and user_id=#{userId}")
    int deleteVote(@Param("userId") Integer userId, @Param("rankId") Integer rankId);

    int insertVote(Integer rankId, Integer userId);

    @Delete("delete from love_record where rank_id=#{rankId} and user_id=#{userId};")
    int deleteLove(@Param("userId") Integer userId, @Param("rankId") Integer rankId);

    int insertLove(Integer userId, Integer rankId);

    @Select("select count(*) from vote_record where user_id=#{userId} and rank_id=#{rankId}")
    int countVote(@Param("rankId") Integer rankId, @Param("userId") Integer userId);

    @Select("select count(*) from love_record where user_id=#{userId} and rank_id=#{rankId}")
    int countLove(@Param("rankId") Integer rankId, @Param("userId") Integer userId);

    List<Integer> selectLovedRankIds(@Param("userId") Integer userId,
                                     @Param("pageSize") Integer pageSize,
                                     @Param("offset") Integer offset);

    @Select("select count(*) from like_comment where com_id=#{comId}")
    int countLike(@Param("comId") Integer comId);

    @Select("select count(*) from like_comment where com_id=#{comId} and user_id=#{userId}")
    int countLikeByUser(@Param("comId") Integer comId, @Param("userId") Integer userId);

    int insertLike(Integer userId, Integer comId);

    @Delete("delete from like_comment where com_id=#{comId} and user_id=#{userId}")
    int deleteLike(Integer comId, Integer userId);

}
