package com.haoyu.reggiedemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoyu.reggiedemo.mapper.OrderDetailMapper;
import com.haoyu.reggiedemo.pojo.OrderDetail;
import com.haoyu.reggiedemo.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * 订单细节
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
