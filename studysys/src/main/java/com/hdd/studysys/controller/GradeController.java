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
import com.hdd.studysys.dto.GradeDTO;
import com.hdd.studysys.entity.Grade;
import com.hdd.studysys.service.GradeService;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestHeader;

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
    public int insert(@RequestBody Grade grade,
            @RequestHeader(value = "role", required = false) String role,
            @RequestHeader(value = "referenceId", required = false) Integer referenceId) {
        return gradeService.insertWithCheck(grade, role, referenceId);
    }

    @PutMapping("")
    @Operation(summary = "修改成绩")
    public int update(@RequestBody Grade grade,
            @RequestHeader(value = "role", required = false) String role,
            @RequestHeader(value = "referenceId", required = false) Integer referenceId) {
        return gradeService.updateWithCheck(grade, role, referenceId);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除成绩")
    public int deleteById(@PathVariable Integer id,
            @RequestHeader(value = "role", required = false) String role,
            @RequestHeader(value = "referenceId", required = false) Integer referenceId) {
        return gradeService.deleteByIdWithCheck(id, role, referenceId);
    }

    @GetMapping("/my")
    public List<GradeDTO> myGrades(@RequestHeader("referenceId") Integer studentId) {
        return gradeService.selectByStudentIdWithCourseName(studentId);
    }

    @GetMapping("/schedule/{scheduleId}")
    @Operation(summary = "查看某门课的成绩（教师/管理员）")
    public List<Grade> scheduleGrades(@PathVariable Integer scheduleId,
            @RequestHeader(value = "role", required = false) String role,
            @RequestHeader(value = "referenceId", required = false) Integer referenceId) {
        return gradeService.selectByScheduleId(scheduleId, role, referenceId);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索成绩")
    public List<Grade> search(@RequestParam(required = false) Integer studentId,
            @RequestParam(required = false) Integer scheduleId,
            @RequestParam(required = false) Integer grade) {
        return gradeService.search(studentId, scheduleId, grade);
    }
}