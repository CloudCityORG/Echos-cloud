package com.murk.serviceagent.Service.impl;

import com.murk.serviceagent.Mapper.UserBehaviorMapper;
import com.murk.serviceagent.Service.UserBehaviorService;
import com.murk.serviceagent.pojo.UserBehavior;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserBehaviorServiceImpl implements UserBehaviorService {
    @Autowired
    private UserBehaviorMapper userBehaviorMapper;
    @Override
    public List<UserBehavior> getUserBehavior(int userId, int UTId, int songId, int STId) {
       if(userId==0){
           return Collections.emptyList();
       }else{
           return userBehaviorMapper.selectUserBehaviorById(userId,UTId,songId,STId);
       }

    }
}
