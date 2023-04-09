package com.haoyu.reggiedemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoyu.reggiedemo.mapper.AddressBookMapper;
import com.haoyu.reggiedemo.pojo.AddressBook;
import com.haoyu.reggiedemo.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
