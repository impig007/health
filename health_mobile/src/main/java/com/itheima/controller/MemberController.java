package com.itheima.controller;
/*会员相关操作*/

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Member;
import com.itheima.service.MemberService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/member")
public class MemberController {
    @Autowired
    private JedisPool jedisPool;
    @Reference
    private MemberService memberService;
    @RequestMapping("/login")
    public Result login(@RequestBody Map map, HttpServletResponse response){
        String validateCode = (String) map.get("validateCode");
        String telephone = (String) map.get("telephone");
        String redisValidateCode = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_LOGIN);
        //判断验证码是否正确
        //首先判断redis中的验证码是否过期
        if (redisValidateCode==null){
            //验证码已经过期
            return new Result(false, MessageConstant.VALIDATECODE_ORVERDUE);
        }
        //验证码没有过期
        //判断验证码是否正确
        if (!redisValidateCode.equals(validateCode)){
            //验证码错误
            return new Result(false,MessageConstant.VALIDATECODE_ERROR);
        }
        try {
            //验证码通过,判断用户是否是会员
            Member member  = memberService.findByTelephone(telephone);
            if (member==null){
                //非会员,调用service注册为会员
                member.setRegTime(new Date());
                member.setPhoneNumber(telephone);
                memberService.rigister(member);
            }
            //是会员,将手机号存入cookie并返回到浏览器
            Cookie cookie = new Cookie("rigisted_telephone",telephone);
            cookie.setMaxAge(60*60*24*30);
            response.addCookie(cookie);
            //将会员信息存入redis中,并设置有效时间为半个小时
            String strMember = JSON.toJSON(member).toString();
            jedisPool.getResource().setex(telephone,60*30,strMember);
            return new Result(true,MessageConstant.LOGIN_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"未知错误");
        }

    }
}
