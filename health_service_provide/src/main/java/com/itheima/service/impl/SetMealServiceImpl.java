package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.mapper.SetMealMapper;

import com.itheima.pojo.Setmeal;
import com.itheima.service.SetMealService;


import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = SetMealService.class)
public class SetMealServiceImpl implements SetMealService {

    @Autowired
    private SetMealMapper setMealMapper;

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    //可以直接从配置文件中取到key对应的value值
    @Value("${out_put_path}")
    private String outputPath;


    @Override
    public PageResult findByPage(QueryPageBean queryPageBean) throws Exception {

        //将所有的数据处理在业务层做，比如封装参数的获取，以及返回数据的封装等
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();

        //开启分页助手
        PageHelper.startPage(currentPage,pageSize);
        Page<Setmeal> pageList = setMealMapper.findByPage(queryString);
        //返回数据的封装
        long total = pageList.getTotal();
        List<Setmeal> rows = pageList.getResult();
        PageResult pageResult = new PageResult(total,rows);
        return pageResult ;
    }

    @Override
    public void add(Setmeal setmeal, Integer[] checkgroupIds) throws Exception {
        //新增检查组,并获取到主键值
        setMealMapper.add(setmeal);
        //新增检查组和检查项关联
        String fileName = setmeal.getImg();
        Integer setmealId= setmeal.getId();
        Map<String,Integer> map = new HashMap<>();
        if (checkgroupIds.length>0&&checkgroupIds!=null){
            for (Integer checkgroupid : checkgroupIds) {
                map.put("setmeal_id",setmealId);
                map.put("checkgroup_id",checkgroupid);
                setMealMapper.addMealGroup(map);
            }
        }
        //将fileName存入redis小集合
        Jedis jedis = jedisPool.getResource();
        jedis.sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES,fileName);
        jedis.close();
        //增改套餐数据库数据都会改变导致页面数据都会发生改变,需要更新页面数据
        generateStaticMoblieHtml();
    }

    @Override
    public List<Setmeal> findAllSetmeal() throws Exception {

        return setMealMapper.findAll();
    }

    @Override
    public Setmeal findByIdDetail(Integer id) throws Exception {
        return setMealMapper.findByIdDetail(id);
    }

    @Override
    public Result delById(Integer id) throws Exception {
        //执行删除命令之前判断是否有关联表
        long relate2Group = setMealMapper.countById(id);
        long relate2Order = setMealMapper.countOrderById(id);
        if (relate2Group>0 || relate2Order>0){
            //已经关联不可删除
            return new Result(false, MessageConstant.DEL_SETMEAL_FAIL);
        }

        Setmeal setmeal = setMealMapper.findByIdDetail(id);
        //执行删除
        setMealMapper.delById(id);
        //将小集合中的图片删除
        Jedis jedis = jedisPool.getResource();
        jedis.srem(RedisConstant.SETMEAL_PIC_DB_RESOURCES,setmeal.getImg());
        //将删除的id存入redis，为了后期的定时删除freemarker生成的界面,redis 必须要存储字符串
        jedis.sadd(RedisConstant.DEL_SETMEAL_ID,String.valueOf(id));
        jedis.close();
        return  new Result(true,MessageConstant.DEL_SETMEAL_SUCCESS);
    }

    @Override
    public void updateById(Setmeal setmeal,Integer[] gids,String oldeFileName) throws Exception {
        //首先更新套餐表中的信息
        setMealMapper.updateById(setmeal);
        //由于不知道原先关联表中是删除了还是新增了，上来先删除，再添加
        //一旦一个表中的数据被外键关联，那么删除这个数据只有把关联表数据全部删除
        setMealMapper.delSetMealAndGroupById(setmeal.getId());
        //再次添加
        Integer sid = setmeal.getId();
        Map<String,Integer> map = new HashMap<>();
        if (gids.length>0&&gids!=null){
            for (Integer gid : gids) {
                map.put("checkgroup_id",gid);
                map.put("setmeal_id",sid);
                setMealMapper.addMealGroup(map);
            }
        }
        //更新图片db集合
        //删除旧图片名称
        Jedis jedis = jedisPool.getResource();

        jedis.srem(RedisConstant.SETMEAL_PIC_DB_RESOURCES,oldeFileName);
        //插入新图片名称
        jedis.sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES,setmeal.getImg());
        jedis.close();
        //重新生成静态页面
        generateStaticMoblieHtml();
    }

    @Override
    public Setmeal findById(Integer id) throws Exception {
        return setMealMapper.findById(id);
    }

    @Override
    public List<Integer> findsetMeal4Groups(Integer id) throws Exception {
        return setMealMapper.findsetMeal4Groups(id);
    }

    @Override
    public List<Map<String, Object>> findSetMealCount() throws Exception {

        return setMealMapper.findSetMealCount();
    }

    //本类中需要生成静态页面方法
    public void generateStaticMoblieHtml()throws Exception{
        //套餐页面需要填入的数据
        List<Setmeal> setmealList = setMealMapper.findAll();
        //生成套餐列表静态页面
        generateMobileSetMealHtml(setmealList);

        //生成套餐详情静态页面，可能生成多个套餐详情，
        // 意思是我们提前把数据填充好的界面准备好，访问路径后跟的id直接就到page目录下取就完事了，但要注意，生成的页面尾缀要有id
        generateMobileSetMealDetailHtml(setmealList);


    }
    //生成套餐列表静态页面方法
    public void generateMobileSetMealHtml(List<Setmeal> setmealList){
        System.out.println(outputPath);
        //数据格式化为map形式
        Map<String,Object> map = new HashMap<>();
        map.put("setmealList",setmealList);
        //套餐列表名称死的
        generateHtml("mobile_setmeal.ftl","m_setmeal.html",map);
    }
    //生成套餐详情静态页面方法
    public void generateMobileSetMealDetailHtml(List<Setmeal> setmealList)throws Exception{
        //准备数据(遍历集合取到每个套餐数据)
        Map<String,Object> map = new HashMap<>();
        for (Setmeal setmeal : setmealList) {
            //这里的setmeal数据单纯只是套餐本身的数据，没有chckgroup 和checkitem 的信息
            map.put("setmeal",setMealMapper.findByIdDetail(setmeal.getId()));
            //调用通用方法生成详情页面,记住这里的targetname应该和id对应起来
            String targetName="m_setmeal_detail_"+setmeal.getId()+".html";
          generateHtml("mobile_setmeal_detail.ftl",targetName,map);
        }
    }
    //生成通用静态页面方法方法
    public  void generateHtml(String templateName, String targetName, Map<String,Object> dataMap){
        //获取配置的对象
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        Writer writerStream=null;
        try {
            //创建模板类
            Template template = configuration.getTemplate(templateName);
            //创建目标文件输出流
            writerStream = new FileWriter(new File(outputPath+File.separator+targetName));
           //填入数据生成页面
            template.process(dataMap,writerStream);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                writerStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
