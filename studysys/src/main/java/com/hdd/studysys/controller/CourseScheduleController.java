package com.hdd.studysys.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hdd.studysys.entity.CourseSchedule;
import com.hdd.studysys.service.CourseScheduleService;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestHeader;
@RestController
@RequestMapping("/api/course-schedule")
public class CourseScheduleController {
    @Autowired
    private CourseScheduleService courseScheduleService;

    @GetMapping("")
    @Operation(summary = "查询所有课程安排")
    public List<?> selectAll(
            @RequestHeader(value = "role", required = false) String role,
            @RequestHeader(value = "referenceId", required = false) Integer referenceId) {
        if ("teacher".equals(role)) {
            return courseScheduleService.selectByTeacherId(referenceId);
        }
        return courseScheduleService.selectAllWithNames();
    }

    @GetMapping("/{id}")
    @Operation(summary = "按ID查询课程安排")
    public CourseSchedule selectById(@PathVariable Integer id) {
        return courseScheduleService.selectById(id);
    }

    @PostMapping("")
    @Operation(summary = "新增课程安排")
    public int insert(@RequestBody CourseSchedule courseSchedule) {
        return courseScheduleService.insert(courseSchedule);
    }

    @PutMapping("")
    @Operation(summary = "修改课程安排")
    public int update(@RequestBody CourseSchedule courseSchedule) {
        return courseScheduleService.update(courseSchedule);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除课程安排")
    public int deleteById(@PathVariable Integer id) {
        return courseScheduleService.deleteById(id);
    }
}