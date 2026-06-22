package com.hdd.studysys.kafka;

import jakarta.annotation.PostConstruct;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.hdd.studysys.dto.EnrollRequest;
import com.hdd.studysys.service.EnrollmentService;
import java.time.Duration;
@Component
public class EnrollConsumer {

    @Autowired
    private EnrollmentService enrollmentService;
    @Autowired
    private RedissonClient redissonClient;

    @KafkaListener(topics = "enroll-topic", concurrency = "3")
    public void handleEnroll(EnrollRequest req) {
        enrollmentService.enroll(req.getStudentId(), req.getScheduleId());
    }

    @KafkaListener(topics = "drop-topic",groupId = "drop-group")
    public void handleDrop(Integer enrollmentId) {
        enrollmentService.drop(enrollmentId);
    }
}