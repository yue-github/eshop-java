package com.eshop.model.dao.test;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.List;

import org.junit.Test;

import com.eshop.model.dao.BaseDao.ServiceCode;
import com.eshop.service.Member;
import com.eshop.config.TestProjectConfig;
import com.eshop.membership.CustomerGoldService;
import com.eshop.membership.CustomerGradeService;
import com.eshop.membership.CustomerGrowthService;
import com.eshop.membership.CustomerPointService;
import com.eshop.membership.MemberShip;
import com.eshop.model.CouponProduct;
import com.eshop.model.Customer;
import com.eshop.model.CustomerGrade;
import com.jfinal.ext.test.ControllerTestCase;

public class CustomerDaoTest extends ControllerTestCase<TestProjectConfig> {
	
	private Member member = new Member();
	//private Merchant merchant = new Merchant();
	
	@Test
	public void testGetCustomerByEmail() {
		Customer back = Member.getCustomerByEmail("952813870@qq.com");
		boolean flag = back != null;
		assertEquals(false, flag);
	}

	@Test
	public void testGetCustomerByMobilePhone() {
		Customer back = Member.getCustomerByMobilePhone("18813703088");
		boolean flag = back != null;
		assertEquals(false, flag);
	}

	/*@Test
	public void testLogin() {
		Customer back = member.login("12346578901", "123456");
		boolean flag = back != null;
		assertEquals(true, flag);
	}*/

	/*@Test
	public void testHasCode() {
		ServiceCode code = member.hasCode("12s3das","1");
		boolean flag = code == ServiceCode.Success;
		assertEquals(true, flag);
	}

	@Test
	public void testFindPwdWithPhoneNext() {
		Customer back = member.findPwdWithPhoneNext("188137026631", "1", "4153453");
		boolean flag = back != null;
		assertEquals(false, flag);
	}

	@Test
	public void testSetPassword() {
		ServiceCode code = member.setPassword(1, "123456", "123456");
		boolean flag = code == ServiceCode.Success;
		assertEquals(true, flag);
	}

	@Test
	public void testFindPwdWithEmailNext() {
		ServiceCode code = member.findPwdWithEmailNext("952813870@qq.com", "http://fanyi.baidu.com/#en/zh/code", "asd", "jiangquan");
		boolean flag = code == ServiceCode.Success;
		assertEquals(true, flag);
	}

	@Test
	public void testInfo() {
		Customer back = member.info(1);
		boolean flag = back != null;
		assertEquals(false, flag);
	}

	@Test
	public void testUpdatePhoto() {
		ServiceCode code = member.updatePhoto(1, "12345678912");
		boolean flag = code == ServiceCode.Success;
		assertEquals(true, flag);
	}

	@Test
	public void testBindPhone() {
		ServiceCode code = member.bindPhone(1, "18813703086");
		boolean flag = code == ServiceCode.Success;
		assertEquals(true, flag);
	}

	@Test
	public void testUpdateInfo() {
		Customer customer = Customer.dao.findById(1);
		customer.setName("3232");
		ServiceCode code = member.updateInfo(customer);
		boolean flag = code == ServiceCode.Success;
		assertEquals(true, flag);
	}*/

	/*@Test
	public void testAddShoppingCart() {
		Customer customer = Customer.dao.findFirst("select * from customer");
		Product product = Product.dao.findFirst("select * from product");
		ServiceCode code = CustomerDao.addShoppingCart(customer.getId(), product.getId(), 0, 2);
		assertEquals(ServiceCode.Success, code);
	}*/
	
	/*@Test
	public void testSubmitOrderWithShoppingCart() {
		Customer customer = Customer.dao.findFirst("select * from customer");
		Record other = new Record();
		other.set("source", 1);
		other.set("payType", 1);
		ServiceCode code = CustomerDao.submitOrderWithShoppingCart(customer.getId(), other);
		assertEquals(ServiceCode.Success, code);
	}*/
	
