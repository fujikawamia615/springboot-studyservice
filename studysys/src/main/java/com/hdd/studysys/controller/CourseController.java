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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hdd.studysys.entity.Course;
import com.hdd.studysys.service.CourseService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/course")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @GetMapping("")
    @Operation(summary = "查询所有课程", description = "返回全部课程列表")
    public List<Course> selectAll() {
        return courseService.selectAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "按ID查询课程")
    public Course selectById(@PathVariable Integer id) {
        return courseService.selectById(id);
    }

    @PostMapping("")
    @Operation(summary = "新增课程")
    public int insert(@RequestBody Course course) {
        return courseService.insert(course);
    }

    @PutMapping("")
    @Operation(summary = "修改课程")
    public int update(@RequestBody Course course) {
        return courseService.update(course);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除课程")
    public int deleteById(@PathVariable Integer id) {
        return courseService.deleteById(id);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索课程", description = "按课程名称模糊查询或按教师查询")
    public List<Course> search(@RequestParam(required = false) String courseName,
            @RequestParam(required = false) Integer teacherId) {
        return courseService.search(courseName, teacherId);
    }
}
