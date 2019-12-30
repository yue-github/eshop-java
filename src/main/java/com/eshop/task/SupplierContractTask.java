package com.eshop.task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.eshop.config.ITask;
import com.eshop.helper.SMSHelper;
import com.eshop.model.Supplier;
import com.eshop.model.SupplierContract;

/**
 * 合同到期发送通知
 * @author LiuJiaFu
 *
 */
public class SupplierContractTask implements ITask {

	@Override
	public void run() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sdf.format(new Date());
		List<SupplierContract> list = SupplierContract.dao.find("select * from supplier_contract  = ?", date);
		for (SupplierContract s : list) {
			Supplier supplier = Supplier.dao.findById(s.getSupplierId());
			String phone = supplier.getPhone1();
			String content = "【乐驿商城】亲爱的用户，您的合同已到期，为了不影响您的正常工作，请及时与我们联系续签合同，感谢您的合作！";
			SMSHelper.sendMessage(phone, content);
		}
	}

	@Override
	public void stop() {}
	
}
