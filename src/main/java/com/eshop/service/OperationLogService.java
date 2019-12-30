package com.eshop.service;

import java.util.Date;
import com.eshop.model.OperationLog;

import com.eshop.model.User;

public class OperationLogService {
	public void create(User user, String ip, String description, String data, String type) {
		OperationLog log = new OperationLog();
		 log.setUserId(user.getId());
		 log.setUserName(user.getUserName());
		 log.setIp(ip);
		 log.setData("");
		 log.setDescription(description);
		 log.setCreatedAt(new Date());
		 log.setType(type);
		 log.save();
	}
}
