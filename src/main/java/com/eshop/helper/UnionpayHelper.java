package com.eshop.helper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eshop.model.Order;
import com.eshop.model.Shop;
import com.eshop.model.Wallet;
import com.eshop.model.dao.BaseDao;
import com.eshop.service.Member;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Db;
import com.unionpay.acp.sdk.AcpService;
import com.unionpay.acp.sdk.LogUtil;
import com.unionpay.acp.sdk.SDKConfig;
import com.unionpay.acp.sdk.SDKConstants;

/**
 * 银联支付辅助类
 *   @author TangYiFeng
 */
public class UnionpayHelper {
	//商户号
	public static String merId = "898440353312737";
	
	//默认配置的是UTF-8
	public static String encoding_UTF8 = "UTF-8";
	
	public static String encoding_GBK = "GBK";
	//全渠道固定值
	public static String version = "5.0.0";
	
	//后台服务对应的写法参照 FrontRcvResponse.java
	public static String frontUrl = PropKit.use("callBackUrl.txt").get("frontHostName");

	//后台服务对应的写法参照 BackRcvResponse.java
	public static String backUrl = PropKit.use("callBackUrl.txt").get("apiHostName") + PropKit.use("callBackUrl.txt").get("unionPayNotifyUrl");//受理方和发卡方自选填写的域[O]--后台通知地址

	private final static Logger logger = Logger.getLogger("");
	
	// 商户发送交易时间 格式:YYYYMMDDhhmmss
	public static String getCurrentTime() {
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	}
	
	// AN8..40 商户订单号，不能含"-"或"_"
	public static String getOrderId() {
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	}
	
	/**
	 * 统一下单
	 * @param txnAmt 金额 单位分
	 * @param orderId 商户订单号
	 * @return
	 */
	public static String getHtml(String txnAmt, String orderId) {
		String html = getHtml(txnAmt, orderId, frontUrl, backUrl);
		return html;
	}
	
	/**
	 * 统一下单
	 * @param txnAmt 金额
	 * @param orderId 商户订单号
	 * @param theFrontUrl
	 * @param theBackUrl
	 * @return
	 */
	public static String getHtml(String txnAmt, String orderId, String theFrontUrl, String theBackUrl) {
		Map<String, String> requestData = new HashMap<String, String>();
		
		/***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
		requestData.put("version", version);   			  //版本号，全渠道默认值
		requestData.put("encoding", encoding_UTF8); 			  //字符集编码，可以使用UTF-8,GBK两种方式
		requestData.put("signMethod", "01");            			  //签名方法，只支持 01：RSA方式证书加密
		requestData.put("txnType", "01");               			  //交易类型 ，01：消费
		requestData.put("txnSubType", "01");            			  //交易子类型， 01：自助消费
		requestData.put("bizType", "000201");           			  //业务类型，B2C网关支付，手机wap支付
		requestData.put("channelType", "07");           			  //渠道类型，这个字段区分B2C网关支付和手机wap支付；07：PC,平板  08：手机
		
		/***商户接入参数***/
		requestData.put("merId", merId);    	          			  //商户号码，请改成自己申请的正式商户号或者open上注册得来的777测试商户号
		requestData.put("accessType", "0");             			  //接入类型，0：直连商户 
		requestData.put("orderId", orderId);             //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则		
		requestData.put("txnTime", getCurrentTime());        //订单发送时间，取系统时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
		requestData.put("currencyCode", "156");         			  //交易币种（境内商户一般是156 人民币）		
		requestData.put("txnAmt", txnAmt);             			      //交易金额，单位分，不要带小数点
		//requestData.put("reqReserved", "透传字段");        		      //请求方保留域，如需使用请启用即可；透传字段（可以实现商户自定义参数的追踪）本交易的后台通知,对本交易的交易状态查询交易、对账文件中均会原样返回，商户可以按需上传，长度为1-1024个字节		
		
		//前台通知地址 （需设置为外网能访问 http https均可），支付成功后的页面 点击“返回商户”按钮的时候将异步通知报文post到该地址
		//如果想要实现过几秒中自动跳转回商户页面权限，需联系银联业务申请开通自动返回商户权限
		//异步通知参数详见open.unionpay.com帮助中心 下载  产品接口规范  网关支付产品接口规范 消费交易 商户通知
		requestData.put("frontUrl", theFrontUrl);
		
		//后台通知地址（需设置为【外网】能访问 http https均可），支付成功后银联会自动将异步通知报文post到商户上送的该地址，失败的交易银联不会发送后台通知
		//后台通知参数详见open.unionpay.com帮助中心 下载  产品接口规范  网关支付产品接口规范 消费交易 商户通知
		//注意:1.需设置为外网能访问，否则收不到通知    2.http https均可  3.收单后台通知后需要10秒内返回http200或302状态码 
		//    4.如果银联通知服务器发送通知后10秒内未收到返回状态码或者应答码非http200，那么银联会间隔一段时间再次发送。总共发送5次，每次的间隔时间为0,1,2,4分钟。
		//    5.后台通知地址如果上送了带有？的参数，例如：http://abc/web?a=b&c=d 在后台通知处理程序验证签名之前需要编写逻辑将这些字段去掉再验签，否则将会验签失败
		requestData.put("backUrl", theBackUrl);
		
		//////////////////////////////////////////////////
		//
		//       报文中特殊用法请查看 PCwap网关跳转支付特殊用法.txt
		//
		//////////////////////////////////////////////////
		SDKConfig.getConfig().loadPropertiesFromSrc();
		/**请求参数设置完毕，以下对请求参数进行签名并生成html表单，将表单写入浏览器跳转打开银联页面**/
		Map<String, String> submitFromData = AcpService.sign(requestData,encoding_UTF8);  //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
		
		
		String requestFrontUrl = SDKConfig.getConfig().getFrontRequestUrl();  //获取请求银联的前台地址：对应属性文件acp_sdk.properties文件中的acpsdk.frontTransUrl
		String html = AcpService.createAutoFormHtml(requestFrontUrl, submitFromData,encoding_UTF8);   //生成自动跳转的Html表单
		
		LogUtil.writeLog("打印请求HTML，此为请求报文，为联调排查问题的依据："+html);
		//将生成的html写到浏览器中完成自动跳转打开银联支付页面；这里调用signData之后，将html写到浏览器跳转到银联页面之前均不能对html中的表单项的名称和值进行修改，如果修改会导致验签不通过
		return html;
	}
	
