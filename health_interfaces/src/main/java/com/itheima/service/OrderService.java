package com.itheima.service;

import com.itheima.entity.Result;

import java.util.Map;
public interface OrderService {
    Result orderAppointment(Map map)throws Exception;

    Map findById(Integer id)throws Exception;
}