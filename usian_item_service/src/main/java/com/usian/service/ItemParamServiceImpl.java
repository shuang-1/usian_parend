package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.TbItemMapper;
import com.usian.mapper.TbItemParamMapper;
import com.usian.pojo.TbItem;
import com.usian.pojo.TbItemExample;
import com.usian.pojo.TbItemParam;
import com.usian.pojo.TbItemParamExample;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ItemParamServiceImpl implements ItemParamService {

    @Autowired
    private TbItemParamMapper tbItemParamMapper;


    @Override
    public TbItemParam selectItemParamByItemCatId(Long itemCatId) {

        TbItemParamExample tbItemParamExample = new TbItemParamExample();
        TbItemParamExample.Criteria criteria = tbItemParamExample.createCriteria();
        criteria.andItemCatIdEqualTo(itemCatId);
        List<TbItemParam> tbItemParamList = tbItemParamMapper.selectByExampleWithBLOBs(tbItemParamExample);

        return tbItemParamList.get(0);
    }

    @Override
    public PageResult selectItemParamAll(Integer pageNum, Integer rows) {
        PageHelper.startPage(pageNum,rows);
        TbItemParamExample tbItemParamExample = new TbItemParamExample();
        List<TbItemParam> tbItemParams = tbItemParamMapper.selectByExampleWithBLOBs(tbItemParamExample);

        PageInfo<TbItemParam> pageInfo = new PageInfo<>(tbItemParams);

        PageResult pageResult = new PageResult();
        pageResult.setResult(pageInfo.getList());
        pageResult.setTotalPage((int)pageInfo.getTotal());
        pageResult.setPageIndex(pageInfo.getPageNum());

        return pageResult;
    }

    @Override
    public Integer insertItemParam(Long itemCatId, String paramData) {

        TbItemParam tbItemParam = new TbItemParam();
        tbItemParam.setItemCatId(itemCatId);
        tbItemParam.setParamData(paramData);
        tbItemParam.setCreated(new Date());
        tbItemParam.setUpdated(new Date());

        return tbItemParamMapper.insertSelective(tbItemParam);
    }

    @Override
    public Integer selectItemParamByItemCatId2(Long itemCatId) {

        TbItemParamExample tbItemParamExample = new TbItemParamExample();
        TbItemParamExample.Criteria criteria = tbItemParamExample.createCriteria();
        criteria.andItemCatIdEqualTo(itemCatId);

        List<TbItemParam> tbItemParams = tbItemParamMapper.selectByExampleWithBLOBs(tbItemParamExample);
        if(tbItemParams.size()>0){
            return 0;
        }
        return 1;
    }

    @Override
    public Integer deleteItemParamById(Integer id) {
        return tbItemParamMapper.deleteByPrimaryKey(Long.valueOf(id));
    }


}
