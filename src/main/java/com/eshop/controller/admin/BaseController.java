package com.eshop.controller.admin;


import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.eshop.helper.DateHelper;
import com.eshop.model.Resource;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Model;

import jodd.util.URLDecoder;

/**
 * 控制器基类
 * @author TangYiFeng
 */
public class BaseController extends Controller {

	/**
	 * 用来返回的json对象，初始化为{error: 0}
	 */
	public Map<String, Object> jsonObject;
	
	/**
	 * 错误代码，分别为成功、异常、验证错误及功能错误
	 * @author Yang
	 *
	 */
	public enum ErrorCode {
		Success, Exception, Validation, Authority, Function
	}

	/**
	 * 构造方法，初始化jsonObject对象为{error: 0}
	 */
	public BaseController() {
		jsonObject = new HashMap<String, Object>();
		jsonObject.put("error", ErrorCode.Success.ordinal());
	}
	
	/**
	 * 返回错误信息
	 * 内部会调用renderJson返回json格式的错误信息
	 * @param err 错误编码
	 * @param msg 错误文本
	 * @return 始终返回fals
	 */
	public boolean returnError(ErrorCode err, String msg) {
		jsonObject.put("error", err.ordinal());
		jsonObject.put("errmsg", msg);
		renderJson(jsonObject);

		return false;
	}
	
	public void setError(ErrorCode err, String msg) {
		jsonObject.put("error", err.ordinal());
		jsonObject.put("errmsg", msg);
	}
	
	public void setError(int err, String msg) {
		jsonObject.put("error", err);
		jsonObject.put("errmsg", msg);
	}
	
	/**
	 * 返回错误信息
	 * 内部会调用renderJson返回json格式的错误信息
	 * @param err 错误编码
	 * @param msg 错误文本
	 * @return 始终返回fals
	 */
	public boolean returnError(int errCode, String msg) {
		jsonObject.put("error", errCode);
		jsonObject.put("errmsg", msg);
		renderJson(jsonObject);
		return false;
	}

	/**
	 * 验证字符串不能空
	 * @param name 参数名称
	 * @return 成功：true；失败：false，并调用returnError
	 */
	protected boolean validateRequiredString(String name) {
		String v = getPara(name);

		if (v == null || "".equals(v.trim())) {
			return returnError(ErrorCode.Validation, name + "字符串不能空");
		}

		return true;
	}

	/**
	 * 验证字符串长度
	 * @param name 参数名称
	 * @param min 最小长度
	 * @param max 最大长度
	 * @return 成功：true；失败：false，并调用returnError
	 */
	protected boolean validateStringLength(String name, int min, int max) {
		String v = getPara(name);
		int l = v.length();

		if (l > max ) {
			return returnError(ErrorCode.Validation, name + "字符串太长");
		}
		
		if (l < min) {
			return returnError(ErrorCode.Validation, name + "字符串太短");
		}

		return true;
	}
	
	/**
	 * 验证手机号
	 * @param name 参数名称
	 * @return
	 */
	protected boolean validatePhone(String name) {
		String v = getPara(name);

		return true;
	}
	
	/**
	 * 验证邮箱格式
	 * @param name 参数名称
	 * @return 成功：true；失败：false，并调用returnError
	 */
	protected boolean validateEmail(String name) {
		String v = getPara(name);
		if(!Pattern.matches("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$", v)) {
			return returnError(ErrorCode.Validation, "邮箱格式不正确");
		}
		return true;
	}
	
	/**
	 * 验证密码复杂度
	 * @param name 参数名称
	 * @return 成功：true；失败：false，并调用returnError
	 */
	protected boolean validatePassword(String name) {
		String v = getPara(name);
		if(!Pattern.matches("^(?![\\d]+$)(?![a-zA-Z]+$)(?![!#$%^&*]+$)[\\da-zA-Z!#$%^&*]{8,50}$", v)) {
			return returnError(ErrorCode.Validation, "密码太简单，必须字符、数字和符号中的任意两种组合");
		}
		return true;
	}
	
	/**
	 * 获取域名
	 * @return 域名:端口
	 */
	public String getHostName() {
		HttpServletRequest request = this.getRequest();
		
		String port = "";
    	if (request.getServerPort() != 80) {
    		port = ":" +request.getServerPort();
    	}
		
		return "http://" +request.getServerName() + port;
	
	}
	
