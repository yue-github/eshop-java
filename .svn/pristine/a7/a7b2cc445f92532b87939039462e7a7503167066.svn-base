package com.eshop.invoice;

import com.eshop.model.EletronicInvoice;
import com.eshop.model.dao.BaseDao.ServiceCode;

public class EletronicInvoiceService {
	
	/**
	 * 保存电子发票
	 * @param invoiceHead
	 * @param customerId
	 * @param type
	 * @param companyCode
	 * @param phone
	 * @param email
	 * @param invoiceContent
	 * @return
	 */
	public static ServiceCode save(String invoiceHead, int customerId, int type, String companyCode,
			String phone, String email, int invoiceContent) {
		
		EletronicInvoice model;
		ServiceCode code;
		
		int count = EletronicInvoice.dao.find("select * from eletronic_invoice where type = ? and customerId = ?", type, customerId).size();
		
		if (count > 0) {
			model = EletronicInvoice.dao.findFirst("select * from eletronic_invoice where type = ? and customerId = ?", type, customerId);
			setField(model, invoiceHead, customerId, type, companyCode, phone, email, invoiceContent);
			code = update(model);
		} else {
			model = new EletronicInvoice();
			setField(model, invoiceHead, customerId, type, companyCode, phone, email, invoiceContent);
			code = create(model);
		}
		
		return code;
	}
	
	private static void setField(EletronicInvoice model, String invoiceHead, int customerId, int type, String companyCode,
			String phone, String email, int invoiceContent) {
		
		model.setInvoiceHead(invoiceHead);
		model.setCustomerId(customerId);
		model.setType(type);
		model.setCompanyCode(companyCode);
		model.setPhone(phone);
		model.setEmail(email);
		model.setInvoiceContent(invoiceContent);
	}
	
	/**
	 * 创建电子发票
	 * @param model
	 * @return
	 */
	public static ServiceCode create(EletronicInvoice model) {
		if (model == null) {
			return ServiceCode.Failed;
		}
		
		if (model.save()) {
			return ServiceCode.Success;
		} else {
			return ServiceCode.Failed;
		}
	}
	
	/**
	 * 修改电子发票
	 * @param model
	 * @return
	 */
	public static ServiceCode update(EletronicInvoice model) {
		if (model == null) {
			return ServiceCode.Failed;
		}
		
		if (model.update()) {
			return ServiceCode.Success;
		} else {
			return ServiceCode.Failed;
		}
	}
	
	/**
	 * 查看电子发票详情
	 * @param id
	 * @return
	 */
	public static EletronicInvoice getEletronicInvoice(int id) {
		return EletronicInvoice.dao.findById(id);
	}
	
	/**
	 * 查看电子发票
	 * @param type
	 * @param customerId
	 * @return
	 */
	public static EletronicInvoice getEletronicInvoice(int type, int customerId) {
		return EletronicInvoice.dao.findFirst("select * from eletronic_invoice where type = ? and customerId = ?", type, customerId);
	}
	
}
