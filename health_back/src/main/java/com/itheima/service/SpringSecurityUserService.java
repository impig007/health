package com.itheima.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;
import com.itheima.pojo.User;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class SpringSecurityUserService implements UserDetailsService {
    //远程调用服务提供方(实际操作数据库的接口)
    @Reference
    private UserService userService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = null;
        try {
            user = userService.findUserDetailByName(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (user ==null){
            //用户名不存在
            return null;
        }
        //用户存在，查看用户有何角色
        Set<Role> roles = user.getRoles();
        //创建角色权限集合
        List<GrantedAuthority> list= new ArrayList<>();
        for (Role role : roles) {
            //为用户授予角色(这里的keywor的是字符串角色的唯一代称)
            list.add(new SimpleGrantedAuthority(role.getKeyword()));
            //查看每个角色对应权限
            Set<Permission> permissions = role.getPermissions();
            for (Permission permission : permissions) {
                //为用户赋予权限
                list.add(new SimpleGrantedAuthority(permission.getKeyword()));
            }
        }
        //用户安全认证，比对密码这一环节框架为我们做了
        org.springframework.security.core.userdetails.User securityUser = new org.springframework.security.core.userdetails.User(username,user.getPassword(),list);
        return securityUser;
    }
}