	/**
	 * 获取系统路径
	 * @return 系统路径
	 */
	protected String getBaseUrl() {
    	HttpServletRequest request = this.getRequest();
    	
    	String port = "";
    	if (request.getServerPort() != 80) {
    		port = ":" +request.getServerPort();
    	}
		
//		return "http://" +request.getServerName() + port + "/";
		return "http://" +request.getServerName() + port + request.getContextPath() + "/";
	}
	
	protected <M extends Model<M>> List<Object[]> toTableData(List<M> list) {
		List<Object[]> data = new ArrayList<Object[]>();
    	
    	for(M item: list) {
    		data.add(item._getAttrValues());
    	}
    	
    	return data;
	}
	
	@Override
	public String getPara(String name) {
		String p =  this.getAttr(name);
		
		if(p == null || p.equals("") || p.equals("undefined")) {
			p = super.getPara(name);
		}
		
		return p;
	}
	
	public BigDecimal getParaToDecimal(String name) {
		return new BigDecimal(this.getPara(name));
	}
	
	public BigDecimal getParaToDecimalDefault(String name) {
		if (getPara(name) == null) {
			return null;
		}
		
		return getParaToDecimal(name);
	}
	
	public int getParaToIntDefault(String name) {
		if (getPara(name) == null) {
			return 0;
		}
		
		return getParaToInt(name);
	}
	
	public Integer getParaToIntegerDefault(String name) {
		if (getPara(name) == null) {
			return null;
		}
		
		return getParaToInt(name);
	}
	
	public Double getParaToDoubleDefault(String name) {
		if (getPara(name) == null || "".equals(getPara(name))) {
			return null;
		}
		
		return getParaToDecimal(name).doubleValue();
	}
	
	public String getParaToDateTimeDefault(String name) {
		if (getPara(name) == null) {
			return null;
		}
		
		Date date = getParaToDate(name);
		return DateHelper.formatDateTime(date);
	}
	
	public String getParaToDateStr(String name) {
		if (getPara(name) == null || getPara(name).equals("")) {
			return null;
		}
		
		Date date = getParaToDate(name);
		return DateHelper.formatDate(date, "yyyy-MM-dd");
	}
	
	public Date getParaToDateDefault(String name) {
		if (getPara(name) == null) {
			return null;
		}
		
		return getParaToDate(name);
	}
	
	public String getResourcePath(String path) {
		if(path != null && !path.equals("") && path.indexOf("http://") < 0) {
			path = this.getBaseUrl() + path;
        }
		
		return path;
	}
	
	public double cutDecimal(double d) {
		DecimalFormat df = new DecimalFormat("#.00");  
		String str = df.format(d);
		d = Double.parseDouble(str);
		return d;
	}
	
	public String getResourcePathById(int id) {
		Resource resource = Resource.dao.findById(id);
		
		String path = (resource != null) ? resource.getPath() : "";
		path = getResourcePath(path);
		
		return path;
	}
	
	/**
	 * 转换路径
	 * @param path
	 * @return
	 */
	public String getPath(String path) {
		HttpServletRequest request = this.getRequest();
		String serverName = request.getServerName();
    	
    	String port = "";
//    	System.out.println(request.getServerPort());
    	if (request.getServerPort() != 80) {
    		port = ":" +request.getServerPort();
    	}
		if(serverName.indexOf("coral3") > 1){
			port = "";
		}
    	String contextPath = "";
    	if (serverName.indexOf("eebin") == -1) {
    		contextPath = request.getContextPath();
    	}
		
    	String scheme = request.getScheme() + "://";
		String url = scheme + serverName + port + contextPath + "/" + path;
		
		return url;
	}
	
	/**
	 * 获取协议名称 https或http
	 * @return
	 */
	public String getScheme() {
		HttpServletRequest request = this.getRequest();
		return request.getScheme();
	}
	
	/**
	 * 验证字段是否必填，通过返回true，否则返回false
	 * @param params
	 * @return
	 */
	public boolean validate(String[] params) {
		for (String item : params) {
			if (!validateRequiredString(item)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean validateRequiredAndLength(String[] params) {
		for (String item : params) {
			if (!validateRequiredString(item)) {
				return false;
			}
			
			if(!validateStringLength(item, 8, 50)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isEmpty(String name) {
		if (getPara(name) == null || getPara(name).equals("")) {
			return true;
		}
		return false;
	}
	
}