	/**
	 * 获取请求参数中所有的信息
	 * 
	 * @param request
	 * @return
	 */
	public static Map<String, String> getAllRequestParam(final HttpServletRequest request) {
		Map<String, String> res = new HashMap<String, String>();
		Enumeration<?> temp = request.getParameterNames();
		if (null != temp) {
			while (temp.hasMoreElements()) {
				String en = (String) temp.nextElement();
				String value = request.getParameter(en);
				res.put(en, value);
				//在报文上送时，如果字段的值为空，则不上送<下面的处理为在获取所有参数数据时，判断若值为空，则删除这个字段>
				//System.out.println("ServletUtil类247行  temp数据的键=="+en+"     值==="+value);
				if (null == res.get(en) || "".equals(res.get(en))) {
					res.remove(en);
				}
			}
		}
		return res;
	}
	
	/**
	 * 订单支付回调
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	public static void backRcvResponse(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		SDKConfig.getConfig().loadPropertiesFromSrc();
		
		LogUtil.writeLog("BackRcvResponse接收后台通知开始");
		
		String encoding = req.getParameter(SDKConstants.param_encoding);
		// 获取银联通知服务器发送的后台通知参数
		Map<String, String> reqParam = UnionpayHelper.getAllRequestParam(req);
		LogUtil.printRequestLog(reqParam);

		Map<String, String> valideData = null;
		if (null != reqParam && !reqParam.isEmpty()) {
			Iterator<Entry<String, String>> it = reqParam.entrySet().iterator();
			valideData = new HashMap<String, String>(reqParam.size());
			while (it.hasNext()) {
				Entry<String, String> e = it.next();
				String key = e.getKey();
				String value = e.getValue();
				value = new String(value.getBytes(encoding), encoding);
				valideData.put(key, value);
				logger.info("value="+value);
			}
		}

		//重要！验证签名前不要修改reqParam中的键值对的内容，否则会验签不过
		if (!AcpService.validate(valideData, encoding)) {
			LogUtil.writeLog("验证签名结果[失败].");
			//验签失败，需解决验签问题
			logger.info("valideData="+valideData.toString());
			logger.info("encoding="+encoding);
			logger.info("验签失败，需解决验签问题");
			
		} else {
			LogUtil.writeLog("验证签名结果[成功].");
			//【注：为了安全验签成功才应该写商户的成功处理逻辑】交易成功，更新商户订单状态
			
			String orderId =valideData.get("orderId"); //获取后台通知的数据，其他字段也可用类似方式获取
			String respCode =valideData.get("respCode"); //获取应答码，收到后台通知了respCode的值一般是00，可以不需要根据这个应答码判断。
			String queryId = valideData.get("queryId"); //消费交易的流水号，供后续查询用
			
			if(orderId != null && !orderId.equals("")) {
				List<Order> orders = Order.dao.find("select * from `order` where theSameOrderNum = ? or order_code = ?", orderId, orderId);
				for (Order order : orders) {
					if (order.getStatus() == BaseDao.UNPAY) {
			    		Db.update("update `order` set status = ?, tradeNo = ?, payTime = ? where id = ?", BaseDao.PAYED, queryId, new Date(), order.getId());
					}
				}
//				Db.update("update `order` set status = ?, tradeNo = ?, payTime = ? where theSameOrderNum = ? or order_code = ?", BaseDao.PAYED, queryId, new Date(), orderId, orderId);
				//更新商品销售量和销售额
				Member.updateProduct(orderId);
				//客户下单成功，通知商家
	    		Shop shop = Member.getShopByOrderCode(orderId);
	    		Member.informShop(shop, "【乐驿商城】订单支付成功啦，请及时处理订单", "【乐驿商城】您有一笔订单支付成功提醒", "【乐驿商城】订单支付成功啦，请及时处理订单");
			}
			
			logger.info("成功");
		}
		LogUtil.writeLog("BackRcvResponse接收后台通知结束");
		//返回给银联服务器http 200  状态码
		resp.getWriter().print("ok");
	}
	
	/**
	 * 银联充值回调
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	public static void backRcvResponseRecharge(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		SDKConfig.getConfig().loadPropertiesFromSrc();
		
		LogUtil.writeLog("BackRcvResponse接收后台通知开始");
		
		String encoding = req.getParameter(SDKConstants.param_encoding);
		// 获取银联通知服务器发送的后台通知参数
		Map<String, String> reqParam = getAllRequestParam(req);
		LogUtil.printRequestLog(reqParam);

		Map<String, String> valideData = null;
		if (null != reqParam && !reqParam.isEmpty()) {
			Iterator<Entry<String, String>> it = reqParam.entrySet().iterator();
			valideData = new HashMap<String, String>(reqParam.size());
			while (it.hasNext()) {
				Entry<String, String> e = it.next();
				String key = e.getKey();
				String value = e.getValue();
				value = new String(value.getBytes(encoding), encoding);
				valideData.put(key, value);
			}
		}

		//重要！验证签名前不要修改reqParam中的键值对的内容，否则会验签不过
		if (!AcpService.validate(valideData, encoding)) {
			LogUtil.writeLog("验证签名结果[失败].");
			//验签失败，需解决验签问题
			
		} else {
			LogUtil.writeLog("验证签名结果[成功].");
			//【注：为了安全验签成功才应该写商户的成功处理逻辑】交易成功，更新商户订单状态
			
			String orderId =valideData.get("orderId"); //获取后台通知的数据，其他字段也可用类似方式获取
			String respCode =valideData.get("respCode"); //获取应答码，收到后台通知了respCode的值一般是00，可以不需要根据这个应答码判断。
			String queryId = valideData.get("queryId"); //消费交易的流水号，供后续查询用
			
			Wallet wallet = Wallet.dao.findFirst("select * from wallet where tradeNo = ?", orderId);
			
			if (wallet != null) {
				wallet.setIsPaySuccess(1);
				wallet.setFinishTime(new Date());
				wallet.setUpdatedAt(new Date());
				wallet.setTransactionId(queryId);
				wallet.update();
			}
			
		}
		LogUtil.writeLog("BackRcvResponse接收后台通知结束");
		//返回给银联服务器http 200  状态码
		resp.getWriter().print("ok");
	}
	
}
