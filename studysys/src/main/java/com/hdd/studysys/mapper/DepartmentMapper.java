package com.hdd.studysys.mapper;
import java.util.List;
import com.hdd.studysys.entity.Department;

public interface DepartmentMapper {
    List<Department> selectAll();

    Department selectById(Integer departmentId);

    int insert(Department department);

    int batchInsert(List<Department> departmentList);
    
    int update(Department department);
    
    int deleteById(Integer departmentId);

    List<Department> search(String departmentName);


}
