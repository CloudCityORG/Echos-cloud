package com.murk.servicechart.Service;

import com.murk.commonlib.dto.AuditUpdateRequest;
import com.murk.commonlib.pojo.Comment;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CommentService {
    List<Comment> findByStatus(int status, int size, int offset);
    int countByStatus(int status);
    List<Comment> findAllPending();
    void auditUpdate(Integer comId, AuditUpdateRequest req);
    void softDelete(Integer comId);
    void hardDelete(Integer comId);
}
