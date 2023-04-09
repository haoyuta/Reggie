package com.haoyu.reggiedemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoyu.reggiedemo.common.CustomException;
import com.haoyu.reggiedemo.mapper.CategoryMapper;
import com.haoyu.reggiedemo.pojo.Category;
import com.haoyu.reggiedemo.pojo.Dish;
import com.haoyu.reggiedemo.pojo.Setmeal;
import com.haoyu.reggiedemo.service.CategoryService;
import com.haoyu.reggiedemo.service.DishService;
import com.haoyu.reggiedemo.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService{

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long id) {
        //菜品部分
        //添加查询条件，根据分类id进行查询
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count1=dishService.count(dishLambdaQueryWrapper);

        if(count1>0){
            //已关联菜品，抛出一个业务异常
          throw new CustomException("当前分类下已关联菜品，无法删除！");
        }

        //套餐部分
        //添加查询条件，根据分类id进行查询
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2=setmealService.count(setmealLambdaQueryWrapper);

        if(count2>0){
            //已关联套餐，抛出一个业务异常
            throw new CustomException("已关联套餐，无法删除！");
        }

        //正常删除分类
        super.removeById(id);
    }
}


