package com.murk.serviceaudit.Service;

import com.murk.commonlib.pojo.Comment;

public interface AiAuditService {
    int getScore(Comment comment);
}
