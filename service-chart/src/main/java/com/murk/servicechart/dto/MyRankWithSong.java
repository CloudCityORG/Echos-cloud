package com.murk.servicechart.dto;

import com.murk.commonlib.dto.RankSong;
import com.murk.commonlib.dto.RankTagVO;
import com.murk.commonlib.pojo.RankTags;
import com.murk.commonlib.pojo.UserTags;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Setter
@Getter
public class MyRankWithSong {
   private Integer rankId;
   private String rankName;
   private Integer voteCount;
   private String username;
   private Integer userId;
   private Integer loveCount;
   private LocalDateTime publishTime;
   private List<RankSong> rankSongList;

   private boolean Followed;
   private RankTagVO rankTagVOList;
   private List<RankTags> rankTagsList;

   private List<UserTags> userTagList;
}
