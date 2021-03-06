package com.eshop.helper;

import org.apache.log4j.Logger;

import com.eshop.model.Back;
import com.eshop.model.ProductOrder;
import com.eshop.model.Refund;
import com.tencent.WXPay;
import com.tencent.business.RefundBusiness.ResultListener;
import com.tencent.common.Configure;
import com.tencent.protocol.refund_protocol.RefundReqData;
import com.tencent.protocol.refund_protocol.RefundResData;
import com.tencent.protocol.refund_query_protocol.RefundQueryResData;


/**
 * 微信支付辅助类
 * @author TangYiFeng
 */
public class WxRefundHelper {
	
	private static Logger logger = Logger.getLogger(WxRefundHelper.class);
	private WxRefundQueryBusinessHelper wxRefundQueryBusinessHelper = new WxRefundQueryBusinessHelper();
	
	/**
	 * 查询退款状态  -公众号和微信号配置
	 * @param outTradeNo 商家订单号
	 * @param outRefundNo 商家退款单号
	 * @param refundType 退款类型(1退款，2退货)
	 */
	public void refundQuery(String outTradeNo, String outRefundNo, int refundType) {
		String key = "tongyiweixin1409564602TYWX123456";				//签名算法需要用到的秘钥,也就是商户秘钥
	    String appID = "wxdfc5bbcfe8f4a919";        					//公众账号ID
		String mchID = "1409564602";									//商户ID
		
		//初始化配置参数
		Configure.setAppID(appID);
		Configure.setMchID(mchID);
		Configure.setKey(key);
		
		//执行退款接口
		try {
			if (refundType == 1) {
				wxRefundQueryBusinessHelper.refundQueryBusiness(outTradeNo, outRefundNo, new RefundQueryListener());
			} else if (refundType == 2) {
				wxRefundQueryBusinessHelper.refundQueryBusiness(outTradeNo, outRefundNo, new BackQueryListener());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 查询退款状态  -android
	 * @param outTradeNo 商家订单号
	 * @param outRefundNo 商家退款单号
	 * @param refundType 退款类型(1退款，2退货)
	 */
	public void refundQueryAndroid(String outTradeNo, String outRefundNo, int refundType) {
		String key = "tongyiweixin1420395402TYWX123456";				//签名算法需要用到的秘钥,也就是商户秘钥
	    String appID = "wxde28eec7c493e34f";        					//公众账号ID
		String mchID = "1420395402";									//商户ID
		
		//初始化配置参数
		Configure.setAppID(appID);
		Configure.setMchID(mchID);
		Configure.setKey(key);
		
		//执行退款接口
		try {
			if (refundType == 1) {
				wxRefundQueryBusinessHelper.refundQueryBusiness(outTradeNo, outRefundNo, new RefundQueryListener());
			} else if (refundType == 2) {
				wxRefundQueryBusinessHelper.refundQueryBusiness(outTradeNo, outRefundNo, new BackQueryListener());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 退款状态查询监听器  -退款
	 * @author Administrator
	 *
	 */
	class RefundQueryListener implements com.eshop.helper.WxRefundQueryBusinessHelper.ResultListener {
        //API返回ReturnCode不合法，支付请求逻辑错误，请仔细检测传过去的每一个参数是否合法，或是看API能否被正常访问
        @Override
		public void onFailByReturnCodeError(RefundQueryResData refundQueryResData, String out_refund_no) {
        	
        }

        //API返回ReturnCode为FAIL，支付API系统返回失败，请检测Post给API的数据是否规范合法
        @Override
		public void onFailByReturnCodeFail(RefundQueryResData refundQueryResData, String out_refund_no) {
        	
        }

        //支付请求API返回的数据签名验证失败，有可能数据被篡改了
        @Override
		public void onFailBySignInvalid(RefundQueryResData refundQueryResData, String out_refund_no) {
        	
        }

        
        //退款查询失败
        @Override
		public void onRefundQueryFail(RefundQueryResData refundQueryResData, String out_refund_no) {
        	
        }

        //退款查询成功
        @Override
		public void onRefundQuerySuccess(RefundQueryResData refundQueryResData, String out_refund_no) {
        	//商户退款单号
        	String outRefundNo = refundQueryResData.getOut_refund_no();
        	//退款状态
        	String refundStatus = refundQueryResData.getRefund_status();
        	logger.info("退货成功处理");
        	AlipayRefundHelper.handleRefundSuccess(1, out_refund_no);
        }
    }
	
	/**
	 * 退款状态查询监听器  -退货
	 * @author Administrator
	 *
	 */
	class BackQueryListener implements com.eshop.helper.WxRefundQueryBusinessHelper.ResultListener {
        //API返回ReturnCode不合法，支付请求逻辑错误，请仔细检测传过去的每一个参数是否合法，或是看API能否被正常访问
        @Override
		public void onFailByReturnCodeError(RefundQueryResData refundQueryResData, String out_refund_no) {
        	
        }

        //API返回ReturnCode为FAIL，支付API系统返回失败，请检测Post给API的数据是否规范合法
        @Override
		public void onFailByReturnCodeFail(RefundQueryResData refundQueryResData, String out_refund_no) {
        	
        }

        //支付请求API返回的数据签名验证失败，有可能数据被篡改了
        @Override
		public void onFailBySignInvalid(RefundQueryResData refundQueryResData, String out_refund_no) {
        	
        }

        //退款查询失败
        @Override
		public void onRefundQueryFail(RefundQueryResData refundQueryResData, String out_refund_no) {
        	
        }

        //退款查询成功
        @Override
		public void onRefundQuerySuccess(RefundQueryResData refundQueryResData, String out_refund_no) {
        	//商户退款单号
        	String outRefundNo = refundQueryResData.getOut_refund_no();
        	//退款状态
        	String refundStatus = refundQueryResData.getRefund_status();
        	logger.info("退货成功处理");
        	AlipayRefundHelper.handleRefundSuccess(2, out_refund_no);
        }
    }
	
	/**
	 * pc和微信公众号退款
	 * @param outTradeNo
	 * @param outRefundNo
	 * @param totalFee
	 * @param refundFee
	 * @param refundType 退款类型(1退款，2退货)
	 * @return boolean
	 */
	public void refundPc(String outTradeNo, String outRefundNo, int totalFee, int refundFee, int refundType) {
		String key = "tongyiweixin1409564602TYWX123456";				//签名算法需要用到的秘钥,也就是商户秘钥
	    String appID = "wxdfc5bbcfe8f4a919";          					//公众账号ID
		String mchID = "1409564602";									//商户ID
		String sdbMchID = "";       									//子商户ID，受理模式必填
//		String certLocalPath = "/var/lib/tomcat/webapps/eshop/weixinPcCert/apiclient_cert.p12";  //(通译服务器使用)HTTP证书在服务器中的路径，用来加载证书用
		String certLocalPath = "/www/server/tomcat/webapps/eshop/weixinPcCert/apiclient_cert.p12";  //(吴同岳服务器使用)HTTP证书在服务器中的路径，用来加载证书用
		//String certLocalPath = "D:\\weixinPcCert\\apiclient_cert.p12";  //HTTP证书在服务器中的路径，用来加载证书用
		String certPassword = "1409564602";   							//HTTP证书的密码，默认等于MCHID
		
		//初始化配置参数
		WXPay.initSDKConfiguration(key, appID, mchID, sdbMchID, certLocalPath, certPassword);
		
		String transactionID = "";
		String deviceInfo = "127.0.0.1";
		String refundFeeType = "CNY";
		String opUserID = mchID;
		
		logger.info("退款中");
		
		//构造提交参数
		RefundReqData refundReqData = new RefundReqData(transactionID, outTradeNo, deviceInfo, outRefundNo, totalFee, refundFee, opUserID, refundFeeType);
		//执行退款接口
		try {
			if (refundType == 1) {
				WXPay.doRefundBusiness(refundReqData, new RefundListener());
			} else {
				WXPay.doRefundBusiness(refundReqData, new BackListener());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("退款错误信息："+e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * android App退款
	 * @param outTradeNo
	 * @param outRefundNo
	 * @param totalFee
	 * @param refundFee
	 * @param refundType 1退款，2退货
	 * @return
	 * @throws Exception
	 */
	public void refundAndroid(String outTradeNo, String outRefundNo, int totalFee, int refundFee, int refundType) {
		String key = "tongyiweixin1420395402TYWX123456";					//签名算法需要用到的秘钥,也就是商户秘钥
		String appID = "wxde28eec7c493e34f";          						//公众账号ID
		String mchID = "1420395402";										//商户ID
		String sdbMchID = "";       										//子商户ID，受理模式必填
		//String certLocalPath = "D:\\weixinAppCert\\apiclient_cert.p12";  	// HTTP证书在服务器中的路径，用来加载证书用
//		String certLocalPath = "/var/lib/tomcat/webapps/eshop/weixinAppCert/apiclient_cert.p12";  	// HTTP证书在服务器中的路径，用来加载证书用
		String certLocalPath = "/www/server/tomcat/webapps/eshop/weixinAppCert/apiclient_cert.p12";  	//(吴同岳服务器) HTTP证书在服务器中的路径，用来加载证书用
		String certPassword = "1420395402";   								//HTTP证书的密码，默认等于MCHID

		//初始化配置参数
		WXPay.initSDKConfiguration(key, appID, mchID, sdbMchID, certLocalPath, certPassword);
		
		String transactionID = "";
		String deviceInfo = "127.0.0.1";
		String refundFeeType = "CNY";
		String opUserID = mchID;
		
		//构造提交参数
		RefundReqData refundReqData = new RefundReqData(transactionID, outTradeNo, deviceInfo, outRefundNo, totalFee, refundFee, opUserID, refundFeeType);

		
		//执行退款接口
		try {
			if (refundType == 1) {
				WXPay.doRefundBusiness(refundReqData, new RefundListener());
			} else {
				WXPay.doRefundBusiness(refundReqData, new BackListener());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//退款业务监听器
	class RefundListener implements ResultListener {
		
		//API返回ReturnCode不合法，支付请求逻辑错误，请仔细检测传过去的每一个参数是否合法，或是看API能否被正常访问
		@Override
		public void onFailByReturnCodeError(RefundResData arg0) {
			// TODO Auto-generated method stub
			logger.info("申请退款接口：API返回ReturnCode不合法");
		}

		//API返回ReturnCode为FAIL，支付API系统返回失败，请检测Post给API的数据是否规范合法
		@Override
		public void onFailByReturnCodeFail(RefundResData arg0) {
			// TODO Auto-generated method stub
			logger.info("return_msg="+arg0.getReturn_msg());  
			logger.info("申请退款接口：API返回ReturnCode为FAIL2");
		}

		//支付请求API返回的数据签名验证失败，有可能数据被篡改了
		@Override
		public void onFailBySignInvalid(RefundResData arg0) {
			// TODO Auto-generated method stub
			logger.info("申请退款接口：支付请求API返回的数据签名验证失败");
		}

		//退款失败
		@Override
		public void onRefundFail(RefundResData arg0) {
			// TODO Auto-generated method stub
			logger.info("申请退款接口：退款失败");
		}

		//退款成功
		@Override
		public void onRefundSuccess(RefundResData arg0) {
			// TODO Auto-generated method stub
			logger.info("退款成功");
			
			//改变产品明细退款状态，并改变退款记录的退款状态
        	String refundCode = arg0.getOut_refund_no();
			
			Refund refund = Refund.dao.findFirst("select * from refund where refund_code = ?", refundCode);
			
			//0待审核；1退款中；2审核不通过；3退款成功；4退款失败
			if (refund != null) {
				refund.setStatus(1);
				refund.update();
				
				int productOrderId = refund.getProductOrderId();
				ProductOrder productOrder = ProductOrder.dao.findById(productOrderId);
				
				//状态(1正常，2申请退款，3不通过，4退款中，5退款成功，6已退货, 7已评价，8退款失败)
				if (productOrder != null) {
					productOrder.setStatus(4);
					productOrder.update();
				}
			}
		}
	}
	
	//退货业务监听器
	class BackListener implements ResultListener {
		
		//API返回ReturnCode不合法，支付请求逻辑错误，请仔细检测传过去的每一个参数是否合法，或是看API能否被正常访问
		@Override
		public void onFailByReturnCodeError(RefundResData arg0) {
			// TODO Auto-generated method stub
			logger.info("申请退款接口：API返回ReturnCode不合法");
		}

		//API返回ReturnCode为FAIL，支付API系统返回失败，请检测Post给API的数据是否规范合法
		@Override
		public void onFailByReturnCodeFail(RefundResData arg0) {
			// TODO Auto-generated method stub
			logger.info("退款return_msg="+arg0.getReturn_msg());
			logger.info("申请退款接口：API返回ReturnCode为FAIL3");
		}

		//支付请求API返回的数据签名验证失败，有可能数据被篡改了
		@Override
		public void onFailBySignInvalid(RefundResData arg0) {
			// TODO Auto-generated method stub
			logger.info("申请退款接口：支付请求API返回的数据签名验证失败");
		}

		//退款失败
		@Override
		public void onRefundFail(RefundResData arg0) {
			// TODO Auto-generated method stub
			logger.info("申请退款接口：退款失败");
		}

		//退款成功
		@Override
		public void onRefundSuccess(RefundResData arg0) {
			// TODO Auto-generated method stub
			logger.info("退款成功");
			
			//改变产品明细退款状态，并改变退款记录的退款状态
        	String refundCode = arg0.getOut_refund_no();
			
			Back back = Back.dao.findFirst("select * from back where refund_code = ?", refundCode);
			
			//0待审核；1退款中；2审核不通过；3退款成功；4退款失败
			if (back != null) {
				back.setStatus(1);
				back.update();
				
				int productOrderId = back.getProductOrderId();
				ProductOrder productOrder = ProductOrder.dao.findById(productOrderId);
				
				//状态(1正常，2申请退款，3不通过，4退款中，5退款成功，6已退货, 7已评价，8退款失败)
				if (productOrder != null) {
					productOrder.setStatus(4);
					productOrder.update();
				}
			}
		}
	}
	
} 
