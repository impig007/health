package com.itheima.utils;

import java.util.UUID;

public class UuidUtils {
    public static String getFileName(String originalFilename){
        int i = originalFilename.lastIndexOf(".");
        String pattern = originalFilename.substring(i);
        return UUID.randomUUID().toString()+pattern;
    }
}