	/*@Test
	public void testSubmitOrderWithProduct() {
		Product product = Product.dao.findFirst("select * from product");
		Customer customer = Customer.dao.findFirst("select * from customer");
		Record other = new Record();
		other.set("source", 1);
		other.set("payType", 1);
		ServiceCode code = CustomerDao.submitOrderWithProduct(customer.getId(), product.getId(), 0, 2, other);
		assertEquals(ServiceCode.Success, code);
	}*/

	/*@Test
	public void testFindShoppingCartItems() {
		List<Record> count = member.findShoppingCartItems(5, 3);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

	@Test
	public void testGetShoppingCartItemsCount() {
		int count = member.getShoppingCartItemsCount(6, 6);
		boolean flag = count > 0;
		assertEquals(false, flag);
	}

	@Test
	public void testClearShoppingCart() {
		ServiceCode back = member.clearShoppingCart(1);
		boolean flag = back != null;
		assertEquals(false, flag);
	}

	@Test
	public void testUpdateShoppingCartNum() {
		ServiceCode back = member.updateShoppingCartNum(1,4);
		boolean flag = back != null;
		assertEquals(false, flag);
	}

	@Test
	public void testSelectedShoppingCart() {
		ServiceCode back = member.selectedShoppingCart("1", 20);
		boolean flag = back != null;
		assertEquals(false, flag);
	}

	@Test
	public void testSubmitOrder() {
		ServiceCode back = member.submitOrder(1, "","2");
		//[{shop_id:1,product_id:1,price_id:2,amount:3,selectProterties:"票型：成人票 房型：双人房"},...]
		List<Record> products = new ArrayList<Record>();
		Record prod = new Record();
		prod.set("shop_id", 1);
		prod.set("product_id", 1);
		prod.set("price_id", 1);
		prod.set("amount", "3");
		prod.set("selectProterties", "票型：成人票 房型：双人房");
		products.add(prod);
		Record other = new Record();
		other.set("source", 1);
		other.set("payType", 1);
		ServiceCode back = member.submitOrder(1, products, other);
		boolean flag = back != null;
		assertEquals(false, flag);
	}*/

	/*@Test
	public void testPay() {
		Order order = Order.dao.findFirst("select * from `order` where status = ?", BaseDao.UNPAY);
		ServiceCode code = member.pay(order.getId());
		assertEquals(ServiceCode.Success, code);
	}*/

	/*@Test
	public void testConfirm() {
		Order order = Order.dao.findFirst("select * from `order` where status = ?", BaseDao.DISPATCHED);
		ServiceCode count = member.confirm(order.getId());
		boolean flag = count != null;
		assertEquals(false, flag);
	}*/

	/*@Test
	public void testSubmitComment() {
		ProductReview model = new ProductReview();
		model.setComments("22");
		model.setComments("33");
		model.setComments("44");
		ServiceCode back = member.submitComment(model);
		boolean flag = back != null;
		assertEquals(false, flag);
	}*/

	/*@Test
	public void testCancel() {
		Order order = Order.dao.findFirst("select * from `order` where status = ?", BaseDao.UNPAY);
		ServiceCode code = member.cancel(order.getId());
		assertEquals(ServiceCode.Success, code);
	}*/

	/*@Test
	public void testFindCommentItems() {
		List<Record> back = member.findCommentItems(1, 2, 3, 4, 5, 6, "2", "cc", "vv", "gg");
		boolean flag = back != null;
		assertEquals(false, flag);
	}

	@Test
	public void testGetCommentItemsCount() {
		int count = member.getCommentItemsCount(1, 2, 3, 4, "2", "5", "2", "4");
		boolean flag = count > 0;
		assertEquals(false, flag);
	}

	@Test
	public void testGetComment() {
		ProductReview count = member.getComment(2);
		boolean flag = count != null;
		assertEquals(false, flag);
	}

	@Test
	public void testDeleteComment() {
		ServiceCode back = member.deleteComment(3);
		boolean flag = back != null;
		assertEquals(false, flag);
	}*/

