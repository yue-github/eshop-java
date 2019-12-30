package com.eshop.controller.admin;

import java.util.ArrayList;
import java.util.List;

import com.eshop.helper.FileHelper;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;

/**
 * 文件-控制器
 * @author TangYiFeng
 */
public class FileController extends AdminBaseController {
	
	// 文件白名单
	private String[] white_list = {"png", "jpg", "jpeg", "gif", "xls", "xlsx", "doc", "docx", "ppt", "pptx"};

	private boolean isInWhiteList(String originalName) {
		String ext = originalName.split("\\.")[1];
		ext = ext.toLowerCase();
		for (int i = 0; i < this.white_list.length; i++) {
			if (ext.equals(this.white_list[i])) {
				return true;
			}
		}
		return false;
	}
	
	/**
     * 上传文件
     * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
     */
	public void upload() {
		UploadFile uploadFile = this.getFile("file");
		
		if (!this.isInWhiteList(uploadFile.getOriginalFileName())) {
			returnError(-1, "该类型文件不允许上传");
			return;
		}
		
		Record result = FileHelper.uploadFile(uploadFile);
		
		int error = result.getInt("error");
		String errmsg = result.getStr("errmsg");
		
		if (error == 0) {
			jsonObject.put("path", result.getStr("path"));
		}
		
		setError(error, errmsg);
		renderJson(jsonObject);
	}
	
	public void uploadFile() {
		UploadFile uploadFile = this.getFile("file");
		Record result = FileHelper.uploadFile(uploadFile);
		
		int error = result.getInt("error");
		String path = "";
		
		if (error == 0) {
			path = this.getPath(result.getStr("path"));
		}
		
		List<String> list = new ArrayList<String>();
		list.add(path);
		
		jsonObject.put("errno", 0);
		jsonObject.put("data", list);
		renderJson(jsonObject);
	}
	
	public void saveBase64() {
		String imgStr = getPara("file");
		Record result = FileHelper.uploadFile(imgStr);
		
		int error = result.getInt("error");
		String errmsg = result.getStr("errmsg");
		
		if (error == 0) {
			jsonObject.put("path", result.getStr("path"));
		}
		
		setError(error, errmsg);
		renderJson(jsonObject);
	}
}
