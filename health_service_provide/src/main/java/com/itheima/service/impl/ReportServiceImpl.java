package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.mapper.MemberMapper;

import com.itheima.mapper.OrderMapper;
import com.itheima.pojo.HotSetMeal;
import com.itheima.pojo.Report;
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
    public Report getBusinnessReport() throws Exception {
        String todayStr = DateUtils.parseDate2String(DateUtils.getToday());
        String thisWeekmondayStr = DateUtils.parseDate2String(DateUtils.getThisWeekMonday());
        String thisMonthFirstDayStr = DateUtils.parseDate2String(DateUtils.getFirstDay4ThisMonth());
        Report report  =  new Report();
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
        List<HotSetMeal> hotSetmeal = orderMapper.findHotSetmeal();
        report.setReportDate(reportDate);
        report.setTodayNewMember(todayNewMember);
        report.setTotalMember(totalMember);
        report.setThisWeekNewMember(thisWeekNewMember);
        report.setThisMonthNewMember(thisMonthNewMember);
        report.setTodayOrderNumber(todayOrderNumber);
        report.setTodayVisitsNumber(todayVisitsNumber);
        report.setThisWeekOrderNumber(thisWeekOrderNumber);
        report.setThisWeekVisitsNumber(thisWeekVisitsNumber);
        report.setThisMonthOrderNumber(thisMonthOrderNumber);
        report.setThisMonthVisitsNumber(thisMonthVisitsNumber);
        report.setHotSetmeal(hotSetmeal);
        return report;
    }
}
