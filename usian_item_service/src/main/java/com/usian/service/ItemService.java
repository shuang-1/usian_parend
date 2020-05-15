package com.usian.service;

import com.usian.pojo.TbItem;
import com.usian.utils.PageResult;
import com.usian.utils.Result;

public interface ItemService {

    TbItem selectItemInfo(Long itemId);

    PageResult selectTbItemAllByPage(Integer page, Integer rows);

    Result insertTbItem(TbItem tbItem, String desc, String itemParams);
}

