package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.CheckGroup;
import com.itheima.service.CheckGroupService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/checkGroup")
public class ChecGruopController {
    @Reference
    private CheckGroupService checkGroupService;
   @RequestMapping("/findByPage")
    public PageResult findByPage(@RequestBody QueryPageBean queryPageBean){

       PageResult pageResult =null;
       try {
           pageResult =  checkGroupService.findByPage(queryPageBean);
       } catch (Exception e) {
           e.printStackTrace();
       }
       return pageResult;
   }
   @PreAuthorize("hasAuthority('CHECKGROUP_ADD')")
   @RequestMapping("/add")
    public Result add(@RequestBody CheckGroup checkGroup,Integer[] checkitemIds){
       try {
           checkGroupService.add(checkGroup,checkitemIds);
           //执行成功
           return  new Result(true, MessageConstant.ADD_CHECKGROUP_SUCCESS);
       } catch (Exception e) {
           //执行失败
           return  new Result(false, MessageConstant.ADD_CHECKGROUP_FAIL);
       }
   }
   @RequestMapping("/findById")
    public Result findById(Integer id){
       try {
           CheckGroup checkGroup = checkGroupService.findById(id);
           //查询成功
            return  new Result(true,MessageConstant.QUERY_CHECKGROUP_SUCCESS,checkGroup);

       } catch (Exception e) {
           //查询失败
           return new Result(false,MessageConstant.QUERY_CHECKGROUP_FAIL);
       }
   }
    @RequestMapping("/findGroup4Items")
    public List<Integer> findGroup4Items(Integer id){
        try {
            return checkGroupService.findGroup4Items(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    @PreAuthorize("hasAuthority('CHECKGROUP_EDIT')")
    @RequestMapping("/updateById")
    public Result updateById(@RequestBody CheckGroup checkGroup,Integer[] checkitemIds){
        try {
            checkGroupService.updateById(checkGroup,checkitemIds);
            //执行成功
            return  new Result(true, MessageConstant.EDIT_CHECKGROUP_SUCCESS);
        } catch (Exception e) {
            //执行失败
            return  new Result(false, MessageConstant.EDIT_CHECKGROUP_FAIL);
        }
    }
    @PreAuthorize("hasAuthority('CHECKGROUP_DELETE')")
    @RequestMapping("/delById")
    public Result delById(Integer id)  {
        Result result =null;
        try {
            result = checkGroupService.delById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    @RequestMapping("/findAll")
    public Result findAll()  {
        return checkGroupService.findAll();
    }

}
