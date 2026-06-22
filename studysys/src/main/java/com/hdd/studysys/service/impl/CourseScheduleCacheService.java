package com.hdd.studysys.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.hdd.studysys.dto.EnrollmentDTO;
import com.hdd.studysys.entity.CourseSchedule;
import com.hdd.studysys.mapper.CourseScheduleMapper;

import jakarta.annotation.PostConstruct;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class CourseScheduleCacheService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private CourseScheduleMapper courseScheduleMapper;
    @Autowired
    private com.hdd.studysys.mapper.EnrollmentMapper enrollmentMapper;

    @PostConstruct
    public void clearAll() {
        // 临时改成 *，重启一次清掉所有数据
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null)
            redisTemplate.delete(keys);
    }

    private static final String INFO_KEY = "schedule:info:";
    private static final String ENROLLED_KEY = "schedule:enrolled:";
    private static final String STUDENT_SCHEDULES = "student:schedules:";
    private static final String STUDENT_STATUS = "student:status:";

    public CourseSchedule getSchedule(Integer scheduleId) {
        CourseSchedule schedule = (CourseSchedule) redisTemplate.opsForValue().get(INFO_KEY + scheduleId);
        if (schedule == null) {
            schedule = courseScheduleMapper.selectById(scheduleId);
            if (schedule != null) {
                CourseSchedule cache = new CourseSchedule();
                BeanUtils.copyProperties(schedule, cache, "enrolledCount");
                redisTemplate.opsForValue().set(INFO_KEY + scheduleId, cache, 1, TimeUnit.HOURS);
            }
        }
        if (schedule != null) {
            Integer count = (Integer) redisTemplate.opsForValue().get(ENROLLED_KEY + scheduleId);
            if (count == null) {
                count = courseScheduleMapper.selectById(scheduleId).getEnrolledCount();
                count = count != null ? count : 0;
                redisTemplate.opsForValue().set(ENROLLED_KEY + scheduleId, count);
            }
            schedule.setEnrolledCount(count);
        }
        return schedule;
    }

    public boolean incrementEnrolled(Integer scheduleId, Integer capacity) {
        String key = ENROLLED_KEY + scheduleId;
        if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
            CourseSchedule s = courseScheduleMapper.selectById(scheduleId);
            int count = s != null && s.getEnrolledCount() != null ? s.getEnrolledCount() : 0;
            redisTemplate.opsForValue().set(key, count);
        }
        Long enrolled = redisTemplate.opsForValue().increment(key);
        if (enrolled != null && enrolled > capacity) {
            redisTemplate.opsForValue().decrement(key);
            return false;
        }
        return true;
    }

    public void decrementEnrolled(Integer scheduleId) {
        String key = ENROLLED_KEY + scheduleId;
        if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
            CourseSchedule s = courseScheduleMapper.selectById(scheduleId);
            int count = s != null && s.getEnrolledCount() != null ? s.getEnrolledCount() : 0;
            redisTemplate.opsForValue().set(key, count);
        }
        redisTemplate.opsForValue().decrement(key);
    }

    public void warmUp(CourseSchedule s) {
        String key = INFO_KEY + s.getScheduleId();
        if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
            CourseSchedule cache = new CourseSchedule();
            BeanUtils.copyProperties(s, cache, "enrolledCount");
            redisTemplate.opsForValue().set(key, cache, 1, TimeUnit.HOURS);
        }
    }

    public boolean isEnrolled(Integer studentId, Integer scheduleId) {
        Object status = redisTemplate.opsForHash().get(STUDENT_STATUS + studentId, scheduleId.toString());
        if (status != null)
            return "ENROLLED".equals(status);
        // Redis无数据，从DB加载
        loadStudentEnrollments(studentId);
        return "ENROLLED".equals(
                redisTemplate.opsForHash().get(STUDENT_STATUS + studentId, scheduleId.toString()));
    }

    public String getEnrollStatus(Integer studentId, Integer scheduleId) {
        Object status = redisTemplate.opsForHash().get(STUDENT_STATUS + studentId, scheduleId.toString());
        if (status != null)
            return (String) status;
        loadStudentEnrollments(studentId);
        status = redisTemplate.opsForHash().get(STUDENT_STATUS + studentId, scheduleId.toString());
        return status != null ? (String) status : null;
    }

    public void setEnrolled(Integer studentId, Integer scheduleId, CourseSchedule s) {
        redisTemplate.opsForHash().put(STUDENT_STATUS + studentId, scheduleId.toString(), "ENROLLED");
        redisTemplate.opsForHash().put(STUDENT_SCHEDULES + studentId,
                scheduleId.toString(), s.getDayOfWeek() + "," + s.getStartPeriod() + "," + s.getEndPeriod());
    }

    public void loadStudentEnrollments(Integer studentId) {
        List<EnrollmentDTO> list = enrollmentMapper.selectByStudentId(studentId);
        for (EnrollmentDTO dto : list) {
            redisTemplate.opsForHash().put(STUDENT_STATUS + studentId,
                    dto.getScheduleId().toString(), "ENROLLED");
            redisTemplate.opsForHash().put(STUDENT_SCHEDULES + studentId,
                    dto.getScheduleId().toString(),
                    dto.getDayOfWeek() + "," + dto.getStartPeriod() + "," + dto.getEndPeriod());
        }
    }

    // 替换原 removeEnrolled 方法
    public void setDropped(Integer studentId, Integer scheduleId) {
        redisTemplate.opsForHash().put(STUDENT_STATUS + studentId, scheduleId.toString(), "DROPPED");
        redisTemplate.opsForHash().delete(STUDENT_SCHEDULES + studentId, scheduleId.toString());
    }

    public boolean checkTimeConflict(Integer studentId, Integer dayOfWeek, Integer start, Integer end) {
        Map<Object, Object> map = redisTemplate.opsForHash().entries(STUDENT_SCHEDULES + studentId);
        for (var entry : map.entrySet()) {
            String[] p = ((String) entry.getValue()).split(",");
            int d = Integer.parseInt(p[0]), s = Integer.parseInt(p[1]), e = Integer.parseInt(p[2]);
            if (d == dayOfWeek && s < end && e > start)
                return true;
        }
        return false;
    }

    public void evictInfo(Integer scheduleId) {
        redisTemplate.delete(INFO_KEY + scheduleId);
    }

    public void enqueueEnrollment(Integer studentId, Integer scheduleId, String status) {
        redisTemplate.opsForList().rightPush("enroll:queue",
                studentId + ":" + scheduleId + ":" + status);
    }
}