package com.xyt.xojbackendgateway.handler;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.xyt.xojbackendjudgeservice.common.ResultUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class SentinelBlockRequestHandler implements BlockRequestHandler {

    @Override
    public Mono<ServerResponse> handleRequest(ServerWebExchange exchange, Throwable t) {
        // 判断是否是html访问，如果是则转发url
        if (acceptsHtml(exchange)) {
            return htmlErrorResponse();
        }
         //如果是接口访问，则返回提示
        return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(fromValue(ResultUtils.error(HttpStatus.TOO_MANY_REQUESTS.value(),"请求太多了,请稍后再试")));
    }

    private Mono<ServerResponse> htmlErrorResponse() {
        String url="http://localhost:8080/current/limiting";
        URI uri =URI.create(url);
        return ServerResponse.temporaryRedirect(uri)
                .headers(httpHeaders -> {
                    httpHeaders.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,"*");
                    httpHeaders.add(HttpHeaders.VARY, "Origin");
                    httpHeaders.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
                    httpHeaders.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "*");
                    httpHeaders.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "*");
                    httpHeaders.set(HttpHeaders.CACHE_CONTROL, "no-store");
                    httpHeaders.set(HttpHeaders.PRAGMA, "no-cache");
                })
                .build();
    }

    private boolean acceptsHtml(ServerWebExchange exchange) {
        try {
            List<MediaType> acceptedMediaTypes = exchange.getRequest().getHeaders().getAccept();
            acceptedMediaTypes.remove(MediaType.ALL);
            MediaType.sortBySpecificityAndQuality(acceptedMediaTypes);
            return acceptedMediaTypes.stream()
                    .anyMatch(MediaType.TEXT_HTML::isCompatibleWith);
        } catch (InvalidMediaTypeException ex) {
            return false;
        }
    }

}
