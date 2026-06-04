package com.hdd.studysys.mapper;

import java.util.List;

import com.hdd.studysys.entity.Grade;

public interface GradeMapper {
    List<Grade> selectAll();

    Grade selectById(Integer gradeId);

    int insert(Grade grade);

    int update(Grade grade);

    int deleteById(Integer gradeId);

    List<Grade> search(Integer studentId, Integer courseId, Integer grade);
}
