package com.murk.serviceaudit.Feign;

import com.murk.commonlib.dto.AuditUpdateRequest;
import com.murk.commonlib.pojo.Comment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name="service-chart")
public interface ChartFeignClient {
    /** 分页查询 */
    @GetMapping("/api/audit/comments")
    List<Comment> getComments(@RequestParam("page") int page,
                              @RequestParam("size") int size,
                              @RequestParam("status") int status);

    /** 计数 */
    @GetMapping("/api/audit/comments/count")
    int countComments(@RequestParam("status") int status);

    /** AI 审核专用：全量获取 */
    @GetMapping("/api/audit/comments/all")
    List<Comment> getAllPending();

    /** 更新审核结果 */
    @PutMapping("/api/audit/comments/{comId}")
    void updateComment(@PathVariable("comId") Integer comId,
                       @RequestBody AuditUpdateRequest request);

    /** 软删除 */
    @PutMapping("/api/audit/comments/{comId}/soft-delete")
    void softDelete(@PathVariable("comId") Integer comId);

    /** 硬删除（清空内容） */
    @PutMapping("/api/audit/comments/{comId}/hard-delete")
    void hardDelete(@PathVariable("comId") Integer comId);
}

