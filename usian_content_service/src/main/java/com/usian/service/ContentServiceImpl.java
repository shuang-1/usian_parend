package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.netflix.discovery.converters.Auto;
import com.usian.mapper.TbContentMapper;
import com.usian.pojo.TbContent;
import com.usian.pojo.TbContentExample;
import com.usian.redis.RedisClient;
import com.usian.utils.AdNode;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ContentServiceImpl implements ContentService {

    @Value("${AD_CATEGORY_ID}")
    private Long AD_CATEGORY_ID;

    @Value("${AD_HEIGHT}")
    private Integer AD_HEIGHT;

    @Value("${AD_WIDTH}")
    private Integer AD_WIDTH;

    @Value("${AD_HEIGHTB}")
    private Integer AD_HEIGHTB;

    @Value("${AD_WIDTHB}")
    private Integer AD_WIDTHB;

    @Value("${portal_ad_redis_key}")
    private String portal_ad_redis_key;

    @Autowired
    private TbContentMapper tbContentMapper;

    @Autowired
    private RedisClient redisClient;

    @Override
    public PageResult selectTbContentAllByCategoryId(Long categoryId, Integer page, Integer rows) {

        PageHelper.startPage(page,rows);

        TbContentExample tbContentExample = new TbContentExample();
        TbContentExample.Criteria criteria = tbContentExample.createCriteria();
        criteria.andCategoryIdEqualTo(categoryId);

        List<TbContent> tbContents = tbContentMapper.selectByExampleWithBLOBs(tbContentExample);
        PageInfo<TbContent> pageInfo = new PageInfo<>(tbContents);
        PageResult pageResult = new PageResult();
        pageResult.setPageIndex(pageInfo.getPageNum());
        pageResult.setTotalPage(pageInfo.getPages());
        pageResult.setResult(pageInfo.getList());

        return pageResult;
    }

    @Override
    public Integer insertTbContent(TbContent tbContent) {

        tbContent.setCreated(new Date());
        tbContent.setUpdated(new Date());
        Integer num = tbContentMapper.insertSelective(tbContent);
        redisClient.hdel(portal_ad_redis_key,AD_CATEGORY_ID.toString());
        return num;
    }

    @Override
    public Integer deleteContentByIds(Long ids) {
        Integer num = tbContentMapper.deleteByPrimaryKey(ids);
        redisClient.hdel(portal_ad_redis_key,AD_CATEGORY_ID.toString());
        return num;
    }

    @Override
    public List<AdNode> selectFrontendContentByAD() {

        //去reids缓存查询
        List<AdNode> adNodeListRedis = (List<AdNode>) redisClient.hget("portal_ad_redis_key", AD_CATEGORY_ID.toString());
        if(adNodeListRedis!=null){
            return adNodeListRedis;
        }
        //如果没有取数据库查询
        TbContentExample tbContentExample = new TbContentExample();
        TbContentExample.Criteria criteria = tbContentExample.createCriteria();
        criteria.andCategoryIdEqualTo(AD_CATEGORY_ID);

        List<TbContent> tbContents = tbContentMapper.selectByExample(tbContentExample);

        List<AdNode> adNodeList = new ArrayList<>();
        for (TbContent tbContent : tbContents){
            AdNode adNode = new AdNode();
            adNode.setSrc(tbContent.getPic());
            adNode.setSrcB(tbContent.getPic2());
            adNode.setHref(tbContent.getUrl());
            adNode.setHeight(AD_HEIGHT);
            adNode.setWidth(AD_WIDTH);
            adNode.setHeightB(AD_HEIGHTB);
            adNode.setWidthB(AD_WIDTHB);
            adNodeList.add(adNode);
        }
        //存入reids缓存
        redisClient.hset(portal_ad_redis_key,AD_CATEGORY_ID.toString(),adNodeList);

        return adNodeList;
    }
}
