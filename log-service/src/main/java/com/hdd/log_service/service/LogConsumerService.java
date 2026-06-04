package com.hdd.log_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class LogConsumerService {

    private static final Logger log = LoggerFactory.getLogger(LogConsumerService.class);

    @KafkaListener(topics = "login-events")
    public void listenLoginEvent(String message) {
        log.info("【登录事件】{}", message);
    }

    @KafkaListener(topics = "db-errors")
    public void listenDbError(String message) {
        log.error("【数据库异常】{}", message);
    }

    @KafkaListener(topics = "operation-logs")
    public void listenOperationLog(String message) {
        log.info("【操作日志】{}", message);
    }
}