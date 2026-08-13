package com.murk.serviceuser.Controller;

import com.murk.commonlib.pojo.UserTags;
import com.murk.commonlib.utils.Result;
import com.murk.serviceuser.Mapper.FriendMapper;
import com.murk.serviceuser.Mapper.UserMapper;
import com.murk.serviceuser.Mapper.UserTagsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 供其它微服务调用 service-user 的 user_tags 接口
 */
@RestController
@RequestMapping("/api/user")
public class UserTagApiController {

    @Autowired
    private UserTagsMapper userTagsMapper;
    @Autowired
    private FriendMapper friendMapper;
    @Autowired
    private UserMapper userMapper;

    @GetMapping("/follow")
    public Result<Boolean> isFollowed(@RequestParam("userId") Integer userId,
                                      @RequestParam("followId") Integer followId) {
        return Result.success(friendMapper.isFollowed(userId, followId) != null);
    }

    @GetMapping("/tag/ids")
    public Result<List<Integer>> getUserTagIds(@RequestParam("userId") Integer userId) {
        return Result.success(userTagsMapper.selectTagIdsByUserId(userId));
    }

    @GetMapping("/tags")
    public Result<List<UserTags>> getUserTags(@RequestParam("userId") Integer userId) {
        return Result.success(userTagsMapper.selectByUserId(userId));
    }

    @GetMapping("/tag")
    public Result<UserTags> getUserTag(@RequestParam("userId") Integer userId,
                                       @RequestParam("tagId") Integer tagId) {
        return Result.success(userTagsMapper.selectByUserIdAndTagId(userId, tagId));
    }

    @PostMapping("/tag")
    public Result<Void> addUserTag(@RequestBody UserTags userTags) {
        userTagsMapper.add(userTags);
        return Result.success();
    }

    @PostMapping("/tag/update")
    public Result<Void> updateUserTag(@RequestBody UserTags userTags) {
        userTagsMapper.update(userTags);
        return Result.success();
    }

    @DeleteMapping("/tag")
    public Result<Void> deleteUserTag(@RequestParam("userId") Integer userId,
                                      @RequestParam("tagId") Integer tagId) {
        userTagsMapper.deleteByTagIdinuser(tagId, userId);
        return Result.success();
    }

    @PutMapping("/tag/association")
    public Result<Integer> updateUserTagAssociation(@RequestParam("userId") Integer userId,
                                                    @RequestParam("oldTagId") Integer oldTagId,
                                                    @RequestParam("targetTagId") Integer targetTagId) {
        return Result.success(userTagsMapper.updateUserTagAssociation(userId, oldTagId, targetTagId));
    }

    @GetMapping("/{userId}/username")
    public Result<String> getUsernameById(@PathVariable("userId") Integer userId) {
        return Result.success(userMapper.getUsernameById(userId));
    }
}
