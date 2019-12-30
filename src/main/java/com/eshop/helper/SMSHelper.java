package com.eshop.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import com.jfinal.plugin.activerecord.Record;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

/**
 * 短信辅助类
 * @author TangYiFeng
 */
public class SMSHelper {
	
	private static String user = "通驿高速";     //账号
	private static String passwd = "123123";   //密码
	private static String url = "http://115.29.194.198/api/http";
	
	/**
     * 发送手机验证码
     * @param phone 手机号码
     * @param content 内容
     * @return record 成功:{error:0,codeToken:token值,code:验证码} 失败:{error:!=0,errmsg:错误信息}
     */
    public static Record sendCode(String phone) {
    	String code = MathHelper.getRandom(100000, 1000000) + "";
    	String content = "【乐驿商城】您的手机验证码为" + code;
    	
    	long msgCode = SMSHelper.sendMessage(phone, content);
    	Record result = new Record();
    	
    	if (msgCode <= 0) {
    		result.set("error", 1);
    		result.set("errmsg", "发送短信失败");
    		return result;
    	}
    	
    	String codeToken = TokenHelper.create();
    	CacheHelper.put(codeToken, code);
    	
    	result.set("error", 0);
    	result.set("codeToken", codeToken);
    	result.set("code", code);
    	result.set("phone", phone);
    	return result;
    }

    /**
     * 发送短信
     * @param phone 手机号码
     * @param content 内容
     * @return code 大于0表示成功，否则表示失败
     */
    public static long sendMessage(String phone, String content) {
    	if (phone == null || phone.equals("")) {
			return 0;
		}
    	if (content == null || content.equals("")) {
			return 0;
		}
    	String param = "act=sendmsg&user=" + user + "&passwd=" + passwd + "&msg=" + content + "&phone=" + phone;
    	String code = sendPost(url, param);
    	
    	System.out.println("code="+code);
    	
    	return Long.parseLong(code);
    } 
    
    /**
     * 向指定 URL 发送POST方法的请求
     * @param url 发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            System.out.println(result+">>>>");
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }    

}