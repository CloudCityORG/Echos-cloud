package com.murk.serviceuser.Mapper;

import com.murk.serviceuser.pojo.UserFollow;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FriendMapper {

    /**
     * 获取当前用户互关的好友ID列表
     */
    List<Integer> selectMutualFriendIds(@Param("userId") Integer userId,
                                        @Param("limit") Integer limit);

    @Select("SELECT * FROM user_follow WHERE user_id = #{currentUserId} AND follow_id = #{targetUserId}")
    UserFollow isFollowed(Integer currentUserId, Integer targetUserId);

    @Insert("INSERT INTO user_follow (user_id, follow_id, is_mutual, create_time) VALUES (#{userId}, #{followId}, #{isMutual}, #{createTime})")
    void add(UserFollow userFollow);

    @Delete("DELETE FROM user_follow WHERE user_id = #{currentUserId} AND follow_id = #{targetUserId}")
    void delete(@Param("currentUserId") Integer currentUserId, @Param("targetUserId") Integer targetUserId);

    /**
     * 获取我的粉丝列表
     */
    @Select("SELECT * FROM user_follow WHERE user_id = #{currentUserId}")
    List<UserFollow> getMyFriends(Integer currentUserId);

    @Select( "SELECT * FROM user_follow WHERE follow_id = #{currentUserId}")
    List<UserFollow> getMymaster(Integer currentUserId);

    @Update("UPDATE user_follow SET is_mutual = #{isMutual} WHERE user_id = #{userId} AND follow_id = #{followId}")
    void update(UserFollow userFollow);

    /**
     * 查询“我关注的人”列表（并计算是否互关）
     * @param userId 当前用户的ID
     * @param currentUserId 当前登录用户的ID（用于计算互关状态）
     */
    List<UserFollow> selectFollowList(@Param("userId") Integer userId, @Param("currentUserId") Integer currentUserId);

    /**
     * 查询“关注我的人”列表（并计算是否互关）
     * @param userId 当前用户的ID（作为被关注者）
     * @param currentUserId 当前登录用户的ID（用于计算互关状态）
     */
    List<UserFollow> selectFanList(@Param("userId") Integer userId, @Param("currentUserId") Integer currentUserId);
}
