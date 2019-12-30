package com.eshop.helper;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * 邮件辅助类
 *   @author TangYiFeng
 */
public class MailHelper {
	private static String smtpHost = "smtp.163.com";
	private static String user = "topeds@163.com";
	private static String authPassword = "topeds168";
	/*private static String user = "gdluyijia@163.com";
	private static String authPassword = "luyi.765914052";*/

    /**
     * Default constructor
     */
    public MailHelper() {
    	
    }
    
    /**
     * 发送邮箱
     * @param receiveEmail 收件人邮箱
     * @param subject 标题
     * @param content 内容
     * @throws MessagingException 
     */
    public static void sendEmail(String receiveEmail, String subject, String content) throws MessagingException {
    	// 配置发送邮件的环境属性
        final Properties props = new Properties();
        /*
         * 可用的属性： mail.store.protocol / mail.transport.protocol / mail.host /
         * mail.user / mail.from
         */
        // 表示SMTP发送邮件，需要进行身份验证
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", smtpHost);
        // 发件人的账号
        props.put("mail.user", user);
        // 访问SMTP服务时需要提供的密码
        props.put("mail.password", authPassword);

        // 构建授权信息，用于进行SMTP进行身份验证
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // 用户名、密码
                String userName = props.getProperty("mail.user");
                String password = props.getProperty("mail.password");
                return new PasswordAuthentication(userName, password);
            }
        };
        // 使用环境属性和授权信息，创建邮件会话
        Session mailSession = Session.getInstance(props, authenticator);
        // 创建邮件消息
        MimeMessage message = new MimeMessage(mailSession);
        // 设置发件人
        InternetAddress form = new InternetAddress(props.getProperty("mail.user"));
        message.setFrom(form);

        // 设置收件人
        InternetAddress to = new InternetAddress(receiveEmail);
        message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(props.getProperty("mail.user")));
        message.setRecipient(RecipientType.TO, to);
        // 设置邮件标题
        message.setSubject(subject);

        // 设置邮件的内容体
        message.setContent(content, "text/html;charset=UTF-8");

        // 发送邮件
        Transport.send(message);
    }
    
}