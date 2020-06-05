package com.usian.listener;

import com.usian.service.SearchService;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SearchMQListener {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private SearchService searchService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "search_queue",durable = "true"),
            exchange = @Exchange(value = "item_exchange",type = ExchangeTypes.TOPIC),
            key =  {"item.add"}
    ))

    public void listen(String msg) throws IOException {
        System.out.println(msg);
        int i = searchService.addDocement(msg);
        if(i>0){
            throw new RuntimeException("同步失败");
        }
    }
}
