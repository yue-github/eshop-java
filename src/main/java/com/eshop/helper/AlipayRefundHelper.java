package com.eshop.helper;

import java.util.*;

import org.apache.log4j.Logger;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.eshop.model.Back;
import com.eshop.model.ProductOrder;
import com.eshop.model.Refund;


/**
 * 支付宝辅助类
 *   @author TangYiFeng
 */
public class AlipayRefundHelper {
	private static Logger logger = Logger.getLogger(AlipayRefundHelper.class);

    /**
     * Default constructor
     */
    public AlipayRefundHelper() {
    	
    }
    
    /**
     * 订单查询处理 -成功
     * @param refundType
     * @param refundNo
     */
    public static void handleRefundSuccess(int refundType, String refundNo) {
    	if (refundType == 1) {
    		//0待审核；1退款中；2审核不通过；3退款成功；4退款失败
        	Refund refund = Refund.dao.findFirst("select * from refund where refund_code = ?", refundNo);
        	
        	if (refund == null) {
        		return;
        	}
        	
        	//状态(1正常，2申请退款，3不通过，4退款中，5退款成功，6已退货, 7已评价，8退款失败)
        	int productOrderId = refund.getProductOrderId();
    		ProductOrder productOrder = ProductOrder.dao.findById(productOrderId);
    		
    		refund.setStatus(3);
    		refund.setSuccessTime(new Date());
			
			if (productOrder != null) {
				productOrder.setStatus(5);
				productOrder.update();
			}
			
			refund.update();
    	} else if (refundType == 2) {
    		//0待审核；1退款中；2审核不通过；3已收货；4退款成功；5退款失败
        	Back back = Back.dao.findFirst("select * from back where refund_code = ?", refundNo);
        	
        	if (back == null) {
        		return;
        	}
        	
        	//状态(1正常，2申请退款，3不通过，4退款中，5退款成功，6已退货, 7已评价，8退款失败)
        	int productOrderId = back.getProductOrderId();
    		ProductOrder productOrder = ProductOrder.dao.findById(productOrderId);
    		
    		back.setSuccessTime(new Date());
    		back.setStatus(4);
			
			if (productOrder != null) {
				productOrder.setStatus(5);
				productOrder.update();
			}
			
			back.update();
    	}
    }
    
    /**
     * 订单查询处理  -失败
     * @param refundType
     * @param refundNo
     */
    public static void handleRefundFail(int refundType, String refundNo) {
    	if (refundType == 1) {
    		//0待审核；1退款中；2审核不通过；3退款成功；4退款失败
        	Refund refund = Refund.dao.findFirst("select * from refund where refund_code = ?", refundNo);
        	
        	if (refund == null) {
        		return;
        	}
        	
        	//状态(1正常，2申请退款，3不通过，4退款中，5退款成功，6已退货, 7已评价，8退款失败)
        	int productOrderId = refund.getProductOrderId();
    		ProductOrder productOrder = ProductOrder.dao.findById(productOrderId);
    		
    		refund.setStatus(4);
			
			if (productOrder != null) {
				productOrder.setStatus(8);
				productOrder.update();
			}
			
			refund.update();
    	} else if (refundType == 2) {
    		//0待审核；1退款中；2审核不通过；3已收货；4退款成功；5退款失败
        	Back back = Back.dao.findFirst("select * from back where refund_code = ?", refundNo);
        	
        	if (back == null) {
        		return;
        	}
        	
        	//状态(1正常，2申请退款，3不通过，4退款中，5退款成功，6已退货, 7已评价，8退款失败)
        	int productOrderId = back.getProductOrderId();
    		ProductOrder productOrder = ProductOrder.dao.findById(productOrderId);
    		
    		back.setStatus(5);
			
			if (productOrder != null) {
				productOrder.setStatus(8);
				productOrder.update();
			}
			
			back.update();
    	}
    }
    
