package com.kuaidi100.util;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

public class HelgaHelper {

	
	/**
	 * 返回用户真实IP
	 * @param request
	 * @return
	 */
	public static String getIPAddr(HttpServletRequest request) {
		  String ip = request.getHeader("x-forwarded-for"); 
	       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	           ip = request.getHeader("Proxy-Client-IP"); 
	       } 
	       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	           ip = request.getHeader("WL-Proxy-Client-IP"); 
	       } 
	       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	           ip = request.getRemoteAddr(); 
	       } 
	       return ip; 
	}
	
	/**
	 * 获取当天的开始时间-00.00.000
	 * @return
	 */
	public static Date startTime() {
		Calendar todayStart = Calendar.getInstance();
		todayStart.set(Calendar.HOUR_OF_DAY, 0);
		todayStart.set(Calendar.MINUTE, 0);
		todayStart.set(Calendar.SECOND, 0);
		todayStart.set(Calendar.MILLISECOND, 0);
		return todayStart.getTime();
	}
	
	/**
	 * 获取当天的结束时间-59.59.999
	 * @return
	 */
	public static Date endTime() {
		Calendar todayStart = Calendar.getInstance();
		todayStart.set(Calendar.HOUR_OF_DAY, 23);
		todayStart.set(Calendar.MINUTE, 59);
		todayStart.set(Calendar.SECOND, 59);
		todayStart.set(Calendar.MILLISECOND, 999);
		return todayStart.getTime();
	}
}
