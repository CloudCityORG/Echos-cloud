package com.murk.serviceuser.Service;

import com.murk.commonlib.dto.TagsDTO;
import com.murk.commonlib.dto.TagsPageQueryDTO;
import com.murk.commonlib.result.PageResult;
import org.springframework.stereotype.Service;


@Service
public interface TagsService {


    void cancelFromRanking(Integer rankId,Integer tagId);

    void add(TagsDTO tagsDTO, Integer userId);



    PageResult pageQueryUserTags(TagsPageQueryDTO tagsPageQueryDTO);

    void delete(Integer id);

    void update(TagsDTO tagsDTO, Integer tagId);


    PageResult planetary();
}
