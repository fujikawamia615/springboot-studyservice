package com.hdd.studysys.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hdd.studysys.dto.EnrollmentDTO;
import com.hdd.studysys.entity.CourseSchedule;
import com.hdd.studysys.entity.Enrollment;
import com.hdd.studysys.mapper.CourseScheduleMapper;
import com.hdd.studysys.mapper.EnrollmentMapper;
import com.hdd.studysys.service.EnrollmentService;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {
    @Autowired
    private EnrollmentMapper enrollmentMapper;
    @Autowired
    private CourseScheduleMapper courseScheduleMapper;

    @Override
    public Map<String, Object> enroll(Integer studentId, Integer scheduleId) {
        Map<String, Object> result = new HashMap<>();

        CourseSchedule schedule = courseScheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            result.put("success", false);
            result.put("message", "课程安排不存在");
            return result;
        }

        Enrollment existing = enrollmentMapper.selectByStudentAndSchedule(studentId, scheduleId);
        if (existing != null && "ENROLLED".equals(existing.getStatus())) {
            result.put("success", false);
            result.put("message", "已选过此课程，请勿重复选课");
            return result;
        }

        if (schedule.getEnrolledCount() >= schedule.getCapacity()) {
            result.put("success", false);
            result.put("message", "该课程已满员");
            return result;
        }

        List<EnrollmentDTO> conflicts = enrollmentMapper.selectTimeConflict(
                studentId, schedule.getDayOfWeek(),
                schedule.getStartPeriod(), schedule.getEndPeriod());
        if (!conflicts.isEmpty()) {
            result.put("success", false);
            result.put("message", "选课时间与「" + conflicts.get(0).getCourseName() + "」冲突");
            return result;
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setScheduleId(scheduleId);
        enrollment.setStudentId(studentId);
        enrollment.setStatus("ENROLLED");
        enrollmentMapper.insert(enrollment);
        courseScheduleMapper.updateEnrolledCount(scheduleId, 1);

        result.put("success", true);
        result.put("message", "选课成功");
        return result;
    }

    @Override
    public Map<String, Object> drop(Integer enrollmentId) {
        Map<String, Object> result = new HashMap<>();
        Enrollment enrollment = enrollmentMapper.selectById(enrollmentId);
        if (enrollment == null) {
            result.put("success", false);
            result.put("message", "选课记录不存在");
            return result;
        }
        enrollmentMapper.updateStatus(enrollmentId, "DROPPED");
        courseScheduleMapper.updateEnrolledCount(enrollment.getScheduleId(), -1);
        result.put("success", true);
        result.put("message", "退选成功");
        return result;
    }

    @Override
    public List<EnrollmentDTO> myEnrollments(Integer studentId) {
        return enrollmentMapper.selectByStudentId(studentId);
    }

    @Override
    public List<EnrollmentDTO> scheduleEnrollments(Integer scheduleId) {
        return enrollmentMapper.selectByScheduleId(scheduleId);
    }
}