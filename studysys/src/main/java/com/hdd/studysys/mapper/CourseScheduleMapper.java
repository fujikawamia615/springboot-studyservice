package com.hdd.studysys.mapper;

import java.util.List;
import com.hdd.studysys.entity.CourseSchedule;
import com.hdd.studysys.dto.CourseScheduleDTO;

public interface CourseScheduleMapper {
    List<CourseSchedule> selectAll();

    CourseSchedule selectById(Integer scheduleId);

    CourseSchedule selectByIdForUpdate(Integer scheduleId);
    
    int insert(CourseSchedule courseSchedule);

    int update(CourseSchedule courseSchedule);

    int deleteById(Integer scheduleId);

    int updateEnrolledCount(Integer scheduleId, int delta);

    List<CourseScheduleDTO> selectAllWithNames();

    List<CourseScheduleDTO> selectByTeacherId(Integer teacherId);
}