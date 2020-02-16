package com.itheima.mapper;

import com.github.pagehelper.Page;

import com.itheima.pojo.Setmeal;

import java.util.List;
import java.util.Map;

public interface SetMealMapper {
    Page<Setmeal> findByPage(String queryString)throws Exception;

    void add(Setmeal setmeal)throws Exception;

    void addMealGroup(Map<String, Integer> map)throws Exception;

    List<Setmeal> findAll()throws Exception;

    Setmeal findByIdDetail(Integer id)throws Exception;

    long countById(Integer id)throws Exception;

    void delById(Integer id)throws Exception;

    void updateById(Setmeal setmeal)throws Exception;

    void delSetMealAndGroupById(Integer id)throws Exception;

    Setmeal findById(Integer id)throws Exception;

    List<Integer> findsetMeal4Groups(Integer id)throws Exception;

    List<Map<String, Object>> findSetMealCount()throws Exception;

    long countOrderById(Integer id)throws Exception;
}
