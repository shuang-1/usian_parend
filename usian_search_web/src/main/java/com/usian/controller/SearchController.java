package com.usian.controller;


import com.usian.feign.SearchServiceFeign;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/frontend/searchItem")
public class SearchController {

    @Autowired
    private SearchServiceFeign searchServiceFeign;

    @RequestMapping("/importAll")
    public Result importAll(){
        boolean b = searchServiceFeign.importAll();
        if(b==true){
            return Result.ok();
        }
        return Result.error("添加索引库失败");
    }
}
