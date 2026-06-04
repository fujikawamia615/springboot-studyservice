package com.hdd.studysys.service;

import java.util.List;

import com.hdd.studysys.dto.StudentWithDeptDTO;
import com.hdd.studysys.entity.Student;

public interface StudentService {
    List<Student> selectAll();

    Student selectById(Integer studentId);

    int insert(Student student);

    int update(Student student);

    int deleteById(Integer studentId);
    
    List<StudentWithDeptDTO> selectAllWithDept();

    StudentWithDeptDTO selectByIdWithDept(Integer studentId);

    List<Student> search(String studentName, Integer departmentId);
}
