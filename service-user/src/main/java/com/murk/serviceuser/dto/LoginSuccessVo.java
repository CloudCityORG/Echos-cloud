package com.murk.serviceuser.dto;

import lombok.Data;

@Data
public class LoginSuccessVo {
    private String token;
    private String userId;
    private String userName;
}
