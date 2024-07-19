package com.xyt.xojbackendjudgeservice.mq;

import cn.hutool.core.util.StrUtil;
import com.rabbitmq.client.Channel;
import com.xyt.xojbackendjudgeservice.service.JudgeService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class MessageConsumer {

    @Resource
    private JudgeService judgeService;

    @SneakyThrows
    @RabbitListener(queues = {MqConstant.NORMAL_QUEUE}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receiveMessage message = {}", message);
        try {
            Long questionSubmitId = Long.parseLong(message);
            if (StrUtil.isBlankIfStr(questionSubmitId)) {
                throw new RuntimeException("提交信息异常");
            }
            judgeService.doJudge(questionSubmitId);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @SneakyThrows
    @RabbitListener(queues = MqConstant.DEAD_QUEUE)
    public void receiveDeadMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receiveDeadMessage message = {}", message);
        try {
            Long questionSubmitId = Long.parseLong(message);
            if (StrUtil.isBlankIfStr(questionSubmitId)) {
                throw new RuntimeException("提交信息异常");
            }
            judgeService.doJudge(questionSubmitId);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            channel.basicNack(deliveryTag, false, false);
        }
    }

}
