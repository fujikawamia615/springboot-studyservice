package com.hdd.studysys.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.hdd.studysys.dto.EnrollRequest;
import com.hdd.studysys.dto.EnrollmentDTO;
import com.hdd.studysys.entity.CourseSchedule;
import com.hdd.studysys.service.EnrollmentService;
import com.hdd.studysys.service.impl.CourseScheduleCacheService;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api/enrollment")
public class EnrollmentController {
    @Autowired
    private EnrollmentService enrollmentService;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    private CourseScheduleCacheService cacheService;

    @PostMapping("/enroll")
    @SentinelResource(value = "enroll", blockHandler = "enrollBlock")
    public Map<String, Object> enroll(
            @RequestHeader("referenceId") Integer studentId,
            @RequestParam Integer scheduleId) {
        // --- 快速预检（Redis，不落库）---
        String status = cacheService.getEnrollStatus(studentId, scheduleId);
        if ("ENROLLED".equals(status))
            return Map.of("success", false, "message", "已选过此课程，请勿重复选课");

        CourseSchedule schedule = cacheService.getSchedule(scheduleId);
        if (schedule == null)
            return Map.of("success", false, "message", "课程安排不存在");

        if (cacheService.checkTimeConflict(studentId, schedule.getDayOfWeek(),
                schedule.getStartPeriod(), schedule.getEndPeriod()))
            return Map.of("success", false, "message", "选课时间冲突");
        // --- 预检结束 ---

        EnrollRequest req = new EnrollRequest();
        req.setStudentId(studentId);
        req.setScheduleId(scheduleId);
        kafkaTemplate.send("enroll-topic", req);
        return Map.of("success", true, "message", "选课请求已提交");
    }

    public Map<String, Object> enrollBlock(
            @RequestHeader("referenceId") Integer studentId,
            @RequestParam Integer scheduleId,
            BlockException e) {
        return Map.of("success", false, "message", "系统繁忙，请稍后重试");
    }

    @PostMapping("/drop")
    public Map<String, Object> drop(@RequestParam Integer enrollmentId) {
        kafkaTemplate.send("drop-topic", enrollmentId);
        return Map.of("success", true, "message", "退选请求已提交");
    }

    @GetMapping("/my")
    @Operation(summary = "查看我的选课列表")
    public List<EnrollmentDTO> myEnrollments(@RequestHeader("referenceId") Integer studentId) {
        return enrollmentService.myEnrollments(studentId);
    }

    @GetMapping("/schedule/{scheduleId}")
    public List<EnrollmentDTO> scheduleEnrollments(@PathVariable Integer scheduleId,
                                                   @RequestHeader(value = "role", required = false) String role,
                                                   @RequestHeader(value = "referenceId", required = false) Integer referenceId) {
        return enrollmentService.scheduleEnrollmentsWithCheck(scheduleId, role, referenceId);
    }

    @PostMapping("/enroll-direct")
    public Map<String, Object> enrollDirect(
            @RequestHeader("referenceId") Integer studentId,
            @RequestParam Integer scheduleId) {
        return enrollmentService.enroll(studentId, scheduleId);
    }
}