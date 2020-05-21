package com.usian.service;

import com.usian.mapper.TbContentCategoryMapper;
import com.usian.pojo.TbContentCategory;
import com.usian.pojo.TbContentCategoryExample;
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

        return tbContentCategoryMapper.insertSelective(tbContentCategory);
    }

    @Override
    public Integer deleteContentCategoryById(Long category) {
        return tbContentCategoryMapper.deleteByPrimaryKey(category);
    }

    @Override
    public Integer updateContentCategory(Integer id, String name) {

        TbContentCategory tbContentCategory = new TbContentCategory();
        tbContentCategory.setName(name);
        tbContentCategory.setStatus(1);
        tbContentCategory.setSortOrder(1);
        tbContentCategory.setIsParent(false);
        tbContentCategory.setCreated(new Date());
        tbContentCategory.setUpdated(new Date());

        TbContentCategoryExample tbContentCategoryExample = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = tbContentCategoryExample.createCriteria();
        criteria.andIdEqualTo(Long.valueOf(id));

        Integer num = tbContentCategoryMapper.updateByExampleSelective(tbContentCategory, tbContentCategoryExample);

        return num;
    }
}
