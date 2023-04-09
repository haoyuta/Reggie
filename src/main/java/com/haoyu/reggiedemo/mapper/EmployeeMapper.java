package com.haoyu.reggiedemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haoyu.reggiedemo.pojo.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * 员工
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
