package com.itheima.controller;

import com.aliyuncs.exceptions.ClientException;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.utils.SMSUtils;
import com.itheima.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

@RestController
@RequestMapping("/validateCode")
public class ValidateCodeController {
    @Autowired
    private JedisPool jedisPool;
    @RequestMapping("/send4Order")
    public Result send4Order(String telephone){
        //生成验证码
        Integer validateCode = ValidateCodeUtils.generateValidateCode(4);
        //调用工具类发送短信
        try {
            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,telephone,validateCode.toString());
        } catch (ClientException e) {
            e.printStackTrace();
            //发送失败
            return new Result(false, MessageConstant.SEND_VALIDATECODE_SUCCESS);
        }
        //发送成功，将验证码存入redis,五分钟，注意前端30s并不代表验证码存活时间只是防止你重复点击/
        //每个用户验证码对应也给set集合
        jedisPool.getResource().setex(telephone+RedisMessageConstant.SENDTYPE_ORDER,300,validateCode.toString());
        return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
    }
    @RequestMapping("/send4Login")
    public Result send4Login(String telephone){
        //生成验证码
        Integer validateCode = ValidateCodeUtils.generateValidateCode(6);
        //调用工具类发送短信
        try {
            SMSUtils.sendShortMessage(SMSUtils.LOGIN_VALIDATE,telephone,validateCode.toString());
        } catch (ClientException e) {
            e.printStackTrace();
            //发送失败
            return new Result(false, MessageConstant.SEND_VALIDATECODE_SUCCESS);
        }
        //发送成功，将验证码存入redis,五分钟，注意前端30s并不代表验证码存活时间只是防止你重复点击/
        //每个用户验证码对应也给set集合
        jedisPool.getResource().setex(telephone+RedisMessageConstant.SENDTYPE_LOGIN,60*60*5,validateCode.toString());
        return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
    }
}
