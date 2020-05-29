package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.TbContentCategoryMapper;
import com.usian.mapper.TbContentMapper;
import com.usian.pojo.TbContent;
import com.usian.pojo.TbContentCategory;
import com.usian.pojo.TbContentCategoryExample;
import com.usian.pojo.TbContentExample;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ContentCategoryServiceImpl implements ContentCategoryService {

    @Autowired
    private TbContentCategoryMapper tbContentCategoryMapper;


    @Override
    public List<TbContentCategory> selectContentCategoryByParentId(Long id) {

        TbContentCategoryExample tbContentCategoryExample = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = tbContentCategoryExample.createCriteria();
        criteria.andParentIdEqualTo(id);

        List<TbContentCategory> tbContentCategories = tbContentCategoryMapper.selectByExample(tbContentCategoryExample);
        return tbContentCategories;
    }

    @Override
    public Integer insertContentCategory(Integer parentId, String name) {

        TbContentCategory tbContentCategory = new TbContentCategory();
        tbContentCategory.setParentId(Long.valueOf(parentId));
        tbContentCategory.setName(name);
        tbContentCategory.setStatus(1);
        tbContentCategory.setSortOrder(1);
        tbContentCategory.setIsParent(false);
        tbContentCategory.setCreated(new Date());
        tbContentCategory.setUpdated(new Date());

        Integer num = tbContentCategoryMapper.insertSelective(tbContentCategory);

        TbContentCategory tbContentCategory1 = tbContentCategoryMapper.selectByPrimaryKey(Long.valueOf(parentId));
        if(!tbContentCategory1.getIsParent()){
            tbContentCategory1.setIsParent(true);
            tbContentCategory1.setUpdated(new Date());
            Integer num2 = tbContentCategoryMapper.updateByPrimaryKeySelective(tbContentCategory1);
        }

        return num;
    }

    @Override
    public Integer deleteContentCategoryById(Long categoryId) {

        TbContentCategory tbContentCategory = tbContentCategoryMapper.selectByPrimaryKey(categoryId);
        if(tbContentCategory.getIsParent()==true){
            return 0;
        }

        Integer num = tbContentCategoryMapper.deleteByPrimaryKey(categoryId);

        TbContentCategoryExample tbContentCategoryExample = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = tbContentCategoryExample.createCriteria();
        criteria.andParentIdEqualTo(tbContentCategory.getParentId());
        List<TbContentCategory> tbContentCategories = tbContentCategoryMapper.selectByExample(tbContentCategoryExample);
        if(tbContentCategories.size()>0){
            return num;
        }else{
            TbContentCategory tbContentCategory1 = new TbContentCategory();
            tbContentCategory1.setId(tbContentCategory.getParentId());
            tbContentCategory1.setIsParent(false);
            tbContentCategory1.setUpdated(new Date());
            Integer n = tbContentCategoryMapper.updateByPrimaryKeySelective(tbContentCategory1);
            return n;
        }

    }

    @Override
    public Integer updateContentCategory(Long id, String name) {

        TbContentCategory tbContentCategory = new TbContentCategory();
        tbContentCategory.setId(id);
        tbContentCategory.setName(name);
        tbContentCategory.setUpdated(new Date());

        Integer num = tbContentCategoryMapper.updateByPrimaryKeySelective(tbContentCategory);

        return num;
    }



}
