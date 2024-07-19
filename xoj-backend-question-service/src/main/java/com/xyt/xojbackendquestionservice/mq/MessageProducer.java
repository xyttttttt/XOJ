package com.xyt.xojbackendquestionservice.mq;

import com.rabbitmq.client.AMQP;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
public class MessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;


    public void sendMessage(String exchange,String routingKey,String message){
        rabbitTemplate.convertAndSend(exchange,routingKey,message,msg -> {
            //发送消息时候的延迟时长
            msg.getMessageProperties().setExpiration("10000");
            return msg;
        });
    }


}