    /**
     * 退款状态查询
     * @param outTradeNo 订单号
     * @param outRequestNo 退款单号
     * @param refundType 退款类型(1退款，2退货)
     */
    public void refundQuery(String outTradeNo, String outRequestNo, int refundType) {
    	AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",APPID,APP_PRIVATE_KEY,"json","GBK",ALIPAY_PUBLIC_KEY,"RSA");
    	AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
    	
    	request.setBizContent("{" +
    	"\"out_trade_no\":\"" + outTradeNo + "\"," +
    	"\"out_request_no\":\"" + outRequestNo + "\"" +
    	"}");
    	
		try {
			AlipayTradeFastpayRefundQueryResponse response = alipayClient.execute(request);
			if(response.isSuccess()){
	    		AlipayRefundHelper.handleRefundSuccess(refundType, outRequestNo);
	    	} else {
	    		AlipayRefundHelper.handleRefundFail(refundType, outRequestNo);
	    	}
		} catch (AlipayApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * 支付宝退款,这里要注意一点：bigcontent参数不要带有空格
     * @param outTradeNo 订单号
     * @param refundAmount 退款金额，单位元
     * @param outRequestNo 退款单号
     * @return
     */
	public boolean refund(String outTradeNo, double refundAmount, String outRequestNo) {
    	AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",APPID,APP_PRIVATE_KEY,"json",CHARSET,ALIPAY_PUBLIC_KEY,"RSA");
    	AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
    	request.setBizContent("{" +
    	"\"out_trade_no\":\"" + outTradeNo + "\"," +
    	"\"refund_amount\":" + refundAmount + "," +
    	"\"refund_reason\":\"正常退款\"," +
    	"\"out_request_no\":\"" + outRequestNo + "\"" +
    	"}");
    	AlipayTradeRefundResponse response;
    	
		try {
			response = alipayClient.execute(request);
			if(response.isSuccess()){
		    	System.out.println("调用成功");
		    	return true;
	    	} else {
	    		System.out.println("调用失败");
	    		return false;
	    	}
		} catch (AlipayApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    	
    }
	
	private static final String APPID = "2016112803459053";
    private static final String APP_PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAL2F/3ZJbWVybaCs" +
            "gfVnjRqTWvO+DW4wQ5YP8r9g81nZ0TUMAx5xBfc2gtKb51zeRWHWvaW4JyttrBMY" +
            "i9ag2l7JBSz3io+uyJBWbqx2BTS+LdwbmuuSk+ZyZ2QuRMGIc5rzTlzpXjtot8Oq" +
            "cWZXjRf76By55aoG+dGW16EHlRYpAgMBAAECgYAq3mxybRl5oPB7L4oV4F5ibIJe" +
            "L9cJ3ZDsGJ9n+p4Q7NwNi7II6LfYu1PMH8etkflPsCslmQoDIMOTbjHzUl8SPcEm" +
            "izpiPbkejwSZpmzg63EKqfwMmtgSVYylNGQnGGo1TwWJogUQkQZnQi1CcT/djj2v" +
            "pBVxj8OijZelhWPPoQJBAOoSrRhYl/SMiN8m/ISp7u2Z4e7i6lJTVBEmKoKxbdRN" +
            "B3NevLcXb69dOqsNi3j0r4JAU6fYl2q7J/mUEsA1WSsCQQDPRvnJWczCdzky+ivQ" +
            "o61JbeEd0gCUTrZXU0ajigKqmHRkzwKR5t88njnTPaUD228ATMc+cu7gZz1rE2ye" +
            "zXv7AkAIK/6/CtjDQOTGZaYLO6OLyAOLQ4DKvv4AW0TAq1EaSFYqQPR1r+vGQ4r4" +
            "zdBAuMon0fcZtQYUQ9Rxcin1a+dhAkA8IiYzYQcDX91LScLOW73ZMLR/lz4tyIwj" +
            "13gF1/MgFOynllrzgxw+lNTH0Pl/nfidCKlBF8zvc2QhHViE1a5jAkAir3sCzwLh" +
            "pkPx3hfxL7RUQOti8MY9jOjurHZ48z4p0DUX/R5bATGe9KxmG9zHtHOBKRFAxs6w" +
            "OLGaqzvPFdyq";
    private static final String CHARSET = "UTF-8";
    private static final String ALIPAY_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB";


}