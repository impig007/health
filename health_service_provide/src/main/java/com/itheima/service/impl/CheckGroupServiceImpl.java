package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.mapper.checkGroupMapper;
import com.itheima.pojo.CheckGroup;

import com.itheima.service.CheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = CheckGroupService.class)
public class CheckGroupServiceImpl implements CheckGroupService{
    @Autowired
    private checkGroupMapper checkGroupMapper;
    @Override
    public PageResult findByPage(QueryPageBean queryPageBean) throws Exception {

        //将所有的数据处理在业务层做，比如封装参数的获取，以及返回数据的封装等
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();

        //开启分页助手
        PageHelper.startPage(currentPage,pageSize);
        Page<CheckGroup> pageList = checkGroupMapper.findByPage(queryString);
        //返回数据的封装
        long total = pageList.getTotal();
        List<CheckGroup> rows = pageList.getResult();
        PageResult pageResult = new PageResult(total,rows);
        return pageResult ;

    }

    @Override
    public void add(CheckGroup checkGroup,Integer[] checkitemIds) throws Exception {
        //新增检查组,并获取到主键值·
        checkGroupMapper.add(checkGroup);
        //新增检查组和检查项关联
        Integer groupId = checkGroup.getId();
        Map<String,Integer> map = new HashMap<>();
        if (checkitemIds.length>0&&checkitemIds!=null){
            for (Integer checkitemId : checkitemIds) {
                map.put("checkgroup_id",groupId);
                map.put("checkitem_id",checkitemId);
                checkGroupMapper.addGroupAndItem(map);
            }
        }
    }

    @Override
    public CheckGroup findById(Integer id) throws Exception {
        return checkGroupMapper.findById(id);
    }

    @Override
    public List<Integer> findGroup4Items(Integer id) throws Exception {

        return checkGroupMapper.findGroup4Items(id);
    }

    @Override
    public void updateById(CheckGroup checkGroup, Integer[] checkitemIds) throws Exception {
        //首先更新检查组对应id的信息
        checkGroupMapper.updateById(checkGroup);
        //由于不知道原先关联表中的检查项是删除了还是新增了，上来先删除，再添加
        //一旦一个表中的数据被外键关联，那么删除这个数据只有把关联表数据全部删除
        checkGroupMapper.delGroupAndItemsById(checkGroup.getId());
        //再次添加
        Integer groupId = checkGroup.getId();
        Map<String,Integer> map = new HashMap<>();
        if (checkitemIds.length>0&&checkitemIds!=null){
            for (Integer checkitemId : checkitemIds) {
                map.put("checkgroup_id",groupId);
                map.put("checkitem_id",checkitemId);
                checkGroupMapper.addGroupAndItem(map);
            }
        }
    }

    @Override
    public Result delById(Integer id) throws Exception {
        //判断关联表中关于id的记录是否大于0
        long count = checkGroupMapper.countById(id);

        if (count>0){
            //已关联，无法删除
            return  new Result(false,"该记录已经关联检查项，无法删除");
        }
        //无关联，可以删除
        checkGroupMapper.delById(id);
        return   new Result(true, MessageConstant.DELETE_CHECKGROUP_SUCCESS);
    }

    @Override
    public Result findAll() {
        List<CheckGroup> checkGroupList=null;
        try {
            checkGroupList= checkGroupMapper.findAll();
            return new Result(true,MessageConstant.QUERY_CHECKITEM_SUCCESS,checkGroupList);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true,MessageConstant.QUERY_CHECKITEM_FAIL,checkGroupList);
        }
    }
    //抽取
}
