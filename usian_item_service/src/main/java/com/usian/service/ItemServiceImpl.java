package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.TbItemMapper;
import com.usian.pojo.TbItem;
import com.usian.pojo.TbItemExample;
import com.usian.utils.IDUtils;
import com.usian.utils.PageResult;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ItemServiceImpl implements ItemService {

    @Autowired
    private TbItemMapper tbItemMapper;

    @Override
    public TbItem selectItemInfo(Long itemId){
        return tbItemMapper.selectByPrimaryKey(itemId);
    }

    @Override
    public PageResult selectTbItemAllByPage(Integer page, Integer rows) {
        PageHelper.startPage(page,rows);

        TbItemExample tbItemExample = new TbItemExample();

        TbItemExample.Criteria criteria = tbItemExample.createCriteria();
        criteria.andStatusEqualTo((byte)1);

        List<TbItem> tbItemList = tbItemMapper.selectByExample(tbItemExample);
        PageInfo<TbItem> pageInfo = new PageInfo<>(tbItemList);

        PageResult pageResult = new PageResult();
        pageResult.setPageIndex(page);
        pageResult.setTotalPage((int)pageInfo.getTotal());
        pageResult.setResult(tbItemList);

        return pageResult;
    }

    @Override
    public Result insertTbItem(TbItem tbItem, String desc, String itemParams) {

        //补齐tbitem的数据
        long itemId = IDUtils.genItemId();

        return null;
    }
}
