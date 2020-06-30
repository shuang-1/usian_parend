package com.usian.controller;

import com.usian.feign.ItemServiceFeignClient;
import com.usian.pojo.TbItem;
import com.usian.utils.PageResult;
import com.usian.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/backend/item")
@Api("商品管理接口")
public class ItemController {

    @Autowired
    private ItemServiceFeignClient itemServiceFeignClient;

    @PostMapping("/selectItemInfo")
    @ApiOperation(value = "查询商品基本信息",notes = "根据itemId查询商品的基本信息")
    @ApiImplicitParam(name = "itemId",type = "Long",value = "商品的id")
    public Result selectItemInfo(Long itemId){
        TbItem tbItem = itemServiceFeignClient.selectItemInfo(itemId);
        if(tbItem != null){
            return Result.ok(tbItem);
        }
        return Result.error("查无结果");
    }

    //分页查询商品信息
    @GetMapping("selectTbItemAllByPage")
    @ApiOperation(value = "分页查询",notes = "分页查询数据，每页展示3条数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",type = "Integer",value = "页码", defaultValue = "1"),
            @ApiImplicitParam(name = "rows",type = "Integer",value = "每页多少条数据", defaultValue = "2")
    })
    public Result selectTbItemAllByPage(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "2") Integer rows){

        PageResult pageResult = itemServiceFeignClient.selectTbItemAllByPage(page,rows);

        if(pageResult.getResult() != null && pageResult.getResult().size() > 0){
            return Result.ok(pageResult);
        }

        return Result.error("查无结果");

    }

    //添加
    @PostMapping("/insertTbItem")
    @ApiOperation(value = "添加商品信息",notes = "添加商品及描述和规格参数信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "desc",type = "String",value = "商品的描述"),
            @ApiImplicitParam(name = "itemParams",type = "String",value = "商品的规格参数")
    })
    public Result insertTbItem(TbItem tbItem,String desc,String itemParams){
        Integer insertTbitemNum = itemServiceFeignClient.insertTbItem(tbItem,desc,itemParams);
        if(insertTbitemNum==3){
            return Result.ok();
        }
        return Result.error("添加失败");
    }

    //预更新数据
    @RequestMapping("/preUpdateItem")
    public Result preUpdateItem(Long itemId){
        Map<String,Object> map = itemServiceFeignClient.preUpdateItem(itemId);
        if(map!=null){
            return Result.ok(map);
        }
        return Result.error("没有查询到数据");
    }

    //修改
    @RequestMapping("/updateTbItem")
    public Result updateTbItem(TbItem tbItem,String desc,String itemParams){
        Integer num = itemServiceFeignClient.updateTbItem(tbItem,desc,itemParams);
        if(num==3){
            return Result.ok();
        }
        return Result.error("修改失败");
    }

    //删除
    @RequestMapping("deleteItemById")
    public Result deleteItemById(Long itemId){
        Integer num = itemServiceFeignClient.deleteItemById(itemId);

        if(num==1){
            return Result.ok();
        }
        return Result.error("删除失败");
    }
}
