package com.eshop.visit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.eshop.helper.QueryHelper;
import com.eshop.model.CustomerVisit;
import com.eshop.model.dao.BaseDao;
import com.jfinal.plugin.activerecord.Page;


/**
 * 
 * @author Helga
 *
 */
public class CustomerVisitService {

	
		/**
		 * 页面访问
		 * @param model
		 */
		public static BaseDao.ServiceCode visitPage(CustomerVisit model) {
			
			if(!model.save()) {
		        return BaseDao.ServiceCode.Failed;
			}
			return BaseDao.ServiceCode.Success;
			
		}
		/**
		 * 页面访问列表
		 * @param offset
		 * @param length
		 * @param startTime
		 * @param endTime
		 * @param page
		 * @return
		 */
		public static Page<CustomerVisit> findCustomerVisits(int pageIndex, int length, String startTime, String endTime, String page) {
			QueryHelper helper = new QueryHelper("customer_visit", "c");
			if(startTime != null && startTime.length() > 0) {
				helper.addCondition("DATE_FORMAT(c.create_at,'%Y-%m-%d')>=?", startTime);
			}
			if(endTime != null && startTime.length() > 0) {
				helper.addCondition("DATE_FORMAT(c.create_at,'%Y-%m-%d')<=?", endTime);
			}
			if(page != null && "".equals(page)) {
				helper.addCondition("c.page=?", page);
			}
			Page<CustomerVisit> list = CustomerVisit.dao.paginate(pageIndex, length, "select * ", helper.getQuerySql(), helper.getParams().toArray());
			return list;
		}
		
		/**
		 * 统计页面访问的IP/UV/PV
		 * @param startTime
		 * @param endTime
		 * @return
		 */
		public static Map<String, Integer> count(String startTime, String endTime){
			QueryHelper helper = new QueryHelper("customer_visit", "c");
			if(startTime != null && startTime.length() > 0) {
				helper.addCondition("DATE_FORMAT(c.create_at,'%Y-%m-%d')>=?", startTime);
			}
			if(endTime != null && startTime.length() > 0) {
				helper.addCondition("DATE_FORMAT(c.create_at,'%Y-%m-%d')<=?", endTime);
			}
			
			List<CustomerVisit> list = CustomerVisit.dao.find("select * "+ helper.getQuerySql(), helper.getParams().toArray());
			
			Map<String, Integer> count = new HashMap<>(3);
			//某一时段点击页面的的总记录数
			count.put("pv", list.size());
			//某一时段的不重复的ip数
			Set<String> ips = new HashSet<>(list.size());
			//某一时间端的不重复的会员访问
			Set<String> customerNames = new HashSet<>(list.size());
			if(list != null && list.size() > 0) {
				String customerName = "";
				for(int i=0; i<list.size(); i ++) {
					
					ips.add(list.get(i).getIp().trim());
					customerName = list.get(i).getCustomerName().trim();
					if(customerName != null && customerName.length() > 0) {
						customerNames.add(customerName);
					}
				}
			}
			count.put("ip", ips.size());
			count.put("uv", customerNames.size());
			//清空列表
			list.clear();
			ips.clear();
			customerNames.clear();
			return count;
		}
		/**
		 * 统计页面访问数量和来源数量
		 * @param offset
		 * @param length
		 * @param startTime
		 * @param endTime
		 * @param page
		 * @return
		 */
		public static Map<String, Object> countPageAndSource(String startTime, String endTime) {
			QueryHelper helper1 = new QueryHelper("customer_visit", "c");
			QueryHelper helper2 = new QueryHelper("customer_visit", "c");
			if(startTime != null && startTime.length() > 0) {
				helper1.addCondition("DATE_FORMAT(c.create_at,'%Y-%m-%d')>=?", startTime);
				helper2.addCondition("DATE_FORMAT(c.create_at,'%Y-%m-%d')>=?", startTime);
			}
			if(endTime != null && startTime.length() > 0) {
				helper1.addCondition("DATE_FORMAT(c.create_at,'%Y-%m-%d')<=?", endTime);
				helper2.addCondition("DATE_FORMAT(c.create_at,'%Y-%m-%d')<=?", endTime);
			}
			
			helper1.addGroupByProperty("page");
			helper2.addGroupByProperty("source");
			//统计页面访问量分页查询
			//Page<CustomerVisit> pageList = CustomerVisit.dao.paginate(pageIndex, length, "select page, count(*) as pageNumbers", helper1.getQuerySql(), helper1.getParams().toArray());
			String pageSql = "select page, count(*) as pageNumbers ";
			String sourceSql = "select source, count(*) as sourceNumbers ";
			List<CustomerVisit> pageList = CustomerVisit.dao.find(pageSql+ helper1.getQuerySql(), helper1.getParams().toArray());
			List<CustomerVisit> sourceList = CustomerVisit.dao.find(sourceSql+ helper2.getQuerySql(), helper2.getParams().toArray());
			Map<String, Object> result = new HashMap<>(3);
			//result.put("totalRow", pageList.getTotalRow());
			result.put("pages", pageList);
			result.put("sources", sourceList);
			return result;
		}
		
		
	}
	

