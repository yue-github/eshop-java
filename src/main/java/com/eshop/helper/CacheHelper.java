package com.eshop.helper;

import java.util.*;

import com.jfinal.plugin.ehcache.CacheKit;

/**
 * 缓存辅助类
 * @author TangYiFeng
 */
public class CacheHelper {
	
	private static int minute = 120;
	
	/**
	 * 加入缓存
	 * @param name 缓存名称
	 * @param value 值
	 */
	public static void put(String name, Object value) {
		CacheKit.put(name, new Date().getTime(), value);
	}
	
	/**
	 * 用name获取缓存
	 * @param name 缓存名称
	 */
	public static Object get(String name) {
		List keys = CacheKit.getKeys(name);
		
		if(keys.size() <= 0) {
			return null;
		}
		
		long time = Long.parseLong(keys.get(0).toString());
		
		long ntiem = new Date().getTime();
		double m = (double)(ntiem-time) / 60 / 1000;
		
		Object v = CacheKit.get(name, time);
		
//		CacheKit.removeAll(name);
//		
//		if(m > minute) {
//			System.out.println("缓存过期。。。");
//			return null;
//		}
//		
//		put(name, v);
	
		return v;
	}
	
	public static void reset(String name, Object value) {
		List keys = CacheKit.getKeys(name);
		
		if(keys.size() <= 0) {
			return;
		}
		
		long time = Long.parseLong(keys.get(0).toString());
		
		CacheKit.put(name, time, value);
	}
	
	public static boolean isExpire(String name, int expireMinute) {
		List keys = CacheKit.getKeys(name);
		
		if(keys.size() <= 0) {
			return true;
		}
		
		long time = Long.parseLong(keys.get(0).toString());
		
		long ntiem = new Date().getTime();
		double m = (double)(ntiem-time) / 60 / 1000;
		
		return m > expireMinute;
	}
	
	/**
	 * 清除缓存
	 * @param name
	 * @return boolean
	 */
	public static void remove(String name) {
		CacheKit.removeAll(name);
	}
}