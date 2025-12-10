package com.easylive.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;

public class StringTools {
    public static String upperCaseFirstLetter(String field){
        if(isEmpty(field)){
            return field;}
        if(field.length()>1&&Character.isUpperCase(field.charAt(1))) {
            return field;
        }
        return field.substring(0, 1).toUpperCase() + field.substring(1);
    }
    public static boolean isEmpty(String str){
        if(str==null||"".equals(str)||"null".equals(str)||"\u0000".equals(str)){
            return true;
        }else{
            return false;
        }
    }
    public static final String getRandomString(Integer count) {
        return RandomStringUtils.random(count,true,true);
    }
    public static final String getRandomNumber(Integer count){
        return RandomStringUtils.random(count,false,true);
    }
    public static final String encodeByMd5(String originString){
        return StringTools.isEmpty(originString)?null: DigestUtils.md5Hex(originString);
    }

    public static boolean pathIsOk(String Path){
        if(StringTools.isEmpty(Path)){
            return true;
        }
        if(path.contains("../")||path.contains("..\\")){
            return false;
        }
        return true;
    }
    public static String getFileSuffix(String fileName){
        if(StringTools.isEmpty(fileName)||!fileName.contains(".")){
            return null;
        }
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        return suffix;
    }
}
