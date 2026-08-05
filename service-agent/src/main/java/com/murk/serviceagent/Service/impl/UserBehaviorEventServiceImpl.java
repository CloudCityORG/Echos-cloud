package com.murk.serviceagent.Service.impl;

import com.murk.serviceagent.Mapper.UserBehaviorEventMapper;
import com.murk.serviceagent.Service.UserBehaviorEventService;
import com.murk.serviceagent.pojo.UserBehaviorEventEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserBehaviorEventServiceImpl implements UserBehaviorEventService {

    private final UserBehaviorEventMapper userBehaviorEventMapper;

    @Override
    public void save(UserBehaviorEventEntity event) {
        if (event.getUserId() == null || event.getTargetId() == null
                || event.getActionType() == null || event.getTargetType() == null) {
            throw new IllegalArgumentException("用户行为事件关键字段不能为空");
        }
        userBehaviorEventMapper.insert(event);
    }

    @Override
    public List<UserBehaviorEventEntity> listByUserId(Integer userId, Integer limit) {
        return userBehaviorEventMapper.selectByUserId(userId, limit == null ? 50 : limit);
    }

    @Override
    public List<UserBehaviorEventEntity> listRecentLikes(Integer userId, Integer limit) {
        return userBehaviorEventMapper.selectRecentEvents(userId, 1, limit == null ? 20 : limit);
    }

    @Override
    public List<UserBehaviorEventEntity> listRecentCollects(Integer userId, Integer limit) {
        return userBehaviorEventMapper.selectRecentEvents(userId, 2, limit == null ? 20 : limit);
    }

    @Override
    public List<UserBehaviorEventEntity> listRecentBrowses(Integer userId, Integer limit) {
        return userBehaviorEventMapper.selectRecentEvents(userId, 3, limit == null ? 20 : limit);
    }
}
