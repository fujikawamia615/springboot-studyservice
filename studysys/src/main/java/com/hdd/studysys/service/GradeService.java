package com.hdd.studysys.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.hdd.studysys.dto.GradeDTO;
import com.hdd.studysys.entity.Grade;

public interface GradeService {

    PageInfo<Grade> selectAll(Integer page, Integer size);

    Grade selectById(Integer gradeId);

    int insert(Grade grade);

    int update(Grade grade);

    int deleteById(Integer gradeId);

    List<Grade> search(Integer studentId, Integer scheduleId, Integer Grade);

    List<Grade> selectByStudentId(Integer studentId);

    List<Grade> selectByScheduleId(Integer scheduleId, String role, Integer referenceId);

    int insertWithCheck(Grade grade, String role, Integer referenceId);

    int updateWithCheck(Grade grade, String role, Integer referenceId);

    int deleteByIdWithCheck(Integer gradeId, String role, Integer referenceId);

    List<GradeDTO> selectByStudentIdWithCourseName(Integer studentId);
}