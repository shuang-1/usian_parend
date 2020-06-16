package com.usian.controller;

import com.usian.feign.CartServiceFeign;
import com.usian.feign.ItemServiceFeignClient;
import com.usian.pojo.TbItem;
import com.usian.utils.CookieUtils;
import com.usian.utils.JsonUtils;
import com.usian.utils.Result;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@RequestMapping("/frontend/cart")
public class CartController {

    @Value("${CART_COOKIE_KEY}")
    public String CART_COOKIE_KEY;

    @Value("${CART_COOKIE_EXPIRE}")
    public Integer CART_COOKIE_EXPIRE;

    @Autowired
    private CartServiceFeign cartServiceFeign;

    @Autowired
    private ItemServiceFeignClient itemServiceFeignClient;

    //将商品添加到购物车
    @RequestMapping("/addItem")
    public Result addItem(Long itemId, String userId, @RequestParam(defaultValue = "1") Integer
            num, HttpServletRequest request, HttpServletResponse response){
        try {
            if(StringUtils.isBlank(userId)){
                //查询商品列表
                Map<String, TbItem> map = getCart(request);

                //添加到购物车
                addItemToCart(itemId,map,num);

                //把购物车列表存入cookie
                addClientCookie(request,response,map);
                return Result.ok();

            }else{
                //登录状态
                //从redis中查询购物车列表是否存在
                Map<String,TbItem> map = getCartFromRedis(userId);
                //获取cookie中的商品列表
                Map<String, TbItem> cart = getCart(request);
                //加入购物车
                addItemToCart(itemId,map,num);
                //把购物车列表加入redis缓存中
                Boolean aBoolean = addCartToRedis(map,userId);
                if(aBoolean==true){
                    return Result.ok();
                }
                return Result.error("error");
            }
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("error");
        }
    }

    //从redis中查询购物车列表是否存在    登录状态
    private Map<String, TbItem> getCartFromRedis(String userId) {
        Map<String, TbItem> map = cartServiceFeign.getCartFromRedis(userId);
        if(map!=null && map.size()>0){
            return map;
        }
        return new HashMap<>();
    }

    //把购物车列表加入redis缓存中  登录
    private Boolean addCartToRedis(Map<String, TbItem> map, String userId) {
        return cartServiceFeign.addCartToRedis(map,userId);
    }

    private void addClientCookie(HttpServletRequest request, HttpServletResponse response, Map<String, TbItem> map) {
        String s = JsonUtils.objectToJson(map);
        CookieUtils.setCookie(request,response,CART_COOKIE_KEY,s,CART_COOKIE_EXPIRE,true);
    }

    private void addItemToCart(Long itemId, Map<String, TbItem> map, Integer num) {
        TbItem tbItem = map.get(itemId.toString());

        if(tbItem!=null){
            tbItem.setNum(tbItem.getNum()+num);
        }else{
            tbItem = itemServiceFeignClient.selectItemInfo(itemId);
            tbItem.setNum(num);
        }
        map.put(itemId.toString(),tbItem);
    }


    //查询商品的购物车
    private Map<String, TbItem> getCart(HttpServletRequest request) {

        String cookieValue = CookieUtils.getCookieValue(request, CART_COOKIE_KEY, true);

        if(StringUtils.isNotBlank(cookieValue)){
            //存在
            Map<String,TbItem> map = JsonUtils.jsonToMap(cookieValue, TbItem.class);
            return map;
        }
        return new HashMap<>();
    }



    //查询购物车的所有商品
    @RequestMapping("/showCart")
    public Result showCart(String userId,Long itemId,HttpServletRequest request,HttpServletResponse response){
        try {

            List<TbItem> list = new ArrayList<>();
            //判断是否登录
            if(StringUtils.isBlank(userId)){
                //先获取购物车
                Map<String, TbItem> cart = getCart(request);

                Set<String> strings = cart.keySet();
                for (String string : strings) {
                    list.add(cart.get(string));
                }
            }else{
                //去redis查询商品列表
                Map<String, TbItem> cart = getCartFromRedis(userId);
                Set<String> strings = cart.keySet();
                for (String string : strings) {
                    list.add(cart.get(string));
                }
                return Result.ok(list);
            }
            return Result.ok(list);
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("500");
        }
    }

    //修改购物车
    @RequestMapping("/updateItemNum")
    public Result updateItemNum(HttpServletRequest request,HttpServletResponse response,Long itemId,Integer num,String userId){
        try {
            if(StringUtils.isBlank(userId)){
                //获取购物车列表
                Map<String, TbItem> cart = getCart(request);
                TbItem tbItem = cart.get(itemId.toString());
                tbItem.setNum(num);
                cart.put(tbItem.getId().toString(),tbItem);
                //添加到cookie
                addClientCookie(request,response,cart);
            }else{
                //获取redis中的商品列表
                Map<String, TbItem> cart = getCartFromRedis(userId);
                TbItem tbItem = cart.get(itemId.toString());
                tbItem.setNum(num);
                cart.put(itemId.toString(),tbItem);
                addCartToRedis(cart,userId);
            }
            return Result.ok();
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("修改失败");
        }
    }

    //删除购物车商品
    @RequestMapping("/deleteItemFromCart")
    public Result deleteItemFromCart(String userId,Long itemId,HttpServletRequest request,HttpServletResponse response){
        try {
            if(StringUtils.isBlank(userId)){
                //获取购物车列表
                Map<String, TbItem> cart = getCart(request);
                cart.remove(itemId.toString());
                //添加到cookie
                addClientCookie(request,response,cart);
            }else{
                Map<String, TbItem> cart = getCartFromRedis(userId);
                cart.remove(itemId.toString());
                addCartToRedis(cart,userId);
            }
            return Result.ok();
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("删除失败");
        }
    }
}
