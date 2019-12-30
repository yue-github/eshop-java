package com.eshop.helper;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jfinal.log.Logger;

/**
 * 微信登录辅助类
 * 
 * @author TangYiFeng
 */
public class WxLoginPCHelper {

	/*private static String appId = "wx751b0a4eccc5d04a";
	private static String appSecret = "7057c26ccfd2c6f70a383a13b5430992";*/
	
	private static String appId = "wxdfc5bbcfe8f4a919";
	private static String appSecret = "tongyiweixin1409564602TYWX123456";
	private final Logger logger = Logger.getLogger("");

	/**
     * 生成用于获取access_token的Code的Url
     *
     * @param redirectUrl
     * @return
     */
    public static String getRequestCodeUrl(String redirectUrl) {
        return String.format("https://open.weixin.qq.com/connect/qrconnect?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect",
        		appId, redirectUrl, "snsapi_login", "xxxx_state");
    }
    
    public Map<String, String> getUserInfoAccessToken(String code) {
    	logger.info("code=" + code);
        JsonObject object = null;
        Map<String, String> data = new HashMap();
        try {
            String url = String.format("https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code",
            		appId, appSecret, code);
            CloseableHttpClient httpClient = HttpClients.createDefault();  
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            String tokens = EntityUtils.toString(httpEntity, "utf-8");
            logger.info("tokens="+tokens);
            Gson token_gson = new Gson();
            object = token_gson.fromJson(tokens, JsonObject.class);
            data.put("openid", object.get("openid").toString().replaceAll("\"", ""));
            data.put("access_token", object.get("access_token").toString().replaceAll("\"", ""));
        } catch (Exception ex) {
            logger.error("fail to request wechat access token. [error={}]", ex);
        }
        return data;
    }
    
    /**
     * 获取用户信息
     *
     * @param accessToken
     * @param openId
     * @return
     */
    public Map<String, String> getUserInfo(String accessToken, String openId) {
        Map<String, String> data = new HashMap();
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openId + "&lang=zh_CN";
        JsonObject userInfo = null;
        try {
        	CloseableHttpClient httpClient = HttpClients.createDefault(); 
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            String response = EntityUtils.toString(httpEntity, "utf-8");
            logger.info("response="+response);
            Gson token_gson = new Gson();
            userInfo = token_gson.fromJson(response, JsonObject.class);
            data.put("openid", userInfo.get("openid").toString().replaceAll("\"", ""));
            data.put("nickname", userInfo.get("nickname").toString().replaceAll("\"", ""));
            data.put("city", userInfo.get("city").toString().replaceAll("\"", ""));
            data.put("province", userInfo.get("province").toString().replaceAll("\"", ""));
            data.put("country", userInfo.get("country").toString().replaceAll("\"", ""));
            data.put("headimgurl", userInfo.get("headimgurl").toString().replaceAll("\"", ""));
            data.put("unionid", userInfo.get("unionid").toString().replaceAll("\"", ""));
            
            
        } catch (Exception ex) {
            logger.error("fail to request wechat user info. [error={}]", ex);
        }
        return data;
    }
}
