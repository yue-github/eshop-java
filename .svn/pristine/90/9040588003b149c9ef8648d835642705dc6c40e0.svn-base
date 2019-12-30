package com.eshop.excel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderFormatting;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;

import com.eshop.helper.DateHelper;
import com.eshop.helper.ExcelHelper;
import com.eshop.helper.ExcelUtil2;
import com.jfinal.plugin.activerecord.Record;

public class ExportSupplierOrderList {
	
	public static String getOrderStatus(int source) {
		if (source == 1) {
		    return "PC端";
		} else if (source == 2) {
		    return "微信公众号";
		} else if (source == 3) {
		    return "安卓APP";
		} else if (source == 4) {
		    return "苹果APP";
		} else {
		    return "";
		}
	}
	
	public static String exportTripList(String name, List<Record> list, Record statistics, 
			String startTime, String endTime, Map<String, String> searchMap, String headerTitle) {
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Sheet1");
		HSSFRow row = null;
		HSSFCell cell = null;
		
		// 设置表头
		int rowIndex = 0;
		int lastCol = 17;
		ExcelUtil2.creatHeaderTitle(wb, sheet, headerTitle, 0, 0, 0, lastCol, false);
		rowIndex++;
		
		// 设置查询条件
		row = sheet.createRow(rowIndex);
		cell = row.createCell(0);
		cell.setCellValue("供应商：");
		cell = row.createCell(1);
		cell.setCellValue(searchMap.get("strSupplierName"));
		cell = row.createCell(3);
		cell.setCellValue("时间段：");
		cell = row.createCell(4);
		cell.setCellValue(searchMap.get("strStartTime") + "至" + searchMap.get("strEndTime"));
		cell = row.createCell(7);
		cell.setCellValue("订单状态：");
		cell = row.createCell(8);
		cell.setCellValue(searchMap.get("strStatus"));
		cell = row.createCell(11);
		cell.setCellValue("支付方式：");
		cell = row.createCell(12);
		cell.setCellValue(searchMap.get("strPayType"));
		cell = row.createCell(15);
		cell.setCellValue("单位：");
		cell = row.createCell(16);
		cell.setCellValue("元");
		rowIndex++;
		
		// 设置列表标题
		Map<String, String> headers = new LinkedHashMap<String, String>();
		headers.put("num", "序号");
		headers.put("supplier_name", "供应商");
		headers.put("payTime", "支付日期");
		headers.put("payType", "支付方式");
		headers.put("sendOutTime", "发货日期");
		headers.put("order_code", "订单号");
		headers.put("tradeType", "交易类型");
		headers.put("expressCode", "快递单号");
		headers.put("logisticsName", "快递名称");
		headers.put("product_name", "产品名称及规格");
		headers.put("unit", "单位");
		headers.put("unitOrdered", "发货数量");
		headers.put("totalSale0", "产品代售金额小计");
		headers.put("refundCash", "退货总金额");
		headers.put("totalSale", "总代售总额");
		headers.put("commission", "佣金比例（%）");
		headers.put("commissionPayable", "佣金（代理服务费）");
		headers.put("note", "备注");
		row = sheet.createRow(rowIndex);
		HSSFCellStyle style = ExcelUtil2.getStyle9(wb, true, true, 10, "宋体");
		ExcelUtil2.setTitle(row, headers, style);
		rowIndex++;
		
		// 设置列表内容
		int num = 1;
		for (Record item : list) {
			int k = 0;
			row = sheet.createRow(rowIndex);
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				String key = entry.getKey();
				cell = row.createCell((short) k);
				cell.setCellStyle(style);
				
				if (key.equals("num")) {
					cell.setCellValue(num);
				}
				else if (key.equals("payType")) {
					String payType = ExcelHelper.convertPayType(item.getInt(key));
					cell.setCellValue(payType);
				}
				else if (key.equals("totalSale0")) {
					cell.setCellValue(item.getNumber("totalSale").doubleValue());
				} 
				else if (key.equals("product_name")) {
					String productName = item.getStr("product_name");
					String selectProterties = item.getStr("selectProterties");
					cell.setCellValue(productName + " " + selectProterties);
				}
				else {
					String result = "";
					if (item.get(key) != null) {
						result = String.valueOf(item.get(key));
					}
					cell.setCellValue(result);
				}
				
				k++;
			}

			rowIndex++;
			num++;
		}
		
