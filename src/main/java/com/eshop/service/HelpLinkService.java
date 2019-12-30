package com.eshop.service;

import java.sql.SQLException;
import java.util.List;

import com.eshop.model.HelpLink;
import com.eshop.model.dao.BaseDao;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

/**
 * 帮助链接业务层
 * 
 * @author
 *
 */
public class HelpLinkService {

	/**
	 * 批量查询帮助链接
	 * 
	 * @param offset
	 * @param count
	 * @param title
	 * @return
	 */
	public static List<Record> findHelpLinkItems(int offset, int count, String title) {
		String sql = findHelpLinkItemsSql(title);
		sql = BaseDao.appendLimitSql(sql, offset, count);
		return Db.find(sql);
	}

	/**
	 * 批量查询帮助链接的总数量
	 * 
	 * @param title
	 * @return
	 */
	public static int countRoleItems(String title) {
		String sql = findHelpLinkItemsSql(title);
		return Db.find(sql).size();
	}

	/**
	 * 组装sql语句
	 * 
	 * @param title
	 * @return
	 */
	private static String findHelpLinkItemsSql(String title) {
		String sql = "select * from help_link where id != 0";
		if (title != null && !title.equals("")) {
			sql += " and title like '%" + title + "%'";
		}
		sql += " order by sortNumber";
		return sql;
	}

	/**
	 * 获取某个链接
	 * 
	 * @param id
	 * @return
	 */
	public static Record get(int id) {
		Record result = Db.findFirst("select * from help_link where id = ?", id);
		return result;
	}

	/**
	 * 新建帮助链接
	 * 
	 * @return
	 */
	public static ServiceCode create(String title, String content, Integer sortNumber) {
		HelpLink helpLink = new HelpLink();
		helpLink.setTitle(title);
		helpLink.setContent(content);
		helpLink.setSortNumber(sortNumber);
		if (!helpLink.save()) {
			return ServiceCode.Failed;
		}

		return ServiceCode.Success;
	}

	/**
	 * 修改帮助链接
	 * 
	 * @param id
	 * @param title
	 * @param content
	 * @param sortNumber
	 * @return
	 */
	public static ServiceCode update(int id, String title, String content, Integer sortNumber) {
		HelpLink helpLink = HelpLink.dao.findById(id);
		if (helpLink == null) {
			return ServiceCode.Failed;
		}
		helpLink.setTitle(title);
		helpLink.setContent(content);
		helpLink.setSortNumber(sortNumber);

		if (!helpLink.update()) {
			return ServiceCode.Failed;
		}

		return ServiceCode.Success;
	}

	/**
	 * 删除某个链接
	 * 
	 * @param id
	 * @return
	 */
	public static ServiceCode delete(int id) {
		if (!HelpLink.dao.deleteById(id)) {
			return ServiceCode.Failed;
		}
		return ServiceCode.Success;
	}

	/**
	 * 批量删除
	 * 
	 * @param ids
	 * @return
	 */
	public static ServiceCode batchDelete(final List<String> ids) {
		boolean success = Db.tx(new IAtom() {

			@Override
			public boolean run() throws SQLException {
				try {
					for (String item : ids) {
						int id = Integer.parseInt(item);
						delete(id);
					}
				} catch (Exception e) {
					return false;
				}
				return true;
			}
		});

		return success ? ServiceCode.Success : ServiceCode.Failed;
	}

	/**
	 * 获取所有帮助链接
	 * 
	 * @return
	 */
	public static List<HelpLink> getAllHelpLinks() {
		return HelpLink.dao.find("select * from help_link order by sortNumber");
	}
}
