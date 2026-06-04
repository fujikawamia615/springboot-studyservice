package com.hdd.studysys.mapper;

import java.util.List;

import com.hdd.studysys.entity.Course;

public interface CourseMapper {
    List<Course> selectAll();

    Course selectById(Integer courseId);

    int insert(Course course);

    int update(Course course);
    
    int deleteById(Integer courseId);

    List<Course> search(String courseName, Integer teacherId);
}