		HSSFCellStyle cellStyleA = ExcelUtil2.getStyle8(wb, 10, false, "宋体", true, false, true);
		
		row = sheet.createRow(rowIndex);  
		row.createCell(0).setCellValue("截止" + endTime + "，累计发货  " + statistics.getInt("totalUnitOrdered") + " 单，" + "，累计退货  " + statistics.getInt("totalRefundAmount") + " 单，" + " 销售总成本" + statistics.getDouble("totalSaleCost") + " 元，销售总额 " + statistics.getDouble("totalSale") + " 元，退款总额" + statistics.getDouble("totalRefundCash") + " 元；" +
				"其中微信支付" + statistics.getDouble("weixin") + "元(微信App：" + statistics.getDouble("weixinApp") + "，微信网页：" + statistics.getDouble("weixinPc") + "元)，支付宝支付：" + statistics.getDouble("alipay") + "元，银联支付：" + statistics.getDouble("unionpay") + "元，余额支付：" + statistics.getDouble("balancepay") + "元");
		row.getCell(0).setCellStyle(cellStyleA);
		getTogetherAndaddBorder(rowIndex, rowIndex, 0, lastCol, sheet, wb);
		row.setHeightInPoints(15);
		
		int half = (int) Math.floor(lastCol / 2);
		int nextHalf = half + 1;
		
		rowIndex++;
		row = sheet.createRow(rowIndex);  
		row.createCell(0).setCellValue("甲方确认：");
		row.getCell(0).setCellStyle(cellStyleA);
		row.createCell(nextHalf).setCellValue("乙方确认:");
		row.getCell(nextHalf).setCellStyle(cellStyleA);;
		getTogetherAndaddBorder(rowIndex, rowIndex + 1, 0, half, sheet, wb);
		getTogetherAndaddBorder(rowIndex, rowIndex + 1, nextHalf, lastCol, sheet, wb);
		row.setHeightInPoints((float)14.25);
		
		rowIndex = rowIndex + 2;
		row = sheet.createRow(rowIndex);  
		row.createCell(0).setCellValue("日       期：");
		row.getCell(0).setCellStyle(cellStyleA);
		row.createCell(nextHalf).setCellValue("日       期：");
		row.getCell(nextHalf).setCellStyle(cellStyleA);
		getTogetherAndaddBorder(rowIndex, rowIndex, 0, half, sheet, wb);
		getTogetherAndaddBorder(rowIndex, rowIndex, nextHalf, lastCol, sheet, wb);
		row.setHeightInPoints((float)14.25);
		
		rowIndex++;
		//设置制表人，审核人的字体样式
		HSSFCellStyle spcs2 = wb.createCellStyle();
		spcs2.setAlignment(CellStyle.ALIGN_LEFT);
		spcs2.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		HSSFFont spcs2FontStyle=wb.createFont();
		spcs2FontStyle.setFontName("宋体");
		spcs2FontStyle.setFontHeightInPoints((short)8);
		spcs2.setFont(spcs2FontStyle);
		
