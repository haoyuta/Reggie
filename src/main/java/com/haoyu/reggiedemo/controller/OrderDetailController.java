package com.haoyu.reggiedemo.controller;

import com.haoyu.reggiedemo.common.R;
import com.haoyu.reggiedemo.pojo.OrderDetail;
import com.haoyu.reggiedemo.pojo.Orders;
import com.haoyu.reggiedemo.service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单细节
 */
@RestController
@RequestMapping("/orderdetail")
@Slf4j
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;


}
