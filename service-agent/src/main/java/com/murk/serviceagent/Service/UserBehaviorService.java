package com.murk.serviceagent.Service;

import com.murk.serviceagent.pojo.UserBehavior;

import java.util.List;

public interface UserBehaviorService {
    List<UserBehavior> getUserBehavior(int userId, int UTId, int songId, int STId);
}
