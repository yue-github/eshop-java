package com.eshop.log;

import org.apache.log4j.Logger;

public class Log {
	
	private static Logger logger = Logger.getLogger("Log");
	
	public static void info(String message) {
		logger.info(message);
	}
	
	public static void warn(String message) {
		logger.warn(message);
	}
	
	public static void error(String message) {
		logger.error(message);
	}
	
}
