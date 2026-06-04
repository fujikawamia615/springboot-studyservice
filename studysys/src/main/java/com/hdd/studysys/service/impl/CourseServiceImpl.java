package com.hdd.studysys.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.hdd.studysys.entity.Course;
import com.hdd.studysys.mapper.CourseMapper;
import com.hdd.studysys.service.CourseService;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseMapper courseMapper;

    @Override
    public List<Course> selectAll() {
        return courseMapper.selectAll();
    }

    @Override
    @Cacheable(value = "courses", key = "#courseId", unless = "#result == null")
    public Course selectById(Integer courseId) {
        return courseMapper.selectById(courseId);
    }

    @Override
    public int insert(Course course) {
        return courseMapper.insert(course);
    }

    @Override
    @CachePut(value = "courses", key = "#course.courseId")
    public int update(Course course) {
        return courseMapper.update(course);
    }

    @Override
    @CacheEvict(value = "courses", key = "#courseId")
    public int deleteById(Integer courseId) {
        return courseMapper.deleteById(courseId);
    }

    @Override
    public List<Course> search(String courseName, Integer teacherId) {
        return courseMapper.search(courseName, teacherId);
    }
}
