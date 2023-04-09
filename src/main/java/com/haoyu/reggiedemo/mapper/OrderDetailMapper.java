package com.haoyu.reggiedemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haoyu.reggiedemo.pojo.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单明细
 */
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
