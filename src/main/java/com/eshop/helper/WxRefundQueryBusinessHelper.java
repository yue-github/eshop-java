package com.eshop.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.tencent.common.RandomStringGenerator;
import com.tencent.common.Signature;
import com.tencent.common.Util;
import com.tencent.protocol.refund_query_protocol.RefundQueryResData;
import com.tencent.common.Configure;
import com.tencent.common.MD5;

public class WxRefundQueryBusinessHelper {
	
	private static Logger logger = Logger.getLogger(WxRefundQueryBusinessHelper.class);
	
	public interface ResultListener{
        //API返回ReturnCode不合法，支付请求逻辑错误，请仔细检测传过去的每一个参数是否合法，或是看API能否被正常访问
        void onFailByReturnCodeError(RefundQueryResData refundQueryResData, String out_refund_no);

        //API返回ReturnCode为FAIL，支付API系统返回失败，请检测Post给API的数据是否规范合法
        void onFailByReturnCodeFail(RefundQueryResData refundQueryResData, String out_refund_no);

        //支付请求API返回的数据签名验证失败，有可能数据被篡改了
        void onFailBySignInvalid(RefundQueryResData refundQueryResData, String out_refund_no);

        //退款查询失败
        void onRefundQueryFail(RefundQueryResData refundQueryResData, String out_refund_no);

        //退款查询成功
        void onRefundQuerySuccess(RefundQueryResData refundQueryResData, String out_refund_no);

    }
	
    public void refundQueryBusiness(String out_trade_no, String out_refund_no, ResultListener resultListener) throws ParserConfigurationException, IOException, SAXException {
    	String nonce_str = RandomStringGenerator.getRandomStringByLength(32);
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("appid", Configure.getAppid());
    	map.put("mch_id", Configure.getMchid());
    	map.put("nonce_str", nonce_str);  //随机字符串
    	map.put("out_trade_no", out_trade_no);
    	map.put("out_refund_no", out_refund_no);
    	
    	String sign = getSign(map);
    	map.put("sign", sign);  //签名
    	
    	String xml = toXml(map);
    	String url = "https://api.mch.weixin.qq.com/pay/refundquery";
    	String refundQueryServiceResponseString = new String(Http.httpPostXml(url, xml));
    	
        RefundQueryResData refundQueryResData = (RefundQueryResData) Util.getObjectFromXML(refundQueryServiceResponseString, RefundQueryResData.class);
        
        if (refundQueryResData == null || refundQueryResData.getReturn_code() == null) {
            logger.info("Case1:退款查询API请求逻辑错误，请仔细检测传过去的每一个参数是否合法，或是看API能否被正常访问");
            resultListener.onFailByReturnCodeError(refundQueryResData, out_refund_no);
            return;
        }
        
        if (refundQueryResData.getReturn_code().equals("FAIL")) {
            ///注意：一般这里返回FAIL是出现系统级参数错误，请检测Post给API的数据是否规范合法
        	logger.info("Case2:退款查询API系统返回失败，请检测Post给API的数据是否规范合法");
            resultListener.onFailByReturnCodeFail(refundQueryResData, out_refund_no);
        } else {
        	logger.info("退款查询API系统成功返回数据");
            //--------------------------------------------------------------------
            //收到API的返回数据的时候得先验证一下数据有没有被第三方篡改，确保安全
            //--------------------------------------------------------------------

            if (!Signature.checkIsSignValidFromResponseString(refundQueryServiceResponseString)) {
            	logger.info("Case3:退款查询API返回的数据签名验证失败，有可能数据被篡改了");
                resultListener.onFailBySignInvalid(refundQueryResData, out_refund_no);
                return;
            }

            if (refundQueryResData.getResult_code().equals("FAIL")) {
            	logger.info("出错，错误码：" + refundQueryResData.getErr_code() + "     错误信息：" + refundQueryResData.getErr_code_des());
            	logger.info("Case4:【退款查询失败】");
                resultListener.onRefundQueryFail(refundQueryResData, out_refund_no);
                //退款失败时再怎么延时查询退款状态都没有意义，这个时间建议要么再手动重试一次，依然失败的话请走投诉渠道进行投诉
            } else {
                //退款成功
            	logger.info("Case5:【退款查询成功】");
            	logger.info("退货退款");
                resultListener.onRefundQuerySuccess(refundQueryResData, out_refund_no);
            }
        }
    }
    
    private String toQuery(Map<String, Object> map) {
    	String query = "";
    	
    	for (Entry<String, Object> entry : map.entrySet()) {
    		query += entry.getKey() + "=" + entry.getValue() + "&";
    	}
    	
    	query = query.substring(0, query.length() - 1);
    	
    	return query;
    }
    
    private String toXml(Map<String, Object> map) {
    	String xml = "<xml>";
    	
    	for (Entry<String, Object> entry : map.entrySet()) {
    		xml += "<" + entry.getKey() + ">" + entry.getValue() + "</" + entry.getKey() + ">";
    	}
    	
    	xml += "</xml>";
    	
    	return xml;
    }
    
    private static String getSign(Map<String,Object> map){
        ArrayList<String> list = new ArrayList<String>();
        for(Map.Entry<String,Object> entry:map.entrySet()){
            if(entry.getValue()!=""){
                list.add(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }
        int size = list.size();
        String [] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < size; i ++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        result += "key=" + Configure.getKey();
        //Util.log("Sign Before MD5:" + result);
        result = MD5.MD5Encode(result).toUpperCase();
        //Util.log("Sign Result:" + result);
        return result;
    }
}
