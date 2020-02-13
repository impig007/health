package com.itheima.mapper;

import com.github.pagehelper.Page;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.CheckItem;

import java.util.List;

public interface CheckItemMappers {

    Page<CheckItem> findByPage(String queryString)throws Exception;

    void add(CheckItem checkItem)throws Exception;

    CheckItem findById(int id)throws Exception;

    void updateById(CheckItem checkItem)throws Exception;

    void delById(Integer id)throws Exception;

    long countById(int id)throws Exception;

    List<CheckItem> findAll()throws Exception;
}
