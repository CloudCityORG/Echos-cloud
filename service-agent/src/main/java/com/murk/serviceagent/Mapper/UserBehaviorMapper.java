package com.murk.serviceagent.Mapper;

import com.murk.serviceagent.pojo.UserBehavior;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface UserBehaviorMapper {
    List<UserBehavior> selectUserBehaviorById(@Param("userId") int userId, @Param("UTId") int UTId,
                                              @Param("songId") int songId, @Param("STId") int STId);
}
