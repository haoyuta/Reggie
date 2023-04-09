package com.haoyu.reggiedemo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haoyu.reggiedemo.common.R;
import com.haoyu.reggiedemo.pojo.Category;
import com.haoyu.reggiedemo.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 分类
 */
@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> add(@RequestBody Category category){
        log.info("新增分类信息:{}",category.toString());

        categoryService.save(category);
        return R.success("添加分类成功");
    }

    /**
     * 分类信息分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    //mybatisplus为我们提供了一个Page对象
    public R<Page> page(int page, int pageSize){
        log.info("page={},pageSize={}",page,pageSize);

        //构建分页构造器
        Page pageInfo=new Page(page,pageSize);

        //构建条件构造器
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();

        //添加排序条件,通过分类位置进行排升序序
        queryWrapper.orderByAsc(Category::getSort);

        //执行查询，根据条件查询
        categoryService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 根据id删除分类信息
     * 特别注意：这里使用的remove()是自定义方法，直接定义在service下，还可在自定义方法中套用mybatisplus的方法，以达到扩展方法的作用
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteByid(Long ids){
        log.info("删除分类id:{}",ids);
        categoryService.remove(ids);
        return R.success("删除分类信息成功");

    }

    /**
     * 根据id修改状态信息
     * @param category
     * @param request
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category, HttpServletRequest request){
        log.info(category.toString());

        ////更新时间
        //category.setUpdateTime(LocalDateTime.now());
        ////更新修改的管理员Id
        //Long empId= (Long)request.getSession().getAttribute("employee");
        //employee.setUpdateUser(empId);

        //更新用户数据
        categoryService.updateById(category);

        return R.success("状态信息修改成功");
    }

    /**
     * 根据条件查询分类数据，便于菜品或套餐管理添加分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    //注意这里可以用对象作为参数，因为传的不是json数据，所以不用加注解
    //注意这里因为得到的数据对象有多个，所以使用list集合
    public R<List<Category>> list(Category category){
        log.info(category.toString());

        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();

        //添加条件
        //先判断传过来的数据是否为空，然后选取对应数据
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());

        //先按数据库数据顺序升序排列，如果相同，就按更新数据降序排列
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list=categoryService.list(queryWrapper);

        return R.success(list);
    }
}
