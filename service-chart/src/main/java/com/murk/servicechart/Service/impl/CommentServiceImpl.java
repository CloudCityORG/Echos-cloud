package com.murk.servicechart.Service.impl;

import com.murk.commonlib.dto.AuditUpdateRequest;
import com.murk.commonlib.pojo.Comment;
import com.murk.servicechart.Mapper.CommentMapper;
import com.murk.servicechart.Service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Override
    public List<Comment> findByStatus(int status, int size, int offset) {
        // status=0 → 对应原来的 selectAllComment
        // status=1 → 对应原来的 selectAllComment1
        return commentMapper.selectByStatus(status, size, offset);
    }
     @Override
    public int countByStatus(int status) {
        // 对应原来的 selectAll / selectAll1
        return commentMapper.countByStatus(status);
    }
     @Override
    public List<Comment> findAllPending() {
        // 对应原来的 selectAllCom
        return commentMapper.selectAllPending();
    }
    @Override
    public void auditUpdate(Integer comId, AuditUpdateRequest req) {
        // 合并了 updateScore1 / updateScore2 / updateScore3
        if (req.isClearContent()) {
            // 原 updateScore1：评分 + 清空内容 + 状态 + 删除标记
            commentMapper.updateFull(comId, req.getRiskScore(), req.getStatus(), req.getIdDelete());
        } else if (req.getIdDelete() != null) {
            // 原 updateScore2：评分 + 状态 + 删除标记
            commentMapper.updateWithDelete(comId, req.getRiskScore(), req.getStatus(), req.getIdDelete());
        } else {
            // 原 updateScore3：评分 + 状态
            commentMapper.updateStatusAndScore(comId, req.getStatus(), req.getRiskScore());
        }
    }
   @Override
    public void softDelete(Integer comId) {
        commentMapper.softDelete(comId);   // 原 deleteComment1
    }
   @Override
    public void hardDelete(Integer comId) {
        commentMapper.hardDelete(comId);   // 原 deleteComment2
    }
}
