package com.usian.controller;

import com.usian.feign.ItemServiceFeignClient;
import com.usian.pojo.TbItemParam;
import com.usian.utils.PageResult;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backend/itemParam")
public class ItemParemController {

    @Autowired
    private ItemServiceFeignClient itemServiceFeignClient;

    @RequestMapping("/selectItemParamByItemCatId/{itemCatId}")
    public Result selectItemParamByItemCatId(@PathVariable Long itemCatId){
        TbItemParam tbItemParam = itemServiceFeignClient.selectItemParamByItemCatId(itemCatId);
        if(tbItemParam!=null){
            return Result.ok(tbItemParam);
        }
        return Result.error("查无结果");
    }
    /**
     * 商品规格查询
     */

    @RequestMapping("/selectItemParamAll")
    public Result selectItemParamAll(@RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "2") Integer rows){
        PageResult pageResult = itemServiceFeignClient.selectItemParamAll(pageNum,rows);
        if(pageResult.getResult().size()>0){
            return Result.ok(pageResult);
        }
        return Result.error("查无结果");
    }

    /**
     * 商品规格的添加
     */

    @RequestMapping("/insertItemParam")
    public Result insertItemParam(@RequestParam Long itemCatId, @RequestParam String paramData){

        Integer num2 = itemServiceFeignClient.selectItemParamByItemCatId2(itemCatId);
        if(num2==0){
            return Result.error("添加失败");
        }

        Integer num = itemServiceFeignClient.insertItemParam(itemCatId,paramData);
        if(num==1){
            return Result.ok();
        }
        return Result.error("添加失败");
    }

    /**
     * 商品规格的删除
     *
     */

    @RequestMapping("/deleteItemParamById")
    public Result deleteItemParamById(Integer id){
       Integer num =  itemServiceFeignClient.deleteItemParamById(id);
       if(num==1){
           return Result.ok();
       }
       return Result.error("删除失败");
    }
}
