package com.usian.controller;

import com.usian.pojo.TbItem;
import com.usian.service.ItemService;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service/item")
public class insertTbItem {

    @Autowired
    private ItemService itemService;

    @RequestMapping("/insertTbItem")
    public Result insertTbItem(@RequestBody TbItem tbItem, @RequestParam String desc, @RequestParam String itemParams){
        return itemService.insertTbItem(tbItem,desc,itemParams);
    }
}
