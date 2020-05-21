package com.usian.controller;

import com.usian.feign.ContentServiceFeign;
import com.usian.pojo.TbContentCategory;
import com.usian.utils.Result;
import javafx.scene.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/backend/content")
public class ContentCategoryController {
    @Autowired
    private ContentServiceFeign contentServiceFeign;

    @RequestMapping("/selectContentCategoryByParentId")
    public Result selectContentCategoryByParentId(@RequestParam(defaultValue = "0") Long id){
        List<TbContentCategory> tbContentCategories = contentServiceFeign.selectContentCategoryByParentId(id);
        if(tbContentCategories.size()>0 && tbContentCategories!=null){
            return Result.ok(tbContentCategories);
        }
        return Result.error("查无结果");
    }

    @RequestMapping("/insertContentCategory")
    public Result insertContentCategory(@RequestParam Integer parentId,@RequestParam String name){
        Integer num = contentServiceFeign.insertContentCategory(parentId,name);
        if(num==1){
            return Result.ok();
        }
        return Result.error("添加失败");
    }

    @RequestMapping("/deleteContentCategoryById")
    public Result deleteContentCategoryById(@RequestParam Long categoryId){
        Integer num = contentServiceFeign.deleteContentCategoryById(categoryId);
        if(num==1){
            return Result.ok();
        }
        return Result.error("删除失败");
    }

    @RequestMapping("/updateContentCategory")
    public Result updateContentCategory(Integer id, String name){
        Integer num = contentServiceFeign.updateContentCategory(id,name);
        if(num==1){
            return Result.ok();
        }
        return Result.error("修改错误");
    }
}