		HSSFRow rw11 = sheet.createRow(rowIndex);  
		String time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
		rw11.createCell(0).setCellValue("时间："+time);
		rw11.createCell(7).setCellValue("制表人：");
		rw11.createCell(11).setCellValue("审核人：");
		rw11.getCell(0).setCellStyle(spcs2);
		rw11.getCell(7).setCellStyle(spcs2);
		rw11.getCell(11).setCellStyle(spcs2);
		sheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex+1,0,6));
		sheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex+1,7,10));
		sheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex+1,11,lastCol));
		rw11.setHeightInPoints((float)14.25);
		
		return ExcelHelper.getPath(name, wb);
	}
	
	/**
	 * 供应商对账明细
	 * @param name 文件名称
	 * @param headers 表头
	 * @param data 数据
	 * @return 文件路径
	 */
	public static String exportSupplierOrderList(String name, List<Record> list, Record statistics, 
			String startTime, String endTime, Map<String, String> supplierMap) {
		
		// 创建了一个excel文件
		HSSFWorkbook wb = new HSSFWorkbook(); 
		// 创建了一个工作簿
		HSSFSheet sheet = wb.createSheet("Sheet1"); 
		
		HSSFCellStyle titleStyle=wb.createCellStyle();
		HSSFCellStyle cellStyleA=wb.createCellStyle();
		HSSFCellStyle cellStyleB=wb.createCellStyle();
		HSSFCellStyle cellStyleC=wb.createCellStyle();
		HSSFCellStyle headerStyle=wb.createCellStyle();
		HSSFCellStyle tableStyle=wb.createCellStyle();
	
		HSSFFont titleFontStyle=wb.createFont();
		HSSFFont cellFontStyleA=wb.createFont();
		HSSFFont cellFontStyleB=wb.createFont();
		HSSFFont cellFontStyleC=wb.createFont();
		HSSFFont headerFontStyle=wb.createFont();
		HSSFFont tableFontStyle=wb.createFont();
		//设置字体样式  
		titleFontStyle.setFontName("宋体");
		cellFontStyleA.setFontName("宋体");
		cellFontStyleB.setFontName("宋体");
		cellFontStyleC.setFontName("宋体");
		headerFontStyle.setFontName("宋体");
		tableFontStyle.setFontName("宋体");
		//设置字体高度  
		titleFontStyle.setFontHeightInPoints((short)14);
		cellFontStyleA.setFontHeightInPoints((short)10); 
		cellFontStyleB.setFontHeightInPoints((short)10);
		cellFontStyleC.setFontHeightInPoints((short)10);
		headerFontStyle.setFontHeightInPoints((short)10);
		
		titleStyle.setAlignment(CellStyle.ALIGN_CENTER);
		titleStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		cellStyleA.setAlignment(CellStyle.ALIGN_LEFT);
		cellStyleA.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		cellStyleB.setAlignment(CellStyle.ALIGN_LEFT);
		cellStyleB.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		cellStyleC.setAlignment(CellStyle.ALIGN_RIGHT);
		cellStyleC.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		headerStyle.setAlignment(CellStyle.ALIGN_CENTER);
		headerStyle.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
		tableStyle.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
		
		titleStyle.setFont(titleFontStyle);
		headerStyle.setFont(headerFontStyle);
		cellStyleA.setFont(cellFontStyleA);
		cellStyleB.setFont(cellFontStyleB);
		cellStyleC.setFont(cellFontStyleC);
		tableStyle.setFont(tableFontStyle);
	
		addOneBorder(tableStyle);
		addOneBorder(cellStyleA);
		
		//第一行
		int rowIndex = 0;   //行索引
		HSSFRow rw0=sheet.createRow(0);
		rw0.createCell(0).setCellValue("供应商对账明细表");
		rw0.getCell(0).setCellStyle(titleStyle);
		getTogetherAndaddBorder(0,0,0,15,sheet,wb);
		rw0.setHeightInPoints((float)18.75);
		
		//第二行
		HSSFRow rw1 = sheet.createRow(1);  
		rw1.createCell(0).setCellValue("供货单位：" + supplierMap.get("spName"));
		rw1.getCell(0).setCellStyle(cellStyleA);
		rw1.createCell(7).setCellValue("收货单位：广东通驿高速公路服务区有限公司");
		rw1.getCell(7).setCellStyle(cellStyleB);
		getTogetherAndaddBorder(1,1,0,6,sheet,wb);
		getTogetherAndaddBorder(1,1,7,15,sheet,wb);
		rw1.setHeightInPoints(15);
		
		//第三行
		HSSFRow rw2 = sheet.createRow(2);  
		rw2.createCell(0).setCellValue("联 系 人：" + supplierMap.get("spContactPerson"));
		rw2.getCell(0).setCellStyle(cellStyleA);
		rw2.createCell(7).setCellValue("联 系 人：" + "陈家忠");
		rw2.getCell(7).setCellStyle(cellStyleB);
		getTogetherAndaddBorder(2,2,0,6,sheet,wb);
		getTogetherAndaddBorder(2,2,7,15,sheet,wb);
		rw2.setHeightInPoints(15);
		
		//第四行
		HSSFRow rw3 = sheet.createRow(3);  
		rw3.createCell(0).setCellValue("电　　话：" + supplierMap.get("spPhone"));
		rw3.getCell(0).setCellStyle(cellStyleA);
		rw3.createCell(7).setCellValue("电　　话：020-22353742");
		rw3.getCell(7).setCellStyle(cellStyleB);
		getTogetherAndaddBorder(3,3,0,6,sheet,wb);
		getTogetherAndaddBorder(3,3,7,15,sheet,wb);
		rw3.setHeightInPoints(15);
		
		rowIndex = 4;
	
		//第五行
		HSSFRow rw4 = sheet.createRow(rowIndex++);  
		rw4.createCell(0).setCellValue("结款方式：" + supplierMap.get("spAccountPeriod"));
		rw4.getCell(0).setCellStyle(cellStyleA);
		rw4.createCell(7).setCellValue("");
		rw4.getCell(0).setCellStyle(cellStyleA);
		rw4.getCell(7).setCellStyle(cellStyleA);
		getTogetherAndaddBorder(4,4,0,6,sheet,wb);
		getTogetherAndaddBorder(4,4,7,15,sheet,wb);
		rw4.setHeightInPoints((float)14.25);
	
		//第六行
		addOneBorder(headerStyle);
		
		Map<String, String> headers = new LinkedHashMap<String, String>();
		headers.put("num", "序号");
		headers.put("supplier_name", "供应商");
		headers.put("payTime", "支付日期");
		headers.put("sendOutTime", "发货日期");
		headers.put("order_code", "订单号");
		headers.put("product_name", "产品名称及规格");
		headers.put("unitOrdered", "发货数量");
		headers.put("unitCost", "产品单位成本");
		headers.put("totalProductCost", "产品成本小计");
		headers.put("deliveryPrice", "快递费用");
		headers.put("totalCost", "总成本");
		headers.put("totalActualProductPrice", "产品销售金额小计");
		headers.put("refundCash", "退货总金额");
		headers.put("totalSale", "销售总额");
		headers.put("payType", "支付方式");
		headers.put("source", "平台类型");
		
		int k = 0;
		HSSFRow rww = sheet.createRow(rowIndex++);
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			HSSFCell cell1 = rww.createCell((short) k);
			cell1.setCellValue(entry.getValue());
			cell1.setCellStyle(headerStyle);
			k++;
		}
		rww.setHeightInPoints((float)12.75);
		
		int num = 1;
		HSSFCellStyle styleLeft = ExcelUtil2.getStyle8(wb, 10, false, "宋体", true, false, false);
		for (Record item : list) {
			k = 0;
			HSSFRow row = sheet.createRow(rowIndex);
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				String key = entry.getKey();
				HSSFCell cell = row.createCell((short) k);
				cell.setCellStyle(headerStyle);
				
				if (key.equals("num")) {
					cell.setCellValue(num);
				}
				else if (key.equals("payType")) {
					if (item.get("payType") != null) {
						String payType = ExcelHelper.getOrderPayType(item.getInt("payType"));
						cell.setCellValue(payType);
					} else {
						cell.setCellValue("");
					}
				}
				else if (key.equals("payTime")) {
					String payTime = "";
					if (item.get("payTime") != null) {
						payTime = DateHelper.formatDateTime(item.getDate("payTime"));
					}
					cell.setCellValue(payTime);
					cell.setCellStyle(styleLeft);
				}
				else if (key.equals("sendOutTime")) {
					String sendOutTime = "";
					if (item.get("sendOutTime") != null) {
						sendOutTime = DateHelper.formatDateTime(item.getDate("sendOutTime"));
					}
					cell.setCellValue(sendOutTime);
					cell.setCellStyle(styleLeft);
				}
				else if (key.equals("product_name")) {
					String productName = item.getStr("product_name");
					String selectProterties = item.getStr("selectProterties");
					if (selectProterties != null && !selectProterties.equals("")) {
						productName = productName + "(" + selectProterties + ")";
					}
					cell.setCellValue(productName);
					cell.setCellStyle(styleLeft);
				}
				else if (key.equals("source")) {
					cell.setCellValue(getOrderStatus(item.getInt("source")));
					cell.setCellStyle(styleLeft);
				}
				else {
					String result = "";
					if (item.get(key) != null) {
						result = String.valueOf(item.get(key));
					}
					cell.setCellValue(result);
				}
				
				k++;
			}

			rowIndex++;
			num++;
		}
		
		HSSFRow rw5 = sheet.createRow(rowIndex);  
		rw5.createCell(0).setCellValue("截止" + endTime + "，累计发货  " + statistics.getInt("totalUnitOrdered") + " 单，" + "，累计退货  " + statistics.getInt("totalRefundAmount") + " 单，" + " 销售总成本" + statistics.getDouble("totalSaleCost") + " 元，销售总额 " + statistics.getDouble("totalSale") + " 元，退款总额" + statistics.getDouble("totalRefundCash") + " 元；" +
				"其中微信支付" + statistics.getDouble("weixin") + "元(微信App：" + statistics.getDouble("weixinApp") + "，微信网页：" + statistics.getDouble("weixinPc") + "元)，支付宝支付：" + statistics.getDouble("alipay") + "元，银联支付：" + statistics.getDouble("unionpay") + "元，余额支付：" + statistics.getDouble("balancepay") + "元");
		HSSFCellStyle spcs = wb.createCellStyle();
		spcs.setAlignment(CellStyle.ALIGN_LEFT);
		spcs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		rw5.getCell(0).setCellStyle(spcs);
		getTogetherAndaddBorder(rowIndex,rowIndex,0,15,sheet,wb);
		rw5.setHeightInPoints(15);
		
		rowIndex++;
		HSSFRow rw6 = sheet.createRow(rowIndex);  
		rw6.createCell(0).setCellValue("截止" + endTime+ "累计应收（付）货款：" + statistics.getDouble("totalSaleCost") + " 元");
		rw6.getCell(0).setCellStyle(cellStyleA);
		rw6.createCell(7).setCellValue("截止" + endTime + "应开增值税普通（专用）发票累计：" + statistics.getDouble("totalSaleCost") + " 元");
		rw6.getCell(7).setCellStyle(cellStyleA);
		getTogetherAndaddBorder(rowIndex,rowIndex,0,6,sheet,wb);
		getTogetherAndaddBorder(rowIndex,rowIndex,7,15,sheet,wb);
		rw6.setHeightInPoints((float)14.25);
		
		rowIndex++;
		HSSFRow rw9 = sheet.createRow(rowIndex);  
		rw9.createCell(0).setCellValue("甲方确认：");
		rw9.getCell(0).setCellStyle(cellStyleA);
		rw9.createCell(7).setCellValue("乙方确认:");
		rw9.getCell(7).setCellStyle(cellStyleA);;
		getTogetherAndaddBorder(rowIndex,rowIndex+1,0,6,sheet,wb);
		getTogetherAndaddBorder(rowIndex,rowIndex+1,7,15,sheet,wb);
		rw9.setHeightInPoints((float)14.25);
		
		rowIndex = rowIndex + 2;
		HSSFRow rw10 = sheet.createRow(rowIndex);  
		rw10.createCell(0).setCellValue("日       期：");
		rw10.getCell(0).setCellStyle(cellStyleA);
		rw10.createCell(7).setCellValue("日       期：");
		rw10.getCell(7).setCellStyle(cellStyleA);
		getTogetherAndaddBorder(rowIndex,rowIndex,0,6,sheet,wb);
		getTogetherAndaddBorder(rowIndex,rowIndex,7,15,sheet,wb);
		rw10.setHeightInPoints((float)14.25);
		
	
		rowIndex = rowIndex + 1;
		//设置制表人，审核人的字体样式
		HSSFCellStyle spcs2 = wb.createCellStyle();
		spcs2.setAlignment(CellStyle.ALIGN_LEFT);
		spcs2.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		HSSFFont spcs2FontStyle=wb.createFont();
		spcs2FontStyle.setFontName("宋体");
		spcs2FontStyle.setFontHeightInPoints((short)8);
		spcs2.setFont(spcs2FontStyle);
		
		HSSFRow rw11 = sheet.createRow(rowIndex);  
		String time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
		rw11.createCell(0).setCellValue("时间："+time);
		rw11.createCell(7).setCellValue("制表人：");
		rw11.createCell(11).setCellValue("审核人：");
		rw11.getCell(0).setCellStyle(spcs2);
		rw11.getCell(7).setCellStyle(spcs2);
		rw11.getCell(11).setCellStyle(spcs2);
		sheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex+1,0,6));
		sheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex+1,7,10));
		sheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex+1,11,15));
		rw11.setHeightInPoints((float)14.25);
	
		float[] colWidth = {6.5f, 27.63f, 8.38f, 8.38f, 12.38f, 26.38f, 8.38f, 6.63f, 13.75f, 13f, 14.5f,
				6.38f, 13.38f, 7.13f};
		for(int i = 0; i < colWidth.length;i++) {
			sheet.setColumnWidth(i, (int) (256*(colWidth[i]+2)));
		}
		
		return ExcelHelper.getPath(name, wb);
	}
	
	private static String getInvoiceType(String invoiceType) {
		String strInvoiceType = "普通发票";
		
		if (invoiceType.equals("general") || invoiceType.equals("all")) {
			strInvoiceType = "普通发票";
		} else if (invoiceType.equals("value_add")) {
			strInvoiceType = "增值税发票";
		}
		
		return strInvoiceType;
	}
	
	/**
	 * 合并单元格并加边框
	 * 注意要先加样式再调用这个方法
	 * @param firstCol
	 * @param lastCol
	 * @param firstRow
	 * @param lastRow
	 * @param sheet
	 * @param wb
	 */
	static void getTogetherAndaddBorder(int firstCol,int lastCol,int firstRow,int lastRow ,HSSFSheet sheet,HSSFWorkbook wb) {
		CellRangeAddress cra = new CellRangeAddress(firstCol,lastCol,firstRow,lastRow);
		sheet.addMergedRegion(cra);
		addBorder(cra,sheet,wb);
	}
	
	/**
	 * 合并单元格加边框
	 * @param cra
	 * @param sheet
	 * @param wb
	 */
	static void addBorder(/*HSSFCellStyle style*/CellRangeAddress cra, HSSFSheet sheet, HSSFWorkbook wb) {
		// 使用RegionUtil类为合并后的单元格添加边框  
        RegionUtil.setBorderBottom(1, cra, sheet, wb); // 下边框  
        RegionUtil.setBorderLeft(1, cra, sheet, wb); // 左边框  
        RegionUtil.setBorderRight(1, cra, sheet, wb); // 右边框  
        RegionUtil.setBorderTop(1, cra, sheet, wb); // 上边框
	}
	
	/**
	 * 将一个单元格的cellstyle加边框
	 * @param style
	 * @return
	 */
	static HSSFCellStyle addOneBorder(HSSFCellStyle style) {
		//设置上下左右四个边框宽度
      style.setBorderTop(BorderFormatting.BORDER_THIN);
      style.setBorderBottom(BorderFormatting.BORDER_THIN);
      style.setBorderLeft(BorderFormatting.BORDER_THIN);
      style.setBorderRight(BorderFormatting.BORDER_THIN);
      //设置上下左右四个边框颜色
      style.setTopBorderColor(HSSFColor.BLACK.index);
      style.setBottomBorderColor(HSSFColor.BLACK.index);
      style.setLeftBorderColor(HSSFColor.BLACK.index);
      style.setRightBorderColor(HSSFColor.BLACK.index);
      
      return style;
	}

}
