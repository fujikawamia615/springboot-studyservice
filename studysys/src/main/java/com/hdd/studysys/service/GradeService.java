package com.hdd.studysys.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.hdd.studysys.entity.Grade;

public interface GradeService {

    PageInfo<Grade> selectAll(Integer page, Integer size);

    Grade selectById(Integer gradeId);

    int insert(Grade grade);

    int update(Grade grade);

    int deleteById(Integer gradeId);
    
    List<Grade> search(Integer studentId, Integer courseId, Integer Grade);
}
