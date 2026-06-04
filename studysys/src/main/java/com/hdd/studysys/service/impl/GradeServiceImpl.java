package com.hdd.studysys.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hdd.studysys.entity.Grade;
import com.hdd.studysys.mapper.GradeMapper;
import com.hdd.studysys.service.GradeService;

@Service
public class GradeServiceImpl implements GradeService {
    @Autowired
    private GradeMapper gradeMapper;

    @Override
    public PageInfo<Grade> selectAll(Integer page, Integer size) {
        PageHelper.startPage(page, size);
        return new PageInfo<>(gradeMapper.selectAll());
    }

    @Override
    @Cacheable(value = "grades", key = "#gradeId", unless = "#result == null")
    public Grade selectById(Integer gradeId) {
        return gradeMapper.selectById(gradeId);
    }

    @Override
    public int insert(Grade grade) {
        return gradeMapper.insert(grade);
    }

    @Override
    @CachePut(value = "grades", key = "#grade.gradeId")
    public int update(Grade grade) {
        return gradeMapper.update(grade);
    }

    @Override
    @CacheEvict(value = "grades", key = "#gradeId")
    public int deleteById(Integer gradeId) {
        return gradeMapper.deleteById(gradeId);
    }

    @Override
    public List<Grade> search(Integer studentId, Integer courseId, Integer Grade) {
        return gradeMapper.search(studentId, courseId, Grade);
    }
}
