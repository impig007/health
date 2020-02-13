package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.CheckItem;

public interface CheckItemService {


    PageResult findByPage(QueryPageBean queryPageBean)throws Exception;

    void add(CheckItem checkItem)throws Exception;

    CheckItem findById(int id)throws Exception;

    void updateById(CheckItem checkItem)throws Exception;

    Result delById(Integer id) throws Exception;

    Result findAll();
}
