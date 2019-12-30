package com.eshop.excel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.*;
import org.apache.poi.ss.usermodel.BorderFormatting;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;

import com.eshop.helper.ExcelHelper;
import com.jfinal.plugin.activerecord.Record;

public class ExportSupplierOrderSummary {

	
	/**
	 * 供应商对账汇总表
	 * @param name 文件名称
	 * @param supplierId 
	 * @param statistics 
	 * @param endTime 
	 * @param startTime 
	 * @param list 
	 * @param headers 
	 * @param headers 表头
	 * @param data 数据
	 * @return 文件路径
	 */
	public static String exportSupplierOrderSummary(String name, List<Record> list, String startTime, String endTime, Record statistics, Map<String, String> searchMap) {
		HSSFWorkbook wb = new HSSFWorkbook(); // 创建了一个excel文件
		HSSFSheet sheet = wb.createSheet("Sheet1"); // 创建了一个工作簿
		HSSFCellStyle titleStyle=wb.createCellStyle();
		HSSFCellStyle cellStyleA=wb.createCellStyle();
		HSSFCellStyle cellStyleB=wb.createCellStyle();
		HSSFCellStyle headerStyle=wb.createCellStyle();

		HSSFFont titleFontStyle=wb.createFont();
		HSSFFont cellFontStyleA=wb.createFont();
		HSSFFont cellFontStyleB=wb.createFont();
		HSSFFont headerFontStyle=wb.createFont();
		//设置字体样式  
		titleFontStyle.setFontName("宋体");
		cellFontStyleA.setFontName("宋体");
		cellFontStyleB.setFontName("Arial Unicode MS"); 
		headerFontStyle.setFontName("宋体");
		//设置字体高度  
		titleFontStyle.setFontHeightInPoints((short)14);
		cellFontStyleA.setFontHeightInPoints((short)10); 
		cellFontStyleB.setFontHeightInPoints((short)10);   
		headerFontStyle.setFontHeightInPoints((short)10);
		
		titleStyle.setAlignment(CellStyle.ALIGN_CENTER);
		titleStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		cellStyleA.setAlignment(CellStyle.ALIGN_LEFT);
		cellStyleA.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		cellStyleB.setAlignment(CellStyle.ALIGN_LEFT);
		cellStyleB.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		headerStyle.setAlignment(CellStyle.ALIGN_CENTER);
		headerStyle.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
		
		titleStyle.setFont(titleFontStyle);
		headerStyle.setFont(headerFontStyle);
		cellStyleA.setFont(cellFontStyleA);
		cellStyleB.setFont(cellFontStyleB);
	
		//第一行
		HSSFRow rw0 = sheet.createRow(0);
		rw0.createCell(0).setCellValue("供应商对账汇总表（"+searchMap.get("spStatus")+"）");
		rw0.getCell(0).setCellStyle(titleStyle); 
		getTogetherAndaddBorder(0,0,0,11,sheet,wb);
		rw0.setHeightInPoints((float)18.75);
		
		//第二行
		HSSFRow rw1 = sheet.createRow(1);  
		rw1.createCell(0).setCellValue("供货单位：" + searchMap.get("spName"));
		rw1.createCell(6).setCellValue("收货单位：广东通驿高速公路服务区有限公司");
		rw1.getCell(0).setCellStyle(cellStyleA);
		rw1.getCell(6).setCellStyle(cellStyleB);
		getTogetherAndaddBorder(1,1,0,5,sheet,wb);
		getTogetherAndaddBorder(1,1,6,11,sheet,wb);
		rw1.setHeightInPoints(15);
		
		//第三行
		HSSFRow rw2 = sheet.createRow(2);  
		rw2.createCell(0).setCellValue("联　系人："+ searchMap.get("spContactPerson"));
		rw2.createCell(6).setCellValue("联　系人：" + "陈家忠");
		rw2.getCell(0).setCellStyle(cellStyleA);
		rw2.getCell(6).setCellStyle(cellStyleB);
		getTogetherAndaddBorder(2,2,0,5,sheet,wb);
		getTogetherAndaddBorder(2,2,6,11,sheet,wb);
		rw2.setHeightInPoints(15);
		
		//第四行
		HSSFRow rw3 = sheet.createRow(3);
		rw3.createCell(0).setCellValue("电　　话："+ searchMap.get("spPhone"));
		rw3.createCell(6).setCellValue("电　　话：020-22353742");
		rw3.getCell(0).setCellStyle(cellStyleA);
		rw3.getCell(6).setCellStyle(cellStyleB);
		getTogetherAndaddBorder(3,3,0,5,sheet,wb);
		getTogetherAndaddBorder(3,3,6,11,sheet,wb);
		rw3.setHeightInPoints(15);
		
		//第五行
		HSSFRow rw4 = sheet.createRow(4);  
		
		rw4.createCell(0).setCellValue("结款方式："+ searchMap.get("spAccountPeriod"));
		rw4.createCell(6).setCellValue("");
		rw4.getCell(0).setCellStyle(cellStyleA);
		rw4.getCell(6).setCellStyle(cellStyleA);
		getTogetherAndaddBorder(4,4,0,5,sheet,wb);
		getTogetherAndaddBorder(4,4,6,11,sheet,wb);
		rw4.setHeightInPoints((float)14.25);

		//第六行
		addOneBorder(headerStyle);
		
		Map<String, String> headers = new LinkedHashMap<String, String>();
		headers.put("num", "序号");
		headers.put("sendOutTime", "发货日期");
		headers.put("supplier_name", "供应商");
		headers.put("tradeType", "交易类型");
		headers.put("unitOrdered", "发货总数量");
		headers.put("totalProductCost", "产品总成本");
		headers.put("totalActualDeliveryCharge", "快递总金额");
		headers.put("totalCost", "总成本");
		headers.put("totalPrice", "产品总金额");
		headers.put("backAmount", "退货总数量");
		headers.put("totalRefund", "退货总金额");
		headers.put("totalSalable", "总销售额");
		int k = 0;
		HSSFRow rw5 = sheet.createRow(5);
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			HSSFCell cell1 = rw5.createCell((short) k);
			cell1.setCellValue(entry.getValue());
			cell1.setCellStyle(headerStyle);
			k++;
		}
		rw5.setHeightInPoints((float)12.75);
		
