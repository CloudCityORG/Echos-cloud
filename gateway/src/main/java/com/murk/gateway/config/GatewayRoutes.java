package com.murk.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutes {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // 用户服务 —— 路径以 /api/user 开头的请求，转发到 service-user
                .route("service-user", r -> r
                        .path("/api/user/**")
                        .uri("lb://service-user")    // lb = load balance，从 Nacos 找服务
                )

                // 榜单服务
                .route("service-chart", r -> r
                        .path("/api/chart/**")
                        .uri("lb://service-chart")
                )

                // Agent 服务
                .route("service-agent", r -> r
                        .path("/api/agent/**")
                        .uri("lb://service-agent")
                )

                // 审核服务
                .route("service-audit", r -> r
                        .path("/api/audit/**")
                        .uri("lb://service-audit")
                )

                // 投票服务
                .route("service-vote", r -> r
                        .path("/api/vote/**")
                        .uri("lb://service-vote")
                )

                // WebSocket 服务
                .route("service-websocket", r -> r
                        .path("/ws/**")
                        .uri("lb://service-websocket")
                )

                .build();
    }
}
