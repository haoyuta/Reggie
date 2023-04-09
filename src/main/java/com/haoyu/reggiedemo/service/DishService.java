package com.haoyu.reggiedemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haoyu.reggiedemo.dto.DishDto;
import com.haoyu.reggiedemo.pojo.Dish;

public interface DishService extends IService<Dish> {

    public void saveWithFlavor(DishDto dishDto);

    public DishDto getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);
}
