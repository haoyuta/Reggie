package com.haoyu.reggiedemo.dto;

import com.haoyu.reggiedemo.pojo.DishFlavor;
import com.haoyu.reggiedemo.pojo.Setmeal;
import com.haoyu.reggiedemo.pojo.SetmealDish;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据传输对象，用于封装页面提交的数据
 * 套餐及套餐菜品
 */
@Data
public class SetmealDto extends Setmeal {

    //套餐菜品信息属性
    private List<SetmealDish> setmealDishes = new ArrayList<>();

    private String categoryName;

}

