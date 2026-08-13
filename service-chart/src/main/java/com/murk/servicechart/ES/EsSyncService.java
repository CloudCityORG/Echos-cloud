package com.murk.servicechart.ES;

import com.murk.commonlib.dto.RankSong;
import com.murk.servicechart.Mapper.UserPublishMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@ConditionalOnProperty(name = "spring.data.elasticsearch.repositories.enabled", havingValue = "true", matchIfMissing = true)
public class EsSyncService {
    @Autowired
    private SongSearchRepository songSearchRepository;
    @Autowired
    private UserPublishMapper userPublishMapper;
    @Value("${myapp.category:1}")
    private int category;

    @PostConstruct
    public void init() {
        try {
            syncAllSongs(category, 100, 0);
        } catch (Exception e) {
            log.warn("ES 初始化同步失败，应用仍将继续启动: {}", e.getMessage(), e);
        }
    }

    public void syncAllSongs(int category, int pageSize, int offset) {
        List<RankSong> list = userPublishMapper.selectES();
        for (RankSong rs : list) {
            // 转成 ES 文档
            Songdocument doc = new Songdocument();
            doc.setSongId(String.valueOf(rs.getSongId()));
            doc.setSongName(rs.getSongName());
            doc.setSingerName(rs.getSingerName());
            doc.setVoteCount(rs.getVoteCount());
            // rankName 暂时不填，或者用 rankId 查
            // doc.setRankName(rankName);

            // 存进 ES
            songSearchRepository.save(doc);
        }
        log.info("✅ ES 同步完成！共 {} 条", list.size());
    }
}
