package com.itheima.mapper;

import com.github.pagehelper.Page;
import com.itheima.pojo.CheckGroup;
import com.itheima.pojo.CheckItem;

import java.util.List;
import java.util.Map;

public interface checkGroupMapper {
    Page<CheckGroup> findByPage(String queryString)throws Exception;

    void add(CheckGroup checkGroup )throws Exception;

    void addGroupAndItem(Map<String,Integer> map)throws Exception;

    CheckGroup findById(Integer id)throws Exception;

    List<Integer> findGroup4Items(Integer id)throws Exception;

    void updateById(CheckGroup checkGroup)throws Exception;

    void delGroupAndItemsById(Integer id)throws Exception;

    long countById(Integer id)throws Exception ;

    void delById(Integer id)throws Exception;

    List<CheckGroup> findAll()throws Exception;
}
