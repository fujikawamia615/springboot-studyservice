package com.hdd.studysys.service;

import java.util.List;
import com.hdd.studysys.entity.CourseSchedule;

public interface CourseScheduleService {
    List<CourseSchedule> selectAll();

    CourseSchedule selectById(Integer scheduleId);

    int insert(CourseSchedule courseSchedule);

    int update(CourseSchedule courseSchedule);

    int deleteById(Integer scheduleId);
}