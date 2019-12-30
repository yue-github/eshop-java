package com.eshop.helper;

import com.cike.util.Config;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;

/**
 * 微信登录辅助类
 * 
 * @author TangYiFeng
 */
public class WxLoginHelper {

	/**
	 * 获取微信认证URL
	 * @param redirectURI 返回URI
	 * @return
	 */
	public static String getUrl(String redirectURI) {
		WxMpInMemoryConfigStorage config = new WxMpInMemoryConfigStorage();
		config.setAppId(Config.APPID); // 设置微信公众号的appid
		config.setSecret(Config.APPSECRET); // 设置微信公众号的app corpSecret

		WxMpService wxMpService = new WxMpServiceImpl();
		wxMpService.setWxMpConfigStorage(config);
		String url = wxMpService.oauth2buildAuthorizationUrl(redirectURI, WxConsts.OAUTH2_SCOPE_USER_INFO, null);

		return url;
	}

	/**
	 * 获取微信用户信息
	 * @param code 微信返回码
	 * @return 微信用户信息
	 * @throws WxErrorException
	 */
	public static WxMpUser getUserInfo(String code) throws WxErrorException {
		WxMpInMemoryConfigStorage config = new WxMpInMemoryConfigStorage();
		config.setAppId(Config.APPID); // 设置微信公众号的appid
		config.setSecret(Config.APPSECRET); // 设置微信公众号的app corpSecret

		WxMpService wxMpService = new WxMpServiceImpl();
		wxMpService.setWxMpConfigStorage(config);

		WxMpOAuth2AccessToken wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
		WxMpUser wxMpUser = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);

		return wxMpUser;
	}
	
}
