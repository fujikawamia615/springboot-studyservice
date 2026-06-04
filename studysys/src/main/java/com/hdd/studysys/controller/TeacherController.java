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

import com.hdd.studysys.entity.Teacher;
import com.hdd.studysys.service.TeacherService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/teacher")

public class TeacherController {
    @Autowired
    private TeacherService teacherService;

    @GetMapping("")
    @Operation(summary = "查询所有教师", description = "返回全部教师列表")
    public List<Teacher> selectAll() {
        return teacherService.selectAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "按ID查询教师")
    public Teacher selectById(@PathVariable Integer id) {
        return teacherService.selectById(id);
    }

    @PostMapping("")
    @Operation(summary = "新增教师")
    public int insert(@RequestBody Teacher teacher) {
        return teacherService.insert(teacher);
    }

    @PutMapping("")
    @Operation(summary = "修改教师")
    public int update(@RequestBody Teacher teacher) {
        return teacherService.update(teacher);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除教师")
    public int deleteById(@PathVariable Integer id) {
        return teacherService.deleteById(id);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索教师", description = "按姓名或学院搜索教师")
    public List<Teacher> search(@RequestParam(required = false) String teacherName,
            @RequestParam(required = false) Integer departmentId) {
        return teacherService.search(teacherName, departmentId);
    }
}
