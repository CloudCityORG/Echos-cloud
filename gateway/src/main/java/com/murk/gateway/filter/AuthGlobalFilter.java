package com.murk.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {
   @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
       String path = exchange.getRequest().getURI().getPath();
       if (path.startsWith("/api/user/login") || path.startsWith("/api/user/register")) {
           return chain.filter(exchange);
       }
       //检查token（从请求头获取）
       String token = exchange.getRequest().getHeaders().getFirst("Authorization");
       if (token == null || token.isEmpty()) {
           exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
           return exchange.getResponse().setComplete();
       }
       //验证token是否合法
       //放行
       return chain.filter(exchange);
   }
       @Override
       public int getOrder() {
         return -1;
   }
}
