package com.haoyu.reggiedemo.dto;


import com.haoyu.reggiedemo.pojo.Dish;
import com.haoyu.reggiedemo.pojo.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;


/**
 * 数据传输对象，用于封装页面提交的数据
 * 菜品及口味
 */
@Data
public class DishDto extends Dish {

    //口味属性
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
