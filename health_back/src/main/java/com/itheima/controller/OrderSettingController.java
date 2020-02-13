package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.OrderSetting;
import com.itheima.service.OrderSettingService;
import com.itheima.utils.POIUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ordersetting")
public class OrderSettingController {
    @Reference
    private OrderSettingService orderSettingService;
   //文件上传
    @RequestMapping("/upload")
    public Result upload(MultipartFile excelFile){
        try {
            //工具类解析excel表格返回list集合
            List<String[]> list = POIUtils.readExcel(excelFile);
            //controller中应该将前台数据做简单处理后存入数据库（保存的话要么是一个对象，要么是对象集合）
            List<OrderSetting> orderSettingList = new ArrayList<>();
            //遍历list拿到每一行数据,封装后存入List集合
            for (String[] row : list) {
                //日期
                String date = row[0];
                //可预约人数
                String number = row[1];
                OrderSetting orderSetting = new OrderSetting(new Date(date),Integer.parseInt(number));
                orderSettingList.add(orderSetting);
            }
            orderSettingService.save(orderSettingList);
            return new Result(true,MessageConstant.IMPORT_ORDERSETTING_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.IMPORT_ORDERSETTING_FAIL);
        }
    }

   @RequestMapping("/findByYearAndMonth")
    public Result findByYearAndMonth(String date){
       try {
           List<Map> list =  orderSettingService.findByYearAndMonth(date);
           //查询成功
            return  new Result(true,MessageConstant.GET_ORDERSETTING_SUCCESS,list);

       } catch (Exception e) {
           //查询失败
           return new Result(false,MessageConstant.GET_ORDERSETTING_FAIL);
       }
   }
   @PreAuthorize("hasAuthority('ORDERSETTING')")
    @RequestMapping("/updateNumByDate")
    //springmvc为我们提供了一个简单日期转换格式必须指定为2019-12-11格式，所以这里可以直接来接
    public Result updateNumByDate(@RequestBody OrderSetting orderSetting){
        try {
            orderSettingService.updateNumByDate(orderSetting);
            //查询成功
            return  new Result(true,MessageConstant.ORDERSETTING_SUCCESS);

        } catch (Exception e) {
            //查询失败
            return new Result(false,MessageConstant.ORDERSETTING_FAIL);
        }
    }

}
