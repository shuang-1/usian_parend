package com.usian.controller;

import com.usian.feign.ItemServiceFeignClient;
import com.usian.pojo.TbItem;
import com.usian.pojo.TbItemDesc;
import com.usian.pojo.TbItemParamItem;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/frontend/detail")
@RestController
public class DetailController {


    @Autowired
    private ItemServiceFeignClient itemServiceFeignClient;

    @RequestMapping("/selectItemInfo")
    public Result selectItemInfo(Long itemId){
        TbItem tbItem = itemServiceFeignClient.selectItemInfo(itemId);
        if(tbItem!=null){
            return Result.ok(tbItem);
        }
        return Result.error("查无数据");
    }

    @RequestMapping("/selectItemDescByItemId")
    public Result selectItemDescByItemId(Long itemId){
        TbItemDesc tbItemDesc = itemServiceFeignClient.selectItemDescByItemId(itemId);
        if(tbItemDesc!=null){
            return Result.ok(tbItemDesc);
        }
        return Result.error("查无数据");
    }

    @RequestMapping("/selectTbItemParamItemByItemId")
    public Result selectTbItemParamItemByItemId(Long itemId){
        TbItemParamItem tbItemParamItem = itemServiceFeignClient.selectTbItemParamItemByItemId(itemId);
        if(tbItemParamItem!=null){
            return Result.ok(tbItemParamItem);
        }
        return Result.error("查无数据");
    }
}
