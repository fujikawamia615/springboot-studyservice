package com.hdd.gateway_service.filter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.hdd.gateway_service.util.JwtUtil;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthGatewayFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;

    private static final List<String> WHITE_LIST = List.of(
            "/api/user/login",
            "/api/user/register",
            "/swagger-ui",
            "/v3/api-docs");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (WHITE_LIST.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        if (!path.startsWith("/api/")) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validate(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        Claims claims = jwtUtil.parse(token);
        String role = claims.get("role", String.class);
        Integer referenceId = claims.get("referenceId", Integer.class);
        String method = request.getMethod().name();

        // ========== 角色权限检查 ==========
        if (!"ADMIN".equals(role)) {
            // 1. 用户管理接口 → 仅 ADMIN
            if (path.startsWith("/api/user/")) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
            if ("student".equals(role)) {
                // 学生：仅允许选课、看课表、看自己信息
                if ("GET".equals(method) && path.equals("/api/course-schedule")) {
                    // 允许查看课程安排
                } else if (path.equals("/api/enrollment/enroll") && "POST".equals(method)) {
                    // 允许选课
                } else if (path.equals("/api/enrollment/drop") && "POST".equals(method)) {
                    // 允许退选
                } else if (path.startsWith("/api/enrollment/my") && "GET".equals(method)) {
                    // 允许查看自己的选课
                } else if (path.startsWith("/api/student/") && "GET".equals(method)) {
                    // 允许查看个人信息（服务层会校验是否自己）
                } else if (path.startsWith("/api/grade/my") && "GET".equals(method)) {
                    // 允许查看自己的成绩
                } else {
                    // 未明确允许的接口全部禁止
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }
            }else if ("teacher".equals(role)) {
                // 教师：允许查看自己教的课程的选课名单
                if (path.startsWith("/api/enrollment/schedule/") && "GET".equals(method)) {
                    // 允许（服务层会校验是否自己的课）
                } else if (path.equals("/api/course-schedule") && "GET".equals(method)) {
                    // 允许查看课程安排（服务层按 teacher_id 过滤）
                } else if (path.startsWith("/api/teacher/") && "GET".equals(method)) {
                    // 允许查看个人信息（服务层会校验是否自己）
                } else if (path.startsWith("/api/grade") && "GET".equals(method)) {
                    // 允许查询成绩
                } else if (path.startsWith("/api/grade")) {
                    // 允许教师增删改成绩
                } else{
                    // 未明确允许的接口全部禁止
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }
            }
        }

        ServerHttpRequest modifiedRequest = request.mutate()
                .header("userId", claims.getSubject())
                .header("role", role)
                .header("referenceId", referenceId != null ? referenceId.toString() : "")
                .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    @Override
    public int getOrder() {
        return -100;
    }
}