package com.itheima.mapper;

import com.itheima.pojo.Role;

import java.util.Set;

public interface RoleMapper {
    Set<Role> findByUid(Integer uid)throws Exception;
}
