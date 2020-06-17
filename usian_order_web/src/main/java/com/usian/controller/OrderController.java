package com.usian.controller;

import com.usian.feign.CartServiceFeign;
import com.usian.feign.OrderServiceFeign;
import com.usian.pojo.OrderInfo;
import com.usian.pojo.TbItem;
import com.usian.pojo.TbOrder;
import com.usian.pojo.TbOrderShipping;
import com.usian.utils.Result;
import org.apache.commons.lang.StringUtils;
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

    @Autowired
    private OrderServiceFeign orderServiceFeign;

    //确认订单数据展示
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

    //订单确认
    @RequestMapping("/insertOrder")
    public Result insertOrder(String orderItem, TbOrder tbOrder, TbOrderShipping tbOrderShipping){

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderItem(orderItem);
        orderInfo.setTbOrder(tbOrder);
        orderInfo.setTbOrderShipping(tbOrderShipping);

        String orderId = orderServiceFeign.insertOrder(orderInfo);
        if(StringUtils.isNotBlank(orderId)){
            return Result.ok(orderId);
        }
        return Result.error("错误");
    }
}
