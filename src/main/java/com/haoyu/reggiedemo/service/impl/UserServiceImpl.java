package com.haoyu.reggiedemo.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoyu.reggiedemo.mapper.UserMapper;
import com.haoyu.reggiedemo.pojo.User;
import com.haoyu.reggiedemo.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
