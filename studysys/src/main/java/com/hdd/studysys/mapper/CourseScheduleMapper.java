package com.hdd.studysys.mapper;

import java.util.List;
import com.hdd.studysys.entity.CourseSchedule;

public interface CourseScheduleMapper {
    List<CourseSchedule> selectAll();

    CourseSchedule selectById(Integer scheduleId);

    int insert(CourseSchedule courseSchedule);

    int update(CourseSchedule courseSchedule);

    int deleteById(Integer scheduleId);

    int updateEnrolledCount(Integer scheduleId, int delta);
}