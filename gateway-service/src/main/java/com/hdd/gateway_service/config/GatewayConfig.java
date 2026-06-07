package com.hdd.gateway_service.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

        @Bean
        public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
                return builder.routes()
                                .route("user-service", r -> r.path("/api/user/**")
                                                .uri("http://localhost:8081"))
                                .route("studysys", r -> r.path("/api/**")
                                                .uri("http://localhost:8080"))
                                .build();
        }
}