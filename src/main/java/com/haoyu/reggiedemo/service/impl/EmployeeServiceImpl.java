package com.haoyu.reggiedemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoyu.reggiedemo.mapper.EmployeeMapper;
import com.haoyu.reggiedemo.pojo.Employee;
import com.haoyu.reggiedemo.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
