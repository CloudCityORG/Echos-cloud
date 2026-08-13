package com.murk.serviceuser.Controller;

import com.murk.commonlib.dto.UserNameVO;
import com.murk.commonlib.utils.Result;
import com.murk.serviceuser.Service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserApiController {
    @Autowired
    private UserInfoService userInfoService;

    @PostMapping("/batch")
    public Result<List<UserNameVO>> getUsersByIds(@RequestBody List<Integer> userIds) {
        List<UserNameVO> list = userInfoService.getNamesByIds(userIds);
        return Result.success(list);
    }

    @PostMapping("/names")
    public Result<Map<Integer, String>> getUsernamesByIds(@RequestBody List<Integer> userIds) {
        List<UserNameVO> list = userInfoService.getNamesByIds(userIds);
        if (list == null) {
            return Result.success(Map.of());
        }
        Map<Integer, String> map = list.stream()
                .collect(Collectors.toMap(UserNameVO::getUserId, UserNameVO::getUserName, (a, b) -> a));
        return Result.success(map);
    }
}
