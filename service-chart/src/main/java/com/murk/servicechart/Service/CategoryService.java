package com.murk.servicechart.Service;

import com.murk.servicechart.pojo.Category;
import com.murk.commonlib.utils.Result;

import java.util.List;

public interface CategoryService {
    Result<List<Category>> selectAllCategoryId();

    Result<Category> selectCategoryId(Integer categoryId);
}
