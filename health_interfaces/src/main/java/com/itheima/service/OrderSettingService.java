package com.itheima.service;

import com.itheima.pojo.OrderSetting;

import java.util.List;
import java.util.Map;

public interface OrderSettingService {
    void save(List<OrderSetting> orderSettingList)throws Exception;

    List<Map> findByYearAndMonth(String date)throws Exception;

    void updateNumByDate(OrderSetting orderSetting)throws Exception;
}
