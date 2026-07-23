package com.murk.servicechart.ES;
import com.murk.commonlib.dto.MyRankWithSong;
import com.murk.commonlib.dto.RankSong;
import com.murk.servicechart.Mapper.UserPublishMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EsSyncService {
    @Autowired
    private SongSearchRepository songSearchRepository;
    @Autowired
    private UserPublishMapper userPublishMapper;
    @Value("${myapp.category:1}")
    private int category;
    @PostConstruct
    public void init(){
        syncAllSongs(category, 100, 0);
    }

    public void syncAllSongs(int category, int pageSize, int offset){
        List<MyRankWithSong> list = userPublishMapper.selectPublishRank(
                category, pageSize, offset);
        List<RankSong> list1=userPublishMapper.selectES();
        for (RankSong rs : list1) {
            // 2. 转成 ES 文档
            Songdocument doc = new Songdocument();
            doc.setSongId(String.valueOf(rs.getSongId()));
            doc.setSongName(rs.getSongName());
            doc.setSingerName(rs.getSingerName());
            doc.setVoteCount(rs.getVoteCount());
            // rankName 暂时不填，或者用 rankId 查
            // doc.setRankName(rankName);

            // 3. 存进 ES
            songSearchRepository.save(doc);
        }
        System.out.println("✅ ES 同步完成！共 " + list.size() + " 条");

    }
}
