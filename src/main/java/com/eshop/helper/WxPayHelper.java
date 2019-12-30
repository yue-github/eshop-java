package com.eshop.helper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.alibaba.fastjson.JSON;
import com.cike.util.Config;
import com.eshop.log.Log;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.result.WxMpPayCallback;

/**
 * 微信支付辅助类
 * @author TangYiFeng
 */
public class WxPayHelper {
	
	private static String appIdOfApp = "wxde28eec7c493e34f";
	private static String mchIdOfApp = "1420395402";
	private static String partnerKeyOfApp = "tongyiweixin1420395402TYWX123456";

	/**
	 * 公众号统一下单(详见http://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_1)
	 * 在发起微信支付前，需要调用统一下单接口，获取"预支付交易会话标识"
	 * @param openId     支付人openId
	 * @param outTradeNo 商户端对应订单号
	 * @param amt        金额(单位元)
	 * @param body       商品描述
	 * @param tradeType  交易类型 JSAPI，NATIVE，APP，WAP
	 * @param ip         发起支付的客户端IP
	 * @param notifyUrl  通知地址
	 * @throws WxErrorException 
	 */
	public static Object getPrepayIdResult(String openId, String outTradeNo, double amt, String body, String tradeType, String notifyUrl, String ip) throws WxErrorException {
		WxMpInMemoryConfigStorage config = new WxMpInMemoryConfigStorage();
		config.setAppId(Config.APPID); // 设置微信公众号的appid
		config.setSecret(Config.APPSECRET); // 设置微信公众号的app corpSecret
		config.setPartnerId(Config.MCH_ID);
		config.setPartnerKey(Config.KEY);
		
		WxMpService wxMpService = new WxMpServiceImpl();
		wxMpService.setWxMpConfigStorage(config);
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("openid", openId);
		parameters.put("out_trade_no", outTradeNo);
		parameters.put("total_fee", (int)(amt * 100) + "");
		parameters.put("body", body);
		parameters.put("trade_type", tradeType);
		parameters.put("spbill_create_ip", ip);
		parameters.put("notify_url", notifyUrl);
		Log.info("获取prepay参数");
		Object result = wxMpService.getPayService().getPayInfo(parameters);
		Log.info("result="+JSON.toJSONString(result));
		return result;
	}
	
	/**
	 * 统一下单(详见http://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_1)
	 * 在发起微信支付前，需要调用统一下单接口，获取"预支付交易会话标识"
	 * @param openId     支付人openId
	 * @param outTradeNo 商户端对应订单号
	 * @param amt        金额(单位元)
	 * @param body       商品描述
	 * @param tradeType  交易类型 JSAPI，NATIVE，APP，WAP
	 * @param ip         发起支付的客户端IP
	 * @param notifyUrl  通知地址
	 * @throws WxErrorException 
	 */
	public Object getPrepayIdResultForApp(String outTradeNo, double amt, String body, String tradeType, String notifyUrl, String ip){
		Map<String, String> result =  new HashMap<String, String>();
		 String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
		 String nonceStr = genNonceStr();
         String entity = genProductArgs(notifyUrl, outTradeNo, amt + "", ip, nonceStr);   //获取订单信息
         byte[] buf = Http.httpPost(url, entity);
         String content = new String(buf);  //请求成功返回的信息
         //Log.e("orion", content);
         try {
             xmlParseTest(content);  //解析返回的信息
         } catch (IOException e) {
             e.printStackTrace();
         } catch (XmlPullParserException e) {
             e.printStackTrace();
         }
         
         result.put("appId", appIdOfApp);
         result.put("partnerId", mchIdOfApp);
         result.put("prepayId", book.getPrepay_id());
         result.put("nonceStr", nonceStr);
         result.put("timeStamp", String.valueOf(genTimeStamp()));
         result.put("package", "Sign=WXPay");

         List<NameValuePair> signParams = new LinkedList<NameValuePair>();
         signParams.add(new BasicNameValuePair("appid", result.get("appId")));
         signParams.add(new BasicNameValuePair("noncestr", result.get("nonceStr")));
         signParams.add(new BasicNameValuePair("package", result.get("package")));
         signParams.add(new BasicNameValuePair("partnerid", result.get("partnerId")));
         signParams.add(new BasicNameValuePair("prepayid", result.get("prepayId")));
         signParams.add(new BasicNameValuePair("timestamp", result.get("timeStamp")));
     
         result.put("sign", genAppSign(signParams));
         
         return result;
	}

	
	/**
	 * 读取支付结果通知
	 *	
	 */
	public static WxMpPayCallback getPayCallback(String xmlData) {
		WxMpInMemoryConfigStorage config = new WxMpInMemoryConfigStorage();
		config.setAppId(Config.APPID); // 设置微信公众号的appid
		config.setSecret(Config.APPSECRET); // 设置微信公众号的app corpSecret

		WxMpService wxMpService = new WxMpServiceImpl();
		wxMpService.setWxMpConfigStorage(config);

		return wxMpService.getPayService().getJSSDKCallbackData(xmlData);
	}
	
