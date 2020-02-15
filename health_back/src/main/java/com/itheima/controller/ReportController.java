package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;

import com.itheima.pojo.HotSetMeal;
import com.itheima.pojo.Report;
import com.itheima.service.MemberService;

import com.itheima.service.ReportService;
import com.itheima.service.SetMealService;
import com.itheima.utils.POIUtils;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/report")
public class ReportController {
   @Reference
    private MemberService memberService;
   @Reference
   private SetMealService setMealService;
   @Reference
   private ReportService reportService;

    @RequestMapping("/getMemberReport")
    public Result getMemberReport(){
        Map<String,Object> resultMap = new HashMap<>();
        //以当前日期为起点，获取过去十二个月的月份日期，例如2019.11 2019.12
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH,-12);
        List<String> monthList =new ArrayList<>();
        for (int i = 0; i <12 ; i++) {
            calendar.add(Calendar.MONTH,1);
            Date time = calendar.getTime();
            monthList.add(new SimpleDateFormat("yyyy.MM").format(time));
        }
        try {
            List<Integer> memberCountList =memberService.findCountByMonth(monthList);
            resultMap.put("months",monthList);
            resultMap.put("memberCount",memberCountList);
            return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS,resultMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, MessageConstant.GET_MEMBER_NUMBER_REPORT_FAIL);
    }
    //获取套餐对应订单统计数据
    @RequestMapping("/getSetmealReport")
    public Result getSetmealReport(){
        //调用setmealService，统计所有套餐对应的订单数量
        List<Map<String,Object>> setmealCountList = null;
        try {
            setmealCountList = setMealService.findSetMealCount();
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.GET_SETMEAL_COUNT_REPORT_FAIL);
        }

        //定义一个list集合用来存放套餐名
        List<String> setMealNamesList = new ArrayList<>();
        for (Map<String, Object> map : setmealCountList) {
            String name = (String) map.get("name");
            setMealNamesList.add(name);
        }
        //定于map集合用来存放最终date数据
        Map<String,Object> dateMap = new HashMap<>();
        dateMap.put("setmealNames",setMealNamesList);
        dateMap.put("setmealCount",setmealCountList);
        return new Result(true,MessageConstant.GET_SETMEAL_COUNT_REPORT_SUCCESS,dateMap);
    }
    //获取运营数据报表的数据
    @RequestMapping("/getBusinessReportData")
    public Result getBusinessReportData(){
        try {
            Report report = reportService.getBusinnessReport();
            return new Result(true,MessageConstant.GET_BUSINESS_REPORT_SUCCESS,report);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
    }

    @RequestMapping("/exportBusinessReport")
    public Result exportBusinessReport(HttpServletRequest request, HttpServletResponse response){
//        try {
//            //根据服务器地址获取文件excel真实地址(File.separator可以根据系统不同给出不同的分隔符)
//            String realPath = request.getSession().getServletContext().getRealPath("template" + File.separator + "report_template.xlsx");
//            //调用工具类向工作簿中写入数据
//            POIUtils poiUtils = POIUtils.getInstance(realPath);
//            poiUtils.wirteCellValue(2,5,reportDate);
//            poiUtils.wirteCellValue(4,5,todayNewMember);
//            poiUtils.wirteCellValue(4,7,totalMember);
//            poiUtils.wirteCellValue(5,5,thisWeekNewMember);
//            poiUtils.wirteCellValue(5,7,thisMonthNewMember);
//            poiUtils.wirteCellValue(7,5,todayOrderNumber);
//            poiUtils.wirteCellValue(7,7,todayVisitsNumber);
//            poiUtils.wirteCellValue(8,5,thisWeekOrderNumber);
//            poiUtils.wirteCellValue(8,7,thisWeekVisitsNumber);
//            poiUtils.wirteCellValue(9,5,thisMonthOrderNumber);
//            poiUtils.wirteCellValue(9,7,thisMonthVisitsNumber);
//            int starthotRowIndex=12;
//            for (Map map : hotSetmeal) {
//                //取出一个热门套餐
//                String name = (String) map.get("name");
//                poiUtils.wirteCellValue(starthotRowIndex,4,name);
//                Long setmeal_count = (Long) map.get("setmeal_count");
//                poiUtils.wirteCellValue(starthotRowIndex,5,setmeal_count);
//                BigDecimal proportion = (BigDecimal) map.get("proportion");
//                poiUtils.wirteCellValue(starthotRowIndex,6,proportion.doubleValue());
//                starthotRowIndex+=1;
//            }
//
//            //创建相应流
//            OutputStream outputStream=response.getOutputStream();
//            //设置响应的文件格式，通知浏览器以什么方式解析
//            response.setContentType("application/vnd.ms-excel");
//            //设置响应头信息，通知浏览器以附件的方式处理而不是展示
//            response.setHeader("content-Disposition", "attachment;filename=report.xlsx");
//            poiUtils.excelWrite2Broser(outputStream);
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new Result(false,MessageConstant.GET_BUSINESS_REPORT_FAIL);
//        }
        try{
            Report result = reportService.getBusinnessReport();
            //取出返回结果数据，准备将报表数据写入到Excel文件中
            String reportDate = result.getReportDate();
            Integer todayNewMember = result.getTodayNewMember();
            Integer totalMember = result.getTotalMember();
            Integer thisWeekNewMember = result.getThisWeekNewMember();
            Integer thisMonthNewMember = result.getThisMonthNewMember();
            Integer todayOrderNumber = result.getTodayOrderNumber();
            Integer thisWeekOrderNumber = result.getThisWeekOrderNumber();
            Integer thisMonthOrderNumber = result.getThisMonthOrderNumber();
            Integer todayVisitsNumber = result.getTodayVisitsNumber();
            Integer thisWeekVisitsNumber = result.getThisWeekVisitsNumber();
            Integer thisMonthVisitsNumber = result.getThisMonthVisitsNumber();
            List<HotSetMeal> hotSetmeal = result.getHotSetmeal();

            String filePath = request.getSession().getServletContext().getRealPath("template")+ File.separator+"report_template.xlsx";
            //基于提供的Excel模板文件在内存中创建一个Excel表格对象
            XSSFWorkbook excel = new XSSFWorkbook(new FileInputStream(new File(filePath)));
            //读取第一个工作表
            XSSFSheet sheet = excel.getSheetAt(0);

            XSSFRow row = sheet.getRow(2);
            row.getCell(5).setCellValue(reportDate);//日期

            row = sheet.getRow(4);
            row.getCell(5).setCellValue(todayNewMember);//新增会员数（本日）
            row.getCell(7).setCellValue(totalMember);//总会员数

            row = sheet.getRow(5);
            row.getCell(5).setCellValue(thisWeekNewMember);//本周新增会员数
            row.getCell(7).setCellValue(thisMonthNewMember);//本月新增会员数

            row = sheet.getRow(7);
            row.getCell(5).setCellValue(todayOrderNumber);//今日预约数
            row.getCell(7).setCellValue(todayVisitsNumber);//今日到诊数

            row = sheet.getRow(8);
            row.getCell(5).setCellValue(thisWeekOrderNumber);//本周预约数
            row.getCell(7).setCellValue(thisWeekVisitsNumber);//本周到诊数

            row = sheet.getRow(9);
            row.getCell(5).setCellValue(thisMonthOrderNumber);//本月预约数
            row.getCell(7).setCellValue(thisMonthVisitsNumber);//本月到诊数

            int rowNum = 12;
            for (HotSetMeal hotSetMeal : hotSetmeal) {
                String name = hotSetMeal.getName();
                Long setmeal_count = hotSetMeal.getSetmeal_count();
                BigDecimal proportion = hotSetMeal.getProportion();
                row = sheet.getRow(rowNum ++);
                row.getCell(4).setCellValue(name);//套餐名称
                row.getCell(5).setCellValue(setmeal_count);//预约数量
                row.getCell(6).setCellValue(proportion.doubleValue());//占比
            }
            //使用输出流进行表格下载,基于浏览器作为客户端下载
            OutputStream out = response.getOutputStream();
            response.setContentType("application/vnd.ms-excel");//代表的是Excel文件类型
            response.setHeader("content-Disposition", "attachment;filename=report.xlsx");//指定以附件形式进行下载
            excel.write(out);
            out.flush();
            out.close();
            excel.close();
            return null;
        }catch (Exception e){
            return new Result(false,MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
    }
    @RequestMapping("/exportPdfBusinessReport")
    public void exportPdfBusinessReport(HttpServletRequest request, HttpServletResponse response){
        try{
            Report _result = reportService.getBusinnessReport();
            //取出返回结果数据，准备将报表数据写入到Excel文件中
            List<HotSetMeal> _hotSetmeal = _result.getHotSetmeal();
            //转化为map集合
            Map<String, Object> result = JSON.parseObject(JSON.toJSONString(_result), new TypeReference<Map<String, Object>>() {});
            List<Map<String,Object>> hotSetmealList = new ArrayList<>();
            for (HotSetMeal hotSetMeal : _hotSetmeal) {
                Map<String, Object> hotSetmealMap = JSON.parseObject(JSON.toJSONString(hotSetMeal), new TypeReference<Map<String, Object>>() {});
                //将integer转化为long类型，（记住大的装小的直接强转，）
                Integer _setmeal_count = (Integer) hotSetmealMap.get("setmeal_count");
                long setmeal_count = _setmeal_count.longValue();
                hotSetmealMap.put("setmeal_count",setmeal_count);
                hotSetmealList.add(hotSetmealMap);
            }

                //获取pdf模板的绝对路径
            String pdfTempletPath= request.getSession().getServletContext().getRealPath("/template")+ File.separator+"report_template_pdf.jrxml";
            //编译模板文件后生成的编译文件路径
            String jasperPath = request.getSession().getServletContext().getRealPath("/template")+ File.separator+"report_pdf.jasper";
            //编译
            JasperCompileManager.compileReportToFile(pdfTempletPath,jasperPath);
            //填充数据，获取一个jasperPrint对象
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperPath, result, new JRBeanCollectionDataSource(hotSetmealList));
            //使用输出流进行表格下载,基于浏览器作为客户端下载
            OutputStream out = response.getOutputStream();
            response.setContentType("application/pdf");
            response.setHeader("content-Disposition", "attachment;filename=report.pdf");//指定以附件形式进行下载
            //将文件写入输出流带到前端
            JasperExportManager.exportReportToPdfStream(jasperPrint,out);
        }catch (Exception e){
            e.printStackTrace();

        }
    }
}
