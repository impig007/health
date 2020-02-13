package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.CheckItem;
import com.itheima.service.CheckItemService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/checkItem")
public class CheckItemController {
    @Reference
    private CheckItemService checkItemService;
   @RequestMapping("/findByPage")
    public PageResult findByPage(@RequestBody QueryPageBean queryPageBean){
       PageResult pageResult =null;
       try {
           pageResult =  checkItemService.findByPage(queryPageBean);
       } catch (Exception e) {
           e.printStackTrace();
       }
       return pageResult;
   }
    @PreAuthorize("hasAuthority('CHECKITEM_ADD')")
   @RequestMapping("/add")
    public Result add(@RequestBody CheckItem checkItem){
       try {
           checkItemService.add(checkItem);
           //执行成功
           return  new Result(true, MessageConstant.ADD_CHECKITEM_SUCCESS);
       } catch (Exception e) {
           //执行失败
           return  new Result(false, MessageConstant.ADD_CHECKITEM_FAIL);
       }
   }
   @RequestMapping("/findById")
    public Result findById(Integer id){
       try {
           CheckItem checkItem = checkItemService.findById(id);
           //查询成功
            return  new Result(true,MessageConstant.QUERY_CHECKITEM_SUCCESS,checkItem);

       } catch (Exception e) {
           //查询失败
           return new Result(false,MessageConstant.QUERY_CHECKITEM_FAIL);
       }
   }
    @PreAuthorize("hasAuthority('CHECKITEM_EDIT')")
    @RequestMapping("/updateById")
    public Result updateById(@RequestBody CheckItem checkItem){
        try {
            checkItemService.updateById(checkItem);
            //执行成功
            return  new Result(true, MessageConstant.EDIT_CHECKITEM_SUCCESS);
        } catch (Exception e) {
            //执行失败
            return  new Result(false, MessageConstant.EDIT_CHECKITEM_FAIL);
        }
    }
    //想要调用delebyid这个方法（访问这个资源）必须具有CHECKITEM_DELETE权限
    @PreAuthorize("hasAuthority('CHECKITEM_DELETE')")
    @RequestMapping("/delById")
    public Result delById(Integer id)  {
        Result result =null;
        try {
            result = checkItemService.delById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    @RequestMapping("/findAll")
    public Result findAll()  {

        return checkItemService.findAll();

    }

}
