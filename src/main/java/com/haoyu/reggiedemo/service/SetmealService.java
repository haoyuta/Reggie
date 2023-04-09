package com.haoyu.reggiedemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haoyu.reggiedemo.dto.SetmealDto;
import com.haoyu.reggiedemo.pojo.Setmeal;
import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    //保存套餐信息并保存套餐菜品信息
    public void saveWithSetmealDish(SetmealDto setmealDto);

    //删除套餐信息并删除套餐菜品信息
    public void deleteByids(List<Long> ids);
}
