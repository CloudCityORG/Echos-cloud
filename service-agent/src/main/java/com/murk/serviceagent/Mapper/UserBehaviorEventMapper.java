package com.murk.serviceagent.Mapper;

import com.murk.serviceagent.pojo.UserBehaviorEventEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserBehaviorEventMapper {

    int insert(UserBehaviorEventEntity event);

    List<UserBehaviorEventEntity> selectByUserId(@Param("userId") Integer userId,
                                                  @Param("limit") Integer limit);

    List<UserBehaviorEventEntity> selectRecentEvents(@Param("userId") Integer userId,
                                                      @Param("actionType") Integer actionType,
                                                      @Param("limit") Integer limit);
}
