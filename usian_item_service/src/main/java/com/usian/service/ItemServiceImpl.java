package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.TbItemCatMapper;
import com.usian.mapper.TbItemDescMapper;
import com.usian.mapper.TbItemMapper;
import com.usian.mapper.TbItemParamItemMapper;
import com.usian.pojo.*;
import com.usian.redis.RedisClient;
import com.usian.utils.IDUtils;
import com.usian.utils.PageResult;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ItemServiceImpl implements ItemService {

    @Autowired
    private TbItemMapper tbItemMapper;
    
    @Autowired
    private TbItemDescMapper tbItemDescMapper;
    
    @Autowired
    private TbItemParamItemMapper tbItemParamItemMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    @Autowired
    private RedisClient redisClient;

    @Value("${ITEM_INFO}")
    private String ITEM_INFO;

    @Value("${BASE}")
    private String BASE;

    @Value("${DESC}")
    private String DESC;

    @Value("${PARAM}")
    private String PARAM;

    @Value("${ITEM_INFO_EXPIRE}")
    private Integer ITEM_INFO_EXPIRE;

    @Override
    public TbItem selectItemInfo(Long itemId) {
        //从redis查询数据
        TbItem tbItem = (TbItem) redisClient.get(ITEM_INFO +":"+ itemId +":"+ BASE);
        if(tbItem!=null){
            return tbItem;
        }
        //如果没有从数据库查询
        tbItem = tbItemMapper.selectByPrimaryKey(itemId);

        /**
         * 缓存的击穿
         */
        if(redisClient.setnx("SETNX_ITEM_LOCK_KEY"+":"+itemId,itemId,30)){
            //缓存的穿透
            if(tbItem!=null){
                //存入redis
                redisClient.set(ITEM_INFO +":"+ itemId +":"+ BASE,tbItem);
                redisClient.expire(ITEM_INFO +":"+ itemId +":"+ BASE,ITEM_INFO_EXPIRE);
            }else{
                //存入redis
                redisClient.set(ITEM_INFO +":"+ itemId +":"+ BASE,null);
                redisClient.expire(ITEM_INFO +":"+ itemId +":"+ BASE,30);
            }
            redisClient.del("SETNX_ITEM_LOCK_KEY"+":"+itemId);
            return tbItem;
        }else{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return selectItemInfo(itemId);
        }

    }

    @Override
    public PageResult selectTbItemAllByPage(Integer page, Integer rows) {
        PageHelper.startPage(page,rows);

        TbItemExample tbItemExample = new TbItemExample();
        tbItemExample.setOrderByClause("updated desc");

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
    public Integer insertTbItem(TbItem tbItem, String desc, String itemParams) {

        //补齐tbitem的数据
        long itemId = IDUtils.genItemId();
        Date date = new Date();
        tbItem.setStatus((byte)1);
        tbItem.setId(itemId);
        tbItem.setCreated(date);
        tbItem.setUpdated(date);
        Integer tbitemNum = tbItemMapper.insertSelective(tbItem);

        //补齐商品描述的数据
        TbItemDesc tbItemDesc = new TbItemDesc();
        tbItemDesc.setItemId(itemId);
        tbItemDesc.setCreated(date);
        tbItemDesc.setUpdated(date);
        tbItemDesc.setItemDesc(desc);
        Integer tbitemDescNum = tbItemDescMapper.insertSelective(tbItemDesc);

        //补全商品规格的数据
        TbItemParamItem tbItemParamItem = new TbItemParamItem();
        tbItemParamItem.setItemId(itemId);
        tbItemParamItem.setCreated(date);
        tbItemParamItem.setUpdated(date);
        tbItemParamItem.setParamData(itemParams);
        Integer itemParamItemNum = tbItemParamItemMapper.insertSelective(tbItemParamItem);

        //发送amqp消息
        amqpTemplate.convertAndSend("item_exchange","item.add",itemId);

        return tbitemNum+tbitemDescNum+itemParamItemNum;
    }

    @Override
    public Map<String,Object> preUpdateItem(Long id) {

        Map<String,Object> map = new HashMap<>();

        TbItem tbItems = tbItemMapper.selectByPrimaryKey(id);

        TbItemDesc tbItemDesc = tbItemDescMapper.selectByPrimaryKey(id);

        TbItemCat tbItemCat = tbItemCatMapper.selectByPrimaryKey(tbItems.getCid());

        TbItemParamItemExample tbItemParamItemExample = new TbItemParamItemExample();
        TbItemParamItemExample.Criteria criteria = tbItemParamItemExample.createCriteria();
        criteria.andItemIdEqualTo(id);
        List<TbItemParamItem> tbItemParamItems = tbItemParamItemMapper.selectByExampleWithBLOBs(tbItemParamItemExample);
        if(tbItemParamItems.size()>0 && tbItemParamItems!=null){
            map.put("itemParamItem",tbItemParamItems.get(0).getParamData());
        }

        map.put("item",tbItems);
        map.put("itemDesc",tbItemDesc.getItemDesc());
        map.put("itemCat",tbItemCat.getName());

        return map;


    }

    @Override
    public Integer updateTbItem(TbItem tbItem, String desc, String itemParams) {

        //补齐商品的数据
        tbItem.setStatus((byte)1);
        tbItem.setCreated(new Date());
        tbItem.setUpdated(new Date());
        Integer tbitemNum = tbItemMapper.updateByPrimaryKeySelective(tbItem);

        //补齐描述的信息
        TbItemDesc tbItemDesc = new TbItemDesc();
        tbItemDesc.setItemId(tbItem.getId());
        tbItemDesc.setCreated(new Date());
        tbItemDesc.setUpdated(new Date());
        tbItemDesc.setItemDesc(desc);
        Integer tbitemDescNum = tbItemDescMapper.updateByPrimaryKeySelective(tbItemDesc);

        //补全商品规格的数据
        TbItemParamItemExample tbItemParamItemExample = new TbItemParamItemExample();
        TbItemParamItemExample.Criteria criteria = tbItemParamItemExample.createCriteria();
        criteria.andItemIdEqualTo(tbItem.getId());
        TbItemParamItem tbItemParamItem = new TbItemParamItem();
        tbItemParamItem.setItemId(tbItem.getId());
        tbItemParamItem.setCreated(new Date());
        tbItemParamItem.setUpdated(new Date());
        tbItemParamItem.setParamData(itemParams);
        Integer itemParamItemNum = tbItemParamItemMapper.updateByExampleSelective(tbItemParamItem,tbItemParamItemExample);

        redisClient.del(ITEM_INFO+":"+tbItem.getId()+":"+BASE);
        redisClient.del(ITEM_INFO+":"+tbItem.getId()+":"+DESC);
        redisClient.del(ITEM_INFO+":"+tbItem.getId()+":"+PARAM);

        return tbitemNum+tbitemDescNum+itemParamItemNum;
    }

    @Override
    public TbItemDesc selectItemDescByItemId(Long itemId) {
        //从redis查询
        TbItemDesc tbItemDesc = (TbItemDesc) redisClient.get(ITEM_INFO + ":" + itemId + ":" + DESC);
        if(tbItemDesc!=null){
            return tbItemDesc;
        }
        //如果没有从数据库查询
        tbItemDesc = tbItemDescMapper.selectByPrimaryKey(itemId);
        /**
         * 缓存的击穿
         */
        if(redisClient.setnx("SETNX_ITEM_DESC_KEY"+":"+itemId,itemId,30)){
            //缓存穿透
            if(tbItemDesc!=null){
                //存入redis
                redisClient.set(ITEM_INFO + ":" + itemId + ":" + DESC,tbItemDesc);
                redisClient.expire(ITEM_INFO + ":" + itemId + ":" + DESC,ITEM_INFO_EXPIRE);
            }else{
                //存入redis
                redisClient.set(ITEM_INFO + ":" + itemId + ":" + DESC,null);
                redisClient.expire(ITEM_INFO + ":" + itemId + ":" + DESC,30);
            }
            redisClient.del("SETNX_ITEM_DESC_KEY"+":"+itemId);
            return tbItemDesc;
        }else{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return selectItemDescByItemId(itemId);
        }

    }

    @Override
    public Integer deleteItemById(Long itemId) {

        Integer num = tbItemMapper.deleteByPrimaryKey(itemId);

        redisClient.del(ITEM_INFO+":"+itemId+":"+BASE);
        redisClient.del(ITEM_INFO+":"+itemId+":"+DESC);
        redisClient.del(ITEM_INFO+":"+itemId+":"+PARAM);

        return num;
    }

}
