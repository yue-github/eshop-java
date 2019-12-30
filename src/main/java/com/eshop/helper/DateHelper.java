package com.eshop.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateHelper {
	
	/**
	 * 把Date对象转换成相应格式的字符串
	 * @param date
	 * @param format yyyy-MM-dd HH:mm:ss
	 * @param offsetDay
	 * @return
	 */
	public static String formatDate(Date date, String format, int offsetDay) {
    	if (date == null) {
			return null;
		}
    	
    	Calendar calendar = new GregorianCalendar(); 
        calendar.setTime(date); 
        calendar.add(Calendar.DATE, offsetDay);
        date = calendar.getTime(); 
    	String strDate = new SimpleDateFormat(format).format(date);
    	return strDate;
    }
	
	/**
	 * 把Date对象转换成时间格式的字符串
	 * @param date
	 * @return 返回：2017-12-12
	 */
	public static String formatDate(Date date) {
    	if (date == null) {
			return null;
		}
    	
    	String format = "yyyy-MM-dd";
    	String strDate = new SimpleDateFormat(format).format(date);
    	
    	return strDate;
    }
	
	/**
	 * 把Date对象转换成时间格式的字符串
	 * @param date
	 * @return 返回：2017-12-12 12:30:54
	 */
	public static String formatDateTime(Date date) {
    	if (date == null) {
			return null;
		}
    	
    	String format = "yyyy-MM-dd HH:mm:ss";
    	String strDate = new SimpleDateFormat(format).format(date);
    	
    	return strDate;
    }


	/**
	 * 把Date对象转换成时间格式的字符串
	 * @param date
	 * @param format yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String formatDate(Date date, String format) {
    	if (date == null) {
			return null;
		}
    	
    	String strDate = new SimpleDateFormat(format).format(date);
    	
    	return strDate;
    }
	
	/**
	 * 把字符串转成Date对象
	 * @param strDate
	 * @param format yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static Date strToDate(String strDate, String format) {
    	SimpleDateFormat fmt = new SimpleDateFormat(format);
    	try {
			return fmt.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
    }
	
	public static Date strToDate(String strDate) {
    	SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
    	try {
			return fmt.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
    }
	
	public static Date strToDateTime(String strDate) {
    	SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	try {
			return fmt.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
    }
	
	/**
	 * 获取今天日期
	 * @return 如：2017-02-03
	 */
	public static String today() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
        return sdf.format(new Date());
	}
	
	/**
	 * 获取昨天日期
	 * @return 如：2017-02-03
	 */
	public static String yesterday() {
		Calendar cal = Calendar.getInstance();
	    cal.add(Calendar.DATE, -1);
		return new SimpleDateFormat( "yyyy-MM-dd").format(cal.getTime());
	}
	
	/**
     * 获取当前月份的第一天
     * @return  例如：2016-03-01
     */
    public static String firstDay() {
        return getDay(0, 1);
    }
    
    /**
     * 获取当前月份的第一天
     * @return  例如：2016-03-01
     */
    public static String lastDay() {
        return getDay(1, 0);
    }
    
    /**
     * 获取年月日
     * @param offsetMonth
     * @param day
     * @return
     */
    private static String getDay(int offsetMonth, int day) {
    	Calendar cale = Calendar.getInstance();  
        cale.add(Calendar.MONTH, offsetMonth);  
        cale.set(Calendar.DAY_OF_MONTH, day);  
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(cale.getTime());
        return date;
    }
    
    
    /**
     * 指定日期追加天数
     * @param s  指定日期， 如：2016-03-01
     * @param n 天数
     * @return 追加后的日期
     */
    public static String addDay(String s, int n) {   
        try {   
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");   
  
            Calendar cd = Calendar.getInstance();   
            cd.setTime(sdf.parse(s));   
            cd.add(Calendar.DATE, n);//增加一天   
            //cd.add(Calendar.MONTH, n);//增加一个月   
  
            return sdf.format(cd.getTime());   
  
        } catch (Exception e) {   
            return null;   
        }   
  
    }   

	/**
	 * date2比date1多的天数
	 * @param date1    
	 * @param date2
	 * @return    
	 */
	public static int differentDays(Date date1,Date date2)
	{
	    Calendar cal1 = Calendar.getInstance();
	    cal1.setTime(date1);
	    
	    Calendar cal2 = Calendar.getInstance();
	    cal2.setTime(date2);
	   int day1= cal1.get(Calendar.DAY_OF_YEAR);
	    int day2 = cal2.get(Calendar.DAY_OF_YEAR);
	    
	    int year1 = cal1.get(Calendar.YEAR);
	    int year2 = cal2.get(Calendar.YEAR);
	    if(year1 != year2)   //不同年
	    {
	        int timeDistance = 0 ;
	        for(int i = year1 ; i < year2 ; i ++)
	        {
	            if(i%4==0 && i%100!=0 || i%400==0)    //闰年            
	            {
	                timeDistance += 366;
	            }
	            else    //不是闰年
	            {
	                timeDistance += 365;
	            }
	        }
	        
	        return timeDistance + (day2-day1) ;
	    }
	    else    //同年
	    {
	       
	        return day2-day1;
	    }
	}
	
}
