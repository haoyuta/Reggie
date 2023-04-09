package com.haoyu.reggiedemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haoyu.reggiedemo.pojo.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 */
@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}
