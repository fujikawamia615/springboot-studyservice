package com.hdd.studysys.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.hdd.studysys.dto.StudentWithDeptDTO;
import com.hdd.studysys.entity.Student;
import com.hdd.studysys.mapper.StudentMapper;
import com.hdd.studysys.service.StudentService;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentMapper studentMapper;

    @Override
    public List<Student> selectAll() {
        return studentMapper.selectAll();
    }

    @Override
    @Cacheable(value = "students", key = "#studentId", unless = "#result == null")
    public Student selectById(Integer studentId) {
        return studentMapper.selectById(studentId);
    }

    @Override
    public int insert(Student student) {
        return studentMapper.insert(student);
    }

    @Override
    @Caching(put = {
            @CachePut(value = "students", key = "#student.studentId")
    }, evict = {
            @CacheEvict(value = "studentsWithDept", key = "#student.studentId")
    })
    public int update(Student student) {
        return studentMapper.update(student);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "students", key = "#studentId"),
            @CacheEvict(value = "studentsWithDept", key = "#studentId")
    })
    public int deleteById(Integer studentId) {
        return studentMapper.deleteById(studentId);
    }

    @Override
    public List<StudentWithDeptDTO> selectAllWithDept() {
        return studentMapper.selectAllWithDept();
    }

    @Override
    @Cacheable(value = "studentsWithDept", key = "#studentId", unless = "#result == null")
    public StudentWithDeptDTO selectByIdWithDept(Integer studentId) {
        return studentMapper.selectByIdWithDept(studentId);
    }

    @Override
    public List<Student> search(String studentName, Integer departmentId) {
        return studentMapper.search(studentName, departmentId);
    }
}
