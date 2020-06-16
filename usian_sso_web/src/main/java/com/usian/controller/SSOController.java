package com.usian.controller;

import com.usian.Feign.SSOServiceFeign;
import com.usian.feign.CartServiceFeign;
import com.usian.feign.ItemServiceFeignClient;
import com.usian.pojo.TbItem;
import com.usian.pojo.TbUser;
import com.usian.utils.CookieUtils;
import com.usian.utils.JsonUtils;
import com.usian.utils.Result;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/frontend/sso")
public class SSOController {

    @Autowired
    private SSOServiceFeign ssoServiceFeign;

    @Autowired
    private CartServiceFeign cartServiceFeign;

    @Autowired
    private ItemServiceFeignClient itemServiceFeignClient;


    //校验
    @RequestMapping("/checkUserInfo/{checkValue}/{checkFlag}")
    public Result checkUserInfo(@PathVariable String checkValue,@PathVariable Integer checkFlag){
        Boolean aBoolean = ssoServiceFeign.checkUserInfo(checkValue,checkFlag);
        if(aBoolean==true){
            return Result.ok();
        }
        return Result.error("500");
    }

    //注册
    @RequestMapping("/userRegister")
    public Result userRegister(TbUser tbUser){
        Integer aBoolean = ssoServiceFeign.userRegister(tbUser);
        if(aBoolean==1){
            return Result.ok();
        }
        return Result.error("注册失败");
    }

    //登录
    @RequestMapping("/userLogin")
    public Result userLogin(String username , String password, HttpServletRequest request, HttpServletResponse response){
        Map map = ssoServiceFeign.userLogin(username,password);

        Integer userid = (Integer) map.get("userid");

        if(map!=null && map.size()>0){

            String redisKey = CookieUtils.getCookieValue(request, "CART_COOKIE_KEY", true);
            if(StringUtils.isNotBlank(redisKey)){
                //获取cookie的购物车
                Map<String, TbItem> map1 = JsonUtils.jsonToMap(redisKey, TbItem.class);
                //获取redis的购物车
                Map<String, TbItem> cartFromRedis = cartServiceFeign.getCartFromRedis(userid.toString());

                Set<String> strings = map1.keySet();

                for (String string : strings) {
                    addItemToCart(map1.get(string).getId(),cartFromRedis,map1.get(string).getNum());
                }

                cartServiceFeign.addCartToRedis(cartFromRedis,userid.toString());

                CookieUtils.deleteCookie(request,response,"CART_COOKIE_KEY");
            }
            return Result.ok(map);
        }
        return Result.error("登录失败");
    }

    private void addItemToCart(Long id, Map<String, TbItem> cartFromRedis, Integer num) {
        TbItem tbItem = cartFromRedis.get(id.toString());

        if(tbItem!=null){
            tbItem.setNum(tbItem.getNum()+num);
        }else{
            tbItem = itemServiceFeignClient.selectItemInfo(id);
            tbItem.setNum(num);
        }
        cartFromRedis.put(id.toString(),tbItem);
    }

    //token查询用户信息展示
    @RequestMapping("/getUserByToken/{token}")
    public Result getUserByToken(@PathVariable String token){
        TbUser tbUser = ssoServiceFeign.getUserByToken(token);
        if(tbUser!=null){
            return Result.ok();
        }
        return Result.error("查无结果");
    }


    //退出
    @RequestMapping("/logOut")
    public Result logOut(String token){
        Integer num = ssoServiceFeign.logOut(token);
        if(num==1){
            return Result.ok();
        }
        return Result.error("退出失败");
    }
}
