package com.hdd.studysys.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import com.hdd.studysys.mapper.DepartmentMapper;
import com.hdd.studysys.service.DepartmentService;
import com.hdd.studysys.entity.Department;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentMapper departmentMapper;

    @Override
    public List<Department> selectAll() {
        return departmentMapper.selectAll();
    }

    @Override
    public Department selectById(Integer departmentId) {
        return departmentMapper.selectById(departmentId);
    }

    @Override
    public int insert(Department department) {
        return departmentMapper.insert(department);
    }

    @Override
    public int batchInsert(List<Department> departmentList) {
        return departmentMapper.batchInsert(departmentList);
    }

    @Override
    public int update(Department department) {
        return departmentMapper.update(department);
    }

    @Override
    public int deleteById(Integer departmentId) {
        return departmentMapper.deleteById(departmentId);
    }

    @Override
    public List<Department> search(String departmentName) {
        return departmentMapper.search(departmentName);
    }
}
