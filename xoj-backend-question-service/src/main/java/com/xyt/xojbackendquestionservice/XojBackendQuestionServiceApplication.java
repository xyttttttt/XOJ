package com.xyt.xojbackendquestionservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.xyt.xojbackendquestionservice.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.xyt")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.xyt.oxibackendserviceclient.service"})
public class XojBackendQuestionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(XojBackendQuestionServiceApplication.class, args);
    }

}