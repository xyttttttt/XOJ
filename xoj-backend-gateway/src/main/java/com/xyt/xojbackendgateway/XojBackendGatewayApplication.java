package com.xyt.xojbackendgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@EnableCaching
public class XojBackendGatewayApplication {


    public static void main(String[] args) {
        System.getProperties().setProperty("csp.sentinel.app.type", "1");
        SpringApplication.run(XojBackendGatewayApplication.class, args);
    }

}
