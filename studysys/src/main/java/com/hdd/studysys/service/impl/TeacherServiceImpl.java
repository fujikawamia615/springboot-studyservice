package com.hdd.studysys.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.hdd.studysys.entity.Teacher;
import com.hdd.studysys.mapper.TeacherMapper;
import com.hdd.studysys.service.TeacherService;

@Service
public class TeacherServiceImpl implements TeacherService {
    @Autowired
    TeacherMapper teacherMapper;

    @Override
    public List<Teacher> selectAll() {
        return teacherMapper.selectAll();
    }

    @Override
    @Cacheable(value = "teachers", key = "#teacherId", unless = "#result == null")
    public Teacher selectById(Integer teacherId) {
        return teacherMapper.selectById(teacherId);
    }

    @Override
    public int insert(Teacher teacher) {
        return teacherMapper.insert(teacher);
    }

    @Override
    @CachePut(value = "teachers", key = "#teacher.teacherId")
    public int update(Teacher teacher) {
        return teacherMapper.update(teacher);
    }

    @Override
    @CacheEvict(value = "teachers", key = "#teacherId")
    public int deleteById(Integer teacherId) {
        return teacherMapper.deleteById(teacherId);
    }

    @Override
    public List<Teacher> search(String teacherName, Integer departmentId) {
        return teacherMapper.search(teacherName, departmentId);
    }
}
