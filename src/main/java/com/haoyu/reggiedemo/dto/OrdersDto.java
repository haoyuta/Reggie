package com.haoyu.reggiedemo.dto;

import com.haoyu.reggiedemo.pojo.OrderDetail;
import com.haoyu.reggiedemo.pojo.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
