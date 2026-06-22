package com.hdd.studysys.service.impl;

import java.util.List;
import java.util.Map;

import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hdd.studysys.dto.EnrollmentDTO;
import com.hdd.studysys.entity.CourseSchedule;
import com.hdd.studysys.entity.Enrollment;
import com.hdd.studysys.mapper.EnrollmentMapper;
import com.hdd.studysys.service.EnrollmentService;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {
    @Autowired
    private EnrollmentMapper enrollmentMapper;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private CourseScheduleCacheService cacheService;

    @Override
    public Map<String, Object> enroll(Integer studentId, Integer scheduleId) {
        RReadWriteLock rwLock = redissonClient.getReadWriteLock("enroll:" + scheduleId);
        RLock readLock = rwLock.readLock();
        readLock.lock();
        try {
            CourseSchedule schedule = cacheService.getSchedule(scheduleId);
            if (schedule == null)
                return error("课程安排不存在");

            String status = cacheService.getEnrollStatus(studentId, scheduleId);
            if ("ENROLLED".equals(status))
                return error("已选过此课程，请勿重复选课");

            if (cacheService.checkTimeConflict(studentId, schedule.getDayOfWeek(),
                    schedule.getStartPeriod(), schedule.getEndPeriod()))
                return error("选课时间冲突");
        } finally {
            readLock.unlock();
        }

        RLock writeLock = rwLock.writeLock();
        writeLock.lock();
        try {
            if (!cacheService.incrementEnrolled(scheduleId,
                    cacheService.getSchedule(scheduleId).getCapacity()))
                return error("该课程已满员");

            cacheService.setEnrolled(studentId, scheduleId, cacheService.getSchedule(scheduleId));
            cacheService.enqueueEnrollment(studentId, scheduleId, "ENROLLED");
            return success("选课成功");
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Map<String, Object> drop(Integer enrollmentId) {
        Enrollment enrollment = enrollmentMapper.selectById(enrollmentId);
        if (enrollment == null)
            return error("选课记录不存在");
        enrollmentMapper.updateStatus(enrollmentId, "DROPPED");
        cacheService.decrementEnrolled(enrollment.getScheduleId());
        cacheService.setDropped(enrollment.getStudentId(), enrollment.getScheduleId());
        return success("退选成功");
    }

    @Override
    public List<EnrollmentDTO> myEnrollments(Integer studentId) {
        return enrollmentMapper.selectByStudentId(studentId);
    }

    @Override
    public List<EnrollmentDTO> scheduleEnrollments(Integer scheduleId) {
        return enrollmentMapper.selectByScheduleId(scheduleId);
    }

    @Override
    public List<EnrollmentDTO> scheduleEnrollmentsWithCheck(Integer scheduleId, String role, Integer referenceId) {
        if ("teacher".equals(role)) {
            CourseSchedule schedule = cacheService.getSchedule(scheduleId);
            if (schedule == null)
                throw new RuntimeException("课程安排不存在");
            if (!schedule.getTeacherId().equals(referenceId))
                throw new RuntimeException("只能查看自己教的课的选课名单");
        }
        return enrollmentMapper.selectByScheduleId(scheduleId);
    }

    private Map<String, Object> error(String msg) {
        return Map.of("success", false, "message", msg);
    }

    private Map<String, Object> success(String msg) {
        return Map.of("success", true, "message", msg);
    }
}