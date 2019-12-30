package com.eshop.controller.webapp;


import com.eshop.interceptor.CustomerPcAuthInterceptor;
import com.jfinal.aop.Before;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 *
 * 时间控制器
 * 返回时间数组
 */
public class DateController extends  WebappBaseController{

    private static SimpleDateFormat simpleDateFormat = null;

    private static String YYMMDDHH="yyyy-MM-dd HH:mm:ss";

    private static Date date = new Date();

    private static Calendar calendar = Calendar.getInstance();

    private static Integer MAX=20;

    public DateController(){

    }


    public void listDate(){

        simpleDateFormat = new SimpleDateFormat(YYMMDDHH);

        calendar.setTime(date);

        Integer Default = 0;
        LinkedList linkedList = new LinkedList();
        for(Default = 0;Default<MAX;Default++){
            calendar.setTime(date);
            calendar.add(Calendar.DATE,Default);
            // 时
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            // 分
            calendar.set(Calendar.MINUTE, 0);
            // 秒
            calendar.set(Calendar.SECOND, 0);
            linkedList.add(simpleDateFormat.format(calendar.getTime()));
        }
        HashMap<String,Object>map = new HashMap<>();
        map.put("listDate",linkedList);
        renderJson(map);
    }

//    public static void main(String[] args){
//        simpleDateFormat = new SimpleDateFormat(YYMMDDHH);
//
//        calendar.setTime(date);
//
//        Integer Default = 0;
//        LinkedList linkedList = new LinkedList();
//        for(Default = 0;Default<MAX;Default++){
//            calendar.setTime(date);
//            calendar.add(Calendar.DATE,Default);
//            // 时
//            calendar.set(Calendar.HOUR_OF_DAY, 0);
//            // 分
//            calendar.set(Calendar.MINUTE, 0);
//            // 秒
//            calendar.set(Calendar.SECOND, 0);
//            linkedList.add(simpleDateFormat.format(calendar.getTime()));
//        }
//
//        for(int i = 0;i<linkedList.size();i++){
//            System.out.println(">"+linkedList.get(i));
//        }
//    }
}
