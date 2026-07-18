package com.murk.commonlib.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RankTags {
    private int RTId;
    private int rankId;
    private int userId;
    private int tagId;
    private Date createTime;
}
