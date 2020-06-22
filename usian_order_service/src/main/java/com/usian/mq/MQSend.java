package com.usian.mq;

import com.usian.mapper.LocalMessageMapper;
import com.usian.pojo.LocalMessage;
import com.usian.utils.JsonUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MQSend implements ReturnCallback, ConfirmCallback {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private LocalMessageMapper localMessageMapper;

    public void sengMsg(LocalMessage localMessage){
        //获取消息的订单id
        String orderNo = localMessage.getOrderNo();
        RabbitTemplate rabbitTemplate = (RabbitTemplate) this.amqpTemplate;
        //确认开启回退
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(this);//确认回调
        rabbitTemplate.setReturnCallback(this);//失败回退
        //用于确认之后更改本地消息状态或删除本地消息--本地消息id
        CorrelationData correlationData = new CorrelationData(localMessage.getTxNo());
        //发送消息
        rabbitTemplate.convertAndSend("order_exchage","order.add", JsonUtils.objectToJson(localMessage),correlationData);


    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        //获取消息id
        String id = correlationData.getId();

        if(ack){
            LocalMessage localMessage = localMessageMapper.selectByPrimaryKey(id);
            localMessage.setState(1);
            localMessageMapper.updateByPrimaryKeySelective(localMessage);
        }

    }

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        System.out.println("return--message:" + new String(message.getBody())
                + ",exchange:" + exchange + ",routingKey:" + routingKey);
    }
}
