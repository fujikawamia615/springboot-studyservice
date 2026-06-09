package com.hdd.studysys.mapper;

import java.util.List;

import com.hdd.studysys.dto.GradeDTO;
import com.hdd.studysys.entity.Grade;
import org.apache.ibatis.annotations.Param;
public interface GradeMapper {
    List<Grade> selectAll();

    Grade selectById(Integer gradeId);

    int insert(Grade grade);

    int update(Grade grade);

    int deleteById(Integer gradeId);

    List<Grade> search(Integer studentId, Integer scheduleId, Integer grade);

    List<Grade> selectByStudentId(Integer studentId);

    List<Grade> selectByScheduleId(Integer scheduleId);
   
    List<Grade> selectByScheduleIdForTeacher(@Param("scheduleId") Integer scheduleId, @Param("teacherId") Integer teacherId);

    int countScheduleByTeacher(@Param("scheduleId") Integer scheduleId, @Param("teacherId") Integer teacherId);
    
    int deleteByIdWithTeacherCheck(@Param("gradeId") Integer gradeId, @Param("teacherId") Integer teacherId);

    List<GradeDTO> selectByStudentIdWithCourseName(Integer studentId);
}
