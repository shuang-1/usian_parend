package com.usian.controller;

import com.usian.Feign.SSOServiceFeign;
import com.usian.pojo.TbUser;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/frontend/sso")
public class SSOController {

    @Autowired
    private SSOServiceFeign ssoServiceFeign;
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
    public Result userLogin(String username ,String password){
        Map map = ssoServiceFeign.userLogin(username,password);
        if(map!=null && map.size()>0){
            return Result.ok(map);
        }
        return Result.error("登录失败");
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
