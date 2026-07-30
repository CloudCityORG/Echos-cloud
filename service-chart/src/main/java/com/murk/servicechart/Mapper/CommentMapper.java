package com.murk.servicechart.Mapper;

import com.murk.commonlib.pojo.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {
    // ---- 你 chart 原有的评论 CRUD 不动，下面只补 audit 需要的 ----

    @Select("select * from comment where status=#{status} limit #{size} offset #{offset}")
    List<Comment> selectByStatus(@Param("status") int status,
                                 @Param("size") int size,
                                 @Param("offset") int offset);

    @Select("select count(*) from comment where status=#{status}")
    int countByStatus(@Param("status") int status);

    @Select("select * from comment where status=0 and id_delete=0")
    List<Comment> selectAllPending();

    // 原 updateScore1
    @Update("update comment set risk_score=#{riskScore}, comment_content=null, " +
            "status=#{status}, id_delete=#{idDelete} where com_id=#{comId}")
    void updateFull(@Param("comId") Integer comId, @Param("riskScore") Integer riskScore,
                    @Param("status") Integer status, @Param("idDelete") Integer idDelete);

    // 原 updateScore2
    @Update("update comment set risk_score=#{riskScore}, status=#{status}, " +
            "id_delete=#{idDelete} where com_id=#{comId}")
    void updateWithDelete(@Param("comId") Integer comId, @Param("riskScore") Integer riskScore,
                          @Param("status") Integer status, @Param("idDelete") Integer idDelete);

    // 原 updateScore3
    @Update("update comment set status=#{status}, risk_score=#{riskScore} where com_id=#{comId}")
    void updateStatusAndScore(@Param("comId") Integer comId, @Param("status") Integer status,
                              @Param("riskScore") Integer riskScore);

    // 原 deleteComment1
    @Update("update comment set status=1, id_delete=0 where com_id=#{comId}")
    void softDelete(@Param("comId") Integer comId);

    // 原 deleteComment2
    @Update("update comment set status=1, id_delete=1, comment_content=null where com_id=#{comId}")
    void hardDelete(@Param("comId") Integer comId);
}