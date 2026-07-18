package com.murk.serviceuser.Service;

import com.murk.serviceuser.dto.LoginSuccessVo;
import com.murk.serviceuser.dto.UserLoginDTO;
import com.murk.serviceuser.dto.UserRegisterDTO;
import com.murk.commonlib.utils.Result;

public interface UserInfoService {
    //登录功能
    Result<LoginSuccessVo> login(UserLoginDTO userLoginDTO);
    //注册功能
    Result<String> register(UserRegisterDTO userRegisterDTO);
    //首页功能
   /*返回值设为集合类型，从Mapper层的功能考虑
   Mapper层要查询所有Id主键，将它们都封装到集合中*/



}
