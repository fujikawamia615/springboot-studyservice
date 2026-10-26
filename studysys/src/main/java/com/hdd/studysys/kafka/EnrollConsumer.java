package com.hdd.studysys.kafka;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.hdd.studysys.dto.EnrollRequest;
import com.hdd.studysys.service.EnrollmentService;
import com.hdd.studysys.service.NotificationService;

import java.util.HashMap;
import java.util.Map;

@Component
public class EnrollConsumer {

    @Autowired
    private EnrollmentService enrollmentService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private RedissonClient redissonClient;

    @KafkaListener(topics = "enroll-topic", concurrency = "3")
    public void handleEnroll(EnrollRequest req) {
        Map<String,Object> map=enrollmentService.enroll(req.getStudentId(), req.getScheduleId());
        String msg="课程安排: "+req.getScheduleId()+map.get("message");
        notificationService.send("student",req.getStudentId(), msg);
    }
    @KafkaListener(topics = "drop-topic",groupId = "drop-group")
    public void handleDrop(Integer enrollmentId) {
        enrollmentService.drop(enrollmentId);
    }
}