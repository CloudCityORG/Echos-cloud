package com.murk.servicechart.Mapper;

import com.murk.commonlib.pojo.Tags;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 操作 chart 库 tags_dictionary 表
 */
@Mapper
public interface TagsMapper {

    @Insert("INSERT INTO tags_dictionary(tag_name, use_count) VALUES(#{tagName}, #{useCount})")
    @Options(useGeneratedKeys = true, keyProperty = "tagId")
    void add(Tags tags);

    @Delete("DELETE FROM tags_dictionary WHERE tag_id = #{tagId}")
    void deleteById(Integer tagId);

    @Update("UPDATE tags_dictionary SET tag_name = #{tagName}, use_count = #{useCount} WHERE tag_id = #{tagId}")
    void update(@Param("tagId") Integer tagId, @Param("tagName") String tagName, @Param("useCount") Integer useCount);

    @Select("SELECT * FROM tags_dictionary WHERE tag_name = #{tagName}")
    Tags selectTagsByName(String tagName);

    @Update("UPDATE tags_dictionary SET use_count = use_count + 1 WHERE tag_id = #{tagId}")
    void incrementUseCount(Integer tagId);

    @Select("SELECT * FROM tags_dictionary WHERE tag_id = #{tagId}")
    Tags selectById(Integer tagId);

    @Select("SELECT tag_id FROM tags_dictionary WHERE tag_name = #{tagName}")
    Integer selectTagIdByName(String tagName);

    @Update("UPDATE tags_dictionary SET use_count = use_count - 1 WHERE tag_id = #{tagId}")
    void updateUseCountfu(Integer tagId);

    @Select("SELECT * FROM tags_dictionary WHERE tag_id IN (${tagIds}) ORDER BY use_count DESC")
    List<Tags> selectByIds(@Param("tagIds") String tagIds);

    @Select("SELECT tag_name, use_count FROM tags_dictionary ORDER BY use_count DESC LIMIT #{limit}")
    List<Tags> planetary(Integer limit);
}
