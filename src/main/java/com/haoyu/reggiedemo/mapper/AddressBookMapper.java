package com.haoyu.reggiedemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haoyu.reggiedemo.pojo.AddressBook;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收货地址
 */
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
