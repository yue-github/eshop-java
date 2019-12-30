package com.eshop.invoice;

import java.sql.SQLException;

import com.eshop.log.Log;
import com.eshop.logistics.CustomerAddress;
import com.eshop.model.VatInvoice;
import com.eshop.model.VatInvoiceItem;
import com.eshop.model.dao.BaseDao.ServiceCode;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

public class VatInvoiceService {
	
	/**
	 * 保存增值税发票公司信息
	 * @param model
	 * @return
	 */
	public static ServiceCode saveVatInvoice(final VatInvoice model) {
		if (model == null) {
			return ServiceCode.Failed;
		}
		
		boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					Db.update("delete from vat_invoice where customerId = ?", model.getCustomerId());
					model.save();
				} catch (Exception e) {
					Log.error(e.getMessage() + ",保存增值税发票公司信息错误");
					return false;
				}
				return true;
			}
		});
		
		return success ? ServiceCode.Success : ServiceCode.Failed;
	}
	
	/**
	 * 保存增值税发票收票人信息
	 * @param model
	 * @return
	 */
	public static ServiceCode saveVatInvoice(final VatInvoiceItem model) {
		if (model == null) {
			return ServiceCode.Failed;
		}
		
		String province = CustomerAddress.getProvinceName(model.getProvinceId());
		String city = CustomerAddress.getCityName(model.getCityId());
		String district = CustomerAddress.getDistrictName(model.getDistrictId());
		model.setProvince(province);
		model.setCity(city);
		model.setDistrict(district);
		
		boolean success = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				try {
					Db.update("delete from vat_invoice_item where customerId = ?", model.getCustomerId());
					model.save();
				} catch (Exception e) {
					Log.error(e.getMessage() + ",保存增值税发票收票人信息错误");
					return false;
				}
				return true;
			}
		});
		
		return success ? ServiceCode.Success : ServiceCode.Failed;
	}
	
	/**
	 * 查看增值税发票详情
	 * @param customerId
	 * @return
	 */
	public static Record get(int customerId) {
		String sql = "select * from vat_invoice as a" +
				" left join vat_invoice_item as b on a.customerId = b.customerId" +
				" where a.customerId = " + customerId;
		return Db.findFirst(sql);
	}
	
}
