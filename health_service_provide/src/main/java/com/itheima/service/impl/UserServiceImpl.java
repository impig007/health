package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.mapper.PermissionMapper;
import com.itheima.mapper.RoleMapper;
import com.itheima.mapper.UserMapper;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;
import com.itheima.pojo.User;
import com.itheima.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@Service(interfaceClass = UserService.class)
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private PermissionMapper permissionMapper;
    //根据用户名查询用户，对应角色，角色对应权限
    @Override
    public User findUserDetailByName(String username) throws Exception {
        //这里不用关联查询，分步骤来进行
        //根据用户名查询用户表
        User user = userMapper.findByUserName(username);

        Integer uid = user.getId();
        //根据用户id查询对应角色表
        Set<Role> roleSet = roleMapper.findByUid(uid);
        //遍历role集合，查询每个角色对应的权限
        for (Role role : roleSet) {
            Integer rid = role.getId();
            Set<Permission> permissionSet = permissionMapper.findByRid(rid);
            role.setPermissions(permissionSet);
        }
        user.setRoles(roleSet);
        return user;
    }
}
