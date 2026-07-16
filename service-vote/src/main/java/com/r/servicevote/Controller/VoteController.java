package com.r.servicevote.Controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class VoteController {
    @PostMapping("/vote")
    @ResponseBody
    @Operation(summary = "点赞榜单", description = "点赞榜单")
    public com.music.utils.Result<String> vote(@RequestParam("token")String token, @RequestParam("rankId")Integer rankId){
        if(token == null || token.isEmpty()) {
            return com.music.utils.Result.error("token不能为空哦");
        }
        try{
            Integer userId = com.music.utils.JwtUtils.getUserIdFromToken(token);
            if(rankId==null){
                return com.music.utils.Result.error("likeId不能为空");
            }
            // 和热门榜单接口保持同一套投票计分逻辑：
            // 统一走 HotRankService，确保计数、ZSet、排名战报一致
            boolean result=hotRankService.insertVote(userId,rankId);
            if(result==true){
                log.info("投票成功 ");
                hotRankService.updateHotRank(rankId);
                return com.music.utils.Result.success("点赞成功，战报正在后台生成...");

            }else {
                return com.music.utils.Result.error("点赞榜单失败");
            }
        }catch (Exception e){
            return com.music.utils.Result.error("点赞该榜单失败"+e.getMessage());
        }
    }


}
