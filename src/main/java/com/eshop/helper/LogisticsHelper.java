package com.eshop.helper;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.eshop.model.Order;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.kuaidi100.util.HttpRequest;
import com.kuaidi100.util.JacksonHelper;
import com.kuaidi100.util.NoticeRequest;
import com.kuaidi100.util.NoticeResponse;
import com.kuaidi100.util.Result;
import com.kuaidi100.util.TaskRequest;
import com.kuaidi100.util.TaskResponse;

/**
 * 物流辅助类
 * 该物流辅助类使用快递100提供的接口，所以使用该类之前，请先确认是否已申请快递100的接口
 * @author TangYiFeng
 */
public class LogisticsHelper {
	private static String key = "IsqYfONo8558";  //秘钥
	private static Logger logger = Logger.getLogger(LogisticsHelper.class);
	
	/**
	 * 提交订阅请求
	 * @param company 快递公司编号
	 * @param expressCode 快递单号
	 * @return 0未提交订阅请求，1订阅成功，2订阅失败
	 */
	public static int postOrder(String company, String expressCode) {
		TaskRequest req = new TaskRequest();
		req.setCompany(company);
		req.setNumber(expressCode); 
		req.getParameters().put("callbackurl", PropKit.use("callBackUrl.txt").get("apiHostName") + PropKit.use("callBackUrl.txt").get("logisticsNotifyUrl"));
		req.setKey(key);
		
		HashMap<String, String> p = new HashMap<String, String>(); 
		p.put("schema", "json");
		p.put("param", JacksonHelper.toJSON(req));
		
		logger.info("快递100param=" + JacksonHelper.toJSON(req));
		
		try {
			String ret = HttpRequest.postData("http://www.kuaidi100.com/poll", p, "UTF-8");
			TaskResponse resp = JacksonHelper.fromJSON(ret, TaskResponse.class);
			
			logger.info("expressCode="+expressCode+","+ret);
			
			if(resp.getResult()==true){
				return 1;
			}else{
				return 2;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("订阅异常="+e.getMessage());
			return 0;
		}
	}
	
	/**
	 * 回调请求
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public static void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		NoticeResponse resp = new NoticeResponse();
		resp.setResult(false);
		resp.setReturnCode("500");
		resp.setMessage("保存失败");
		try {
			String param = request.getParameter("param");
			NoticeRequest nReq = JacksonHelper.fromJSON(param,
					NoticeRequest.class);
			
			Result result = nReq.getLastResult();
			String status = result.getStatus(); //监控状态:polling:监控中，shutdown:结束，abort:中止，updateall：重新推送
			String nu = result.getNu(); //快递单号
			
			if (!status.equals("abort")) {
				Order order = Order.dao.findFirst("select * from `order` where expressCode = ? and expressCode is not null", nu);
				order.setExpressDetail(param);
				order.update();
			}
			
			resp.setResult(true);
			resp.setReturnCode("200");
			response.getWriter().print(JacksonHelper.toJSON(resp)); //这里必须返回，否则认为失败，过30分钟又会重复推送。
		} catch (Exception e) {
			resp.setMessage("保存失败" + e.getMessage());
			response.getWriter().print(JacksonHelper.toJSON(resp));//保存失败，服务端等30分钟会重复推送。
		}
	}
	
	/**
	 * 通知处理
	 * @param param
	 */
	public static void notifyHandle(String param) {
		NoticeRequest nReq = JacksonHelper.fromJSON(param, NoticeRequest.class);
		
		Result result = nReq.getLastResult();
		String status = result.getStatus(); //监控状态:polling:监控中，shutdown:结束，abort:中止，updateall：重新推送
		String nu = result.getNu(); //快递单号
		
		if (!status.equals("abort")) {
			Db.update("update `order` set expressDetail = ? where expressCode = ? and expressCode is not null", param, nu);
		}
	}
	
}
