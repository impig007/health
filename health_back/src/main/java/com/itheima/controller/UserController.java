package com.itheima.controller;

import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @RequestMapping("/getUserName")
    public Result getUserName(){
        //security 框架实现存储用户的原理是将登陆成功后的用户存储到session域中
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user==null){
            return new Result(false, MessageConstant.GET_USERNAME_FAIL);
        }
        return new Result(true,MessageConstant.GET_USERNAME_SUCCESS,user.getUsername());
    }
}
