package com.usian.service;

import com.usian.mapper.TbOrderItemMapper;
import com.usian.mapper.TbOrderMapper;
import com.usian.mapper.TbOrderShippingMapper;
import com.usian.pojo.OrderInfo;
import com.usian.pojo.TbOrder;
import com.usian.pojo.TbOrderItem;
import com.usian.pojo.TbOrderShipping;
import com.usian.redis.RedisClient;
import com.usian.utils.IDUtils;
import com.usian.utils.JsonUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Value("${ORDER_ID_KEY}")
    private String ORDER_ID_KEY;

    @Value("${ORDER_ID_BEGIN}")
    private Integer ORDER_ID_BEGIN;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private TbOrderMapper tbOrderMapper;

    @Autowired
    private TbOrderItemMapper tbOrderItemMapper;

    @Autowired
    private TbOrderShippingMapper tbOrderShippingMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    public String insertOrder(OrderInfo orderInfo) {

        //补充订单的信息
        TbOrder tbOrder = orderInfo.getTbOrder();
        if(!redisClient.exists(ORDER_ID_KEY)){
            redisClient.set(ORDER_ID_KEY,ORDER_ID_BEGIN);
        }
        Long orderId = redisClient.incr(ORDER_ID_KEY, 1L);
        tbOrder.setOrderId(orderId.toString());
        tbOrder.setPostFee("0");
        tbOrder.setStatus(1);
        tbOrder.setCreateTime(new Date());
        tbOrder.setUpdateTime(new Date());
        tbOrderMapper.insertSelective(tbOrder);

        //保存商品的详细信息

        String orderItem = orderInfo.getOrderItem();
        List<TbOrderItem> tbOrderItems = JsonUtils.jsonToList(orderItem, TbOrderItem.class);
        for (TbOrderItem tbOrderItem : tbOrderItems) {
            tbOrderItem.setId(IDUtils.genItemId()+"");
            tbOrderItem.setOrderId(orderId.toString());
            tbOrderItemMapper.insertSelective(tbOrderItem);
        }

        //物流信息的保存
        TbOrderShipping tbOrderShipping = orderInfo.getTbOrderShipping();
        tbOrderShipping.setOrderId(orderId.toString());
        tbOrderShipping.setCreated(new Date());
        tbOrderShipping.setUpdated(new Date());
        tbOrderShippingMapper.insertSelective(tbOrderShipping);

        //发送mq消息同步库存
        amqpTemplate.convertAndSend("order_exchange","order.add",orderId);

        return orderId.toString();
    }
}
