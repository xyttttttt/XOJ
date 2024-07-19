package com.xyt.xojbackendjudgeservice.mq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InitRabbitMQBean {

//    @Value("${spring.rabbitmq.host}")
//    private static String host;

    public static void init(){
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("192.168.204.129");
            factory.setUsername("admin");
            factory.setPassword("admin");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(MqConstant.NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
            channel.exchangeDeclare(MqConstant.DEAD_EXCHANGE,BuiltinExchangeType.DIRECT);

            channel.queueDeclare(MqConstant.DEAD_QUEUE,true,false,false,null);
            channel.queueBind(MqConstant.DEAD_QUEUE,MqConstant.DEAD_EXCHANGE,MqConstant.DEAD_ROUTING_KEY);

            //正常队列绑定死信队列信息
            Map<String, Object> params = new HashMap<>();
            //正常队列设置死信交换机 参数 key 是固定值
            params.put("x-dead-letter-exchange", MqConstant.DEAD_EXCHANGE);
            //正常队列设置死信 routing-key 参数 key 是固定值
            params.put("x-dead-letter-routing-key", MqConstant.DEAD_ROUTING_KEY);
            //设置队列的最大长度
            params.put("x-max-length",100);


            //创建队列
            channel.queueDeclare(MqConstant.NORMAL_QUEUE,true,false,false,params);
            channel.queueBind(MqConstant.NORMAL_QUEUE,MqConstant.NORMAL_EXCHANGE,MqConstant.NORMAL_ROUTING_KEY);
            log.info("RabbitMQ启动成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*    public static void main(String[] args) {
        init();
    }*/
}
