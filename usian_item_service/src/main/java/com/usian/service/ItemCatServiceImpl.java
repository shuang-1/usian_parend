package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.TbItemCatMapper;
import com.usian.mapper.TbItemMapper;
import com.usian.pojo.TbItem;
import com.usian.pojo.TbItemCat;
import com.usian.pojo.TbItemCatExample;
import com.usian.pojo.TbItemExample;
import com.usian.redis.RedisClient;
import com.usian.utils.CatNode;
import com.usian.utils.CatResult;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    @Value("${portal_catresult_redis_key}")
    private String portal_catresult_redis_key;

    @Autowired
    private RedisClient redisClient;
    /**
     * 根据id查询商品的类目
     * @param id
     * @return
     */
    @Override
    public List<TbItemCat> selectItemCategoryByParentId(Long id) {

        TbItemCatExample tbItemCatExample = new TbItemCatExample();
        TbItemCatExample.Criteria criteria = tbItemCatExample.createCriteria();
        criteria.andStatusEqualTo(1);
        criteria.andParentIdEqualTo(id);

        return tbItemCatMapper.selectByExample(tbItemCatExample);
    }

    @Override
    public CatResult selectItemCategoryAll() {
        //查询redis缓存
        CatResult catResultRedis = (CatResult) redisClient.get(portal_catresult_redis_key);
        if(catResultRedis!=null){
            return catResultRedis;
        }
        //查询数据库
        CatResult catResult = new CatResult();
       // Long parentId =(long) 0;
        List<?> catList = getCatList(0L);

        catResult.setData(catList);
        //存入redis缓存
        redisClient.set(portal_catresult_redis_key,catResult);

        return catResult;
    }

    private List<?> getCatList(long parentId) {

        //查询父节点
        TbItemCatExample tbItemCatExample = new TbItemCatExample();
        TbItemCatExample.Criteria criteria = tbItemCatExample.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        List<TbItemCat> itemCatList = tbItemCatMapper.selectByExample(tbItemCatExample);

        //循环判断
        List list = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < itemCatList.size(); i++) {
            TbItemCat tbItemCat =  itemCatList.get(i);
            if(tbItemCat.getIsParent()){
                CatNode catNode = new CatNode();
                catNode.setName(tbItemCat.getName());
                catNode.setItem(getCatList(tbItemCat.getId()));
                list.add(catNode);
                count++;
                if(count==18){
                    break;
                }
            }else{
                list.add(tbItemCat.getName());
            }
        }
        return list;
    }
}
