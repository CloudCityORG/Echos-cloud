package com.murk.serviceuser.Mapper;

import com.murk.commonlib.pojo.UserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WebMasterLoginMapper {
    UserInfo selectAdmin(String username);

    void insertAdmin(UserInfo newUser);
}
