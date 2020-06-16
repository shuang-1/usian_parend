package com.usian.com.usian.controller;

import com.usian.feign.CartServiceFeign;
import com.usian.pojo.TbItem;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/frontend/order")
public class OrderController {

    @Autowired
    private CartServiceFeign cartServiceFeign;

    @RequestMapping("/goSettlement")
    public Result goSettlement(String[] ids,String userId){

        List list = new ArrayList<>();

        Map<String, TbItem> cartFromRedis = cartServiceFeign.getCartFromRedis(userId);

        for (String id : ids) {
            TbItem tbItem = cartFromRedis.get(id);
            list.add(tbItem);
        }
        if(list.size()>0){
            return Result.ok(list);
        }
        return Result.error("查无数据");
    }
}
