package com.murk.servicechart.Controller;

import com.murk.commonlib.dto.AuditUpdateRequest;
import com.murk.commonlib.pojo.Comment;
import com.murk.servicechart.Service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//这个Controller及其相应Service，mapper的作用是查询comment数据库完成audit业务层所需要的
//利用Fegin实现
@RestController
@RequestMapping("/api/audit/comments")
public class CommentApiController {
     @Autowired
     private CommentService commentService;

     // ========== 查询 ==========

     /** 分页查评论（status=0 待审核 / status=1 已审核） */
     @GetMapping
     public List<Comment> getComments(@RequestParam int page,
                                      @RequestParam int size,
                                      @RequestParam(defaultValue = "0") int status) {
         int offset = (page - 1) * size;
         return commentService.findByStatus(status, size, offset);
     }

     /** 统计数量 */
     @GetMapping("/count")
     public int countComments(@RequestParam(defaultValue = "0") int status) {
         return commentService.countByStatus(status);
     }

     /** AI 审核专用：全量获取待审核评论 */
     @GetMapping("/all")
     public List<Comment> getAllPending() {
         return commentService.findAllPending();
     }

     // ========== 更新 ==========

     /** 更新审核结果（覆盖 updateScore1 / updateScore2 / updateScore3） */
     @PutMapping("/{comId}")
     public void updateComment(@PathVariable Integer comId,
                               @RequestBody AuditUpdateRequest req) {
         commentService.auditUpdate(comId, req);
     }

     /** 软删除 */
     @PutMapping("/{comId}/soft-delete")
     public void softDelete(@PathVariable Integer comId) {
         commentService.softDelete(comId);
     }

     /** 硬删除（清空内容） */
     @PutMapping("/{comId}/hard-delete")
     public void hardDelete(@PathVariable Integer comId) {
         commentService.hardDelete(comId);
     }
 }
