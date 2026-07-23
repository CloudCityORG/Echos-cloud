package com.murk.servicechart.ES;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongSearchRepository extends ElasticsearchRepository<Songdocument,Integer> {
    //这里注意一定是接口去继承，spring Data ES会自动实现DSSL，即match等等
    //歌手名歌名同时搜索
    List<Songdocument> findBySongName(String songKeywords,String singerKeywords);
    //单搜索歌名或者歌手名
    List<Songdocument> findBySongName(String songKeywords);
    List<Songdocument> findBySingerName(String singerKeywords);
}