	/**
	 * 测试购买流程
	 */
	/*@Test
	public void tetBuy() {
		//提交订单
		Product product = Product.dao.findFirst("select * from product");
		Customer customer = Customer.dao.findFirst("select * from customer");
		Record other = new Record();
		other.set("source", 1);
		other.set("payType", 1);
		Order order = Member.submitOrderWithProduct(customer.getId(), product.getId(), 0, 2, other);
		boolean flag = order != null;
		assertEquals(true, flag);
		//付款
		ServiceCode payCode = member.pay(order.getId());
		assertEquals(ServiceCode.Success, payCode);
		//发货
		ServiceCode dispatchCode = merchant.delivery(order.getId(), "顺丰", "shunfeng", "4567892415");
		assertEquals(ServiceCode.Success, dispatchCode);
		//收货
		ServiceCode confirmCode = member.confirm(order.getId());
		assertEquals(ServiceCode.Success, confirmCode);
		//评价商品
		ProductReview productReview = new ProductReview();
		productReview.setProductId(product.getId());
		productReview.setCustomerId(customer.getId());
		productReview.setRatings(5);
		productReview.setComments("挺好的！");
		ServiceCode submitCommentCode = Member.submitComment(productReview);
		assertEquals(ServiceCode.Success, submitCommentCode);
	}*/
	
	/**
	 * 测试退款流程 -审核通过
	 */
	/*@Test
	public void testRefundPass() {
		//申请退款
		Record productOrder = Db.findFirst("select b.* from `order` as a inner join product_order as b on a.id = b.order_id where a.status = ? and b.status = ?", BaseDao.PAYED, 1);
		Customer customer = Customer.dao.findFirst("select * from customer");
		Refund model = new Refund();
		model.setCustomerId(customer.getId());
		model.setProductOrderId(productOrder.getInt("id"));
		model.setRefundAmount(productOrder.getInt("unitOrdered"));
		model.setRefundCash(productOrder.getBigDecimal("totalActualProductPrice"));
		ServiceCode refundCode = member.refund(model, "['b.png']");
		assertEquals(ServiceCode.Success, refundCode);
		//审核通过
		ServiceCode code = merchant.auditRefund(model.getId(), 1, "", "管理员", model.getApplyCash(), new BigDecimal(0));
		assertEquals(ServiceCode.Success, code);
	}*/
	
	/**
	 * 测试退款流程 -审核不通过
	 */
	/*@Test
	public void testRefundRefuse() {
		//申请退款
		Record productOrder = Db.findFirst("select b.* from `order` as a inner join product_order as b on a.id = b.order_id where a.status = ? and b.status = ?", BaseDao.PAYED, 1);
		Customer customer = Customer.dao.findFirst("select * from customer");
		Refund model = new Refund();
		model.setCustomerId(customer.getId());
		model.setProductOrderId(productOrder.getInt("id"));
		model.setRefundAmount(productOrder.getInt("unitOrdered"));
		model.setRefundCash(productOrder.getBigDecimal("totalActualProductPrice"));
		ServiceCode refundCode = member.refund(model, "['b.png']");
		assertEquals(ServiceCode.Success, refundCode);
		//审核通过
		ServiceCode code = merchant.auditRefund(model.getId(), 2, "", "管理员", model.getApplyCash(), new BigDecimal(0));
		assertEquals(ServiceCode.Success, code);
	}*/
	
	/**
	 * 测试退货流程 -审核通过
	 */
	/*@Test
	public void testReturnedPass() {
		//申请退货
		Record productOrder = Db.findFirst("select b.* from `order` as a inner join product_order as b on a.id = b.order_id where a.status = ?", BaseDao.CONFIRMED);
		Customer customer = Customer.dao.findFirst("select * from customer");
		Back model = new Back();
		model.setCustomerId(customer.getId());
		model.setProductOrderId(productOrder.getInt("id"));
		model.setRefundAmount(productOrder.getInt("unitOrdered"));
		model.setRefundCash(productOrder.getBigDecimal("totalActualProductPrice"));
		ServiceCode returnedCode = member.returned(model, "['b.png']");
		assertEquals(ServiceCode.Success, returnedCode);
		//审核通过
		ServiceCode code = merchant.auditReturned(model.getId(), 1, "", "管理员", model.getApplyCash(), new BigDecimal(0));
		assertEquals(ServiceCode.Success, code);
	}*/
	
