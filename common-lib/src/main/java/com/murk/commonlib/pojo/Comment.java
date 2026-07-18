package com.murk.commonlib.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Comment {
    private int comId;
    private int userId;
    private String comContent;
    private Integer parentId;
    private int rankId;
    private int idDelete;
    private LocalDateTime comTime;
    private int isAi;
    private int status;
    private int reportFlag;
    private int riskScore;
    private String auditRemark;
}

