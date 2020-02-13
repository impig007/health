package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.Setmeal;

import java.util.List;
import java.util.Map;

public interface SetMealService {
    PageResult findByPage(QueryPageBean queryPageBean)throws Exception;

    void add(Setmeal setmeal, Integer[] checkgroupIds)throws Exception;

    List<Setmeal> findAllSetmeal()throws Exception;

    Setmeal findByIdDetail(Integer id)throws Exception;

    Result delById(Integer id)throws Exception;

    void updateById(Setmeal setmeal,Integer[] gids,String olderFileName)throws Exception;

    Setmeal findById(Integer id)throws Exception;

    List<Integer> findsetMeal4Groups(Integer id)throws Exception;

    List<Map<String, Object>> findSetMealCount()throws Exception;
}
