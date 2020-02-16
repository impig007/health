package com.itheima.constant;
//redis中不同图片集合名称
public class RedisConstant {
    //套餐图片所有图片名称
    public static final String SETMEAL_PIC_RESOURCES = "setmealPicResources";
    //套餐图片保存在数据库中的图片名称
    public static final String SETMEAL_PIC_DB_RESOURCES = "setmealPicDbResources";
    //用于存储删除的套餐id，为日后删除做准备
    public static final String DEL_SETMEAL_ID = "delSetmealIds";
}
