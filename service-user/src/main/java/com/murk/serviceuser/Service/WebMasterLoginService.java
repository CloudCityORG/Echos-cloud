package com.murk.serviceuser.Service;

import com.murk.serviceuser.dto.LoginSuccessVo;
import com.murk.serviceuser.dto.UserLoginDTO;
import com.murk.serviceuser.dto.UserRegisterDTO;
import com.murk.commonlib.utils.Result;
import jakarta.validation.Valid;

public interface WebMasterLoginService {
    Result<LoginSuccessVo> selectWebmaster(@Valid UserLoginDTO userLoginDTO);

    Result<String> register(@Valid UserRegisterDTO userRegisterDTO);


}