		int num = 1;
		int rowIndex = 6;
		for (Record item : list) {
			k = 0;
			HSSFRow row = sheet.createRow(rowIndex);
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				String key = entry.getKey();
				HSSFCell cell = row.createCell((short) k);
				cell.setCellStyle(headerStyle);
				
				if (key.equals("num")) {
					cell.setCellValue(num);
				} else if (key.equals("totalCost")) {
					double totalCost = item.getBigDecimal("totalProductCost").doubleValue() + item.getBigDecimal("totalActualDeliveryCharge").doubleValue();
					cell.setCellValue(totalCost);
				} else {
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
		
		rowIndex = list.size() + 6;     //行索引
		int cr = rowIndex;
		HSSFRow rwri = null;
		rwri = sheet.createRow(cr);
		rwri.createCell(0).setCellValue("截止" + endTime +"，累计发货"+ statistics.getInt("totalSendAmount") +"单，" + "累计退货" + statistics.getInt("totalBackAmount") + "单，销售总成本"+ statistics.getDouble("totalCost")+ "元，销售总额"+ statistics.getDouble("totalSalable")+"元，；其中微信支付"+ statistics.getDouble("totalWeiXin") +"  元(微信App："+ statistics.getDouble("totalWxApp")+" 元，微信网页："+ statistics.getDouble("totalWxPc") +" 元)，" + 
				"支付宝支付："+ statistics.getDouble("totalAlipay") +"  元，银联支付："+ statistics.getDouble("totalUnionPay") +" 元");
		HSSFCellStyle spcs = wb.createCellStyle();
		spcs.setAlignment(CellStyle.ALIGN_LEFT);
		spcs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		spcs.setWrapText(true);     
		rwri.getCell(0).setCellStyle(spcs);
		getTogetherAndaddBorder(cr,cr,0,11,sheet,wb);
		rwri.setHeightInPoints(36);
		
		//第n+1行
        cr++;
		rwri = sheet.createRow(cr);  
		rwri.createCell(0).setCellValue(startTime + "-" + endTime +"累计应收（付）货款：" + statistics.getDouble("totalCost") + " 元");
		rwri.createCell(6).setCellValue(startTime + "-" + endTime +"应开增值税普通（专用）发票累计：" + statistics.getDouble("totalCost") + " 元");
		rwri.getCell(0).setCellStyle(cellStyleA);
		rwri.getCell(6).setCellStyle(cellStyleA);
		getTogetherAndaddBorder(cr,cr,0,5,sheet,wb);
		getTogetherAndaddBorder(cr,cr,6,11,sheet,wb);
		rwri.setHeightInPoints((float)14.25);

		//第n+2行
		/*cr++;
		rwri = sheet.createRow(cr);  
		rwri.createCell(0).setCellValue("上月货款金额：");
		rwri.createCell(5).setCellValue(startTime + "-" + endTime +"应（收）付快递成本累计：" + statistics.getDouble("totalDeliveryPrice") + "   元");
		rwri.getCell(0).setCellStyle(cellStyleA);
		rwri.getCell(5).setCellStyle(cellStyleA);
		getTogetherAndaddBorder(cr,cr,0,4,sheet,wb);
		getTogetherAndaddBorder(cr,cr,5,9,sheet,wb);
		rwri.setHeightInPoints((float)14.25);*/

		//第n+3行
		/*cr++;
		rwri = sheet.createRow(cr);  
		rwri.createCell(0).setCellValue("上月开票金额：");
		rwri.createCell(5).setCellValue("上月快递成本总金额：");
		rwri.getCell(0).setCellStyle(cellStyleA);
		rwri.getCell(5).setCellStyle(cellStyleA);
		getTogetherAndaddBorder(cr,cr,0,4,sheet,wb);
		getTogetherAndaddBorder(cr,cr,5,9,sheet,wb);
		rwri.setHeightInPoints((float)14.25);*/

		//第n+4,5行
		cr++;
		rwri = sheet.createRow(cr);  
		rwri.createCell(0).setCellValue("甲方确认：");
		rwri.createCell(6).setCellValue("乙方确认：");
		rwri.getCell(0).setCellStyle(cellStyleA);
		rwri.getCell(6).setCellStyle(cellStyleA);
		getTogetherAndaddBorder(cr,cr+1,0,5,sheet,wb);
		getTogetherAndaddBorder(cr,cr+1,6,11,sheet,wb);
		rwri.setHeightInPoints((float)14.25);
		cr++;
		
		//第n+6行
		cr++;
		rwri = sheet.createRow(cr);  
		rwri.createCell(0).setCellValue("日   期：");
		rwri.createCell(6).setCellValue("日    期：");
		rwri.getCell(0).setCellStyle(cellStyleA);
		rwri.getCell(6).setCellStyle(cellStyleA);
		getTogetherAndaddBorder(cr,cr,0,5,sheet,wb);
		getTogetherAndaddBorder(cr,cr,6,11,sheet,wb);
		rwri.setHeightInPoints((float)14.25);
		
		// 制表人样式
		HSSFCellStyle spcs2 = wb.createCellStyle();
		spcs2.setAlignment(CellStyle.ALIGN_LEFT);
		spcs2.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		HSSFFont spcs2FontStyle=wb.createFont();
		spcs2FontStyle.setFontName("宋体");
		spcs2FontStyle.setFontHeightInPoints((short)8);
		spcs2.setFont(spcs2FontStyle);
		
		//第n+8,9,10行
		cr++;
		rwri = sheet.createRow(cr);  
		String time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
		rwri.createCell(0).setCellValue("时间："+time);
		rwri.createCell(6).setCellValue("制表人：");
		rwri.createCell(9).setCellValue("审核人：");
		rwri.getCell(0).setCellStyle(spcs2);
		rwri.getCell(6).setCellStyle(spcs2);
		rwri.getCell(9).setCellStyle(spcs2);
		getTogether(cr,cr+1,0,5,sheet,wb);
		getTogether(cr,cr+1,6,8,sheet,wb);
		getTogether(cr,cr+1,9,11,sheet,wb);
		rwri.setHeightInPoints((float)14.25);
		
		float[] colWidth = {4.88f, 8.38f, 8f, 7.63f, 17.63f, 6.88f , 13f , 5.25f, 13.75f, 8.13f, 7.13f, 11.13f, 7f};
		for(int i = 0; i < colWidth.length;i++) {
			sheet.setColumnWidth(i, (int) (256*(colWidth[i]+2)));
		}
		
		return ExcelHelper.getPath(name, wb);
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
	
	static void getTogether(int firstCol,int lastCol,int firstRow,int lastRow ,HSSFSheet sheet,HSSFWorkbook wb) {
		CellRangeAddress cra = new CellRangeAddress(firstCol,lastCol,firstRow,lastRow);
		sheet.addMergedRegion(cra);
	}
	
	/**
	 * 合并单元格加边框
	 * @param cra
	 * @param sheet
	 * @param wb
	 */
	static void addBorder(/*HSSFCellStyle style*/CellRangeAddress cra, HSSFSheet sheet, HSSFWorkbook wb) {
//		//设置上下左右四个边框宽度
//        style.setBorderTop(HSSFBorderFormatting.BORDER_THIN);
//        style.setBorderBottom(HSSFBorderFormatting.BORDER_THIN);
//        style.setBorderLeft(HSSFBorderFormatting.BORDER_THIN);
//        style.setBorderRight(HSSFBorderFormatting.BORDER_THIN);
//        //设置上下左右四个边框颜色
//        style.setTopBorderColor(HSSFColor.BLACK.index);
//        style.setBottomBorderColor(HSSFColor.BLACK.index);
//        style.setLeftBorderColor(HSSFColor.BLACK.index);
//        style.setRightBorderColor(HSSFColor.BLACK.index);
		
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
	/**
	 * 获取结款方式
	 * @param payWay
	 * @return
	 */
	public static String getPayWay(String payWay) {
		if(payWay != null && !"".equals(payWay)) {
			String[] arr = payWay.split(",");
			String string = getPayWayType(arr);
			return string;
		}
		return "";
	}
	/**
	 * 选择结款方式
	 * @param payWay
	 * @return
	 */
	public static String getPayWayType(String[] arr) {
		Integer type = Integer.parseInt(arr[1]);
		if("day".equals(arr[0])) {
			
			if(type != 0) {
				return "日结" + arr[1]+"天";
			}
		}else if("month".equals(arr[0])) {
			if(type != 0) {
				return "月结" + arr[1]+"天";
			}else {
				return "当月结" ;
			}
		}
		return "";
	}
}
