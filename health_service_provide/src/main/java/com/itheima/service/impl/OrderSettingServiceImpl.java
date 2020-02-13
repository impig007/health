package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.mapper.OrderSettingMapper;
import com.itheima.pojo.OrderSetting;
import com.itheima.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Service(interfaceClass = OrderSettingService.class)
public class OrderSettingServiceImpl implements OrderSettingService{
    @Autowired
    private OrderSettingMapper orderSettingMapper;
    @Override
    public void save(List<OrderSetting> orderSettingList) throws Exception {
        //遍历集合
        if (orderSettingList!=null && orderSettingList.size()>0){
            for (OrderSetting orderSetting : orderSettingList) {
                //首先查询看表中有无该条记
                long count = orderSettingMapper.findCountByOrderDate(orderSetting.getOrderDate());
                if (count>0){
                    //存在该条记录,更新
                    orderSettingMapper.updataByOrderDate(orderSetting);
                }else {
                    //该条记录不存在，插入
                    orderSettingMapper.save(orderSetting);
                }
            }
        }
    }

    @Override
    public List<Map> findByYearAndMonth(String date) throws Exception {//2019-7
        String begin = date+"-1";
        String end=date+"-31";
        //封装查询条件
        Map<String,String> map = new HashMap();
        map.put("begin",begin);
        map.put("end",end);
        //该时间段内是否有预约设置
        List<OrderSetting> orderSettingList = orderSettingMapper.findByYearAndMonth(map);
        //封装查询结果,结果集合样式 date:1,number:200,reservations:200形式
        List<Map> resultList = new ArrayList<>();
        if (orderSettingList!=null && orderSettingList.size()>0){
            //该段时间存在预约设置，将预约设置以指定格式封装存入集合，用以界面展示
            for (OrderSetting orderSetting : orderSettingList) {
                Map<String,Object> resut = new HashMap<>();
                resut.put("date",orderSetting.getOrderDate().getDate());//获取日期中的日
                resut.put("number",orderSetting.getNumber());
                resut.put("reservations",orderSetting.getReservations());
                resultList.add(resut);
            }
        }
        return resultList;
    }

    @Override
    public void updateNumByDate(OrderSetting orderSetting) throws Exception {
        //判断此日期是否是已经设置过的(excel设置过的表中有记录)
        long count = orderSettingMapper.findCountByOrderDate(orderSetting.getOrderDate());
        if (count>0){
            //已经设置过了，更新
            orderSettingMapper.updataByOrderDate(orderSetting);
            return;
        }
        //没有设置过，插入
        orderSettingMapper.save(orderSetting);

    }
}
