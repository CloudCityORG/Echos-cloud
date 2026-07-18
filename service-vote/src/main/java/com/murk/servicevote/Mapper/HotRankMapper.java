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
    List<MyRankWithSong> listByIds(@Param("rankId") List<Long> rankId);
    Integer CountVote(@Param("rankId") Integer rankId);
    Integer CountLove(@Param("rankId") Integer rankId);

    @Delete("delete from vote_record where rank_id=#{rankId} and user_id=#{userId}")
    int deleteVote(@Param("userId") Integer userId, @Param("rankId") Integer rankId);

    @Select("select * from personal_rank where rank_id=#{rankId} ")
    PersonalRank selectRankById(Integer rankId);

    int insertVote(Integer rankId, Integer userId);

    @Delete("delete from love_record where rank_id=#{rankId} and user_id=#{userId};")
    int deleteLove(@Param("userId") Integer userId, @Param("rankId") Integer rankId);

    int insertLove(Integer userId, Integer rankId);

    @Select("select count(*) from vote_record where user_id=#{userId} and rank_id=#{rankId}")
    int countVote(@Param("rankId") Integer rankId, @Param("userId") Integer userId);

    @Select("select count(*) from love_record where user_id=#{userId} and rank_id=#{rankId}")
    int countLove(@Param("rankId") Integer rankId, @Param("userId") Integer userId);

    List<CommentVo> selectComment(Integer rankId, Integer userId);

    Comment selectCommentById(Integer comId);

    @Update("update comment set id_delete=#{idDelete}  where com_id=#{comId}")
    int updateComment(Integer comId, int i);

    int insertLike(Integer userId, Integer comId);

    @Delete("delete from like_comment where com_id=#{comId} and user_id=#{userId}")
    int deleteLike(Integer comId, Integer userId);

    @Select("SELECT d.* \n" +
            "FROM tags_dictionary d\n" +
            "INNER JOIN rank_tags r ON d.tag_id = r.tag_id\n" +
            "WHERE r.rank_id = #{rankId}\n" +
            "ORDER BY d.use_count ASC")
    List<Tags> selectRankTagsxdj(Integer rankId);

    @Select("select * from rank_tags where rank_id=#{rankId}")
    List<RankTags> selectRankTagslistzfm(Integer rankId);
}
