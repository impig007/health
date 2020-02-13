package com.itheima.jobs;

import com.itheima.constant.RedisConstant;
import com.itheima.utils.JedisUtil;
import com.itheima.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;

public class ClearImagJob {
    @Autowired
    private JedisPool jedisPool;
    public void clearImg(){
        //两个集合相减,返回值是一个set集合,垃圾图片的集合
        Set<String> set = jedisPool.getResource().sdiff(RedisConstant.SETMEAL_PIC_RESOURCES, RedisConstant.SETMEAL_PIC_DB_RESOURCES);
        if (set!=null){
            for (String fileName : set) {
                //调用工具类，删掉图片服务器中的图片
                QiniuUtils.deleteFileFromQiniu(fileName);
                //云服务中的图片删除还需要将大集合中的图片删除
                Jedis jedis = jedisPool.getResource();
                jedis.srem(RedisConstant.SETMEAL_PIC_RESOURCES,fileName);
                JedisUtil.close(jedis);
                System.out.println("自定义任务执行，清理垃圾图片:" + fileName);
            }
        }
    }
}
