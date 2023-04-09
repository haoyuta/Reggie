package com.haoyu.reggiedemo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haoyu.reggiedemo.common.R;
import com.haoyu.reggiedemo.dto.OrdersDto;
import com.haoyu.reggiedemo.pojo.Orders;
import com.haoyu.reggiedemo.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;


    /**
     * 下订单支付
     * @return
     */
    @PostMapping("/submit")
    @Transactional
    public R<String> submit(@RequestBody Orders orders){

        orderService.submit(orders);

        return R.success("支付成功！");
    }


    /**
     * 移动端用户页面展示最新或历史订单
     * @return
     */
    @GetMapping("/userPage")
    public R<Page<OrdersDto>> userPage(int page, int pageSize,String name){

        return R.success(orderService.userPage(page,pageSize));
    }

    /**
     * pc端展示最新或历史订单
     * @return
     */
    @GetMapping("/page")
    public R<Page<OrdersDto>> pcOrderPage(int page, int pageSize){

        return R.success(orderService.pcOrderPage(page,pageSize));
    }
}




