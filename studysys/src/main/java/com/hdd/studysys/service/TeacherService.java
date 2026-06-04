package com.hdd.studysys.service;

import java.util.List;

import com.hdd.studysys.entity.Teacher;

public interface TeacherService {
    List<Teacher> selectAll();

    Teacher selectById(Integer teacherId);

    int insert(Teacher teacher);

    int update(Teacher teacher);

    int deleteById(Integer teacherId);

    List<Teacher> search(String teacherName, Integer departmentId);
} 