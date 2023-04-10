package com.haoyu.reggiedemo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haoyu.reggiedemo.common.R;
import com.haoyu.reggiedemo.dto.SetmealDto;
import com.haoyu.reggiedemo.pojo.*;
import com.haoyu.reggiedemo.service.CategoryService;
import com.haoyu.reggiedemo.service.SetmealDishService;
import com.haoyu.reggiedemo.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    //缓存
    @Autowired
    private CacheManager cacheManager;

    /**
     * 添加套餐信息，同时保存对应的套餐菜品数据
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    //添加套餐信息成功后删除相应的缓存数据
    @CacheEvict(value = "setmealCache",key = "#setmealDto.categoryId+'_1'")
    public R<String> add(@RequestBody SetmealDto setmealDto) {
        log.info("套餐信息：{}", setmealDto);

        setmealService.saveWithSetmealDish(setmealDto);

        return R.success("保存套餐信息成功！");
    }


    /**
     * 套餐信息分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    //mybatisplus为我们提供了一个Page对象
    //特殊之处在于要回显套餐分类
    public R<Page> page(int page, int pageSize, String name) {
        //构造分页构造器对象
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null, Setmeal::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        //执行分页查询
        setmealService.page(setmealPage, queryWrapper);

        //对象拷贝
        //特别注意：这里不拷贝的records属性是Page类的属性，其中包含的内容为返回页面数据的集合
        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");

        //这里取得的是菜品dish数据的集合
        List<Setmeal> records = setmealPage.getRecords();

        //这里设置集合list存储SetmealDto信息
        List<SetmealDto> list = new ArrayList<SetmealDto>();

        for (Setmeal record : records) {
            SetmealDto setmealDto = new SetmealDto();

            BeanUtils.copyProperties(record, setmealDto);

            Long categoryId = record.getCategoryId();//分类id

            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }

            list.add(setmealDto);
        }

        //将list集合存入disDtoPage对象中
        setmealDtoPage.setRecords(list);

        return R.success(setmealDtoPage);
    }

    /**
     * 单个,批量删除套餐
     * 注意，要考虑售卖中的套餐不能删除，需要先停售
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    //删除套餐信息成功后删除setmealCache所有缓存数据
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> deleteSetmeal(@RequestParam List<Long> ids) {
        //调用自定义方法
        setmealService.deleteByids(ids);

        return R.success("套餐删除成功!");
    }

    /**
     * 单个,批量更改套餐销售状态
     *
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    //更改status成功后删除setmealCache所有缓存数据
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> changeStatus(@PathVariable int status, @RequestParam List<Long> ids) {

        List<Setmeal> setmeals = new ArrayList<>();

        for (int i = 0; i < ids.size(); i++) {

            Setmeal setmeal = new Setmeal();
            setmeal.setId(ids.get(i));
            setmeal.setStatus(status);
            //添加对象
            setmeals.add(setmeal);
        }

        //调用方法
        setmealService.updateBatchById(setmeals);
        return R.success("状态修改成功!");
    }

/**
 * 移动端回显套餐信息
 */
    @GetMapping("/list")
    @Cacheable(value = "setmealCache",key = "#setmeal.categoryId+'_'+#setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal){

            //条件构造器
            LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();

            //添加条件
            //根据条件选取对应数据
            //选取id
            queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
            queryWrapper.eq(Setmeal::getStatus,1);  //只查状态为启售的套餐

            //根据顺序升序排列，如果相同按更新数据降序排列
            queryWrapper.orderByDesc(Setmeal::getUpdateTime);

            //用集合存储查询到的菜品信息
            List<Setmeal> list= setmealService.list(queryWrapper);

            return R.success(list);
    }
}

