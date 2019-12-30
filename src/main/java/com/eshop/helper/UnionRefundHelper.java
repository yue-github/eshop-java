package com.eshop.helper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

import com.unionpay.acp.sdk.AcpService;
import com.unionpay.acp.sdk.LogUtil;
import com.unionpay.acp.sdk.SDKConfig;
/**
 * 银联支付辅助类
 *   @author TangYiFeng
 */
public class UnionRefundHelper {
	//商户号
	public static String merId = "898440353312737";
	
	//默认配置的是UTF-8
	public static String encoding_UTF8 = "UTF-8";
	
	public static String encoding_GBK = "GBK";
	//全渠道固定值
	public static String version = "5.0.0";
	
	//后台服务对应的写法参照 FrontRcvResponse.java
	public static String frontUrl = "https://www.eebin.com";

	//后台服务对应的写法参照 BackRcvResponse.java
	public static String backUrl = "https://service.eebin.com";//受理方和发卡方自选填写的域[O]--后台通知地址

	// 商户发送交易时间 格式:YYYYMMDDhhmmss
	public static String getCurrentTime() {
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	}
	
	//日志
	private static Logger logger = Logger.getLogger(UnionRefundHelper.class);
	
	/**
	 * 退款查询
	 * @param orderId
	 * @param txnTime
	 * @param refundType (1退款，2退货)
	 */
	public void refundQuery(String orderId, String txnTime, int refundType) {
		SDKConfig.getConfig().loadPropertiesFromSrc();
		
		Map<String, String> data = new HashMap<String, String>();
		
		/***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
		data.put("version", version);                 //版本号
		data.put("encoding", encoding_UTF8);               //字符集编码 可以使用UTF-8,GBK两种方式
		data.put("signMethod", "01");                          //签名方法 目前只支持01-RSA方式证书加密
		data.put("txnType", "00");                             //交易类型 00-默认
		data.put("txnSubType", "00");                          //交易子类型  默认00
		data.put("bizType", "000201");                         //业务类型 B2C网关支付，手机wap支付
		
		/***商户接入参数***/
		data.put("merId", merId);                  //商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
		data.put("accessType", "0");                           //接入类型，商户接入固定填0，不需修改
		
		/***要调通交易以下字段必须修改***/
		data.put("orderId", orderId);                 //****商户订单号，每次发交易测试需修改为被查询的交易的订单号
		data.put("txnTime", txnTime);                 //****订单发送时间，每次发交易测试需修改为被查询的交易的订单发送时间

		/**请求参数设置完毕，以下对请求参数进行签名并发送http post请求，接收同步应答报文------------->**/
		
		Map<String, String> reqData = AcpService.sign(data,encoding_UTF8);//报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
		
		String url = SDKConfig.getConfig().getSingleQueryUrl();// 交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.singleQueryUrl
		//这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
		Map<String, String> rspData = AcpService.post(reqData,url,encoding_UTF8);
		
		/**对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考------------->**/
		//应答码规范参考open.unionpay.com帮助中心 下载  产品接口规范  《平台接入接口规范-第5部分-附录》
		if(!rspData.isEmpty()){
			if(AcpService.validate(rspData, encoding_UTF8)){
				LogUtil.writeLog("验证签名成功");
				if("00".equals(rspData.get("respCode"))){//如果查询交易成功
					//处理被查询交易的应答码逻辑
					String origRespCode = rspData.get("origRespCode");
					if("00".equals(origRespCode)){
						//交易成功，更新商户订单状态
						AlipayRefundHelper.handleRefundSuccess(refundType, orderId);
					}else if("03".equals(origRespCode) ||
							 "04".equals(origRespCode) ||
							 "05".equals(origRespCode)){
						//需再次发起交易状态查询交易 
						//TODO
					}else{
						//其他应答码为失败请排查原因
						AlipayRefundHelper.handleRefundFail(refundType, orderId);
					}
				}else{//查询交易本身失败，或者未查到原交易，检查查询交易报文要素
					//TODO
				}
			}else{
				LogUtil.writeErrorLog("验证签名失败");
				//TODO 检查验证签名失败的原因
			}
		}else{
			//未返回正确的http状态
			LogUtil.writeErrorLog("未获取到返回报文或返回http状态码非200");
		}
	}
	
