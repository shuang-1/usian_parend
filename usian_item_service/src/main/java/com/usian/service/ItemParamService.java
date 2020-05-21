package com.usian.service;

import com.usian.pojo.TbItemParam;
import com.usian.utils.PageResult;

public interface ItemParamService {
    TbItemParam selectItemParamByItemCatId(Long itemCatId);

    PageResult selectItemParamAll(Integer pageNum, Integer rows);

    Integer insertItemParam(Long itemCatId, String paramData);

    Integer selectItemParamByItemCatId2(Long itemCatId);

    Integer deleteItemParamById(Integer id);
}
