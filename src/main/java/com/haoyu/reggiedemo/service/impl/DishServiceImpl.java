package com.haoyu.reggiedemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoyu.reggiedemo.dto.DishDto;
import com.haoyu.reggiedemo.mapper.DishMapper;
import com.haoyu.reggiedemo.pojo.Dish;
import com.haoyu.reggiedemo.pojo.DishFlavor;
import com.haoyu.reggiedemo.service.DishFlavorService;
import com.haoyu.reggiedemo.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishService dishService;

    /**
     * 新增菜品，同时保存对应的口味数据
     *
     * @param dishDto
     */
    @Override
    @Transactional  //涉及到多张表，开启事务,要么全成功,要么全失败
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品基本信息到菜品表dish
        this.save(dishDto);

        Long dishId = dishDto.getId();//菜品id

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();

        //遍历对象集合，并将菜品id赋给dishFlavor对象
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }

        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }


    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @Override
    @Transactional
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        //复制对象
        BeanUtils.copyProperties(dish, dishDto);

        //查询当前菜品对应口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    /**
     * 更新菜品，同时更新对应的口味数据
     *
     * @param dishDto
     */
    @Override
    @Transactional  //涉及到多张表，开启事务
    public void updateWithFlavor(DishDto dishDto) {
        //更新菜品基本信息到菜品表dish
        dishService.updateById(dishDto);

        Long dishId = dishDto.getId();//菜品id

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();

        //遍历对象集合，并将菜品id赋给dishFlavor对象
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }

        //更新菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.updateBatchById(flavors);
    }

}