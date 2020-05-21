package com.usian.controller;

import com.usian.pojo.TbItemParam;
import com.usian.service.ItemParamService;
import com.usian.utils.PageResult;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service/itemParam")
public class ItemParemController {

    @Autowired
    private ItemParamService itemParamService;

    @RequestMapping("/selectItemParamByItemCatId/{itemCatId}")
    public TbItemParam selectItemParamByItemCatId(@PathVariable Long itemCatId){
        TbItemParam tbItemParam = itemParamService.selectItemParamByItemCatId(itemCatId);
        return tbItemParam;
    }

    @RequestMapping("/selectItemParamAll")
    public PageResult selectItemParamAll(Integer pageNum, Integer rows){
        PageResult pageResult = itemParamService.selectItemParamAll(pageNum,rows);
        return pageResult;
    }

    @RequestMapping("insertItemParam")
    public Integer insertItemParam(Long itemCatId, String paramData){
        return itemParamService.insertItemParam(itemCatId,paramData);
    }

    @RequestMapping("/selectItemParamByItemCatId2")
    public Integer selectItemParamByItemCatId2(Long itemCatId){
        return itemParamService.selectItemParamByItemCatId2(itemCatId);
    }

    @RequestMapping("/deleteItemParamById")
    public Integer deleteItemParamById(Integer id){
        return itemParamService.deleteItemParamById(id);
    }
}
