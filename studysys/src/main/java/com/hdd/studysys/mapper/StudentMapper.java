package com.hdd.studysys.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hdd.studysys.dto.StudentWithDeptDTO;
import com.hdd.studysys.entity.Student;

public interface StudentMapper {
    List<Student> selectAll();
    
    Student selectById(Integer studentId);

    int insert(Student student);

    int update(Student student);
    
    int deleteById(Integer studentId);

    List<StudentWithDeptDTO> selectAllWithDept();
    
    StudentWithDeptDTO selectByIdWithDept(Integer studentId);

    List<Student> search(@Param("studentName") String studentName,
                         @Param("departmentId") Integer departmentId);
}
