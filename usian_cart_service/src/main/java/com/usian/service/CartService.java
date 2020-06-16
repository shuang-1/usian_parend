package com.usian.service;

import com.usian.pojo.TbItem;

import java.util.Map;

public interface CartService {
    Map<String, TbItem> getCartFromRedis(String userId);

    Boolean addCartToRedis(Map<String, TbItem> map, String userId);

}
