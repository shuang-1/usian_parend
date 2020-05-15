package com.usian.comtroller;

import com.usian.feign.ItemServiceFeignClient;
import com.usian.pojo.TbItem;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backend/item")
public class insertTbItem {

    @Autowired
    private ItemServiceFeignClient itemServiceFeignClient;

    @RequestMapping("/insertTbItem")
    public Result insertTbItem(TbItem tbItem,String desc,String itemParams){
        Integer insertTbitemNum = itemServiceFeignClient.insertTbItem(tbItem,desc,itemParams);
        if(insertTbitemNum==3){
            return Result.ok();
        }
        return Result.error("添加失败");
    }
}
