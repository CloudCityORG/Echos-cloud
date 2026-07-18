package com.murk.servicevote.Controller;

import com.murk.servicevote.Service.HotRankService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
@Slf4j
@Controller
public class VoteController {
    @Autowired
    private HotRankService hotRankService;
    @PostMapping("/vote")
    @ResponseBody
    @Operation(summary = "点赞榜单", description = "点赞榜单")
    public com.murk.commonlib.utils.Result<String> vote(@RequestParam("token")String token, @RequestParam("rankId")Integer rankId){
        if(token == null || token.isEmpty()) {
            return com.murk.commonlib.utils.Result.error("token不能为空哦");
        }
        try{
            Integer userId = com.murk.commonlib.utils.JwtUtils.getUserIdFromToken(token);
            if(rankId==null){
                return com.murk.commonlib.utils.Result.error("likeId不能为空");
            }
            // 和热门榜单接口保持同一套投票计分逻辑：
            // 统一走 HotRankService，确保计数、ZSet、排名战报一致
            boolean result=hotRankService.insertVote(userId,rankId);
            if(result==true){
                log.info("投票成功 ");
                hotRankService.updateHotRank(rankId);
                return com.murk.commonlib.utils.Result.success("点赞成功，战报正在后台生成...");

            }else {
                return com.murk.commonlib.utils.Result.error("点赞榜单失败");
            }
        }catch (Exception e){
            return com.murk.commonlib.utils.Result.error("点赞该榜单失败"+e.getMessage());
        }
    }


}
