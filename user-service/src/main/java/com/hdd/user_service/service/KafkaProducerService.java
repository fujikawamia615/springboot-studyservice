package com.hdd.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendLoginEvent(Integer userId, String username) {
        String message = userId + ":" + username;
        kafkaTemplate.send("login-events", message);
    }
}