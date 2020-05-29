package com.usian.service;

import com.usian.pojo.TbContent;
import com.usian.utils.AdNode;
import com.usian.utils.PageResult;

import java.util.List;

public interface ContentService {
    PageResult selectTbContentAllByCategoryId(Long categoryId, Integer page, Integer rows);

    Integer insertTbContent(TbContent tbContent);

    Integer deleteContentByIds(Long ids);

    List<AdNode> selectFrontendContentByAD();
}
