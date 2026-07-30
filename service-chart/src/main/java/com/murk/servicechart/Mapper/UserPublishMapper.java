package com.murk.servicechart.Mapper;

import com.murk.commonlib.pojo.Comment;
import com.murk.commonlib.pojo.Singer;
import com.murk.commonlib.pojo.Song;
import com.murk.servicechart.dto.CommentVo;
import com.murk.servicechart.dto.MyRankWithSong;
import com.murk.commonlib.dto.PersonalRank;
import com.murk.commonlib.dto.RankSong;
import lombok.With;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserPublishMapper {
    int insertRank(PersonalRank personalRank);
    int sixInsert(@Param("list") List<RankSong> ranksongList);

    List<Singer> selectSinger(Integer categoryId);

    List<Song> selectSong(Integer singerId);

    List<MyRankWithSong> selectPublishRank(@Param("category")Integer category,@Param("pageSize") Integer pageSize,
                                           @Param("offset") Integer offset);

    @Select("select count(*) from personal_rank where category_id=#{category}")
    Integer selectTotal(@Param("category") Integer category);

    List<MyRankWithSong> selectSearch(Integer category, Integer pageNum, Integer offset, Integer pageSize, String keyword);

    Integer selectSearchTotal(Integer category, String keyword);

    List<MyRankWithSong> searchRanksByUser(@Param("keyword") String keyword, @Param("userId") Integer userId);

    @Update("update personal_rank set vote_count=#{rows} where rank_id=#{rankId}")
    int updateVoteCount(@Param("rankId") Integer rankId,@Param("rows") int rows);

    @Update("update personal_rank set love_count=#{row1} where rank_id=#{rankId} and user_id=#{userId}")
    int updateLoveCount(Integer userId, int row1, Integer rankId);

    void insertComment(Integer rankId, Integer userId, String content, Integer parentId);

    List<CommentVo> selectComment(@Param("rankId") Integer rankId,@Param("userId") Integer userId);

    Comment selectCommentById(Integer comId);

    @Update("update comment set id_delete=#{idDelete}  where com_id=#{comId}")
    int updateComment(Integer comId, int idDelete);

    @Select("select * from personal_rank where rank_id=#{rankId} ")
    PersonalRank selectRankById(Integer rankId);

    @Select("select * from rank_song")
    List<RankSong> selectES();

    @Select("select rank_id from personal_rank")
    List<Integer> selectAllRankIds();
}
