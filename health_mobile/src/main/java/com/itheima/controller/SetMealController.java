package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Setmeal;
import com.itheima.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import java.util.List;

@RestController
@RequestMapping("/setMeal")
public class SetMealController {
    //jedispool 获取jedis对象来操作redis
    @Autowired
    private JedisPool jedisPool;
    @Reference
    private SetMealService setMealService;

    @RequestMapping("/getAllSetmeal")
    public Result getSAllSetmeal(){
       try {
           //注意
           List<Setmeal> setmealList = setMealService.findAllSetmeal();
           return  new Result(true,MessageConstant.GET_SETMEAL_LIST_SUCCESS,setmealList);
       } catch (Exception e) {
           e.printStackTrace();
           return new Result(false,MessageConstant.GET_SETMEAL_LIST_FAIL);
       }

   }
    @RequestMapping("/findByIdDetail")
    public Result findByIdDetail(int id){
        try {
            Setmeal setmeal = setMealService.findByIdDetail(id);
            return  new Result(true,MessageConstant.QUERY_SETMEAL_SUCCESS,setmeal);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_SETMEAL_FAIL);
        }
    }

}
