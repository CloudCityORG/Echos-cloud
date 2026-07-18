package com.murk.serviceagent.Service.impl;

import com.murk.serviceagent.Mapper.UserInterestVetorMapper;
import com.murk.serviceagent.Service.UserInterestVetorService;
import com.murk.serviceagent.pojo.UserInterestVetor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserInterestVetorServiceImpl implements UserInterestVetorService {
    @Autowired
    private UserInterestVetorMapper userInterestVetorMapper;
    @Override
    public List<UserInterestVetor> getUserInterestVetor(int userId) {
        if(userId==0){
            return Collections.emptyList();
        }else{
            return userInterestVetorMapper.selectUserInterestById(userId);
        }
    }
}