	 //获取产品订单信息
    private String genProductArgs(String backUrl, String tradeNo, String singlePrice, String ip, String nonceStr) {
        StringBuffer xml = new StringBuffer();
        try {

            xml.append("</xml>");
            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();

            packageParams.add(new BasicNameValuePair("appid", appIdOfApp)); //APPID

            packageParams.add(new BasicNameValuePair("body", "商城订单"));  //简单描述

            packageParams.add(new BasicNameValuePair("mch_id", mchIdOfApp));  //商户ID

            packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));   //随机字符串

            packageParams.add(new BasicNameValuePair("notify_url", backUrl)); //通知地址

            packageParams.add(new BasicNameValuePair("out_trade_no",tradeNo));  //商户订单号

            packageParams.add(new BasicNameValuePair("spbill_create_ip", ip)); //终端IP

            //double price = Double.parseDouble(payment_num.getText().toString()) * (Integer.parseInt(singlePrice) * 100);
            double price = Double.parseDouble(singlePrice) * 100;
            int priceInt = (int) price;
            packageParams.add(new BasicNameValuePair("total_fee", priceInt+""));    //微信接收int型价格

            packageParams.add(new BasicNameValuePair("trade_type", "APP"));  //支付类型

            String sign = genAppSign(packageParams);
            packageParams.add(new BasicNameValuePair("sign", sign));  //签名

            String xmlstring = parseNodeToXML(packageParams);   //转化成xml

            return xmlstring;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 解析为xml格式
     * @param treeNodes
     * @return
     */
    private String parseNodeToXML(List<NameValuePair> treeNodes) {
        StringBuffer xmlnodes = new StringBuffer();
        if (treeNodes != null && treeNodes.size() > 0) {
            xmlnodes.append("<xml>");
            for (int i = 0; i < treeNodes.size(); i++) {
                NameValuePair node = treeNodes.get(i);
                xmlnodes.append("<"+node.getName()+">").append(node.getValue()).append("</"+node.getName()+">");
            }
            xmlnodes.append("</xml>");
        }
        //return xmlnodes.toString();
        String xml = xmlnodes.toString();
        try {
            xml = new String(xml.toString().getBytes(), "ISO8859-1");  //商品详情为中文，将其转化为统一编码，不然获取perpred_id失败
            return xml;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    
  //获取支付签名Sign
    private String genAppSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(partnerKeyOfApp);
        String appSign = MD5Helper.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        return appSign;
    }
	
	public class WeixinParentId
    {
        private String returnMsg;
        private String appId;
        private String prepayId;
        public void setReturn_msg(String v) {
            returnMsg = v;
        }

        public void setAppid(String v) {
            appId = v;
        }

        public void setPrepay_id(String v) {
            prepayId = v;
        }

        public String getPrepay_id() {
            return prepayId;
        }

    }
	
	WeixinParentId book = null;                //通过对象Books获取数据
	private void xmlParseTest(String str) throws IOException, XmlPullParserException {
        XmlPullParser pullParser =  XmlPullParserFactory.newInstance().newPullParser();          //获取XmlPullParser对象
        //InputStream is = getContext().getAssets().open("parse.xml");   //解析文本
        ByteArrayInputStream is = new ByteArrayInputStream(str.getBytes("UTF-8"));
        ArrayList<WeixinParentId> books = null ;

        pullParser.setInput(is, "UTF-8");
        int type = pullParser.getEventType();    //获取事件类型
        while (type != XmlPullParser.END_DOCUMENT) {   //结束文本</books>
            switch(type){
                case XmlPullParser.START_DOCUMENT:    //开始文本<books>
                    books = new ArrayList<WeixinParentId>();
                    break;
                case XmlPullParser.START_TAG:    //开始标记   <book>
                    if (pullParser.getName().equals("xml")) {
                        book = new WeixinParentId();
                    }else if (pullParser.getName().equals("return_msg")) {
                        type = pullParser.next();    //指向下一个位置，不然无法获取数据
                        book.setReturn_msg(pullParser.getText());
                    }else if (pullParser.getName().equals("appid")) {
                        type = pullParser.next();
                        book.setAppid(pullParser.getText());
                    }else if (pullParser.getName().equals("prepay_id")) {
                        type = pullParser.next();
                        book.setPrepay_id(pullParser.getText());
                    }

                    break;
                case XmlPullParser.END_TAG:   //结束标记      </books>
                    if (pullParser.getName().equals("book")) {
                        books.add(book);
                        book = null;    //置为空释放资源
                    }
                    break;
            }
            type = pullParser.next();    //指向下一个标记

        }
    }
        
    //获取随机字符串
    private String genNonceStr() {
        Random random = new Random();
        return MD5Helper.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }
    
    //获取时间搓
    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }
}