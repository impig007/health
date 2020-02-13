package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.mapper.MemberMapper;
import com.itheima.mapper.OrderMapper;
import com.itheima.mapper.OrderSettingMapper;
import com.itheima.pojo.Member;
import com.itheima.pojo.Order;
import com.itheima.pojo.OrderSetting;
import com.itheima.service.OrderService;
import com.itheima.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl implements OrderService{
    @Autowired
    private OrderSettingMapper orderSettingMapper;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Result orderAppointment(Map map) throws Exception {
        String _orderDate = (String) map.get("orderDate");
        Date orderDate = DateUtils.parseString2Date(_orderDate);
        String telephone = (String) map.get("telephone");
        String setmealId = (String) map.get("setmealId");
        String name = (String) map.get("name");
        String idCard = (String) map.get("idCard");
        String sex = (String) map.get("sex");
        String orderType = (String) map.get("orderType");
        //判断用户预约那一天有没有进行过预约设置（如果有预约过，预约设置表中应该有记录）
        OrderSetting orderSetting = orderSettingMapper.findByOrderDate(orderDate);
        if (orderSetting==null){
            //没有进行过设置
            return new Result(false, MessageConstant.SELECTED_DATE_CANNOT_ORDER);
        }
        //进行过设置，判断用户预约日期是否已经预约满了
        //可预约人数
        int number = orderSetting.getNumber();
        //以预约人数
        int reservations = orderSetting.getReservations();
        if (reservations>=number){
            //预约以满，无法预约
            return new Result(false,MessageConstant.ORDER_FULL);
        }
        //预约未满，判断用户是否存在重复预约(同一用户在同一天预约了同一个套餐叫做重复预约)
        //先判断用户是否存在
        Member member = memberMapper.findByTelephone(telephone);
        if (member==null){
            //非会员，新建会员添加
            member = new Member();
            member.setName(name);
            member.setPhoneNumber(telephone);
            member.setIdCard(idCard);
            member.setSex(sex);
            member.setRegTime(new Date());
            memberMapper.add(member);
        }
        //是会员
        List<Order> order = orderMapper.findByCondition(new Order(member.getId(),orderDate,Integer.parseInt(setmealId)));
        if (order!=null && order.size()>0){
            //重复预约
            return new Result(false,MessageConstant.HAS_ORDERED);
        }
        //非重复预约
        //增加订单order
        Order addorder = new Order();
        addorder.setOrderDate(orderDate);
        addorder.setOrderType(orderType);
        addorder.setOrderStatus(Order.ORDERSTATUS_NO);
        addorder.setSetmealId(Integer.parseInt(setmealId));
        addorder.setMemberId(member.getId());
        orderMapper.add(addorder);
        //预约设置中当天的预约人数加一
        orderSetting.setReservations(orderSetting.getReservations()+1);
        orderSettingMapper.updataByOrderDate(orderSetting);
        return new Result(true,MessageConstant.ORDER_SUCCESS,addorder.getId());
    }

    @Override
    public Map findById(Integer id) throws Exception {
        return orderMapper.findById4Detail(id);
    }
}
