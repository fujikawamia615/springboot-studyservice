package com.hdd.studysys.schedule;

import com.hdd.studysys.entity.Enrollment;
import com.hdd.studysys.mapper.CourseScheduleMapper;
import com.hdd.studysys.mapper.EnrollmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class EnrolledCountSyncTask {
    private static final String QUEUE_KEY = "enroll:queue";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private CourseScheduleMapper courseScheduleMapper;
    @Autowired
    private EnrollmentMapper enrollmentMapper;

    @Scheduled(fixedRate = 1000, initialDelay = 15000)
    public void sync() {
        syncEnrolledCount();

        // 2. 同步选课记录队列到 t_enrollment
        while(true){
            String item = (String) redisTemplate.opsForList().index(QUEUE_KEY, 0);
            if (item == null) break;
            saveEnrollment(item);
            redisTemplate.opsForList().leftPop(QUEUE_KEY);
        }
    }

    // 1. 同步 enrolled_count 到 t_course_schedule（覆盖写，重复执行无副作用）
    private void syncEnrolledCount() {
        Set<String> keys = redisTemplate.keys("schedule:enrolled:*");
        if (keys != null) {
            for (String key : keys) {
                Integer id = Integer.parseInt(key.replace("schedule:enrolled:", ""));
                Integer count = (Integer) redisTemplate.opsForValue().get(key);
                if (count != null)
                    courseScheduleMapper.setEnrolledCount(id, count);
            }
        }
    }

    // 已存在且为 ENROLLED 时什么都不做，因此同一条消息处理两次结果不变（幂等）
    private void saveEnrollment(String item) {
        String[] parts = item.split(":");
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
