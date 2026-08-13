package com.murk.commonlib.pojo;

import com.github.yulichang.annotation.Table;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table("tags_dictionary")
public class Tags {

    private static final long serialVersionUID = 1L;

    private Integer tagId;
    private String tagName;
    private Integer useCount;

}
