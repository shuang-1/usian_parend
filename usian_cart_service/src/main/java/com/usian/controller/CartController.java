package com.usian.controller;

import com.usian.pojo.TbItem;
import com.usian.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/service/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @RequestMapping("/getCartFromRedis")
    public Map<String, TbItem> getCartFromRedis(String userId){
        return cartService.getCartFromRedis(userId);
    }

    @RequestMapping("/addCartToRedis")
    public Boolean addCartToRedis(@RequestBody Map<String,TbItem> map, String userId){
        return cartService.addCartToRedis(map,userId);
    }

}
