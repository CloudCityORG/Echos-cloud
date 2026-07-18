package com.murk.serviceaudit.Service.impl;

import com.github.pagehelper.PageInfo;
import com.murk.serviceaudit.Feign.ChartFeignClient;          // 🆕 替换 Mapper
import com.murk.commonlib.dto.AuditUpdateRequest;            // 🆕 更新请求体
import com.murk.serviceaudit.Service.AdminCommentService;
import com.murk.serviceaudit.Service.AiAuditService;
import com.murk.commonlib.dto.CommentVo;
import com.murk.commonlib.pojo.Comment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AdminCommentServiceImpl implements AdminCommentService {

    @Autowired
    private ChartFeignClient chartFeignClient;    // 🆕 替代 AdminCommentMapper

    @Autowired
    private AiAuditService aiAuditService;


    @Override
    public PageInfo<Comment> selectAllComment(Integer pageSize, Integer pageNum, Integer offset) {
        List<Comment> allComment = chartFeignClient.getComments(pageNum, pageSize, 0);
        PageInfo<Comment> pageInfo = new PageInfo<>();
        Integer total = chartFeignClient.countComments(0);
        Integer pages = (total + pageSize - 1) / pageSize;
        pageInfo.setList(allComment);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        pageInfo.setPages(pages);
        pageInfo.setTotal(total);
        return pageInfo;
    }

    @Override
    public PageInfo<Comment> selectAllComment1(Integer pageNum, Integer pageSize, Integer offset) {
        List<Comment> allComment = chartFeignClient.getComments(pageNum, pageSize, 1);
        PageInfo<Comment> pageInfo = new PageInfo<>();
        Integer total = chartFeignClient.countComments(1);
        Integer pages = (total + pageSize - 1) / pageSize;
        pageInfo.setList(allComment);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        pageInfo.setPages(pages);
        pageInfo.setTotal(total);
        return pageInfo;
    }

    @Override
    public void aiScan() {
        System.out.println("=====> AI审计开始运行");
        System.out.println("=====================================");

        List<Comment> all = chartFeignClient.getAllPending();
        System.out.println("查到评论数量：" + all.size());

        for (Comment comment : all) {

            System.out.println("评论ID: " + comment.getComId());
            System.out.println("评论内容: " + comment.getComContent());

            int score = aiAuditService.getScore(comment);

            comment.setRiskScore(score);

            // 🆕 构造更新请求
            AuditUpdateRequest req = new AuditUpdateRequest();
            req.setRiskScore(score);

            if (score >= 80) {
                // 高风险 → 拦截 + 删除 + 清空内容
                req.setStatus(1);
                req.setIdDelete(1);
                req.setClearContent(true);              // ← 清空评论内容
                chartFeignClient.updateComment(comment.getComId(), req);
                log.info("🚨 高风险拦截，ID={}", comment.getComId());
            }
            else if (score >= 50) {
                // 中风险 → 标记待审核
                req.setStatus(0);
                req.setIdDelete(comment.getIdDelete());
                // clearContent 默认 false，不清空
                chartFeignClient.updateComment(comment.getComId(), req);
                log.info("⚠️ 中风险标记，ID={}", comment.getComId());
            }
            else {
                // 低风险 → 自动通过
                req.setStatus(1);
                // 不设 idDelete → chart 端按 updateScore3 处理
                chartFeignClient.updateComment(comment.getComId(), req);
                log.info("✅ 低风险通过，ID={}", comment.getComId());
            }

            System.out.println("最终状态：status=" + req.getStatus()
                    + " idDelete=" + req.getIdDelete());
        }

        log.info("AI审计完成");
    }

    @Override
    public boolean updateComment1(Integer comId) {
        chartFeignClient.softDelete(comId);
        return true;
    }

    @Override
    public boolean update2Comment(Integer comId) {
        chartFeignClient.hardDelete(comId);
        return true;
    }
}