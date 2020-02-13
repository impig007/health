package com.itheima.mapper;

import com.itheima.pojo.User;

public interface UserMapper {
    User findByUserName(String username)throws Exception;
}
