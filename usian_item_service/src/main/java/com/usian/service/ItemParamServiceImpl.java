package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.TbItemParamItemMapper;
import com.usian.mapper.TbItemParamMapper;
import com.usian.pojo.TbItemParam;
import com.usian.pojo.TbItemParamExample;
import com.usian.pojo.TbItemParamItem;
import com.usian.pojo.TbItemParamItemExample;
import com.usian.redis.RedisClient;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ItemParamServiceImpl implements ItemParamService {

    @Autowired
    private TbItemParamMapper tbItemParamMapper;

    @Autowired
    private TbItemParamItemMapper tbItemParamItemMapper;

    @Value("${ITEM_INFO}")
    private String ITEM_INFO;

    @Value("${PARAM}")
    private String PARAM;

    @Value("${ITEM_INFO_EXPIRE}")
    private Integer ITEM_INFO_EXPIRE;

    @Autowired
    private RedisClient redisClient;

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

    @Override
    public TbItemParamItem selectTbItemParamItemByItemId(Long itemId) {
        //从redis查询
        TbItemParamItem tbItemParamItem = (TbItemParamItem) redisClient.get(ITEM_INFO + ":" + itemId + ":" + PARAM);
        if(tbItemParamItem!=null){
            return tbItemParamItem;
        }

        if(redisClient.setnx("SETNX_ITEM_PARAM_KEY"+":"+itemId,itemId,30)){
            //如果没有从数据库查询
            TbItemParamItemExample tbItemParamItemExample = new TbItemParamItemExample();
            TbItemParamItemExample.Criteria criteria = tbItemParamItemExample.createCriteria();
            criteria.andItemIdEqualTo(itemId);
            List<TbItemParamItem> tbItemParamItems = tbItemParamItemMapper.selectByExampleWithBLOBs(tbItemParamItemExample);
            //穿透问题
            if(tbItemParamItems!=null && tbItemParamItems.size()>0){
                redisClient.set(ITEM_INFO + ":" + itemId + ":" + PARAM,tbItemParamItems.get(0));
                redisClient.expire(ITEM_INFO + ":" + itemId + ":" + PARAM,ITEM_INFO_EXPIRE);

            }else{
                //解决穿透问题  没有值也存一个null
                redisClient.set(ITEM_INFO + ":" + itemId + ":" + PARAM,null);
                redisClient.expire(ITEM_INFO + ":" + itemId + ":" + PARAM,30);
            }
            redisClient.del("SETNX_ITEM_PARAM_KEY"+":"+itemId);
            return tbItemParamItems.get(0);
        }else{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return selectTbItemParamItemByItemId(itemId);
        }

    }


}
