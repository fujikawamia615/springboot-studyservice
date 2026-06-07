package com.hdd.studysys.mapper;

import java.util.List;
import com.hdd.studysys.entity.Enrollment;
import com.hdd.studysys.dto.EnrollmentDTO;

public interface EnrollmentMapper {
    int insert(Enrollment enrollment);

    Enrollment selectById(Integer enrollmentId);

    List<EnrollmentDTO> selectByStudentId(Integer studentId);

    List<EnrollmentDTO> selectByScheduleId(Integer scheduleId);

    Enrollment selectByStudentAndSchedule(Integer studentId, Integer scheduleId);

    List<EnrollmentDTO> selectTimeConflict(Integer studentId, Integer dayOfWeek,
            Integer startPeriod, Integer endPeriod);

    int updateStatus(Integer enrollmentId, String status);
}