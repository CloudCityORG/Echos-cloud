package com.murk.serviceuser.Mapper;

import com.murk.commonlib.dto.UserNameVO;
import com.murk.commonlib.pojo.UserInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserInfoMapper {
    int insert(UserInfo userInfo);//注册
    UserInfo selectByUsername(String username);//登录

    List<UserNameVO> getNamesByIds(List<Integer> userIds);
}
