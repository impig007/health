package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.Setmeal;
import com.itheima.service.SetMealService;
import com.itheima.utils.JedisUtil;
import com.itheima.utils.QiniuUtils;
import com.itheima.utils.UuidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/setMeal")
public class SetMealController {
    //jedispool 获取jedis对象来操作redis
    @Autowired
    private JedisPool jedisPool;
    @Reference
    private SetMealService setMealService;
    //分页查询
    @RequestMapping("/findByPage")
    public PageResult findByPage(@RequestBody QueryPageBean queryPageBean){
       PageResult pageResult =null;
       try {
           pageResult =  setMealService.findByPage(queryPageBean);
       } catch (Exception e) {
           e.printStackTrace();
       }
       return pageResult;
   }
   //文件上传
    @RequestMapping("/upload")
    public Result upload(MultipartFile imgFile){
        String originalFilename = imgFile.getOriginalFilename();
        String fileName = UuidUtils.getFileName(originalFilename);
        try {
            //将图片上传到七牛云
            QiniuUtils.upload2Qiniu(imgFile.getBytes(),fileName);
            //一旦上传成功，将图片名称存入redis大集合中
            Jedis jedis = jedisPool.getResource();
            jedis.sadd(RedisConstant.SETMEAL_PIC_RESOURCES,fileName);
            JedisUtil.close(jedis);
            return new Result(true,MessageConstant.PIC_UPLOAD_SUCCESS, fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.PIC_UPLOAD_FAIL);
        }
    }
    @PreAuthorize("hasAuthority('SETMEAL_ADD')")
   @RequestMapping("/add")
    public Result add(@RequestBody Setmeal setmeal,Integer[] checkgroupIds){
       try {
           setMealService.add(setmeal,checkgroupIds);
           //执行成功
           return  new Result(true, MessageConstant.ADD_SETMEAL_SUCCESS);
       } catch (Exception e) {
           //执行失败
           return  new Result(false, MessageConstant.ADD_SETMEAL_FAIL);
       }
   }
    @PreAuthorize("hasAuthority('SETMEAL_DELETE')")
    @RequestMapping("/delById")
    public Result delById(Integer id){
        Result result =null;
        try {
            //执行成功
           result= setMealService.delById(id);
        } catch (Exception e) {
            //执行失败
            e.printStackTrace();
        }
        return result;
    }
    @PreAuthorize("hasAuthority('SETMEAL_EDIT')")
    @RequestMapping("/updateById")
    public Result updateById(@RequestBody Setmeal setmeal,Integer[] checkgroupIds,String oldeFileName){
        try {
            setMealService.updateById(setmeal,checkgroupIds,oldeFileName);
            //执行成功
            return  new Result(true, MessageConstant.UPDATE_SETMEAL_SUCCESS);
        } catch (Exception e) {
            //执行失败
            return  new Result(false, MessageConstant.UPDATE_SETMEAL_FAIL);
        }
    }
    @RequestMapping("/findById")
    public Result findById(Integer id){
        try {
            Setmeal setmeal = setMealService.findById(id);
            //查询成功
            return  new Result(true,MessageConstant.QUERY_CHECKGROUP_SUCCESS,setmeal);

        } catch (Exception e) {
            //查询失败
            return new Result(false,MessageConstant.QUERY_CHECKGROUP_FAIL);
        }
    }

    @RequestMapping("/findsetMeal4Groups")
    public List<Integer> findsetMeal4Groups(Integer id){
        try {
            return setMealService.findsetMeal4Groups(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
