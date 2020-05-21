package com.usian.controller;

import com.usian.pojo.TbContentCategory;
import com.usian.service.ContentCategoryService;
import com.usian.service.ContentCategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/service/content")
public class ContentCategoryController {

    @Autowired
    private ContentCategoryService contentCategoryService;

    @RequestMapping("/selectContentCategoryByParentId")
    public List<TbContentCategory> selectContentCategoryByParentId(Long id){
        return contentCategoryService.selectContentCategoryByParentId(id);
    }

    @RequestMapping("/insertContentCategory")
    public Integer insertContentCategory(Integer parentId, String name){
        return contentCategoryService.insertContentCategory(parentId,name);
    }

    @RequestMapping("/deleteContentCategoryById")
    public Integer deleteContentCategoryById(Long category){
        return contentCategoryService.deleteContentCategoryById(category);
    }

    @RequestMapping("/service/content/updateContentCategory")
    public Integer updateContentCategory(Integer id,String name){
        return contentCategoryService.updateContentCategory(id,name);
    }
}
