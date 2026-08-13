package com.murk.commonlib.dto;

import lombok.Data;

@Data
public class AuditUpdateRequest {
    private Integer status;      // 0=待审核 1=已审核
    private Integer riskScore;   // AI 风险评分
    private Integer idDelete;    // 0=未删 1=已删
    private boolean clearContent; // 是否清空评论内容
}