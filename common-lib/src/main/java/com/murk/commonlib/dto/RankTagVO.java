package com.murk.commonlib.dto;

import com.murk.commonlib.pojo.Tags;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RankTagVO {
    private Integer rankId;
    private List<Tags> tags;
}