	/**
	 * 银联退款
	 * @param origQryId
	 * @param txnAmt
	 * @param orderId
	 * @param notifyUrl
	 * @return
	 */
	public boolean refund(String origQryId, String txnAmt, String orderId, String notifyUrl) {
		SDKConfig.getConfig().loadPropertiesFromSrc();
		
		Map<String, String> data = new HashMap<String, String>();
		
		/***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
		data.put("version", version);               //版本号
		data.put("encoding", encoding_UTF8);             //字符集编码 可以使用UTF-8,GBK两种方式
		data.put("signMethod", "01");                        //签名方法 目前只支持01-RSA方式证书加密
		data.put("txnType", "04");                           //交易类型 04-退货		
		data.put("txnSubType", "00");                        //交易子类型  默认00		
		data.put("bizType", "000201");                       //业务类型 B2C网关支付，手机wap支付	
		data.put("channelType", "07");                       //渠道类型，07-PC，08-手机		
		
		/***商户接入参数***/
		data.put("merId", merId);                //商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
		data.put("accessType", "0");                         //接入类型，商户接入固定填0，不需修改		
		data.put("orderId", orderId);          //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则，重新产生，不同于原消费		
		data.put("txnTime", getCurrentTime());      //订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效		
		data.put("currencyCode", "156");                     //交易币种（境内商户一般是156 人民币）		
		data.put("txnAmt", txnAmt);                          //****退货金额，单位分，不要带小数点。退货金额小于等于原消费金额，当小于的时候可以多次退货至退货累计金额等于原消费金额		
		//data.put("reqReserved", "透传信息");                  //请求方保留域，如需使用请启用即可；透传字段（可以实现商户自定义参数的追踪）本交易的后台通知,对本交易的交易状态查询交易、对账文件中均会原样返回，商户可以按需上传，长度为1-1024个字节		
		data.put("backUrl", backUrl);               //后台通知地址，后台通知参数详见open.unionpay.com帮助中心 下载  产品接口规范  网关支付产品接口规范 退货交易 商户通知,其他说明同消费交易的后台通知
		/***要调通交易以下字段必须修改***/
		data.put("origQryId", origQryId);      //****原消费交易返回的的queryId，可以从消费交易后台通知接口中或者交易状态查询接口中获取
		
		/**请求参数设置完毕，以下对请求参数进行签名并发送http post请求，接收同步应答报文------------->**/
		Map<String, String> reqData  = AcpService.sign(data,encoding_UTF8);//报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
		String url = SDKConfig.getConfig().getBackRequestUrl();//交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.backTransUrl

		Map<String, String> rspData = AcpService.post(reqData,url,encoding_UTF8);//这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
		
		boolean flag = false;
		
		/**对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考------------->**/
		//应答码规范参考open.unionpay.com帮助中心 下载  产品接口规范  《平台接入接口规范-第5部分-附录》
		if(!rspData.isEmpty()){
			if(AcpService.validate(rspData, encoding_UTF8)){
				logger.info("验证签名成功12211");
				String respCode = rspData.get("respCode");
				if("00".equals(respCode)){
					//交易已受理，等待接收后台通知更新订单状态,也可以主动发起 查询交易确定交易状态。
					//TODO
					flag = true;
					logger.info("报0000");
				}else if("03".equals(respCode)||
						 "04".equals(respCode)||
						 "05".equals(respCode)){
					//后续需发起交易状态查询交易确定交易状态
					//TODO
					flag = true;
					logger.info("报05");
				}else{
					//其他应答码为失败请排查原因
					//TODO
					logger.info("状态码="+respCode);
				}
			}else{
				logger.info("验证签名失败");
				//TODO 检查验证签名失败的原因
			}
		}else{
			//未返回正确的http状态
			logger.info("未获取到返回报文或返回http状态码非200");
		}
		
		return flag;
	}
}
