package com.hdd.studysys.schedule;

import com.hdd.studysys.entity.Enrollment;
import com.hdd.studysys.mapper.CourseScheduleMapper;
import com.hdd.studysys.mapper.EnrollmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
public class EnrolledCountSyncTask {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private CourseScheduleMapper courseScheduleMapper;
    @Autowired
    private EnrollmentMapper enrollmentMapper;

    @Scheduled(fixedRate = 1000, initialDelay = 15000)
    @Transactional
    public void sync() {
        // 1. 同步 enrolled_count 到 t_course_schedule
        Set<String> keys = redisTemplate.keys("schedule:enrolled:*");
        if (keys != null) {
            for (String key : keys) {
                Integer id = Integer.parseInt(key.replace("schedule:enrolled:", ""));
                Integer count = (Integer) redisTemplate.opsForValue().get(key);
                if (count != null)
                    courseScheduleMapper.setEnrolledCount(id, count);
            }
        }

        // 2. 同步选课记录队列到 t_enrollment
        while (true) {
            Object item = redisTemplate.opsForList().leftPop("enroll:queue");
            if (item == null)
                break;
            String[] parts = ((String) item).split(":");
            Integer sid = Integer.parseInt(parts[0]);
            Integer scid = Integer.parseInt(parts[1]);
            String status = parts[2];

            Enrollment existing = enrollmentMapper.selectByStudentAndSchedule(sid, scid);
            if (existing != null && "DROPPED".equals(existing.getStatus())) {
                enrollmentMapper.updateStatusByStudentAndSchedule(sid, scid, status);
            } else if (existing == null) {
                Enrollment e = new Enrollment();
                e.setStudentId(sid);
                e.setScheduleId(scid);
                e.setStatus(status);
                enrollmentMapper.insert(e);
            }
        }
    }
}