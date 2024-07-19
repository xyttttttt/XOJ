package com.xyt.xojbackendgateway.filter;

import com.alibaba.excel.event.Order;
import com.xyt.xojbackendjudgeservice.utils.JwtUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class GlobalAuthFilter implements GlobalFilter, Order {


    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        String path = serverHttpRequest.getURI().getPath();
        ServerHttpResponse serverHttpResponse = exchange.getResponse();
        //判断路径中是否含有inner
        if (antPathMatcher.match("/**/inner/**",path)){
            serverHttpResponse.setStatusCode(HttpStatus.FORBIDDEN);
            DataBufferFactory dataBufferFactory = serverHttpResponse.bufferFactory();
            DataBuffer dataBuffer = dataBufferFactory.wrap("无权限".getBytes(StandardCharsets.UTF_8));
            return serverHttpResponse.writeWith(Mono.just(dataBuffer));
        }
        String authorizationHeader = serverHttpRequest.getHeaders().getFirst("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring("Bearer ".length());
            if (!"null".equals(token)){
                boolean valid = JwtUtils.checkToken(token);
                if (!valid){
                    serverHttpResponse.setStatusCode(HttpStatus.FORBIDDEN);
                    DataBufferFactory dataBufferFactory = serverHttpResponse.bufferFactory();
                    DataBuffer dataBuffer = dataBufferFactory.wrap("登录过期".getBytes(StandardCharsets.UTF_8));
                    return serverHttpResponse.writeWith(Mono.just(dataBuffer));
                }
            }
        }
        return chain.filter(exchange);
    }

    /**
     * 优先级最高、后期扩展定义多个拦截器，可设置优先级
     * @return
     */
    @Override
    public int order() {
        return 0;
    }
}
