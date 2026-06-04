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

import com.hdd.studysys.dto.StudentWithDeptDTO;
import com.hdd.studysys.entity.Student;
import com.hdd.studysys.service.StudentService;

import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@RequestMapping("/api/student")
public class StudentController {
    private static final Logger log = LoggerFactory.getLogger(StudentController.class);
    @Autowired
    private StudentService studentService;

    @GetMapping("")
    @Operation(summary = "查询所有学生", description = "返回全部学生列表")
    public List<Student> selectAll() {
        log.info("查询所有学生");
        return studentService.selectAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "按ID查询学生")
    public Student selectById(@PathVariable Integer id) {
        return studentService.selectById(id);
    }

    @PostMapping("")
    @Operation(summary = "新增学生")
    public int insert(@RequestBody Student student) {
        return studentService.insert(student);
    }

    @PutMapping("")
    @Operation(summary = "修改学生")
    public int update(@RequestBody Student student) {
        return studentService.update(student);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除学生")
    public int deleteById(@PathVariable Integer id) {
        return studentService.deleteById(id);
    }

    @GetMapping("/withdept")
    @Operation(summary = "查询所有学生（带学院名称）")
    public List<StudentWithDeptDTO> selectAllWithDept() {
        return studentService.selectAllWithDept();
    }

    @GetMapping("/withdept/{id}")
    @Operation(summary = "按ID查询学生（带学院名称）")
    public StudentWithDeptDTO selectByIdWithDept(@PathVariable Integer id) {
        return studentService.selectByIdWithDept(id);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索学生", description = "按姓名模糊查询或按学院查询")
    public List<Student> search(@RequestParam(required = false) String studentName,
            @RequestParam(required = false) Integer departmentId) {
        return studentService.search(studentName, departmentId);
    }
}
