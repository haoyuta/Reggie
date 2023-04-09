package com.haoyu.reggiedemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haoyu.reggiedemo.pojo.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
