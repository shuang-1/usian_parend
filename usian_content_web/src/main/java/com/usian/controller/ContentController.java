package com.usian.controller;

import com.usian.feign.ContentServiceFeign;
import com.usian.pojo.TbContent;
import com.usian.utils.PageResult;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backend/content")
public class ContentController {

    @Autowired
    private ContentServiceFeign contentServiceFeign;

    /**
     * 内容管理的查询展示
     */
    @RequestMapping("/selectTbContentAllByCategoryId")
    public Result selectTbContentAllByCategoryId(@RequestParam Long categoryId, @RequestParam(defaultValue = "1") Integer page,
                                                 @RequestParam(defaultValue = "30") Integer rows){
        PageResult pageResult = contentServiceFeign.selectTbContentAllByCategoryId(categoryId,page,rows);
        if(pageResult.getResult().size()>0 && pageResult.getResult()!=null){
            return Result.ok(pageResult);
        }
        return Result.error("查无结果");
    }

    /**
     * 内容管理的添加
     */
    @RequestMapping("/insertTbContent")
    public Result insertTbContent(TbContent tbContent){
        Integer num = contentServiceFeign.insertTbContent(tbContent);
        if(num==1){
            return Result.ok();
        }
        return Result.error("添加失败");
    }

    /*
    内容管理的删除
     */
    @RequestMapping("/deleteContentByIds")
    public Result deleteContentByIds(@RequestParam Long ids){
        Integer num = contentServiceFeign.deleteContentByIds(ids);
        if(num==1){
            return Result.ok();
        }
        return Result.error("删除失败");
    }
}
