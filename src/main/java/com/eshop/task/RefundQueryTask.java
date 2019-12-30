package com.eshop.task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.eshop.config.ITask;
import com.eshop.helper.AlipayRefundHelper;
import com.eshop.helper.UnionRefundHelper;
import com.eshop.helper.WxRefundHelper;
import com.eshop.model.Back;
import com.eshop.model.Order;
import com.eshop.model.Refund;
import com.eshop.model.dao.BaseDao;

public class RefundQueryTask implements ITask {
	
    @Override
	public void run() {
	    List<Back> backs = Back.dao.find("select * from back where status = ?", 1);
	    for (Back back : backs) {
		   refundQuery(back);
	    }
	    
	    List<Refund> refunds = Refund.dao.find("select * from refund where status = ?", 1);
	    for (Refund refund : refunds) {
	    	refundQuery(refund);
	    }
	    
	    System.out.println("定时执行退款查询接口");
    }
   
    @Override
	public void stop() {
        // 这里的代码会在 task 被关闭前调用
    }
    
    /**
     * 退款状态查询  -退货
     * @param back
     */
    private void refundQuery(Back back) {
    	Order order = BaseDao.getOrderByProductOrderId(back.getProductOrderId());
    	
    	//订单号类型,1同一批订单,2单独一个订单
    	int codeType = order.getCodeType();
    	int payType = order.getPayType();
    	int source = order.getSource();
    	//订单号
    	String tradeNo = (codeType == 1) ? order.getTheSameOrderNum() : order.getOrderCode();
    	//退款单号
    	String outRefundNo = back.getRefundCode();
    	//打款时间
    	String remitTime = getRemitTime(back);
    	
    	//1: 微信支付, 2: 支付宝, 3银联支付，4钱包支付
    	if (payType == 1) {
    		WxRefundHelper wxRefundHelper = new WxRefundHelper();
    		if (source == 1 || source == 2) {
    			wxRefundHelper.refundQuery(tradeNo, outRefundNo, 2);
    		} else if (source == 3) {
    			wxRefundHelper.refundQueryAndroid(tradeNo, outRefundNo, 2);
    		} else if (source == 4) {
    			wxRefundHelper.refundQueryAndroid(tradeNo, outRefundNo, 2);
    		}
    	} else if (payType == 2) {
    		AlipayRefundHelper alipayRefundHelper = new AlipayRefundHelper();
    		alipayRefundHelper.refundQuery(tradeNo, outRefundNo, 2);
    	} else if (payType == 3) {    
    		UnionRefundHelper unionRefundHelper = new UnionRefundHelper();
    		unionRefundHelper.refundQuery(outRefundNo, remitTime, 2);
    	}
    }
    
    /**
     * 退款状态查询  -退款
     * @param refund
     */
    private void refundQuery(Refund refund) {
    	Order order = BaseDao.getOrderByProductOrderId(refund.getProductOrderId());
    	
    	//订单号类型,1同一批订单,2单独一个订单
    	int codeType = order.getCodeType();
    	int payType = order.getPayType();
    	int source = order.getSource();
    	//订单号
    	String tradeNo = (codeType == 1) ? order.getTheSameOrderNum() : order.getOrderCode();
    	//退款单号
    	String outRefundNo = refund.getRefundCode();
    	//打款时间
    	String remitTime = getRemitTime(refund);
    	
    	//1: 微信支付, 2: 支付宝, 3银联支付，4钱包支付
    	if (payType == 1) {
    		WxRefundHelper wxRefundHelper = new WxRefundHelper();
    		if (source == 1 || source == 2) {
    			wxRefundHelper.refundQuery(tradeNo, outRefundNo, 1);
    		} else if (source == 3) {
    			wxRefundHelper.refundQueryAndroid(tradeNo, outRefundNo, 1);
    		} else if (source == 4) {
    			wxRefundHelper.refundQueryAndroid(tradeNo, outRefundNo, 1);
    		}
    	} else if (payType == 2) {
    		AlipayRefundHelper alipayRefundHelper = new AlipayRefundHelper();
    		alipayRefundHelper.refundQuery(tradeNo, outRefundNo, 1);
    	} else if (payType == 3) {
    		UnionRefundHelper unionRefundHelper = new UnionRefundHelper();
    		unionRefundHelper.refundQuery(outRefundNo, remitTime, 1);
    	}
    }
    
    private String getRemitTime(Refund model) {
    	Date remitTime = model.getRemitTime();
    	
    	if (remitTime == null) {
    		remitTime = model.getCreatedAt();
    	}
    	
    	return new SimpleDateFormat("yyyyMMddHHmmss").format(remitTime);
    }
    
    private String getRemitTime(Back model) {
    	Date remitTime = model.getRemitTime();
    	
    	if (remitTime == null) {
    		remitTime = model.getCreatedAt();
    	}
    	
    	return new SimpleDateFormat("yyyyMMddHHmmss").format(remitTime);
    }
}