package com.haoyu.reggiedemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haoyu.reggiedemo.pojo.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分类
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
