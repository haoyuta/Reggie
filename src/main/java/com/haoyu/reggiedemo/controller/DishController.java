package com.haoyu.reggiedemo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haoyu.reggiedemo.common.R;
import com.haoyu.reggiedemo.dto.DishDto;
import com.haoyu.reggiedemo.pojo.Category;
import com.haoyu.reggiedemo.pojo.Dish;
import com.haoyu.reggiedemo.pojo.DishFlavor;
import com.haoyu.reggiedemo.service.CategoryService;
import com.haoyu.reggiedemo.service.DishFlavorService;
import com.haoyu.reggiedemo.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * 菜品控制
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 菜品信息分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    //mybatisplus为我们提供了一个Page对象
    //特殊之处在于要回显菜品分类
    public R<Page> page(int page, int pageSize, String name) {
        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageInfo,queryWrapper);


        //对象拷贝
        //特别注意：这里不拷贝的records属性是Page类的属性，其中包含的内容为返回页面数据的集合
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        //这里取得的是菜品dish数据的集合
        List<Dish> records = pageInfo.getRecords();

        //这里设置集合list存储DishDto信息
        List<DishDto> list = new ArrayList<DishDto>();


        for (Dish record : records) {
            DishDto dishDto=new DishDto();

            BeanUtils.copyProperties(record,dishDto);

            Long categoryId = record.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            list.add(dishDto);
        }

        //将list集合存入disDtoPage对象中
        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 添加菜品信息，同时保存对应的口味数据
     * @param dishDto
     * @return
     */
    @PostMapping
    private R<String> add(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishService.saveWithFlavor(dishDto);

        return R.success("保存菜品信息成功！");
    }

    /**
     * 查询菜品信息，同时查询对应的口味数据
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> updateDish(@PathVariable Long id){

        DishDto dishDto=dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }

    /**
     * 修改菜品信息
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){

        dishService.updateWithFlavor(dishDto);

        return R.success("修改菜品成功！");
    }


    /**
     * 单个,批量更改菜品销售状态
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> changeStatus(@PathVariable int status,@RequestParam List<Long> ids){

        List<Dish> dishes=new ArrayList<>();

        for (int i = 0; i < ids.size(); i++) {

            Dish dish=new Dish();
            dish.setId(ids.get(i));
            dish.setStatus(status);
            //添加对象
            dishes.add(dish);
        }

        //调用方法
        dishService.updateBatchById(dishes);
        return R.success("状态修改成功!");
    }

    /**
     *单个,批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteDish(@RequestParam List<Long> ids){

        for (int i = 0; i < ids.size(); i++) {
            //调用方法
            dishService.removeById(ids.get(i));
        }
        return R.success("菜品删除成功!");
    }


    ///**
    // * 根据条件查询分类数据，便于套餐管理时回显菜品信息
    // * 查询菜品信息，并可通过名称查询菜品
    // * @param
    // * @return
    // */
    //@GetMapping("/list")
    ////注意这里因为得到的数据对象有多个，所以使用list集合
    ////这里使用的参数为dish对象，便于接受信息
    //public R<List<Dish>> list(Dish dish){
    //
    //    //条件构造器
    //    LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
    //
    //    //添加条件
    //    //根据条件选取对应数据
    //    //选取id
    //    queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
    //    queryWrapper.eq(Dish::getStatus,1);  //只查状态为启售的菜品
    //
    //    //选取name
    //    queryWrapper.like(dish.getName()!= null,Dish::getName,dish.getName());
    //
    //    //根据顺序升序排列，如果相同按更新数据降序排列
    //    queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
    //
    //    //用集合存储查询到的菜品信息
    //    List<Dish> list= dishService.list(queryWrapper);
    //
    //    return R.success(list);


    /**
     * 根据条件查询分类数据，便于套餐管理时回显菜品信息
     * 查询菜品信息，并可通过名称查询菜品
     * 移动端回显菜品数据
     * @param
     * @return
     */
    @GetMapping("/list")
    //注意这里因为得到的数据对象有多个，所以使用list集合
    public R<List<DishDto>> list(Dish dish){

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();

        //添加条件
        //根据条件选取对应数据
        //选取id
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);  //只查状态为启售的菜品

        //选取name
        queryWrapper.like(dish.getName()!= null,Dish::getName,dish.getName());

        //用集合存储查询到的菜品信息
        List<Dish> dishList= dishService.list(queryWrapper);

        //这里设置集合list存储DishDto信息
        List<DishDto> dishDtoList = new ArrayList<DishDto>();

        //复制对象并将口味信息存储到DishDto
        for (Dish dish1 : dishList) {
            DishDto dishDto=new DishDto();

            BeanUtils.copyProperties(dish1,dishDto);
            Long dishId = dish1.getId(); //菜品id

            LambdaQueryWrapper<DishFlavor> queryWrapper1=new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId,dishId);

            //根据id查询口味对象
            List<DishFlavor> dishFlavor = dishFlavorService.list(queryWrapper1);


            dishDto.setFlavors(dishFlavor);
            dishDtoList.add(dishDto);
        }

        return R.success(dishDtoList);
    }
}
