package com.usian.controller;

import com.usian.feign.ItemServiceFeignClient;
import com.usian.utils.CatResult;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/frontend/itemCategory")
public class ItemCategoryController {

    @Autowired
    private ItemServiceFeignClient itemServiceFeignClient;

    @RequestMapping("/selectItemCategoryAll")
    public Result selectItemCategoryAll(){
        CatResult catResultList = itemServiceFeignClient.selectItemCategoryAll();
       if(catResultList.getData().size()>0){
           return Result.ok(catResultList);
       }
       return Result.error("查无结果");
    }
}
