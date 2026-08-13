package com.murk.servicechart.ES;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongSearchRepository extends ElasticsearchRepository<Songdocument, String> {
    // 这里注意一定是接口去继承，spring Data ES 会自动实现 DSL，即 match 等等

    // 单搜索歌名
    List<Songdocument> findBySongName(String songKeywords);

    // 单搜索歌手名
    List<Songdocument> findBySingerName(String singerKeywords);

    // 歌手名或歌名同时搜索（OR 查询）
    List<Songdocument> findBySongNameOrSingerName(String songKeywords, String singerKeywords);
}



