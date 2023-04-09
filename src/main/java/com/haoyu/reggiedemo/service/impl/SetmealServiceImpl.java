package com.haoyu.reggiedemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoyu.reggiedemo.common.CustomException;
import com.haoyu.reggiedemo.dto.SetmealDto;
import com.haoyu.reggiedemo.mapper.SetmealMapper;
import com.haoyu.reggiedemo.pojo.Setmeal;
import com.haoyu.reggiedemo.pojo.SetmealDish;
import com.haoyu.reggiedemo.service.SetmealDishService;
import com.haoyu.reggiedemo.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 新增套餐，同时保存对应的套餐菜品数据
     *
     * @param setmealDto
     */
    @Override
    @Transactional  //涉及到多张表，开启事务
    public void saveWithSetmealDish(SetmealDto setmealDto) {
        //保存套餐基本信息到套餐表setmeal
        this.save(setmealDto);

        Long setmealId = setmealDto.getId();//套餐id

        //套餐菜品种类
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        //遍历套餐菜品种类，添加套餐id
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }

        //保存套餐菜品数据到套餐菜品表setmeal_dish
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public void deleteByids(List<Long> ids){
        for (Long id : ids) {
            int status=setmealService.getById(id).getStatus();
            //套餐停售，可删除
            if(status==0) {
                //先删除套餐关联的套餐菜品
                LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(SetmealDish::getSetmealId, id);
                setmealDishService.remove(queryWrapper);

                //再删除套餐
                setmealService.removeById(id);
            }
            else{
                //套餐在售，抛出一个业务异常
                throw new CustomException("套餐在售，无法删除！");
            }
        }
    }
}

