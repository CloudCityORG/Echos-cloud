package com.murk.servicechart.Mapper;

import com.murk.servicechart.pojo.Category;
import com.murk.commonlib.utils.Result;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    Result<List<Category>> selectAllCategoryId();//前端主页分类模块展示


     Category selectCategoryId(Integer categoryId);//用户选择具体类别并跳转

}
