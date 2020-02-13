package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Order;
import com.itheima.service.OrderService;
import com.itheima.utils.SMSUtils;
import com.itheima.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private JedisPool jedisPool;
    @Reference
    private OrderService orderService;
    @RequestMapping("/submit")
    public Result submit(@RequestBody Map map){
        //取出表单中的验证码
        String validateCode = (String) map.get("validateCode");
        String telephone = (String) map.get("telephone");
        String  orderDate = (String) map.get("orderDate");
        //取出redis中的验证码
        String redisValidateCode = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_ORDER);
        //首先判断redis中的验证码是否过期
        if (redisValidateCode==null){
            //验证码已经过期
            return new Result(false,MessageConstant.VALIDATECODE_ORVERDUE);
        }
        //验证码没有过期
        //判断验证码是否正确
        if (!redisValidateCode.equals(validateCode)){
            //验证码错误
            return new Result(false,MessageConstant.VALIDATECODE_ERROR);
        }
        //验证码通过,执行具体的订单服务
        //将订单类型传入map
        map.put("orderType", Order.ORDERTYPE_WEIXIN);
        Result result=null;
        try {
            result = orderService.orderAppointment(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //判断订单服务是否正常执行
        if (!result.isFlag()){
            //服务执行失败，返回服务执行失败result
            return result;
        }
        //预约成功，发送预约成功通知信息,返回服务执行成功result
        //生成验证码
        Integer fackCode= ValidateCodeUtils.generateValidateCode(4);
        try {
            SMSUtils.sendShortMessage(SMSUtils.ORDER_NOTICE,telephone,fackCode.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    @RequestMapping("/findById")
    public Map findById(Integer id){
        Map map = null;
        try {
            map = orderService.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
