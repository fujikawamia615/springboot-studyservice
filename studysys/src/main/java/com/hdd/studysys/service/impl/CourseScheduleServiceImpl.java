package com.hdd.studysys.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hdd.studysys.entity.CourseSchedule;
import com.hdd.studysys.mapper.CourseScheduleMapper;
import com.hdd.studysys.service.CourseScheduleService;
import com.hdd.studysys.dto.CourseScheduleDTO;
@Service
public class CourseScheduleServiceImpl implements CourseScheduleService {
    @Autowired
    private CourseScheduleMapper courseScheduleMapper;

    @Override
    public List<CourseSchedule> selectAll() {
        return courseScheduleMapper.selectAll();
    }

    @Override
    public CourseSchedule selectById(Integer scheduleId) {
        return courseScheduleMapper.selectById(scheduleId);
    }

    @Override
    public int insert(CourseSchedule courseSchedule) {
        return courseScheduleMapper.insert(courseSchedule);
    }

    @Override
    public int update(CourseSchedule courseSchedule) {
        return courseScheduleMapper.update(courseSchedule);
    }

    @Override
    public int deleteById(Integer scheduleId) {
        return courseScheduleMapper.deleteById(scheduleId);
    }

    @Override
    public List<CourseScheduleDTO> selectAllWithNames() {
        return courseScheduleMapper.selectAllWithNames();
    }

    @Override
    public List<CourseScheduleDTO> selectByTeacherId(Integer teacherId) {
        return courseScheduleMapper.selectByTeacherId(teacherId);
    }
}