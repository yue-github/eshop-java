package com.eshop.helper;

import java.text.DecimalFormat;

public class MathHelper {
	
	/**
     * 获取某个区间的随机数
     * @param min 最小数
     * @param max 最大数
     * @return 随机数 [min,max)
     */
    public static int getRandom(int min, int max) {
    	int result = min + (int)(Math.random() * (max - min));
    	return result;
    }
    
    /**
     * 保留两位小数
     * @param d
     * @return
     */
    public static double cutDecimal(double d) {
		DecimalFormat df = new DecimalFormat("#.00");  
		String str = df.format(d);
		d = Double.parseDouble(str);
		return d;
	}
    
}
