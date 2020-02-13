package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.CheckGroup;

import java.util.List;

public interface CheckGroupService {
    PageResult findByPage(QueryPageBean queryPageBean)throws Exception;

    void add(CheckGroup checkGroup,Integer[] checkitemIds)throws Exception;

    CheckGroup findById(Integer id)throws Exception;

    List<Integer> findGroup4Items(Integer id)throws Exception;

    void updateById(CheckGroup checkGroup, Integer[] checkitemIds)throws Exception;

    Result delById(Integer id)throws Exception;

    Result findAll();
}
