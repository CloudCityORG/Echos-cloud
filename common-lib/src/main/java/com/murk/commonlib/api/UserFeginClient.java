package com.murk.commonlib.api;

import com.murk.commonlib.dto.UserNameVO;
import com.murk.commonlib.pojo.UserTags;
import com.murk.commonlib.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient("service-user")
public interface UserFeginClient {

    /**
     * 批量查询用户名
     */
    @PostMapping("/api/user/batch")
    Result<List<UserNameVO>> getUserByIds(@RequestBody List<Integer> userIds);

    /**
     * 批量查询用户名（返回 Map<userId, username>）
     */
    @PostMapping("/api/user/names")
    Result<Map<Integer, String>> getUsernamesByIds(@RequestBody List<Integer> userIds);

    /**
     * 根据 userId 查询用户名
     */
    @GetMapping("/api/user/{userId}/username")
    Result<String> getUsernameById(@PathVariable("userId") Integer userId);

    /**
     * 查询用户拥有的所有标签ID
     */
    @GetMapping("/api/user/tag/ids")
    Result<List<Integer>> getUserTagIds(@RequestParam("userId") Integer userId);

    /**
     * 查询用户拥有的所有用户标签（含标签名）
     */
    @GetMapping("/api/user/tags")
    Result<List<UserTags>> getUserTags(@RequestParam("userId") Integer userId);

    /**
     * 查询用户某个标签的关联记录
     */
    @GetMapping("/api/user/tag")
    Result<UserTags> getUserTag(@RequestParam("userId") Integer userId,
                                @RequestParam("tagId") Integer tagId);

    /**
     * 新增用户标签关联
     */
    @PostMapping("/api/user/tag")
    Result<Void> addUserTag(@RequestBody UserTags userTags);

    /**
     * 更新用户标签关联（使用次数 +1）
     */
    @PostMapping("/api/user/tag/update")
    Result<Void> updateUserTag(@RequestBody UserTags userTags);

    /**
     * 删除用户标签关联
     */
    @DeleteMapping("/api/user/tag")
    Result<Void> deleteUserTag(@RequestParam("userId") Integer userId,
                               @RequestParam("tagId") Integer tagId);

    /**
     * 批量更新用户标签关联（将旧标签替换为新标签）
     */
    @PutMapping("/api/user/tag/association")
    Result<Integer> updateUserTagAssociation(@RequestParam("userId") Integer userId,
                                             @RequestParam("oldTagId") Integer oldTagId,
                                             @RequestParam("targetTagId") Integer targetTagId);

    /**
     * 查询 userId 是否关注了 followId
     */
    @GetMapping("/api/user/follow")
    Result<Boolean> isFollowed(@RequestParam("userId") Integer userId,
                               @RequestParam("followId") Integer followId);
}
