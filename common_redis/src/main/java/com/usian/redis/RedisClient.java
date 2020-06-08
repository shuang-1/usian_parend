package com.usian.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisClient {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**\
     * 指定缓存时间
     */
    public boolean expire(String key, long time){
        return redisTemplate.expire(key,time, TimeUnit.SECONDS);
    }

    /**\
     * 获取过期时间
     */
    public long ttl(String key){
        return redisTemplate.getExpire(key,TimeUnit.SECONDS);
    }

    /**
     *判断key值是否存在
     */
    public boolean exists(String key){
        return redisTemplate.hasKey(key);
    }

    /**
     * string的取数据
     */
    public Object get(String key){
        return key==null?null:redisTemplate.opsForValue().get(key);
    }

    //String存数据
    public void set(String key,Object value){
        redisTemplate.opsForValue().set(key,value);
    }

    //删除数据
    public boolean del(String key){
        return redisTemplate.delete(key);
    }

    //自增
    public long incr(String key,long value){
       return redisTemplate.opsForValue().increment(key,value);
    }

    //自减
    public long decr(String key, long value){
        return redisTemplate.opsForValue().decrement(key,value);
    }

    //hash的取数据
    public Object hget(String key, String item){
        return redisTemplate.opsForHash().get(key,item);
    }

    //hash的存数据
    public void hset(String key,String item,Object value) {
        redisTemplate.opsForHash().put(key, item, value);
    }

    //删除hash中的数据
    public long hdel(String key, String item){
       return redisTemplate.opsForHash().delete(key,item);
    }

    //获取set中的数据
    public Set<Object> smembers(String key){
        return redisTemplate.opsForSet().members(key);
    }

    //把数据存在set中
    public long smembers(String key, Object...values) {
         return redisTemplate.opsForSet().add(key, values);
    }

    //删除set中的数据
    public long setRemove(String key, Object ...values) {
          return redisTemplate.opsForSet().remove(key, values);
    }

    /**
     * 获取list缓存的内容
     * @param key 键
     * @param start 开始
     * @param end 结束  0 到 -1代表所有值
     * @return
     */
    public List<Object> lrange(String key, long start, long end){
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @return
     */
    public void lpush(String key, Object value) {
       redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @return
     */
    public void lpush(String key, List<Object> value) {
        redisTemplate.opsForList().rightPushAll(key, value);
    }

    /**
     * 移除N个值为value
     * @param key 键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lrem(String key,long count,Object value) {
       return redisTemplate.opsForList().remove(key, count, value);
    }

    /**
     * 分布式锁
     * @param key
     * @param count
     * @param value
     * @return
     */
    public Boolean setnx(String key, Object value, long count) {
        try {
            return redisTemplate.opsForValue().setIfAbsent(key, value, count, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
