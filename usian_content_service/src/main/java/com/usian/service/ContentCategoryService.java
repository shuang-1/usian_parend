package com.usian.service;

import com.usian.pojo.TbContentCategory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface ContentCategoryService {
    List<TbContentCategory> selectContentCategoryByParentId(Long id);

    Integer insertContentCategory(Integer parentId, String name);

    Integer deleteContentCategoryById(Long category);

    Integer updateContentCategory(Integer id, String name);
}
