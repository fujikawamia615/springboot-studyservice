package com.hdd.studysys.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hdd.studysys.dto.EnrollmentDTO;
import com.hdd.studysys.service.EnrollmentService;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestHeader;
@RestController
@RequestMapping("/api/enrollment")
public class EnrollmentController {
    @Autowired
    private EnrollmentService enrollmentService;

    @PostMapping("/enroll")
    @Operation(summary = "学生选课", description = "检查重复、容量、时间冲突后选课")
    public Map<String, Object> enroll(
            @RequestHeader("referenceId") Integer studentId,
            @RequestParam Integer scheduleId) {
        return enrollmentService.enroll(studentId, scheduleId);
    }

    @PostMapping("/drop")
    @Operation(summary = "退选课程")
    public Map<String, Object> drop(@RequestParam Integer enrollmentId) {
        return enrollmentService.drop(enrollmentId);
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
}