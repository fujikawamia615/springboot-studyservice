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

import com.hdd.studysys.entity.Department;
import com.hdd.studysys.service.DepartmentService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/department")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;

    @GetMapping("")
    @Operation(summary = "查询所有学院", description = "返回全部学院列表")
    public List<Department> selectAll() {
        return departmentService.selectAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "按ID查询学院")
    public Department selectById(@PathVariable Integer id) {
        return departmentService.selectById(id);
    }

    @PostMapping("")
    @Operation(summary = "新增学院")
    public int insert(@RequestBody Department department) {
        return departmentService.insert(department);
    }

    @PostMapping("/batch")
    @Operation(summary = "批量新增学院")
    public int batchInsert(@RequestBody List<Department> departmentList) {
        return departmentService.batchInsert(departmentList);
    }

    @PutMapping("")
    @Operation(summary = "修改学院")
    public int update(@RequestBody Department department) {
        return departmentService.update(department);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除学院")
    public int deleteById(@PathVariable Integer id) {
        return departmentService.deleteById(id);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索学院", description = "按学院名称模糊查询")
    public List<Department> search(@RequestParam(required = false) String departmentName) {
        return departmentService.search(departmentName);
    }
}
