package com.murk.serviceuser.Mapper;

import com.murk.commonlib.pojo.UserInfo;
import com.murk.servicechart.pojo.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserInfoMapper {
    int insert(UserInfo userInfo);//注册
    UserInfo selectByUsername(String username);//登录
}
