package com.eshop.controller.pc;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import com.eshop.helper.CacheHelper;
import com.eshop.helper.TokenHelper;
import com.eshop.helper.ValidateCode;
import com.jfinal.kit.PathKit;

public class ValidateCodeController extends PcBaseController {

	private ValidateCode validateCode;
	
	public ValidateCodeController() {
		validateCode = new ValidateCode(160,40,5,50);
	}
	
	public String getUUID(){
	    UUID uuid=UUID.randomUUID();

	    String str = uuid.toString(); 
	    String uuidStr=str.replace("-", "");

	    return uuidStr;
	}
	
	public void getValidateCode() {
		String BaseDir = PathKit.getWebRootPath() + File.separator;
		String file = "upload/" + getUUID() + ".png";
		String path = BaseDir + file;
		String vcode = validateCode.getCode();
		String vcodetoken = TokenHelper.create();
		CacheHelper.put(vcodetoken, vcode);
		jsonObject.put("path1",path);
        try {
			validateCode.write(path);
		} catch (IOException e) {
			e.printStackTrace();
		} 
        jsonObject.put("path", this.getPath(file));
        jsonObject.put("vcodeToken", vcodetoken);
        renderJson(jsonObject);
    }
	
	public void demo() {
		HttpServletResponse response = getResponse();
		// 设置响应的类型格式为图片格式  
	    response.setContentType("image/jpeg");  
	    //禁止图像缓存。  
	    response.setHeader("Pragma", "no-cache");  
	    response.setHeader("Cache-Control", "no-cache");  
	    response.setDateHeader("Expires", 0);  

	    try {
			validateCode.write(response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
}