	/**
	 * 测试退货流程 -审核不通过
	 * @throws ParseException 
	 */
	/*@Test
	public void testReturnedRefuse() {
		//申请退货
		Record productOrder = Db.findFirst("select b.* from `order` as a inner join product_order as b on a.id = b.order_id where a.status = ? and b.status = 1", BaseDao.CONFIRMED);
		Customer customer = Customer.dao.findFirst("select * from customer");
		Back model = new Back();
		model.setCustomerId(customer.getId());
		model.setProductOrderId(productOrder.getInt("id"));
		model.setRefundAmount(productOrder.getInt("unitOrdered"));
		model.setRefundCash(productOrder.getBigDecimal("totalActualProductPrice"));
		ServiceCode returnedCode = member.returned(model, "['b.png']");
		assertEquals(ServiceCode.Success, returnedCode);
		//审核
		ServiceCode code = merchant.auditReturned(model.getId(), 2, "", "管理员", model.getApplyCash(), new BigDecimal(0));
		assertEquals(ServiceCode.Success, code);
	}*/
	
	/*@Test
	public void testSubmitOrderEvent() {
		Order order = Order.dao.findFirst("select * from `order`");
		OrderParam orderParam = new OrderParam();
		orderParam.setOrder(order);
		EventRole eventRole = new EventRole();
		eventRole.dispatch(EventEnum.EVENT_SUBMIT_ORDER, orderParam);
	}*/
	
	/*@Test
	public void tetOrder() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = sdf.parse("2017-12-03 16:00:00");
		System.out.println(date.toGMTString());
		System.out.println(date.toLocaleString());
		System.out.println(date.toString());
		date = new Date();
		System.out.println("select a.* from `order` as a left join product_order as b on a.id = b.order_id where a.created_at >= " + date);
		List<Order> orders = Order.dao.find("select a.* from `order` as a left join product_order as b on a.id = b.order_id where a.created_at >= ?", date);
		for (Order order : orders)
			System.out.println(order.getCreatedAt());
		
		List<Coupon> list = Coupon.dao.find("select b.* from customer_coupon as a left join coupon as b on a.couponId = b.id");
		System.out.println(list);
	}*/
	
	@Test
	public void testA() {
		List<CouponProduct> list = CouponProduct.dao.find("select b.* from coupon as a left join coupon_product as b on a.id = b.couponId");
		System.out.println(list);
	}
	
	/**
	 * 测试 +-积分
	 */
	@Test
	public void testAddPoint() {
		int result = CustomerPointService.updatePoint(12, 1, MemberShip.CLICK_ADV_POINT, "点击广告");
		System.out.println("result:" + result);
	}
	
	/**
	 * 测试 +-成长值
	 */
	@Test
	public void testGrowth() {
		int result = CustomerGrowthService.updateGrowth(12, 1, MemberShip.CLICK_ADV_GROWTH, "点击广告");
		System.out.println("result:" + result);
	}
	
	/**
	 * 测试 +-通币
	 */
	@Test
	public void testAddGold() {
		CustomerGoldService.updateGold(12, 1, MemberShip.BUY_SPECIALTY_THIRDPAY_GOLD, "第三方支付");
	}
	
	/**
	 * 测试添加等级规则
	 */
	@Test
	public void testCreateGrade() {
		CustomerGrade model = new CustomerGrade();
		model.setName("超级会员");
		model.setStart(10000);
		model.setEnd(50000);
		ServiceCode code = CustomerGradeService.create(model);
		System.out.println(code);
	}
	
	/**
	 * 测试修改等级规则
	 */
	@Test
	public void testUpdateGrade() {
		CustomerGrade model = new CustomerGrade();
		model.setId(8);
		model.setName("超级会员");
		model.setStart(30000);
		model.setEnd(80000);
		
		ServiceCode code = CustomerGradeService.update(model);
		System.out.println(code);
	}
	
	/**
	 * 测试 修改会员等级和是否禁用会员
	 */
	@Test
	public void testChangeCustomer() {
		Integer isdisabled = 1;
		String grade = "abc";
		Customer model = new Customer();
		model.set("id", 12);
		model.set("disable", isdisabled);
		model.set("grade", grade);
		Member.updateInfo(model);
	}
}
