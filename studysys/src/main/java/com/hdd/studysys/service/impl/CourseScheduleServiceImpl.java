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
    @Autowired
    private CourseScheduleCacheService cacheService;

    @Override
    public List<CourseSchedule> selectAll() {
        List<CourseSchedule> list = courseScheduleMapper.selectAll();
        list.forEach(cacheService::warmUp);
        return list;
    }

    @Override
    public CourseSchedule selectById(Integer scheduleId) {
        return cacheService.getSchedule(scheduleId);
    }

    @Override
    public int insert(CourseSchedule courseSchedule) {
        int r = courseScheduleMapper.insert(courseSchedule);
        cacheService.warmUp(courseSchedule);
        return r;
    }

    @Override
    public int update(CourseSchedule courseSchedule) {
        int r = courseScheduleMapper.update(courseSchedule);
        cacheService.evictInfo(courseSchedule.getScheduleId());
        return r;
    }

    @Override
    public int deleteById(Integer scheduleId) {
        cacheService.evictInfo(scheduleId);
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