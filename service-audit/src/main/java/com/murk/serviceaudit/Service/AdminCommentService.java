package com.murk.serviceaudit.Service;

import com.github.pagehelper.PageInfo;
import com.murk.commonlib.dto.CommentVo;
import com.murk.commonlib.pojo.Comment;

public interface AdminCommentService {
    PageInfo<Comment> selectAllComment(Integer pageNum, Integer pageSize, Integer offset);

    PageInfo<Comment> selectAllComment1(Integer pageNum, Integer pageSize, Integer offset);

    void aiScan();

    boolean updateComment1(Integer comId);

    boolean update2Comment(Integer comId);
}
