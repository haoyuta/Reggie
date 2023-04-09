package com.haoyu.reggiedemo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haoyu.reggiedemo.dto.OrdersDto;
import com.haoyu.reggiedemo.pojo.Orders;

import java.util.List;
public interface OrderService extends IService<Orders> {

    //用户下单支付
    public void submit(Orders orders);

    //移动端用户页面展示最新或历史订单
    public Page<OrdersDto> userPage(int page, int pageSize);

    //pc端用户页面展示最新或历史订单
    public Page<OrdersDto> pcOrderPage(int page, int pageSize);


}
