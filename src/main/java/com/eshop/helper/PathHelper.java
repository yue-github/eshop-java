package com.eshop.helper;

public class PathHelper {

	/**
	 * 相对路径转换为绝对路径
	 * @param baseUrl
	 * @param path
	 * @return
	 */
	public static String getAbsolutePath(String baseUrl, String path) {
    	if (path.indexOf("http") != -1) {
    		return path;
    	}
    	
    	return baseUrl + path;
    }
	
}
