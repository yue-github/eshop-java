package com.eshop.notice;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.eshop.helper.SMSHelper;
import com.eshop.model.Notice;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

public class NoticeService {

	/**
	 * 批量创建消息
	 * 
	 * @param model
	 * @return
	 */
	public static ServiceCode createNotice(String msgContent, Integer msgGrade, List<String> customerIds) {
		if (msgContent == null || msgGrade == null || customerIds == null) {
			return ServiceCode.Failed;
		}

		List<Notice> modelList = new ArrayList<>();
		for (String items : customerIds) {
			Notice model = new Notice();
			model.setCustomerId(Integer.parseInt(items));
			model.setCreatedAt(new Date());
			model.setMsgContent(msgContent);
			model.setMsgGrade(msgGrade);
			modelList.add(model);
		}
		
		Db.batchSave(modelList, modelList.size());
		
		return ServiceCode.Success;
	}
	
	/**
	 * 短信发送消息
	 * @param customer_ids [1,2,3,....] 客户id（必填）
	 * @param msg_grade 消息等级（必填）
	 * @param msg_content 消息内容（必填）
	 * @return 成功：{error: 0}；失败：{error: >0, errmsg: 错误信息}
	 */
	public static ServiceCode sendNoticeBySMS(final List<String> phones, Integer msgGrade, String msgContent) {
		String grade = null;
		switch (msgGrade) {
			case 1: grade = "通知"; break;
			case 2: grade = "重要"; break;
			case 3: grade = "紧急"; break;
			default: grade = "通知"; break;
		}
		final String content = "【乐驿商城】" + msgContent;
		
		boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				for (String phone : phones) {
					try {
						SMSHelper.sendMessage(phone, content);
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				}
				return true;
			}
		});
		return success ? ServiceCode.Success : ServiceCode.Failed;
	}

	/**
	 * 批量获取消息
	 * @param offset
	 * @param length
	 * @param customerId 客户id
	 * @param isRead 1已读，0未读
	 * @return
	 */
	public static List<Record> findNotice(Integer offset, Integer length, Integer customerId, Integer noticeId, Integer isRead) {
		String sql = "select * from notice where customer_id = " + customerId + "";
		if (noticeId != null) {
			sql += " and id = " + noticeId;
		}
		if (isRead != null) {
			sql += " and isRead = " + isRead;
		}
		sql += " ORDER BY created_at DESC";
		sql = BaseDao.appendLimitSql(sql, offset, length);
		System.out.println(sql);
		return Db.find(sql);
	}
	
	/**
	 * 获取某条消息
	 * @param customerId 客户id 
	 * @param noticeId 消息id
	 * @return
	 */
	public static Record getNoice(Integer customerId, Integer noticeId) {
		Record record = Db.findFirst("select * from notice where customer_id = ? and id = ?", customerId, noticeId);
		return record;
	}
	
	/**
	 * 客户全部/未读/已读消息的数量
	 * @param customerId 客户id
	 * @param isRead 0未读，1已读，null全部
	 * @return
	 */
	public static Integer count(Integer customerId, Integer isRead) {
		List<Record> list = null;
		if (isRead != null) {
			//isRead = 0未读， isRead = 1已读
			list = Db.find("select * from notice where customer_id = ? and isRead = ?", customerId, isRead);
		} else {
			//全部
			list = Db.find("select * from notice where customer_id = ?", customerId);
		}
		return list.size();
	}
	
	/**
	 * 修改消息为已读
	 * @param customerId 客户id
	 * @return
	 */
	public static ServiceCode updateRead(Integer customerId, Integer noticeId) {
		int i = Db.update("update notice set isRead = 1 where customer_id = ? and id = ? ", customerId, noticeId);
		return i > 0 ? ServiceCode.Success : ServiceCode.Failed;
	}

	/**
	 * 删除一条消息
	 * @param noticeId 消息id
	 */
	public static ServiceCode delete(Integer noticeId) {
		if (!Notice.dao.deleteById(noticeId)) {
			return ServiceCode.Failed;
		} else {
			return ServiceCode.Success;
		}
	}

	/**
	 * 删除一条消息
	 * @param noticeId 消息id
	 * @param customerId 客户id
	 */
	public static ServiceCode delete(Integer noticeId, Integer customerId) {
		int i = Db.update("DELETE FROM notice WHERE id = ? and customer_id = ?", noticeId, customerId);
		if (i > 0) {
			return ServiceCode.Success;
		} else {
			return ServiceCode.Failed;
		}
	}

	/**
	 * 批量删除消息
	 * 
	 * @param ids
	 * @return
	 */
	public static ServiceCode batchNoticedelete(final List<String> ids) {
		boolean success = Db.tx(new IAtom() {

			@Override
			public boolean run() throws SQLException {
				try {
					for (String item : ids) {
						int id = Integer.parseInt(item);
						Notice.dao.deleteById(id);
					}
				} catch (Exception e) {
					return false;
				}
				return true;
			}
		});
		return success ? ServiceCode.Success : ServiceCode.Failed;
	}
}
