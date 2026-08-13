package com.murk.serviceagent.Service;

import com.murk.serviceagent.pojo.UserBehaviorEventEntity;

import java.util.List;

public interface UserBehaviorEventService {

    void save(UserBehaviorEventEntity event);

    List<UserBehaviorEventEntity> listByUserId(Integer userId, Integer limit);

    List<UserBehaviorEventEntity> listRecentLikes(Integer userId, Integer limit);

    List<UserBehaviorEventEntity> listRecentCollects(Integer userId, Integer limit);

    List<UserBehaviorEventEntity> listRecentBrowses(Integer userId, Integer limit);
}
