package com.usian.quartz;

import com.usian.pojo.TbOrder;
import com.usian.redis.RedisClient;
import com.usian.service.OrderService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;


public class OrderQuartz implements Job {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisClient redisClient;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

            String ip = null;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        if(redisClient.setnx("SETNX_LOCK_KEY",ip,30L)){
            //查询过期订单
            List<TbOrder> tbOrders = orderService.selectOverTimeTbOrder();

            //关闭过期订单
            for (TbOrder tbOrder : tbOrders) {
                orderService.updateOverTimeTbOrder(tbOrder);
                //把过期订单的过期库存加回去
                orderService.updateTbItemByOrderId(tbOrder.getOrderId());
            }
        }else{
            System.out.println("============机器："+ip+" 占用分布式锁，任务正在执行=======================");
        }


    }
}
