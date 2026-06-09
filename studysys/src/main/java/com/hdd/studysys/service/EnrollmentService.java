package com.hdd.studysys.service;

import java.util.List;
import java.util.Map;
import com.hdd.studysys.dto.EnrollmentDTO;

public interface EnrollmentService {
    Map<String, Object> enroll(Integer studentId, Integer scheduleId);

    Map<String, Object> drop(Integer enrollmentId);

    List<EnrollmentDTO> myEnrollments(Integer studentId);

    List<EnrollmentDTO> scheduleEnrollments(Integer scheduleId);

    List<EnrollmentDTO> scheduleEnrollmentsWithCheck(Integer scheduleId, String role, Integer referenceId);
}