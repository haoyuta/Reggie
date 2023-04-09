package com.haoyu.reggiedemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoyu.reggiedemo.mapper.ShoppingCartMapper;
import com.haoyu.reggiedemo.pojo.ShoppingCart;
import com.haoyu.reggiedemo.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
