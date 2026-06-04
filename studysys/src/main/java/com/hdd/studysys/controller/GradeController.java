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

import com.github.pagehelper.PageInfo;
import com.hdd.studysys.entity.Grade;
import com.hdd.studysys.service.GradeService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/grade")
public class GradeController {
    @Autowired
    private GradeService gradeService;

    @GetMapping("")
    @Operation(summary = "分页查询所有成绩")
    public PageInfo<Grade> selectAll(@RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return gradeService.selectAll(page, size);
    }

    @GetMapping("/{id}")
    @Operation(summary = "按ID查询成绩")
    public Grade selectById(@PathVariable Integer id) {
        return gradeService.selectById(id);
    }

    @PostMapping("")
    @Operation(summary = "新增成绩")
    public int insert(@RequestBody Grade grade) {
        return gradeService.insert(grade);
    }

    @PutMapping("")
    @Operation(summary = "修改成绩")
    public int update(@RequestBody Grade grade) {
        return gradeService.update(grade);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除成绩")
    public int deleteById(@PathVariable Integer id) {
        return gradeService.deleteById(id);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索成绩", description = "按学生ID、课程ID或成绩搜索")
    public List<Grade> search(@RequestParam(required = false) Integer studentId,
            @RequestParam(required = false) Integer courseId,
            @RequestParam(required = false) Integer grade) {
        return gradeService.search(studentId, courseId, grade);
    }
}
