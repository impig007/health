package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.mapper.MemberMapper;

import com.itheima.mapper.OrderMapper;
import com.itheima.service.ReportService;
import com.itheima.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = ReportService.class)
public class ReportServiceImpl implements ReportService {
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Map<String, Object> getBusinnessReport() throws Exception {
        String todayStr = DateUtils.parseDate2String(DateUtils.getToday());
        String thisWeekmondayStr = DateUtils.parseDate2String(DateUtils.getThisWeekMonday());
        String thisMonthFirstDayStr = DateUtils.parseDate2String(DateUtils.getFirstDay4ThisMonth());
        Map<String, Object> dateMap = new HashMap<>();
        //当前日期的字符串
        String reportDate = DateUtils.parseDate2String(DateUtils.getToday());
        //今日注册会员人数
        Integer todayNewMember = memberMapper.findMemberCountByDate(todayStr);
        //总会员人数
        Integer totalMember = memberMapper.findMemberTotalCount();
        //本星期新增会员人数(本星期星期一到今天)
        Integer thisWeekNewMember = memberMapper.findMemberCountAfterDate(thisWeekmondayStr);
        //本月新增会员人数(本月一号到今天)
        Integer thisMonthNewMember = memberMapper.findMemberCountAfterDate(thisMonthFirstDayStr);
        //今日预定人数
        Integer todayOrderNumber = orderMapper.findOrderCountByDate(todayStr);
        //今日到诊人数
        Integer todayVisitsNumber = orderMapper.findVisitsCountByDate(todayStr);
        //本周预约人数
        Integer thisWeekOrderNumber = orderMapper.findOrderCountAfterDate(thisWeekmondayStr);
        //本周到诊人数
        Integer thisWeekVisitsNumber = orderMapper.findVisitsCountAfterDate(thisWeekmondayStr);
        //本月预约人数
        Integer thisMonthOrderNumber = orderMapper.findOrderCountAfterDate(thisMonthFirstDayStr);
        //本月到诊人数
        Integer thisMonthVisitsNumber = orderMapper.findVisitsCountAfterDate(thisMonthFirstDayStr);
        //热门订单,查询所有套餐对应的订单数量排序后前三个套餐
        List<Map> hotSetmeal = orderMapper.findHotSetmeal();
        dateMap.put("reportDate",reportDate);
        dateMap.put("todayNewMember",todayNewMember);
        dateMap.put("totalMember",totalMember);
        dateMap.put("thisWeekNewMember",thisWeekNewMember);
        dateMap.put("thisMonthNewMember",thisMonthNewMember);
        dateMap.put("todayOrderNumber",todayOrderNumber);
        dateMap.put("todayVisitsNumber",todayVisitsNumber);
        dateMap.put("thisWeekOrderNumber",thisWeekOrderNumber);
        dateMap.put("thisWeekVisitsNumber",thisWeekVisitsNumber);
        dateMap.put("thisMonthOrderNumber",thisMonthOrderNumber);
        dateMap.put("thisMonthVisitsNumber",thisMonthVisitsNumber);
        dateMap.put("hotSetmeal",hotSetmeal);
        return dateMap;
    }
}
