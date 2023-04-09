package com.haoyu.reggiedemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haoyu.reggiedemo.pojo.Category;

public interface CategoryService extends IService<Category> {

    //自定义方法，用于查询是否关联
    public void remove(Long id);
}
