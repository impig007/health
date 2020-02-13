package com.itheima.mapper;

import com.itheima.pojo.OrderSetting;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrderSettingMapper {

    //根据日期查询记录数做判断
    long findCountByOrderDate(Date orderDate)throws Exception;
    //根据日期更新预约设置
    void updataByOrderDate(OrderSetting orderSetting)throws Exception;
    //添加预约设置
    void save(OrderSetting orderSetting)throws Exception;
    //根据年月查询预约设置
    List<OrderSetting> findByYearAndMonth(Map<String, String> map)throws Exception;

    //根据日期用以查询数据做界面显示
    public OrderSetting findByOrderDate(Date orderDate)throws Exception;


}
