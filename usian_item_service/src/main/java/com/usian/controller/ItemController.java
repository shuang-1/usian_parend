package com.usian.controller;

import com.usian.pojo.TbItem;
import com.usian.service.ItemService;
import com.usian.utils.PageResult;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/service/item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @RequestMapping("/selectItemInfo")
    public TbItem selectItemInfo(Long itemId){
        return itemService.selectItemInfo(itemId);
    }

    @RequestMapping("/selectTbItemAllByPage")
    public PageResult selectTbItemAllByPage(Integer page, Integer rows){
        PageResult pageResult = itemService.selectTbItemAllByPage(page,rows);
        return pageResult;
    }

    //添加
    @RequestMapping("/insertTbItem")
    public Integer insertTbItem(@RequestBody TbItem tbItem, @RequestParam String desc, @RequestParam String itemParams){
        return itemService.insertTbItem(tbItem,desc,itemParams);
    }

    //预修改数据
    @RequestMapping("/preUpdateItem")
    public Map<String,Object> preUpdateItem(@RequestParam Long itemId){
        Map<String,Object> map = itemService.preUpdateItem(itemId);
        return map;
    }

    //删除
    @RequestMapping("/deleteItemById")
    public Integer deleteItemById(@RequestParam Long itemId){
        Integer num = itemService.deleteItemById(itemId);
        return num;
    }


}
