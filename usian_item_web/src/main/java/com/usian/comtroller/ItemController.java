package com.usian.comtroller;

import com.usian.feign.ItemServiceFeignClient;
import com.usian.pojo.TbItem;
import com.usian.utils.PageResult;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backend/item")
public class ItemController {

    @Autowired
    private ItemServiceFeignClient itemServiceFeignClient;

    @RequestMapping("/selectItemInfo")
    public Result selectItemInfo(Long itemId){
        TbItem tbItem = itemServiceFeignClient.selectItemInfo(itemId);
        if(tbItem != null){
            return Result.ok(tbItem);
        }
        return Result.error("查无结果");
    }

    //分页查询商品信息
    @RequestMapping("selectTbItemAllByPage")
    public Result selectTbItemAllByPage(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "2") Integer rows){

        PageResult pageResult = itemServiceFeignClient.selectTbItemAllByPage(page,rows);

        if(pageResult.getResult() != null && pageResult.getResult().size() > 0){
            return Result.ok(pageResult);
        }

        return Result.error("查无结果");

    }
}
