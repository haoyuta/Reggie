package com.haoyu.reggiedemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoyu.reggiedemo.common.BaseContext;
import com.haoyu.reggiedemo.common.CustomException;
import com.haoyu.reggiedemo.common.R;
import com.haoyu.reggiedemo.dto.OrdersDto;
import com.haoyu.reggiedemo.dto.SetmealDto;
import com.haoyu.reggiedemo.mapper.OrderMapper;
import com.haoyu.reggiedemo.pojo.*;
import com.haoyu.reggiedemo.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * 订单
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;


    /**
     * 用户下单
     *
     * @param orders
     */
    @Override
    @Transactional
    public void submit(Orders orders) {
        //获取当前用户
        Long userId = BaseContext.getCurrentId();

        //查询当前的购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);

        if (shoppingCarts == null || shoppingCarts.size() == 0) {
            throw new CustomException("购物车为空，不能下单!");
        }

        //查询用户数据
        User user = userService.getById(userId);

        //查询地址数据
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if (addressBook == null) {
            throw new CustomException("用户地址信息有无，不能下单!");
        }

        //设置订单号,由mybatis-plus提供
        long orderId = IdWorker.getId();

        //代替Integer,保证线程安全
        AtomicInteger amount = new AtomicInteger(0);

        //设置订单明细数据
        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());


        //向订单表插入数据，一条数据
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        //向订单表插入数据，一条数据
        this.save(orders);

        //向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);

        //清空购物车数据
        shoppingCartService.remove(queryWrapper);
    }


    /**
     * 移动端用户页面展示最新或历史订单
     *
     * @param page
     * @param pageSize
     */
    @Override
    public Page<OrdersDto> userPage(int page, int pageSize) {
        //获取当前用户Id
        Long userId = BaseContext.getCurrentId();

        //构造分页构造器对象
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.eq(Orders::getUserId, userId);
        //添加排序条件
        queryWrapper.orderByDesc(Orders::getOrderTime);

        //执行分页查询
        this.page(ordersPage, queryWrapper);

        //对象拷贝
        //特别注意：这里不拷贝的records属性是Page类的属性，其中包含的内容为返回页面数据的集合
        BeanUtils.copyProperties(ordersPage, ordersDtoPage, "records");

        //这里取得的是订单orders数据的集合
        List<Orders> orders = ordersPage.getRecords();

        //这里设置集合list存储OrdersDto信息
        List<OrdersDto> ordersDtos = new ArrayList<OrdersDto>();

        //一个用户有一至多个订单，遍历订单
        for (Orders order : orders) {
            OrdersDto ordersDto = new OrdersDto();

            BeanUtils.copyProperties(order, ordersDto);

            Long orderId = order.getId();//订单id

            //根据id查询订单明细对象
            LambdaQueryWrapper<OrderDetail> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(OrderDetail::getOrderId, orderId);
            List<OrderDetail> orderDetail = orderDetailService.list(queryWrapper1);

            ordersDto.setOrderDetails(orderDetail);

            ordersDtos.add(ordersDto);
        }

        //将list集合存入disDtoPage对象中
        ordersDtoPage.setRecords(ordersDtos);

        return ordersDtoPage;
    }

    /**
     * pc端管理员页面展示最新或历史订单
     *因为是管理，所有先查询移动端所有用户，遍历每一个用户，获取所有订单并展示
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Page<OrdersDto> pcOrderPage(int page, int pageSize) {
        //获取所有用户id
        LambdaQueryWrapper<User> queryWrapper3 = new LambdaQueryWrapper<>();
        queryWrapper3.eq(User::getStatus, 1);
        List<User> userList = userService.list(queryWrapper3);

        //构造分页构造器对象
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();

        for (User user : userList) {
            //获取用户id
            Long userId = user.getId();

            //条件构造器
            LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
            //添加过滤条件
            queryWrapper.eq(Orders::getUserId, userId);
            //添加排序条件
            queryWrapper.orderByDesc(Orders::getOrderTime);

            //执行分页查询
            this.page(ordersPage, queryWrapper);

            //对象拷贝
            //特别注意：这里不拷贝的records属性是Page类的属性，其中包含的内容为返回页面数据的集合
            BeanUtils.copyProperties(ordersPage, ordersDtoPage, "records");

            //这里取得的是订单orders数据的集合
            List<Orders> orders = ordersPage.getRecords();

            //这里设置集合list存储OrdersDto信息
            List<OrdersDto> ordersDtos = new ArrayList<OrdersDto>();

            //一个用户有一至多个订单，遍历订单
            for (Orders order : orders) {
                OrdersDto ordersDto = new OrdersDto();

                BeanUtils.copyProperties(order, ordersDto);

                Long orderId = order.getId();//订单id

                //根据id查询订单明细对象
                LambdaQueryWrapper<OrderDetail> queryWrapper1 = new LambdaQueryWrapper<>();
                queryWrapper1.eq(OrderDetail::getOrderId, orderId);
                List<OrderDetail> orderDetail = orderDetailService.list(queryWrapper1);

                ordersDto.setOrderDetails(orderDetail);

                ordersDtos.add(ordersDto);
            }

            //将list集合存入disDtoPage对象中
            ordersDtoPage.setRecords(ordersDtos);
        }

        return ordersDtoPage;
    }
}


