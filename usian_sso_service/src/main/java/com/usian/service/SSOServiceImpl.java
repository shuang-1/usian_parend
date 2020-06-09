package com.usian.service;

import com.usian.mapper.TbUserMapper;
import com.usian.pojo.TbUser;
import com.usian.pojo.TbUserExample;
import com.usian.redis.RedisClient;
import com.usian.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class SSOServiceImpl implements SSOService {

    @Autowired
    private TbUserMapper tbUserMapper;

    @Autowired
    private RedisClient redisClient;

    @Override
    public boolean checkUserInfo(String checkValue, Integer checkFlag) {

        TbUserExample tbUserExample = new TbUserExample();
        TbUserExample.Criteria criteria = tbUserExample.createCriteria();
        //判断是姓名还是手机号
        if(checkFlag==1){
            criteria.andUsernameEqualTo(checkValue);
        }else if(checkFlag==2){
            criteria.andPhoneEqualTo(checkValue);
        }
        //去数据库查询
        List<TbUser> tbUsers = tbUserMapper.selectByExample(tbUserExample);
        if(tbUsers==null || tbUsers.size()==0){
            return true;
        }
        return false;
    }


    //注册
    @Override
    public Integer userRegister(TbUser tbUser) {
        String digest = MD5Utils.digest(tbUser.getPassword());
        tbUser.setPassword(digest);
        tbUser.setCreated(new Date());
        tbUser.setUpdated(new Date());

        return tbUserMapper.insertSelective(tbUser);
    }

    //登录
    @Override
    public Map userLogin(String username, String password) {

        //从数据库查询'
        String digest = MD5Utils.digest(password);
        TbUserExample tbUserExample = new TbUserExample();
        TbUserExample.Criteria criteria = tbUserExample.createCriteria();
        criteria.andUsernameEqualTo(username);
        criteria.andPasswordEqualTo(digest);
        List<TbUser> tbUsers = tbUserMapper.selectByExample(tbUserExample);
        if(tbUsers.size()==0 || tbUsers==null){
            return null;
        }
        String token = UUID.randomUUID().toString();
        redisClient.set(token,tbUsers.get(0));
        redisClient.expire(token,8600);
        Map map = new HashMap<>();
        map.put("token",token);
        map.put("username",tbUsers.get(0).getUsername());
        map.put("userid",tbUsers.get(0).getId());
        return map;
    }

    @Override
    public TbUser getUserByToken(String token) {

        TbUser tbUser = (TbUser) redisClient.get(token);
        redisClient.expire(token,8600);
        if(tbUser==null){
            return null;
        }
        return tbUser;
    }

    @Override
    public Integer logOut(String token) {
        boolean del = redisClient.del(token);
        if(del==true){
            return 1;
        }
        return 0;
    }
}
