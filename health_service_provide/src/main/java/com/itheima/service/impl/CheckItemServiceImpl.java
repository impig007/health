package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.mapper.CheckItemMappers;
import com.itheima.pojo.CheckItem;
import com.itheima.service.CheckItemService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service(interfaceClass = CheckItemService.class)
public class CheckItemServiceImpl implements CheckItemService {
    @Autowired
    private CheckItemMappers checkItemMapper;

    @Override
    public PageResult findByPage(QueryPageBean queryPageBean) throws Exception {
        //将所有的数据处理在业务层做，比如封装参数的获取，以及返回数据的封装等
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();

        //开启分页助手
        PageHelper.startPage(currentPage,pageSize);
        Page<CheckItem> pageList = checkItemMapper.findByPage(queryString);
        //返回数据的封装
        long total = pageList.getTotal();
        List<CheckItem> rows = pageList.getResult();
        PageResult pageResult = new PageResult(total,rows);
        return pageResult ;
    }

    @Override
    public void add(CheckItem checkItem) throws Exception {
        checkItemMapper.add(checkItem);
    }

    @Override
    public CheckItem findById(int id) throws Exception {

        return checkItemMapper.findById(id);
    }

    @Override
    public void updateById(CheckItem checkItem) throws Exception {
        checkItemMapper.updateById(checkItem);
    }

    @Override
    public Result delById(Integer id) throws Exception {
        //判断关联表中关于id的记录是否大于0
        long count = checkItemMapper.countById(id);
        System.out.println("count"+count);
        if (count>0){
            //已关联，无法删除
            return  new Result(false,"该记录已经关联检查组，无法删除");
        }
        //无关联，可以删除
        checkItemMapper.delById(id);
        return   new Result(true, MessageConstant.DELETE_CHECKITEM_SUCCESS);

    }

    @Override
    public Result findAll() {
        List<CheckItem> checkItemList=null;
        try {
            checkItemList= checkItemMapper.findAll();
            return new Result(true,MessageConstant.QUERY_CHECKITEM_SUCCESS,checkItemList);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true,MessageConstant.QUERY_CHECKITEM_FAIL,checkItemList);
        }
    }
}
