package com.eshop.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.csvreader.CsvReader;
import com.eshop.model.Customer;
import com.eshop.model.Supplier;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;

/**
 * Excel辅助类
 * @author TangYiFeng
 */
public class ExcelHelper {
	public static void toImport() {

	}
	
	/**
	 * 导出供应商列表
	 * @param name
	 * @param list
	 * @param statistics
	 * @param startTime
	 * @param endTime
	 * @param searchMap
	 * @param headerTitle
	 * @return
	 */
	public static String exportSupplierList(String name, List<Record> list, Map<String, String> searchMap) {
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Sheet1");
		HSSFRow row = null;
		HSSFCell cell = null;
		
		// 设置表头
		int rowIndex = 0;
		int lastCol = 13;
		String headerTitle = "供应商列表";
		ExcelUtil2.creatHeaderTitle(wb, sheet, headerTitle, 0, 0, 0, lastCol, false);
		rowIndex++;
		
		// 设置查询条件
		row = sheet.createRow(rowIndex);
		cell = row.createCell(0);
		cell.setCellValue("供应商：");
		
		cell = row.createCell(1);
		cell.setCellValue(searchMap.get("name"));
		
		cell = row.createCell(3);
		cell.setCellValue("联系电话：");
		
		cell = row.createCell(4);
		cell.setCellValue(searchMap.get("phone1"));
		
		cell = row.createCell(7);
		cell.setCellValue("联系人：");
		
		cell = row.createCell(8);
		cell.setCellValue(searchMap.get("contactPerson"));
		
		cell = row.createCell(11);
		cell.setCellValue("类型：");
		
		cell = row.createCell(12);
		cell.setCellValue(searchMap.get("strType"));
		
		rowIndex++;
		
		// 设置列表标题
		Map<String, String> headers = new LinkedHashMap<String, String>();
		headers.put("num", "序号");
		headers.put("name", "供应商");
		headers.put("contactPerson", "联系人");
		headers.put("type", "公司类型");
		headers.put("legalPerson", "法人");
		headers.put("province", "所在省");
		headers.put("city", "所在市");
		headers.put("district", "所在区");
		headers.put("street", "所在街道");
		headers.put("bankName", "开会银行");
		headers.put("bankAccount", "账号");
		headers.put("createName", "开户名");
		headers.put("fixedTelephone", "固定电话");
		headers.put("phone1", "第一联系电话");
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
				else if (key.equals("type")) {
					int type = item.getInt("type");
					String strType = type == 1 ? "个人性质" : "公司性质";
					cell.setCellValue(strType);
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
	 * 导出发票
	 * @param name 文件名称
	 * @param headers 表头
	 * @param data 数据
	 * @return 文件路径
	 */
	public static String exportSuppliers(String name, Map<String, String> headers, List<Record> data) {
		HSSFWorkbook wb = new HSSFWorkbook(); // 创建了一个excel文件
		HSSFSheet sheet = wb.createSheet("Sheet1"); // 创建了一个工作簿
		
		//第一行
		setHeaderTitle(wb, sheet, "供应商对账单", 10);

		int rowIndex = 1;     //行索引
		HSSFRow row1 = sheet.createRow(rowIndex);
		int k = 0;
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			HSSFCell cell1 = row1.createCell((short) k);
			cell1.setCellValue(entry.getValue());

			k++;
		}

		rowIndex++;

		int num = 1;
		
		// 创建内容
		for (Record item : data) {
			k = 0;
			HSSFRow row = sheet.createRow(rowIndex);
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				String key = entry.getKey();
				HSSFCell cell = row.createCell((short) k);
				
				if (key.equals("num")) {
					cell.setCellValue(num);
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
		
		return getPath(name, wb);
	}
	
	/**
	 * 读取csv表内容
	 * @param path
	 * @param encoding
	 * @return ArrayList<String[]>
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static ArrayList<String[]> readCsv(String path, String encoding) throws FileNotFoundException, IOException{
		ArrayList<String[]> csvList = new ArrayList<String[]>(); //用来保存数据  
        String csvFilePath = PathKit.getWebRootPath() + "/" + path;
        CsvReader reader = new CsvReader(csvFilePath, ',', Charset.forName(encoding));    //一般用这编码读就可以了      
           
        while(reader.readRecord()){ //逐行读入除表头的数据      
            csvList.add(reader.getValues()); 
        }              
        reader.close();  
           
        return csvList;
	}
	
	/**
	 * 供应商对账汇总表
	 * @param name 文件名称
	 * @param headers 表头
	 * @param data 数据
	 * @return 文件路径
	 */
	public static String exportSupplierOrderSummary(String name, Map<String, String> headers, List<Record> list, String startTime, String endTime, Record statistics, Integer supplierId) {
		HSSFWorkbook wb = new HSSFWorkbook(); // 创建了一个excel文件
		HSSFSheet sheet = wb.createSheet("Sheet1"); // 创建了一个工作簿
		
		//获取供应商基本信息
    	Supplier supplier = Supplier.dao.findById(supplierId);
    	String supplierName = (supplier != null) ? supplier.getName() : "";
    	String contactPerson = (supplier != null) ? supplier.getContactPerson() : "";
    	String phone = (supplier != null) ? supplier.getPhone1() : "";
		
		//第一行
		setHeaderTitle(wb, sheet, "供应商对账单", 10);
		
		//第二行
		HSSFRow rw1 = sheet.createRow(1);  
		rw1.createCell(0).setCellValue("供货单位：" + supplierName);
		rw1.createCell(5).setCellValue("入驻商户：");
		sheet.addMergedRegion(new CellRangeAddress(1,1,0,4));
		sheet.addMergedRegion(new CellRangeAddress(1,1,5,10));
		
		//第三行
		HSSFRow rw2 = sheet.createRow(2);  
		rw2.createCell(0).setCellValue("联　系人：" + contactPerson);
		rw2.createCell(5).setCellValue("联　系人：");
		sheet.addMergedRegion(new CellRangeAddress(2,2,0,4));
		sheet.addMergedRegion(new CellRangeAddress(2,2,5,10));
		
		//第四行
		HSSFRow rw3 = sheet.createRow(3);  
		rw3.createCell(0).setCellValue("电　　话：" + phone);
		rw3.createCell(5).setCellValue("电　　话：");
		sheet.addMergedRegion(new CellRangeAddress(3,3,0,4));
		sheet.addMergedRegion(new CellRangeAddress(3,3,5,10));
		
		//第五行
		HSSFRow rw4 = sheet.createRow(4);  
		rw4.createCell(0).setCellValue("结款方式：月结30天");
		rw4.createCell(8).setCellValue("时间：");
		rw4.createCell(9).setCellValue(startTime);
		rw4.createCell(10).setCellValue(endTime);
		sheet.addMergedRegion(new CellRangeAddress(4,4,0,1));

		int rowIndex = 5;     //行索引
		HSSFRow row1 = sheet.createRow(rowIndex);
		int k = 0;
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			HSSFCell cell1 = row1.createCell((short) k);
			cell1.setCellValue(entry.getValue());

			k++;
		}

		rowIndex++;

		int num = 1;
		
		// 创建内容
		for (Record item : list) {
			k = 0;
			HSSFRow row = sheet.createRow(rowIndex);
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				String key = entry.getKey();
				HSSFCell cell = row.createCell((short) k);
				
				if (key.equals("num")) {
					cell.setCellValue(num);
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
		
		HSSFRow lastRow = sheet.createRow(++rowIndex);  
		lastRow.createCell(0).setCellValue("产品总金额：" + statistics.getDouble("totalPrice") +
				"，产品总成本：" + statistics.getDouble("totalProductCost") +
				"，发货总数量：" + statistics.getInt("totalSendAmount") +
				"，退款总数量：" + statistics.getInt("totalBackAmount") +
				"，退款总金额：" + statistics.getDouble("totalRefund") +
				"，销售总金额：" + statistics.getDouble("totalSalable") +
				"，其中（微信app支付：" + statistics.getDouble("totalWxApp") +
				"，微信公众号：" + statistics.getDouble("totalWxPc") +
				"，支付宝支付：" + statistics.getDouble("totalAlipay") +
				"，银联支付：" + statistics.getDouble("totalUnionPay") +
				"，余额支付：" + statistics.getDouble("totalWallet") + "）"
				);
		
		sheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex,0,24));
		return getPath(name, wb);
	}
	
	/**
	 * 供应商对账明细
	 * @param name 文件名称
	 * @param headers 表头
	 * @param data 数据
	 * @return 文件路径
	 */
	public static String exportSupplierOrderList(String name, Map<String, String> headers, List<Record> list, Record statistics, String startTime, String endTime, int supplierId) {
		HSSFWorkbook wb = new HSSFWorkbook(); // 创建了一个excel文件
		HSSFSheet sheet = wb.createSheet("Sheet1"); // 创建了一个工作簿
		
		//获取供应商基本信息
    	Supplier supplier = Supplier.dao.findById(supplierId);
    	String supplierName = (supplier != null) ? supplier.getName() : "";
    	String contactPerson = (supplier != null) ? supplier.getContactPerson() : "";
    	String phone = (supplier != null) ? supplier.getPhone1() : "";

		//第一行
		setHeaderTitle(wb, sheet, "供应商对账单", 14);
		int rowIndex = 0;   //行索引
		
		if (supplierId > 0) {
			//第二行
			HSSFRow rw1 = sheet.createRow(1);  
			rw1.createCell(0).setCellValue("供货单位：" + supplierName);
			rw1.createCell(5).setCellValue("入驻商户：");
			sheet.addMergedRegion(new CellRangeAddress(1,1,0,4));
			sheet.addMergedRegion(new CellRangeAddress(1,1,5,14));
			
			//第三行
			HSSFRow rw2 = sheet.createRow(2);  
			rw2.createCell(0).setCellValue("联　系人：" + contactPerson);
			rw2.createCell(5).setCellValue("联　系人：");
			sheet.addMergedRegion(new CellRangeAddress(2,2,0,4));
			sheet.addMergedRegion(new CellRangeAddress(2,2,5,14));
			
			//第四行
			HSSFRow rw3 = sheet.createRow(3);  
			rw3.createCell(0).setCellValue("电　　话：" + phone);
			rw3.createCell(5).setCellValue("电　　话：");
			sheet.addMergedRegion(new CellRangeAddress(3,3,0,4));
			sheet.addMergedRegion(new CellRangeAddress(3,3,5,14));
			
			rowIndex = 4;
		} else {
			rowIndex = 1;
		}
		
		//第五行
		HSSFRow rw4 = sheet.createRow(rowIndex++);  
		rw4.createCell(0).setCellValue("结款方式：月结30天");
		sheet.addMergedRegion(new CellRangeAddress(4,4,0,1));
		
		HSSFRow row1 = sheet.createRow(rowIndex);
		int k = 0;
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			HSSFCell cell1 = row1.createCell((short) k);
			cell1.setCellValue(entry.getValue());

			k++;
		}

		rowIndex++;
		int num = 1;
		
		// 创建内容
		for (Record item : list) {
			k = 0;
			HSSFRow row = sheet.createRow(rowIndex);
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				String key = entry.getKey();
				HSSFCell cell = row.createCell((short) k);
				
				if (key.equals("num")) {
					cell.setCellValue(num);
				} else if (key.equals("selectProterties")) {
					String r = item.getStr("product_name");
					if (item.getStr("selectProterties") != null && !item.getStr("selectProterties").equals("")) {
						r += "(" + item.getStr("selectProterties") + ")";
					}
					cell.setCellValue(r);
				} else if (key.equals("payType")) {
					String p = convertPayType(item.getInt("payType"), item.getInt("source"));
					cell.setCellValue(p);
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
		
		HSSFRow rw5 = sheet.createRow(rowIndex);  
		rw5.createCell(0).setCellValue("        截止" + endTime + "，累计发货  " + statistics.getInt("totalUnitOrdered") + " 单," + " 销售总成本" + statistics.getDouble("totalSaleCost") + " 元，销售总额 " + statistics.getDouble("totalSale") + " 元，退款总额" + statistics.getDouble("totalRefundCash") + " 元；" +
		"其中微信支付" + statistics.getDouble("weixin") + "元(微信App：" + statistics.getDouble("weixinApp") + "，微信公众号：" + statistics.getDouble("weixinPc") + "元)，支付宝支付：" + statistics.getDouble("alipay") + "元，银联支付：" + statistics.getDouble("unionpay") + "元，余额支付：" + statistics.getDouble("balancepay") + "元");
		sheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex,0,24));
		
		rowIndex++;
		HSSFRow rw6 = sheet.createRow(rowIndex);  
		rw6.createCell(0).setCellValue("截止" + endTime + "累计应收（付）货款：           ");
		rw6.createCell(5).setCellValue("截止" + endTime + "应开增值税普通（专用）发票累计：               ");
		//sheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex,0,4));
		sheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex,5,14));
		
		rowIndex++;
		HSSFRow rw7 = sheet.createRow(rowIndex);  
		rw7.createCell(0).setCellValue("上月货款金额：                  ");
		rw7.createCell(5).setCellValue("截止" + endTime + "应（收）付快递成本累计：" + statistics.getDouble("totalDeliveryCost"));
		sheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex,0,4));
		sheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex,5,14));
		
		rowIndex++;
		HSSFRow rw8 = sheet.createRow(rowIndex);  
		rw8.createCell(0).setCellValue("上月开票金额：                    ");
		rw8.createCell(5).setCellValue("上月快递成本总金额：              ");
		sheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex,0,4));
		sheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex,5,14));
		
		rowIndex++;
		HSSFRow rw9 = sheet.createRow(rowIndex);  
		rw9.createCell(1).setCellValue("甲方确认：");
		rw9.createCell(7).setCellValue("乙方确认:");
		sheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex+1,1,2));
		sheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex+1,7,11));
		
		rowIndex = rowIndex + 2;
		HSSFRow rw10 = sheet.createRow(rowIndex);  
		rw10.createCell(1).setCellValue("日       期：");
		rw10.createCell(7).setCellValue("日       期：");
		sheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex,1,2));
		sheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex,7,11));
		
		return getPath(name, wb);
	}
	
	/**
	 * 销售汇总（财务）
	 * @param name 文件名称
	 * @param headers 表头
	 * @param data 数据
	 * @return 文件路径
	 */
	public static String exportOrderPayList(String name, Map<String, String> headers, List<Record> data) {
		HSSFWorkbook wb = new HSSFWorkbook(); // 创建了一个excel文件
		HSSFSheet sheet = wb.createSheet("Sheet1"); // 创建了一个工作簿

		setHeaderTitle(wb, sheet, "销售汇总(财务)", 8);
		
		int rowIndex = 1;     //行索引
		HSSFRow row1 = sheet.createRow(rowIndex);
		int k = 0;
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			HSSFCell cell1 = row1.createCell((short) k);
			cell1.setCellValue(entry.getValue());

			k++;
		}

		rowIndex++;

		int num = 1;
		
		// 创建内容
		for (Record item : data) {
			k = 0;
			HSSFRow row = sheet.createRow(rowIndex);
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				String key = entry.getKey();
				HSSFCell cell = row.createCell((short) k);
				
				if (key.equals("num")) {
					cell.setCellValue(num);
				} else if (key.equals("payType")) {
					String p = convertPayType(item.getNumber("payType").intValue());
					cell.setCellValue(p);
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
		
		return getPath(name, wb);
	}
	
	/**
	 * 导出发票
	 * @param name 文件名称
	 * @param headers 表头
	 * @param data 数据
	 * @return 文件路径
	 */
	public static String exportAuditRecord(String name, Map<String, String> headers, List<Record> data, String startTime, String endTime, Integer payType, Record statistics) {
		HSSFWorkbook wb = new HSSFWorkbook(); // 创建了一个excel文件
		HSSFSheet sheet = wb.createSheet("Sheet1"); // 创建了一个工作簿

		//1: 微信支付, 2: 支付宝, 3银联支付
		String headerTitle = "";
		switch (payType) {
		case 1:	headerTitle = "微信支付对账";		break;
		case 2:	headerTitle = "支付宝支付对账";		break;
		case 3:	headerTitle = "银联支付对账";		break;
		case 4:	headerTitle = "APP微信支付对账";	break;
		default:	headerTitle = "";			break;
		}
		setHeaderTitle(wb, sheet, headerTitle, 9);
		
		int rowIndex = 1;     //行索引
		HSSFRow row0 = sheet.createRow(rowIndex);
		HSSFCell cell0 = row0.createCell(0);
		cell0.setCellValue("开始时间:");
		cell0 = row0.createCell(1);
		cell0.setCellValue(startTime);
		cell0 = row0.createCell(2);
		cell0.setCellValue("结束时间:");
		cell0 = row0.createCell(3);
		cell0.setCellValue(endTime);
		cell0 = row0.createCell(4);
		cell0.setCellValue("商户交易总金额：");
		cell0 = row0.createCell(5);
		cell0.setCellValue(statistics.getDouble("totalPayable"));
		cell0 = row0.createCell(6);
		cell0.setCellValue("平台交易总金额：");
		cell0 = row0.createCell(7);
		cell0.setCellValue(statistics.getDouble("totalThirdPayable"));
		cell0 = row0.createCell(8);
		cell0.setCellValue("对比结果总金额：");
		cell0 = row0.createCell(9);
		cell0.setCellValue(statistics.getDouble("totalDiffer"));
		rowIndex++;
		
		HSSFRow row1 = sheet.createRow(rowIndex);
		int k = 0;
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			HSSFCell cell1 = row1.createCell((short) k);
			cell1.setCellValue(entry.getValue());

			k++;
		}

		rowIndex++;

		int num = 1;
		int totalAmount = 0;
		
		// 创建内容
		for (Record item : data) {
			k = 0;
			HSSFRow row = sheet.createRow(rowIndex);
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				String key = entry.getKey();
				HSSFCell cell = row.createCell((short) k);
				
				if (key.equals("num")) {
					cell.setCellValue(num);
				} else if (key.equals("status")) {
					String s = "";
					int status = item.getInt("status");
					if (status == 1) {
						s = "正常";
					} else if (status == 2) {
						s = "平台缺失";
					} else if (status == 3) {
						s = "系统缺失";
					}
					cell.setCellValue(s);
				} else if (key.equals("payType")) {
					String p = convertPayType(item.getInt("payType"));
					cell.setCellValue(p);
				} else if (key.equals("tradeType")) {
					String s = "";
					int tradeType = item.getInt("tradeType");
					if (tradeType == 1) {
						s = "消费";
					} else if (tradeType == 2) {
						s = "退款";
					}
					cell.setCellValue(s);
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
		
		return getPath(name, wb);
	}
	
	/**
	 * 导出发票
	 * @param name 文件名称
	 * @param headers 表头
	 * @param data 数据
	 * @return 文件路径
	 */
	public static String exportInvoice(String name, Map<String, String> headers, List<Record> data) {
		HSSFWorkbook wb = new HSSFWorkbook(); // 创建了一个excel文件
		HSSFSheet sheet = wb.createSheet("Sheet1"); // 创建了一个工作簿

		setHeaderTitle(wb, sheet, "发票记录", 7);
		
		int rowIndex = 1;     //行索引
		// 创建表头
		HSSFRow row1 = sheet.createRow(rowIndex);
		int k = 0;
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			HSSFCell cell1 = row1.createCell((short) k);
			cell1.setCellValue(entry.getValue());

			k++;
		}

		rowIndex++;

		int num = 1;
		int totalAmount = 0;
		
		// 创建内容
		for (Record item : data) {
			k = 0;
			HSSFRow row = sheet.createRow(rowIndex);
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				String key = entry.getKey();
				HSSFCell cell = row.createCell((short) k);
				
				if (key.equals("num")) {
					cell.setCellValue(num);
				}
				else if (key.equals("amount")) {
					totalAmount++;
					cell.setCellValue(1);
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
		
		if (data.size() > 0) {
			HSSFRow row2 = sheet.createRow(rowIndex);
			HSSFCell cell2 = row2.createCell(0);
			cell2.setCellValue("");
			cell2 = row2.createCell(1);
			cell2.setCellValue("");
			cell2 = row2.createCell(2);
			cell2.setCellValue("");
			cell2 = row2.createCell(3);
			cell2.setCellValue("");
			cell2 = row2.createCell(4);
			cell2.setCellValue("");
			cell2 = row2.createCell(5);
			cell2.setCellValue("");
			cell2 = row2.createCell(6);
			cell2.setCellValue("");
			cell2 = row2.createCell(7);
			cell2.setCellValue(totalAmount);
		}
		
		return getPath(name, wb);
	}
	
	/**
	 * 根据税率标识导出订单
	 * @param name 文件名称
	 * @param headers 表头
	 * @param data 数据
	 * @return 文件路径
	 */
	public static String exportByOrderTax(String name, Map<String, String> headers, List<Record> data, Record statistics) {
		HSSFWorkbook wb = new HSSFWorkbook(); // 创建了一个excel文件
		HSSFSheet sheet = wb.createSheet("Sheet1"); // 创建了一个工作簿

		setHeaderTitle(wb, sheet, "产品品类汇总", 7);
		int rowIndex = 1; 
		HSSFRow row01 = sheet.createRow(rowIndex);
		row01.createCell(0).setCellValue("销售总额");
		row01.createCell(1).setCellValue(statistics.getDouble("totalPayable"));
		row01.createCell(2).setCellValue("退款总额");
		row01.createCell(3).setCellValue(statistics.getDouble("tatalRefundCash"));
		row01.createCell(4).setCellValue("税额");
		row01.createCell(5).setCellValue(statistics.getDouble("totalTaxCost"));
		row01.createCell(6).setCellValue("划账金额");
		row01.createCell(7).setCellValue(statistics.getDouble("totalBalance"));
		rowIndex++;
		
		// 创建表头
		HSSFRow row1 = sheet.createRow(rowIndex);
		int k = 0;
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			HSSFCell cell1 = row1.createCell((short) k);
			cell1.setCellValue(entry.getValue());

			k++;
		}

		rowIndex++;

		int num = 1;
		
		// 创建内容
		for (Record item : data) {
			k = 0;
			HSSFRow row = sheet.createRow(rowIndex);
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				String key = entry.getKey();
				HSSFCell cell = row.createCell((short) k);
				
				if (key.equals("num")) {
					cell.setCellValue(num);
				}
				else if (key.equals("source")) {
					String source = getOrderSource(item.getInt("source"));
					cell.setCellValue(source);
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

		return getPath(name, wb);
	}
	
	/**
	 * 导出汇款方式汇总表
	 * @param name 文件名称
	 * @param headers 表头
	 * @param data 数据
	 * @return 文件路径
	 */
	public static String exportByOrderSource(String name, Map<String, String> headers, List<Record> data, Record statistics) {
		HSSFWorkbook wb = new HSSFWorkbook(); // 创建了一个excel文件
		HSSFSheet sheet = wb.createSheet("Sheet1"); // 创建了一个工作簿

		setHeaderTitle(wb, sheet, "平台类型汇总", 9);
		
		int rowIndex = 1;
		HSSFRow row0 = sheet.createRow(rowIndex);
		
		HSSFCell cell00 = row0.createCell(0);
		cell00.setCellValue("总销售额：");
		cell00.setCellStyle(ExcelUtil2.getStyle5(wb));
		
		HSSFCell cell01 = row0.createCell(1);
		cell01.setCellValue(statistics.getDouble("totalPayable"));
		cell01.setCellStyle(ExcelUtil2.getStyle5(wb));
		
		HSSFCell cell02 = row0.createCell(2);
		cell02.setCellValue("总退款金额：");
		cell02.setCellStyle(ExcelUtil2.getStyle5(wb));
		
		HSSFCell cell03 = row0.createCell(3);
		cell03.setCellValue(statistics.getDouble("totalRefundCash"));
		cell03.setCellStyle(ExcelUtil2.getStyle5(wb));
		
		HSSFCell cell04 = row0.createCell(4);
		cell04.setCellValue("总手续费：");
		cell04.setCellStyle(ExcelUtil2.getStyle5(wb));
		
		HSSFCell cell05 = row0.createCell(5);
		cell05.setCellValue(statistics.getDouble("totalPayRateSum"));
		cell05.setCellStyle(ExcelUtil2.getStyle5(wb));
		
		HSSFCell cell06 = row0.createCell(6);
		cell06.setCellValue("总税额：");
		cell06.setCellStyle(ExcelUtil2.getStyle5(wb));
		
		HSSFCell cell07 = row0.createCell(7);
		cell07.setCellValue(statistics.getDouble("totalTaxCost"));
		cell07.setCellStyle(ExcelUtil2.getStyle5(wb));
		
		HSSFCell cell08 = row0.createCell(8);
		cell08.setCellValue("总划账金额：");
		cell08.setCellStyle(ExcelUtil2.getStyle5(wb));
		
		HSSFCell cell09 = row0.createCell(9);
		cell09.setCellValue(statistics.getDouble("totalBalance"));
		cell09.setCellStyle(ExcelUtil2.getStyle5(wb));
		
		rowIndex++;
		
		// 创建表头
		HSSFRow row1 = sheet.createRow(rowIndex);
		int k = 0;
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			HSSFCell cell1 = row1.createCell((short) k);
			cell1.setCellValue(entry.getValue());
			cell1.setCellStyle(ExcelUtil2.getStyle4(wb));
			k++;
		}

		rowIndex++;
		int num = 1;
		
		// 创建内容
		for (Record item : data) {
			k = 0;
			HSSFRow row = sheet.createRow(rowIndex);
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				String key = entry.getKey();
				HSSFCell cell = row.createCell((short) k);
				cell.setCellStyle(ExcelUtil2.getStyle4(wb));
				
				if (key.equals("num")) {
					cell.setCellValue(num);
				}
				else if (key.equals("source")) {
					String source = getOrderSource(Integer.parseInt(item.get("source").toString()));
					cell.setCellValue(source);
				}
				else if (key.equals("payType")) {
					String payType = getOrderPayType(Integer.parseInt(item.get("payType").toString()));
					cell.setCellValue(payType);
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

		return getPath(name, wb);
	}
	
	/**
	 * 导出销售明细(管理)
	 * @param name 文件名称
	 * @param headers 表头
	 * @param data 数据
	 * @return 文件路径
	 */
	public static String exportActivityCard(String name, List<Record> list) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		HSSFRow row = null;
		HSSFCell cell = null;
		
		int rowIndex = 0; 
		
		Map<String, String> headers = new LinkedHashMap<String, String>();
		headers.put("num", "序号");
		headers.put("code", "卡号");
		headers.put("password", "密码");
		headers.put("discount", "金额");
		headers.put("is_used", "是否已使用");
		headers.put("member_name", "使用人");
		headers.put("use_time", "使用时间");
		headers.put("created_at", "创建时间");
		
		int k = 0;
		row = sheet.createRow(rowIndex);
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			cell = row.createCell((short) k);
			cell.setCellValue(entry.getValue());
			k++;
		}
		row.setHeightInPoints((float)12.75);
		rowIndex++;
		
		int num = 1;
		for (Record item : list) {
			k = 0;
			row = sheet.createRow(rowIndex);
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				String key = entry.getKey();
				cell = row.createCell((short) k);
				
				if (key.equals("num")) {
					cell.setCellValue(num);
				}else {
					String result = item.get(key) != null ? String.valueOf(item.get(key)) : "";
					cell.setCellValue(result);
				}
				
				k++;
			}

			rowIndex++;
			num++;
		}
		
		return getPath(name, workbook);
	}
	
	public static String exportByOrderList(String name, List<Record> data, Record statistics, Map<String, String> searchMap) {
		int s = data.size();
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 11));//合并单元格
		sheet.addMergedRegion(new CellRangeAddress(s+4, s+4, 0, 2));//合并单元格
		CellRangeAddress cellRangeAddress1 = new CellRangeAddress(1, 1, 6, 7);
		sheet.addMergedRegion(cellRangeAddress1);
		//设置表格列的宽度
		ExcelUtil2.setColumnWidth(sheet, new int[] {20*150, 20*180, 20*180, 20*180, 20*150, 20*180, 20*150, 
				20*250, 20*170, 20*170, 20*170, 20*170});
		HSSFRow row = null;
		HSSFCell cell = null;
		CellStyle style4 = ExcelUtil2.getStyle4(workbook);
		//设置头部样式，表单边框
		for (int i = 2; i <= s+3; i++) {  
            row = sheet.createRow(i);  
            row.setHeightInPoints(33);
            for (int j = 0; j <= 11; j++) {  
                cell = row.createCell(j); 
                cell.setCellStyle(style4);  
            }  
        }  
		row = sheet.createRow(0);
		row.setHeightInPoints(40);
		cell = row.createCell(0);
		cell.setCellStyle(ExcelUtil2.getStyle(workbook));//设置单元格样式
		cell.setCellValue("销售明细表查询");
		//设置标题行内容
		HSSFRow row1 = sheet.createRow(1);
		HSSFCell row1Cel1 = row1.createCell(0);
		HSSFCell row1Cel11 = row1.createCell(1);
		HSSFCell row1Cel2 = row1.createCell(2);
		HSSFCell row1Cel21 = row1.createCell(3);
		HSSFCell row1Cel3 = row1.createCell(5);
		HSSFCell row1Cel4 = row1.createCell(6);
		HSSFCell row1Cel5 = row1.createCell(8);
		HSSFCell row1Cel51 = row1.createCell(9);
		HSSFCell row1Cel6 = row1.createCell(10);
		row1Cel1.setCellValue("供应商：");
		row1Cel11.setCellValue(searchMap.get("strSupplierName"));
		row1Cel2.setCellValue("时间段：");
		row1Cel21.setCellValue(searchMap.get("strTime"));
		row1Cel3.setCellValue("订单状态：");
		row1Cel4.setCellValue(searchMap.get("strStatus"));
		row1Cel5.setCellValue("支付方式");
		row1Cel51.setCellValue(searchMap.get("strPayType"));
		row1Cel6.setCellValue("单位：元");
		
		row1Cel1.setCellStyle(ExcelUtil2.getStyle3(workbook));
		row1Cel11.setCellStyle(ExcelUtil2.getStyle2(workbook));
		row1Cel2.setCellStyle(ExcelUtil2.getStyle3(workbook));
		row1Cel21.setCellStyle(ExcelUtil2.getStyle2(workbook));
		row1Cel3.setCellStyle(ExcelUtil2.getStyle3(workbook));
		row1Cel4.setCellStyle(ExcelUtil2.getStyle2(workbook));
		row1Cel5.setCellStyle(ExcelUtil2.getStyle3(workbook));
		row1Cel51.setCellStyle(ExcelUtil2.getStyle2(workbook));
		row1Cel6.setCellStyle(ExcelUtil2.getStyle3(workbook));
		
		//设置标题行内容
		HSSFRow row2 = sheet.getRow(2);
		row2.setHeightInPoints(35);
		HSSFCell row2Cell0 = row2.getCell(0);
		HSSFCell row2Cell1 = row2.getCell(1);
		HSSFCell row2Cell2 = row2.getCell(2);
		HSSFCell row2Cell3 = row2.getCell(3);
		HSSFCell row2Cell4 = row2.getCell(4);
		HSSFCell row2Cell5 = row2.getCell(5);
		HSSFCell row2Cell6 = row2.getCell(6);
		HSSFCell row2Cell7 = row2.getCell(7);
		HSSFCell row2Cell8 = row2.getCell(8);
		HSSFCell row2Cell9 = row2.getCell(9);
		HSSFCell row2Cell10 = row2.getCell(10);
		HSSFCell row2Cell11= row2.getCell(11);
		row2Cell0.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell1.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell2.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell3.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell4.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell5.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell6.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell7.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell8.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell9.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell10.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell11.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell0.setCellValue("序号");
		row2Cell1.setCellValue("交易号");
		row2Cell2.setCellValue("商户订单号");
		row2Cell3.setCellValue("供应商");
		row2Cell4.setCellValue("产品名称");
		row2Cell5.setCellValue("税率");
		row2Cell6.setCellValue("零售单价（含税）");
		row2Cell7.setCellValue("销售数量（产品）");
		row2Cell8.setCellValue("销售金额");
		row2Cell9.setCellValue("优惠金额");
		row2Cell10.setCellValue("退货金额");
		row2Cell11.setCellValue("收款方式");
		
		HSSFRow row8 = sheet.getRow(s+3);
		row8.setHeightInPoints(35);
		HSSFCell row8Cell0 = row8.getCell(0);
		row8Cell0.setCellValue("合计");
		row8Cell0.setCellStyle(ExcelUtil2.getStyle1(workbook));
		//总销售金额
		row8.getCell(8).setCellValue(statistics.getDouble("totalPayable"));
		//总退款金额
		row8.getCell(10).setCellValue(statistics.getDouble("totalRefundCash"));
		
		HSSFRow row10 = sheet.createRow(s+5);
		HSSFCell row10Cell0 = row10.createCell(0);
		HSSFCell row10Cell1 = row10.createCell(8);
		row10Cell0.setCellValue("日期：");
		row10Cell1.setCellValue("打印人：");
		row10Cell0.setCellStyle(ExcelUtil2.getStyle2(workbook));
		row10Cell1.setCellStyle(ExcelUtil2.getStyle2(workbook));
		
		HSSFRow row12 = sheet.createRow(s+4);
		row12.setHeightInPoints(38);
		HSSFCell row12Cell0 = row12.createCell(0);
		row12Cell0.setCellValue("退货用负数显示");
		row12Cell0.setCellStyle(ExcelUtil2.getStyle2(workbook));
		if(data != null && data.size() > 0) {
			Record record = null;
			for(int i=0; i<s; i ++) {
				record = data.get(i);
				row = sheet.getRow(i+3);
				cell = row.getCell(0);
				//序号
				cell.setCellValue(i+1);
				cell = row.getCell(1);
				//交易号
				cell.setCellValue(record.getStr("tradeNo"));
				cell = row.getCell(2);
				//商户订单号
				cell.setCellValue(record.getStr("tradeCode"));
				cell = row.getCell(3);
				//供应商名称
				cell.setCellValue(record.getStr("supplierName"));
				cell = row.getCell(4);
				//产品名称
				cell.setCellValue(record.getStr("product_name"));
				cell = row.getCell(5);
				//税率
				cell.setCellValue(record.getBigDecimal("taxRate").toString());
				cell = row.getCell(6);
				//零售单价
				cell.setCellValue(record.getBigDecimal("actualUnitPrice").toString());
				cell = row.getCell(7);
				//销售数量
				cell.setCellValue(record.getLong("unitOrdered"));
				cell = row.getCell(8);
				//totalPayable销售金额
				cell.setCellValue(record.getBigDecimal("totalPayable").toString());
				cell = row.getCell(9);
				//优惠金额
				cell.setCellValue("0");
				cell = row.getCell(10);
				//退款金额
				cell.setCellValue(record.getBigDecimal("refundCash").toString());
				cell = row.getCell(11);
				//收款方式
				cell.setCellValue(getOrderPayType(record.getNumber("payType").intValue()));
			}
		}

		return getPath(name, workbook);
	}
	
	/**
	 * 导出收支列表
	 * @param name
	 * @param data
	 * @param statistics
	 * @param searchMap
	 * @return
	 */
	public static String exportShouZhi(String name, List<Record> data, Record statistics, Map<String, String> searchMap) {
		int s = data.size();
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));//合并单元格
		//设置表格列的宽度
		ExcelUtil2.setColumnWidth(sheet, new int[] {20*150, 20*350, 20*400, 20*180, 20*150, 20*180, 20*150, 
				20*250, 20*170});
		HSSFRow row = null;
		HSSFCell cell = null;
		CellStyle style4 = ExcelUtil2.getStyle4(workbook);
		//设置头部样式，表单边框
		for (int i = 2; i <= s+3; i++) {  
            row = sheet.createRow(i);  
            row.setHeightInPoints(33);
            for (int j = 0; j <= 8; j++) {  
                cell = row.createCell(j); 
                cell.setCellStyle(style4);  
            }  
        }  
		row = sheet.createRow(0);
		row.setHeightInPoints(40);
		cell = row.createCell(0);
		cell.setCellStyle(ExcelUtil2.getStyle(workbook));//设置单元格样式
		cell.setCellValue("收支管理");
		//设置标题行内容
		HSSFRow row1 = sheet.createRow(1);
		HSSFCell row1Cel1 = row1.createCell(0);
		HSSFCell row1Cel11 = row1.createCell(1);
		HSSFCell row1Cel2 = row1.createCell(2);
		HSSFCell row1Cel21 = row1.createCell(3);
		HSSFCell row1Cel3 = row1.createCell(7);
		row1Cel1.setCellValue("供应商：");
		row1Cel11.setCellValue(searchMap.get("strSupplierName"));
		row1Cel2.setCellValue("时间段：");
		row1Cel21.setCellValue(searchMap.get("startTime")+"至"+searchMap.get("endTime"));
		row1Cel3.setCellValue("单位：元");
		
		row1Cel1.setCellStyle(ExcelUtil2.getStyle3(workbook));
		row1Cel11.setCellStyle(ExcelUtil2.getStyle2(workbook));
		row1Cel2.setCellStyle(ExcelUtil2.getStyle3(workbook));
		row1Cel21.setCellStyle(ExcelUtil2.getStyle2(workbook));
		row1Cel3.setCellStyle(ExcelUtil2.getStyle3(workbook));
		
		//设置标题行内容
		HSSFRow row2 = sheet.getRow(2);
		row2.setHeightInPoints(35);
		HSSFCell row2Cell0 = row2.getCell(0);
		HSSFCell row2Cell1 = row2.getCell(1);
		HSSFCell row2Cell2 = row2.getCell(2);
		HSSFCell row2Cell3 = row2.getCell(3);
		HSSFCell row2Cell4 = row2.getCell(4);
		HSSFCell row2Cell5 = row2.getCell(5);
		HSSFCell row2Cell6 = row2.getCell(6);
		HSSFCell row2Cell7 = row2.getCell(7);
		HSSFCell row2Cell8 = row2.getCell(8);
		row2Cell0.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell1.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell2.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell3.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell4.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell5.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell6.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell7.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell8.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell0.setCellValue("序号");
		row2Cell1.setCellValue("日期");
		row2Cell2.setCellValue("供应商");
		row2Cell3.setCellValue("未发货");
		row2Cell4.setCellValue("在途");
		row2Cell5.setCellValue("已收货");
		row2Cell6.setCellValue("退款");
		row2Cell7.setCellValue("退货");
		row2Cell8.setCellValue("划账金额");
		
		HSSFRow row8 = sheet.getRow(s+3);
		row8.setHeightInPoints(35);
		HSSFCell row8Cell0 = row8.getCell(0);
		row8Cell0.setCellValue("合计");
		row8Cell0.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row8.getCell(1).setCellValue("");
		row8.getCell(2).setCellValue("");
		row8.getCell(3).setCellValue(statistics.getBigDecimal("totalNoSend").toString());
		row8.getCell(4).setCellValue(statistics.getBigDecimal("totalSending").toString());
		row8.getCell(5).setCellValue(statistics.getBigDecimal("totalConfirmed").toString());
		row8.getCell(6).setCellValue(statistics.getBigDecimal("totalRefund").toString());
		row8.getCell(7).setCellValue(statistics.getBigDecimal("totalBack").toString());
		row8.getCell(8).setCellValue(statistics.getBigDecimal("totalBalance").toString());
		
		HSSFRow row10 = sheet.createRow(s+5);
		HSSFCell row10Cell0 = row10.createCell(0);
		HSSFCell row10Cell1 = row10.createCell(7);
		row10Cell0.setCellValue("日期：");
		row10Cell1.setCellValue("打印人：");
		row10Cell0.setCellStyle(ExcelUtil2.getStyle2(workbook));
		row10Cell1.setCellStyle(ExcelUtil2.getStyle2(workbook));
		
		if(data != null && data.size() > 0) {
			Record record = null;
			for(int i=0; i<s; i ++) {
				record = data.get(i);
				row = sheet.getRow(i+3);
				cell = row.getCell(0);
				//序号
				cell.setCellValue(i+1);
				cell = row.getCell(1);
				//日期
				cell.setCellValue(record.getStr("datetime"));
				cell = row.getCell(2);
				//供应商
				cell.setCellValue(record.getStr("supplier_name"));
				cell = row.getCell(3);
				//未发货
				cell.setCellValue(record.getBigDecimal("nosend").toString());
				cell = row.getCell(4);
				//在途
				cell.setCellValue(record.getBigDecimal("sending").toString());
				cell = row.getCell(5);
				//已收货
				cell.setCellValue(record.getBigDecimal("confirmed").toString());
				cell = row.getCell(6);
				//退款
				cell.setCellValue(record.getBigDecimal("refund").toString());
				cell = row.getCell(7);
				//退货
				cell.setCellValue(record.getBigDecimal("back").toString());
				cell = row.getCell(8);
				//划账金额
				cell.setCellValue(record.getBigDecimal("balance").toString());
			}
		}

		return getPath(name, workbook);
	}
	
	/**
	 * 导出销售明细(财务)
	 * @param name 文件名称
	 * @param headers 表头
	 * @param data 数据
	 * @return 文件路径
	 */
	public static String exportByOrderList2(String name, List<Record> data, Record statistics, 
			Map<String, String> searchMap) {
	
		int s = data.size();
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 11));//合并单元格
		sheet.addMergedRegion(new CellRangeAddress(s+4, s+4, 0, 2));//合并单元格
		CellRangeAddress cellRangeAddress1 = new CellRangeAddress(1, 1, 6, 7);
		sheet.addMergedRegion(cellRangeAddress1);
		//设置表格列的宽度
		ExcelUtil2.setColumnWidth(sheet, new int[] {20*150, 20*130, 20*150, 20*160, 20*150, 20*150, 
				20*150, 20*250, 20*170, 20*170, 20*170, 20*170});
		HSSFRow row = null;
		HSSFCell cell = null;
		//设置头部样式，表单边框
		CellStyle style4 = ExcelUtil2.getStyle4(workbook);
		for (int i = 2; i <= s+3; i++) {  
            row = sheet.createRow(i);  
            row.setHeightInPoints(33);
            for (int j = 0; j <= 11; j++) {  
                cell = row.createCell(j); 
                cell.setCellStyle(style4);  
            }  
        }  
		row = sheet.createRow(0);
		row.setHeightInPoints(40);
		cell = row.createCell(0);
		cell.setCellStyle(ExcelUtil2.getStyle(workbook));//设置单元格样式
		cell.setCellValue("销售明细表查询（财务）");
		//设置标题行内容
		HSSFRow row1 = sheet.createRow(1);
		HSSFCell row1Cel1 = row1.createCell(0);
		HSSFCell row1Cel11 = row1.createCell(1);
		HSSFCell row1Cel2 = row1.createCell(2);
		HSSFCell row1Cel21 = row1.createCell(3);
		HSSFCell row1Cel3 = row1.createCell(5);
		HSSFCell row1Cel4 = row1.createCell(6);
		HSSFCell row1Cel5 = row1.createCell(8);
		HSSFCell row1Cel51 = row1.createCell(9);
		HSSFCell row1Cel6 = row1.createCell(10);
		row1Cel1.setCellValue("供应商：");
		row1Cel11.setCellValue(searchMap.get("strSupplierName"));
		row1Cel2.setCellValue("时间段：");
		row1Cel21.setCellValue(searchMap.get("strTime"));
		row1Cel3.setCellValue("订单状态：");
		row1Cel4.setCellValue(searchMap.get("strStatus"));
		row1Cel5.setCellValue("支付方式");
		row1Cel51.setCellValue(searchMap.get("strPayType"));
		row1Cel6.setCellValue("单位：元");
		row1Cel1.setCellStyle(ExcelUtil2.getStyle3(workbook));
		row1Cel11.setCellStyle(ExcelUtil2.getStyle2(workbook));
		row1Cel2.setCellStyle(ExcelUtil2.getStyle3(workbook));
		row1Cel21.setCellStyle(ExcelUtil2.getStyle2(workbook));
		row1Cel3.setCellStyle(ExcelUtil2.getStyle3(workbook));
		row1Cel4.setCellStyle(ExcelUtil2.getStyle2(workbook));
		row1Cel5.setCellStyle(ExcelUtil2.getStyle3(workbook));
		row1Cel51.setCellStyle(ExcelUtil2.getStyle2(workbook));
		row1Cel6.setCellStyle(ExcelUtil2.getStyle3(workbook));
		//设置标题行内容
		HSSFRow row2 = sheet.getRow(2);
		row2.setHeightInPoints(35);
		HSSFCell row2Cell0 = row2.getCell(0);
		HSSFCell row2Cell1 = row2.getCell(1);
		HSSFCell row2Cell2 = row2.getCell(2);
		HSSFCell row2Cell3 = row2.getCell(3);
		HSSFCell row2Cell4 = row2.getCell(4);
		HSSFCell row2Cell5 = row2.getCell(5);
		HSSFCell row2Cell6 = row2.getCell(6);
		HSSFCell row2Cell7 = row2.getCell(7);
		HSSFCell row2Cell8 = row2.getCell(8);
		HSSFCell row2Cell9 = row2.getCell(9);
		HSSFCell row2Cell10 = row2.getCell(10);
		HSSFCell row2Cell11= row2.getCell(11);
		row2Cell0.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell1.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell2.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell3.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell4.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell5.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell6.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell7.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell8.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell9.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell10.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell11.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell0.setCellValue("序号");
		row2Cell1.setCellValue("交易号");
		row2Cell2.setCellValue("商户订单号");
		row2Cell3.setCellValue("供应商");
		row2Cell4.setCellValue("产品名称");
		row2Cell5.setCellValue("税率");
		row2Cell6.setCellValue("零售单价（含税）");
		row2Cell7.setCellValue("销售数量（产品）");
		row2Cell8.setCellValue("销售金额");
		row2Cell9.setCellValue("优惠金额");
		row2Cell10.setCellValue("退货金额");
		row2Cell11.setCellValue("收款方式");
		
		HSSFRow row8 = sheet.getRow(s+3);
		row8.setHeightInPoints(35);
		HSSFCell row8Cell0 = row8.getCell(0);
		row8Cell0.setCellValue("合计");
		row8Cell0.setCellStyle(ExcelUtil2.getStyle1(workbook));
		//总销售金额
		row8.getCell(8).setCellValue(statistics.getDouble("totalPayable"));
		//总退款金额
		row8.getCell(10).setCellValue(statistics.getDouble("totalRefundCash"));
		
		HSSFRow row10 = sheet.createRow(s+5);
		HSSFCell row10Cell0 = row10.createCell(0);
		HSSFCell row10Cell1 = row10.createCell(8);
		row10Cell0.setCellValue("日期：");
		row10Cell1.setCellValue("打印人：");
		row10Cell0.setCellStyle(ExcelUtil2.getStyle2(workbook));
		row10Cell1.setCellStyle(ExcelUtil2.getStyle2(workbook));
		
		if(data != null && data.size() > 0) {
			Record record = null;
			for(int i=0; i<s; i ++) {
				record = data.get(i);
				row = sheet.getRow(i+3);
				cell = row.getCell(0);
				//序号
				cell.setCellValue(i+1);
				cell = row.getCell(1);
				//交易号
				cell.setCellValue(record.getStr("tradeNo"));
				cell = row.getCell(2);
				//商户订单号
				cell.setCellValue(record.getStr("tradeCode"));
				cell = row.getCell(3);
				//供应商名称
				cell.setCellValue(record.getStr("supplierName"));
				cell = row.getCell(4);
				//产品名称
				cell.setCellValue(record.getStr("product_name"));
				cell = row.getCell(5);
				//税率
				cell.setCellValue(record.getBigDecimal("taxRate").toString());
				cell = row.getCell(6);
				//零售单价（含税） 
				cell.setCellValue(record.getBigDecimal("actualUnitPrice").toString());
				cell = row.getCell(7);
				//销售数量
				cell.setCellValue(record.getLong("unitOrdered"));
				cell = row.getCell(8);
				//销售金额
				cell.setCellValue(record.getBigDecimal("totalPayable").toString());
				cell = row.getCell(9);
				//优惠金额
				cell.setCellValue("0");
				cell = row.getCell(10);
				//退款金额
				cell.setCellValue(record.getBigDecimal("refundCash").toString());
				cell = row.getCell(11);
				//收款方式
				cell.setCellValue(getOrderPayType(record.getNumber("payType").intValue()));
			}
		}

		return getPath(name, workbook);
	}
	
	public static String exportPriceInfo(String name, Object object, Object object2, Object object3) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));//合并单元格
		CellRangeAddress cellRangeAddress1 = new CellRangeAddress(8, 9, 2, 7);
		CellRangeAddress cellRangeAddress2 = new CellRangeAddress(8, 9, 0, 1);
		CellRangeAddress cellRangeAddress3 = new CellRangeAddress(7, 7, 0, 7);
		sheet.addMergedRegion(cellRangeAddress1);//合并单元格
		sheet.addMergedRegion(cellRangeAddress2);//合并单元格
		sheet.addMergedRegion(cellRangeAddress3);//合并单元格
		ExcelUtil2.setColumnWidth(sheet, new int[] {20*256, 20*490, 20*200, 20*140, 20*190, 20*260, 20*240, 20*410});
		HSSFRow row = null;
		HSSFCell cell = null;
		 //设置头部样式，已经表单边框
		 for (int i = 2; i < 7; i++) {  
             row = sheet.createRow(i);  
             row.setHeightInPoints(35);
             for (int j = 0; j <= 7; j++) {  
                 cell = row.createCell(j);  
                 cell.setCellStyle(ExcelUtil2.getStyle1(workbook));  
             }  
         }  
		row = sheet.createRow(0);
		row.setHeightInPoints(25);
		cell = row.createCell(0);
		cell.setCellStyle(ExcelUtil2.getStyle(workbook));//设置单元格样式
		cell.setCellValue(" 乐驿商城产品定价表");
		HSSFRow row1 = sheet.createRow(1);
		row1.setHeightInPoints(30);
		HSSFCell row1Cell = row1.createCell(7);
		row1Cell.setCellValue("（单位：元）");
		row1Cell.setCellStyle(ExcelUtil2.getStyle2(workbook));
		HSSFRow row2 = sheet.createRow(2);
		//设置标题行内容
		row2.setHeightInPoints(35);
		HSSFCell row2Cell0 = row2.createCell(0);
		HSSFCell row2Cell1 = row2.createCell(1);
		HSSFCell row2Cell2 = row2.createCell(2);
		HSSFCell row2Cell3 = row2.createCell(3);
		HSSFCell row2Cell4 = row2.createCell(4);
		HSSFCell row2Cell5 = row2.createCell(5);
		HSSFCell row2Cell6 = row2.createCell(6);
		HSSFCell row2Cell7 = row2.createCell(7);
		row2Cell0.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell1.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell2.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell3.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell4.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell5.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell6.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell7.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell0.setCellValue("供应商名称");
		row2Cell1.setCellValue("产品名称");
		row2Cell2.setCellValue("含税成本");
		row2Cell3.setCellValue("税率");
		row2Cell4.setCellValue("未税成本");
		row2Cell5.setCellValue("销售价");
		row2Cell6.setCellValue("发票类型");
		row2Cell7.setCellValue("上架日期");
		//备注
		HSSFRow row7 = sheet.createRow(7);
		row7.setHeightInPoints(35);
		HSSFCell row7Cell0 = row7.createCell(0);
		row7Cell0.setCellStyle(ExcelUtil2.getStyle2(workbook));
		row7Cell0.setCellValue("备注：快递收费标准：广东省内首重8元，续重1kg1元，省外首重10元，续重1kg3元。");
		//尾部
		HSSFRow row8 = sheet.createRow(8);
		HSSFCell row8Cell0 = row8.createCell(0);
		HSSFCell row8Cell1 = row8.createCell(2);
		row8.setHeightInPoints(35);//设置行高
		row8Cell0.setCellValue("制表人：");
		row8Cell1.setCellValue("定价人：");
		row8Cell0.setCellStyle(ExcelUtil2.getStyle2(workbook));
		row8Cell1.setCellStyle(ExcelUtil2.getStyle2(workbook));
		ExcelUtil2.setBorderStyle(CellStyle.BORDER_THIN, cellRangeAddress3, sheet, workbook);
		return getPath(name, workbook);
	}
	
	/**
	 * 导出退款单
	 * @param name 文件名称
	 * @param headers 表头
	 * @param data 数据
	 * @return 文件路径
	 */
	public static String exportRefund(String name, Map<String, String> headers, List<Record> data, int state, Record statistics) {
		HSSFWorkbook wb = new HSSFWorkbook(); // 创建了一个excel文件
		HSSFSheet sheet = wb.createSheet("Sheet1"); // 创建了一个工作簿
		
		String headerTitle = "退款单";
		switch (state) {
		case 0: headerTitle = "未审核退款单";	        break;
		case  1: headerTitle = "已通过退款单"; 		break;
		case  2: headerTitle = "不通过退款单"; 		break;
		}
		
		setHeaderTitle(wb, sheet, headerTitle, 14);
		
		int rowIndex = 1;
		HSSFRow row01 = sheet.createRow(rowIndex);
		HSSFCell cell01 = row01.createCell(0);
		cell01.setCellValue("交易总额：");
		cell01 = row01.createCell(1);
		cell01.setCellValue(statistics.getDouble("totalTradeCash"));
		cell01 = row01.createCell(2);
		cell01.setCellValue("退款总额：");
		cell01 = row01.createCell(3);
		cell01.setCellValue(statistics.getDouble("totalRefundCash"));
		
		rowIndex++;
		
		//0待审核；1退款中；2审核不通过
		if (state == 1) {
			headers.put("remitTime", "处理时间");
			headers.put("successTime", "完成时间");
		} else if (state == 2) {
			headers.put("remitTime", "处理时间");
		}
		
		// 创建表头
		HSSFRow row1 = sheet.createRow(rowIndex);
		int k = 0;
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			HSSFCell cell1 = row1.createCell((short) k);
			cell1.setCellValue(entry.getValue());

			k++;
		}

		rowIndex++;

		int num = 1;
		
		// 创建内容
		for (Record item : data) {
			k = 0;
			HSSFRow row = sheet.createRow(rowIndex);
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				String key = entry.getKey();
				HSSFCell cell = row.createCell((short) k);
				
				if (key.equals("num")) {
					cell.setCellValue(num);
				}
				else if (key.equals("status")) {
					String status = getBackStatus(item.getInt("status"));
					cell.setCellValue(status);
				}
				else if (key.equals("source")) {
					if (item.get("source") != null) {
						String source = getOrderSource(item.getInt("source"));
						cell.setCellValue(source);
					} else {
						cell.setCellValue("");
					}
				}
				else if (key.equals("payType")) {
					if (item.get("payType") != null) {
						String payType = getOrderPayType(item.getInt("payType"));
						cell.setCellValue(payType);
					} else {
						cell.setCellValue("");
					}
				}
				else if (key.equals("orderTime")) {
					if (item.get("orderTime") != null) {
						String orderTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(item.getDate("orderTime"));
						cell.setCellValue(orderTime);
					} else {
						cell.setCellValue("");
					}
				}
				else if (key.equals("payTime")) {
					String payTime = "";
					
					if (item.get("payTime") != null) {
						payTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(item.getDate("payTime"));
					}
					
					cell.setCellValue(payTime);
				}
				else if (key.equals("created_at")) {
					String created_at = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(item.getDate("created_at"));
					cell.setCellValue(created_at);
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

		return getPath(name, wb);
	}
	
	/**
	 * 导出退货单
	 * @param name 文件名称
	 * @param headers 表头
	 * @param data 数据
	 * @return 文件路径
	 */
	public static String exportBack(String name, Map<String, String> headers, List<Record> data, int state, Record statistics) {
		HSSFWorkbook wb = new HSSFWorkbook(); // 创建了一个excel文件
		HSSFSheet sheet = wb.createSheet("Sheet1"); // 创建了一个工作簿

		String headerTitle = "退货单";
		switch (state) {
		case 0: headerTitle = "未审核退货单";	        break;
		case  1: headerTitle = "已通过退货单"; 		break;
		case  2: headerTitle = "不通过退货单"; 		break;
		}
		
		setHeaderTitle(wb, sheet, headerTitle, 16);
		
		int rowIndex = 1;
		HSSFRow row01 = sheet.createRow(rowIndex);
		HSSFCell cell01 = row01.createCell(0);
		cell01.setCellValue("交易总额：");
		cell01 = row01.createCell(1);
		cell01.setCellValue(statistics.getDouble("totalTradeCash"));
		cell01 = row01.createCell(2);
		cell01.setCellValue("退款总额：");
		cell01 = row01.createCell(3);
		cell01.setCellValue(statistics.getDouble("totalRefundCash"));
		
		rowIndex++;
		
		//0待审核；1通过；2审核不通过
		if (state == 1) {
			headers.put("remitTime", "处理时间");
			headers.put("successTime", "完成时间");
		} else if (state == 2) {
			headers.put("remitTime", "处理时间");
		}
		
		// 创建表头
		HSSFRow row1 = sheet.createRow(rowIndex);
		int k = 0;
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			HSSFCell cell1 = row1.createCell((short) k);
			cell1.setCellValue(entry.getValue());

			k++;
		}

		rowIndex++;

		int num = 1;
		
		// 创建内容
		for (Record item : data) {
			k = 0;
			HSSFRow row = sheet.createRow(rowIndex);
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				String key = entry.getKey();
				HSSFCell cell = row.createCell((short) k);
				
				if (key.equals("num")) {
					cell.setCellValue(num);
				}
				else if (key.equals("status")) {
					String status = getBackStatus(item.getInt("status"));
					cell.setCellValue(status);
				}
				else if (key.equals("source")) {
					String source = getOrderSource(item.getInt("source"));
					cell.setCellValue(source);
				}
				else if (key.equals("payType")) {
					if (item.get("payType") != null) {
						String payType = getOrderPayType(item.getInt("payType"));
						cell.setCellValue(payType);
					} else {
						cell.setCellValue("");
					}
				}
				else if (key.equals("orderTime")) {
					if (item.get("orderTime") != null) {
						String orderTime = DateHelper.formatDateTime(item.getDate("orderTime"));
						cell.setCellValue(orderTime);
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
				}
				else if (key.equals("created_at")) {
					String created_at = DateHelper.formatDateTime(item.getDate("created_at"));
					cell.setCellValue(created_at);
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

		return getPath(name, wb);
	}

	/**
	 * 导出excel
	 * @param name 文件名称
	 * @param headers 表头
	 * @param data 数据
	 * @return 文件路径
	 */
	public static String toExport(String name, Map<String, String> headers, List<Model> data) {
		HSSFWorkbook wb = new HSSFWorkbook(); // 创建了一个excel文件
		HSSFSheet sheet = wb.createSheet("Sheet1"); // 创建了一个工作簿

		int rowIndex = 0;     //行索引
		// 创建表头
		HSSFRow row1 = sheet.createRow(rowIndex);
		int k = 0;
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			HSSFCell cell1 = row1.createCell((short) k);
			cell1.setCellValue(entry.getValue());
			
			k++;
		}

		rowIndex++;

		// 创建内容
		for (Model m : data) {
			k = 0;
			HSSFRow row = sheet.createRow(rowIndex);
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				HSSFCell cell = row.createCell((short) k);
				cell.setCellValue(m.getStr(entry.getKey()));
				k++;
			}

			rowIndex++;
		}

		return getPath(name, wb);
	}
	
	/**
	 * 导出excel
	 * @param name 文件名称
	 * @param headers 表头
	 * @param data 数据
	 * @return 文件路径
	 */
	public static String exportBalance(String name, Map<String, String> headers, List<Customer> data) {
		HSSFWorkbook wb = new HSSFWorkbook(); // 创建了一个excel文件
		HSSFSheet sheet = wb.createSheet("Sheet1"); // 创建了一个工作簿

		int rowIndex = 0;     //行索引
		// 创建表头
		HSSFRow row1 = sheet.createRow(rowIndex);
		int k = 0;
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			HSSFCell cell1 = row1.createCell((short) k);
			cell1.setCellValue(entry.getValue());

			k++;
		}

		rowIndex++;

		int num = 1;
		
		// 创建内容
		for (Customer item : data) {
			HSSFRow row2 = sheet.createRow(rowIndex);
			HSSFCell cell2 = row2.createCell(0);
			cell2.setCellValue(num); //序列
			cell2 = row2.createCell(1);
			cell2.setCellValue(item.getName());
			cell2 = row2.createCell(2);
			cell2.setCellValue(item.getMobilePhone()); 
			cell2 = row2.createCell(3);
			cell2.setCellValue(item.getDouble("balance")); 
			num++;
			rowIndex++;
		}

		return getPath(name, wb);
	}
	
	/**
	 * 导出充值记录
	 * @param name 文件名称
	 * @param headers 表头
	 * @param data 数据
	 * @return 文件路径
	 */
	public static String exportRecharge(String name, Map<String, String> headers, List<Record> data, Record statistics) {
		HSSFWorkbook wb = new HSSFWorkbook(); // 创建了一个excel文件
		HSSFSheet sheet = wb.createSheet("Sheet1"); // 创建了一个工作簿

		setHeaderTitle(wb, sheet, "充值记录", 6);
		
		int rowIndex = 1;
		HSSFRow row01 = sheet.createRow(rowIndex);
		row01.createCell(0).setCellValue("总充值金额：");
		row01.createCell(1).setCellValue(statistics.getDouble("totalMoney"));
		rowIndex++;
		
		HSSFRow row1 = sheet.createRow(rowIndex);
		int k = 0;
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			HSSFCell cell1 = row1.createCell((short) k);
			cell1.setCellValue(entry.getValue());

			k++;
		}

		rowIndex++;

		int num = 1;
		for (Record item : data) {
			k = 0;
			HSSFRow row = sheet.createRow(rowIndex);
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				String key = entry.getKey();
				HSSFCell cell = row.createCell((short) k);
				
				if (key.equals("num")) {
					cell.setCellValue(num);
				} else if (key.equals("event")) {
					String s = convertEvent(item.getInt(key));
					cell.setCellValue(s);
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

		return getPath(name, wb);
	}
	
	public static String exportSaleSummry(String name, List<Record> data, Record total, Map<String, String> searchMap) {
		int s = data.size();
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 14));//合并单元格
		CellRangeAddress cellRangeAddress1 = new CellRangeAddress(1, 1, 7, 8);
		sheet.addMergedRegion(cellRangeAddress1);
		//设置表格列的宽度
		ExcelUtil2.setColumnWidth(sheet, new int[] {20*120, 20*300, 20*130, 20*160, 20*150, 20*160, 20*160, 
				20*170, 20*300, 20*200, 20*300, 20*200, 20*200, 20*200, 20*200});
		HSSFRow row = null;
		HSSFCell cell = null;
		 //设置头部样式，已经表单边框
		 for (int i = 2; i <= s+3; i++) {  
             row = sheet.createRow(i);  
             row.setHeightInPoints(35);
             for (int j = 0; j <= 14; j++) {  
                 cell = row.createCell(j);  
                 cell.setCellStyle(ExcelUtil2.getStyle4(workbook));  
             }  
         }  
		row = sheet.createRow(0);
		row.setHeightInPoints(40);
		cell = row.createCell(0);
		cell.setCellStyle(ExcelUtil2.getStyle(workbook));//设置单元格样式
		cell.setCellValue("销售汇总表查询");
		
		HSSFRow row1 = sheet.createRow(1);
		HSSFCell row1Cel1 = row1.createCell(0);
		HSSFCell row1Cel11 = row1.createCell(1);
		HSSFCell row1Cel2 = row1.createCell(4);
		HSSFCell row1Cel21 = row1.createCell(5);
		HSSFCell row1Cel3 = row1.createCell(6);
		HSSFCell row1Cel4 = row1.createCell(7);
		HSSFCell row1Cel5 = row1.createCell(9);
		row1Cel1.setCellValue("供应商：");
		row1Cel11.setCellValue(searchMap.get("supplierName"));
		row1Cel2.setCellValue("时间段：");
		row1Cel21.setCellValue(searchMap.get("startTime") + "至" + searchMap.get("endTime"));
		row1Cel3.setCellValue("订单状态：");
		row1Cel4.setCellValue(searchMap.get("status"));
		row1Cel5.setCellValue("（单位：元）");
		row1Cel1.setCellStyle(ExcelUtil2.getStyle3(workbook));
		row1Cel11.setCellStyle(ExcelUtil2.getStyle2(workbook));
		row1Cel2.setCellStyle(ExcelUtil2.getStyle3(workbook));
		row1Cel21.setCellStyle(ExcelUtil2.getStyle2(workbook));
		row1Cel3.setCellStyle(ExcelUtil2.getStyle3(workbook));
		row1Cel4.setCellStyle(ExcelUtil2.getStyle2(workbook));
		row1Cel5.setCellStyle(ExcelUtil2.getStyle3(workbook));
		//设置标题行内容
		HSSFRow row2 = sheet.getRow(2);
		row2.setHeightInPoints(35);
		HSSFCell row2Cell0 = row2.getCell(0);
		HSSFCell row2Cell1 = row2.getCell(1);
		HSSFCell row2Cell2 = row2.getCell(2);
		HSSFCell row2Cell3 = row2.getCell(3);
		HSSFCell row2Cell4 = row2.getCell(4);
		HSSFCell row2Cell5 = row2.getCell(5);
		HSSFCell row2Cell6 = row2.getCell(6);
		HSSFCell row2Cell7 = row2.getCell(7);
		HSSFCell row2Cell8 = row2.getCell(8);
		HSSFCell row2Cell9 = row2.getCell(9);
		HSSFCell row2Cell10 = row2.getCell(10);
		HSSFCell row2Cell101 = row2.getCell(11);
		HSSFCell row2Cell102 = row2.getCell(12);
		HSSFCell row2Cell103 = row2.getCell(13);
		HSSFCell row2Cell11= row2.getCell(14);
		row2Cell0.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell1.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell2.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell3.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell4.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell5.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell6.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell7.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell8.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell9.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell10.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell101.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell102.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell103.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell11.setCellStyle(ExcelUtil2.getStyle1(workbook));
		row2Cell0.setCellValue("序号");
		row2Cell1.setCellValue("供应商");
		row2Cell2.setCellValue("税率");
		row2Cell3.setCellValue("销售金额");
		row2Cell4.setCellValue("优惠金额");
		row2Cell5.setCellValue("交易次数");
		row2Cell6.setCellValue("退货金额");
		row2Cell7.setCellValue("退货次数");
		row2Cell8.setCellValue("支付宝支付金额");
		row2Cell9.setCellValue("微信支付金额");
		row2Cell10.setCellValue("银联支付金额");
		row2Cell101.setCellValue("余额支付金额");
		row2Cell102.setCellValue("团购卡支付金额");
		row2Cell103.setCellValue("积分兑换金额");
		row2Cell11.setCellValue("订单状态");
		
		HSSFRow row8 = sheet.getRow(s+3);
		row8.setHeightInPoints(35);
		HSSFCell row8Cell0 = row8.getCell(0);
		row8Cell0.setCellValue("合计");
		row8Cell0.setCellStyle(ExcelUtil2.getStyle1(workbook));
		//合计
		row8.getCell(3).setCellValue(total.getDouble("totalPayable"));
		row8.getCell(4).setCellValue(total.getDouble("totalCouponDiscount"));
		row8.getCell(5).setCellValue(total.getInt("totalUnitOrdered"));
		row8.getCell(6).setCellValue(total.getDouble("totalRefundCash"));
		row8.getCell(7).setCellValue(total.getInt("totalRefundAmount"));
		row8.getCell(8).setCellValue(total.getDouble("totalAlipay"));
		row8.getCell(9).setCellValue(total.getDouble("totalWeixinpay"));
		row8.getCell(10).setCellValue(total.getDouble("totalUnionpay"));
		row8.getCell(11).setCellValue(total.getDouble("totalWalletpay"));
		row8.getCell(12).setCellValue(total.getDouble("totalCardpay"));
		row8.getCell(13).setCellValue(total.getDouble("totalPointpay"));
		HSSFRow row10 = sheet.createRow(s+5);
		HSSFCell row10Cell0 = row10.createCell(0);
		HSSFCell row10Cell1 = row10.createCell(8);
		row10Cell0.setCellValue("日期：");
		row10Cell1.setCellValue("打印人：");
		row10Cell0.setCellStyle(ExcelUtil2.getStyle2(workbook));
		row10Cell1.setCellStyle(ExcelUtil2.getStyle2(workbook));
		
		if(data != null && data.size() > 0) {
			Record record = null;
			for(int i=0; i<s; i ++) {
				record = data.get(i);
				row = sheet.getRow(i+3);
				//序号
				cell = row.getCell(0);
				cell.setCellValue(i+1);
				//供应商
				cell = row.getCell(1);
				cell.setCellValue(record.get("supplier_name").toString());
				//税率
				cell = row.getCell(2);
				cell.setCellValue(record.getBigDecimal("taxRate").toString()+"%");
				//销售金额
				cell = row.getCell(3);
				cell.setCellValue(record.getBigDecimal("payable").toString());
				//优惠金额
				cell = row.getCell(4);
				cell.setCellValue(record.getBigDecimal("couponDiscount").toString());
				//交易次数
				cell = row.getCell(5);
				cell.setCellValue(record.getBigDecimal("unitOrdered").toString());
				//退货金额
				cell = row.getCell(6);
				cell.setCellValue(record.getBigDecimal("refundCash").toString());
				//退货次数
				cell = row.getCell(7);
				cell.setCellValue(record.getBigDecimal("refundAmount").toString());
				//支付宝金额
				cell = row.getCell(8);
				cell.setCellValue(record.getBigDecimal("alipay").toString());
				//微信支付金额
				cell = row.getCell(9);
				cell.setCellValue(record.getBigDecimal("weixinpay").toString());
				//银联支付金额
				cell = row.getCell(10);
				cell.setCellValue(record.getBigDecimal("unionpay").toString());
				//余额支付金额
				cell = row.getCell(11);
				cell.setCellValue(record.getBigDecimal("walletpay").toString());
				//团购卡支付金额
				cell = row.getCell(12);
				cell.setCellValue(record.getBigDecimal("walletpay").toString());
				//积分兑换金额
				cell = row.getCell(13);
				cell.setCellValue(record.getBigDecimal("pointpay").toString());
				//订单状态
				cell = row.getCell(14);
				cell.setCellValue(searchMap.get("status"));
			}
		}
		return getPath(name, workbook);
	}
	
	private static String getStatus(Integer status) {
		//-1为全部,1待付款,2待发货,3已发货,4已收货,5取消订单
		String headerTitle = "全部订单";
		if (status != null) {
			switch (status) {
			case -1: headerTitle = "全部订单";			break;
			case  1: headerTitle = "待付款订单"; 		break;
			case  2: headerTitle = "待发货订单"; 		break;
			case  3: headerTitle = "已发货订单"; 		break;
			case  4: headerTitle = "已收货订单"; 		break;
			case  5: headerTitle = "已取消订单"; 		break;
			}
		}
		
		return headerTitle;
	}
	
	/**
	 * 导出excel
	 * @param name 文件名称
	 * @param headers 表头
	 * @param data 数据
	 * @return 文件路径
	 */
	public static String exportOrder(String name, Map<String, String> headers, List<Record> data, 
			Integer status, Record statistics) {
		
		HSSFWorkbook wb = new HSSFWorkbook(); // 创建了一个excel文件
		HSSFSheet sheet = wb.createSheet("Sheet1"); // 创建了一个工作簿
		HSSFRow row = null;
		HSSFCell cell = null;
		CellRangeAddress region = null;
		
		int rowIndex = 0;
		HSSFCellStyle headerStyle = ExcelUtil2.getStyle8(wb, 14, true, "宋体", true, true, true);
		HSSFCellStyle leftStyle = ExcelUtil2.getStyle8(wb, 10, false, "宋体", true, false, true);
		HSSFCellStyle style = ExcelUtil2.getStyle8(wb, 10, false, "宋体", true, true, true);
		
		// 设置供应商列宽
		sheet.setColumnWidth((short)1, (short)(256*30));
		// 设置订单号列宽
		sheet.setColumnWidth((short)2, (short)(256*20));
		// 设置产品列宽
		sheet.setColumnWidth((short)3, (short)(256*40));
		// 设置联系列宽
		sheet.setColumnWidth((short)7, (short)(256*15));
		// 设置联系列宽
		sheet.setColumnWidth((short)11, (short)(256*20));
		// 设置下单时间列宽
		sheet.setColumnWidth((short)12, (short)(256*20));
		// 设置状态列宽
		sheet.setColumnWidth((short)13, (short)(256*10));
		
		// 表头标题
		String headerTitle = getStatus(status);
		row = sheet.createRow(rowIndex);
		cell = row.createCell(0);
		cell.setCellValue(headerTitle);
		region = ExcelUtil2.setCellRange(sheet, rowIndex, rowIndex, 0, 13);
		ExcelUtil2.setBorderStyle(1, region, sheet, wb);
		cell.setCellStyle(headerStyle);
		
		rowIndex++;
		
		// 供货单位
		row = sheet.createRow(rowIndex);
		cell = row.createCell(0);
		cell.setCellValue("供货单位：" + "");
		region = ExcelUtil2.setCellRange(sheet, rowIndex, rowIndex, 0, 4);
		ExcelUtil2.setBorderStyle(1, region, sheet, wb);
		cell.setCellStyle(leftStyle);
		row.setHeight((short)400);
		
		// 入住商户
		cell = row.createCell(5);
		cell.setCellValue("入住商户：" + "广东通驿高速公路服务区有限公司");
		region = ExcelUtil2.setCellRange(sheet, rowIndex, rowIndex, 5, 13);
		ExcelUtil2.setBorderStyle(1, region, sheet, wb);
		cell.setCellStyle(leftStyle);
		
		rowIndex++;
		
		// 供应商联系人
		row = sheet.createRow(rowIndex);
		cell = row.createCell(0);
		cell.setCellValue("联 系 人：" + "");
		region = ExcelUtil2.setCellRange(sheet, rowIndex, rowIndex, 0, 4);
		ExcelUtil2.setBorderStyle(1, region, sheet, wb);
		cell.setCellStyle(leftStyle);
		row.setHeight((short)400);
		
		// 入住商户联系人
		cell = row.createCell(5);
		cell.setCellValue("联 系 人：" + "陈家忠");
		region = ExcelUtil2.setCellRange(sheet, rowIndex, rowIndex, 5, 13);
		ExcelUtil2.setBorderStyle(1, region, sheet, wb);
		cell.setCellStyle(leftStyle);
		
		rowIndex++;
		
		// 供应商电话
		row = sheet.createRow(rowIndex);
		cell = row.createCell(0);
		cell.setCellValue("电    话：" + "");
		region = ExcelUtil2.setCellRange(sheet, rowIndex, rowIndex, 0, 4);
		ExcelUtil2.setBorderStyle(1, region, sheet, wb);
		cell.setCellStyle(leftStyle);
		row.setHeight((short)400);
		
		// 入住商户电话
		cell = row.createCell(5);
		cell.setCellValue("电    话：" + "020-22353742");
		region = ExcelUtil2.setCellRange(sheet, rowIndex, rowIndex, 5, 13);
		ExcelUtil2.setBorderStyle(1, region, sheet, wb);
		cell.setCellStyle(leftStyle);
		
		rowIndex++;
		
		// 结款方式
		row = sheet.createRow(rowIndex);
		cell = row.createCell(0);
		cell.setCellValue("结款方式：" + "月结30天");
		region = ExcelUtil2.setCellRange(sheet, rowIndex, rowIndex, 0, 13);
		ExcelUtil2.setBorderStyle(1, region, sheet, wb);
		cell.setCellStyle(leftStyle);
		row.setHeight((short)400);
		
		rowIndex++;
		
		row = sheet.createRow(rowIndex);
		int k = 0;
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			cell = row.createCell((short) k);
			cell.setCellValue(entry.getValue());
			cell.setCellStyle(style);
			k++;
		}

		rowIndex++;
		int num = 1;
		
		// 创建内容
		for (Record item : data) {
			k = 0;
			row = sheet.createRow(rowIndex);
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				String key = entry.getKey();
				cell = row.createCell((short) k);
				cell.setCellStyle(style);
				
				if (key.equals("num")) {
					cell.setCellValue(num);
				} else if (key.equals("status")) {
					cell.setCellValue(getOrderStatus(item.getInt(key)));
				} else if (key.equals("pStatus")) {
					cell.setCellValue(getProductOrderStatus(item.getInt("pStatus")));
				} else if (key.equals("created_at")) {
					cell.setCellValue(DateHelper.formatDateTime(item.getDate("created_at")));
				} else if (key.equals("payType")) {
					cell.setCellValue(getOrderPayType(item.getInt(key)));
				} else if (key.equals("product_name")) {
					String value = item.getStr("product_name");
					String selectProterties = item.getStr("selectProterties");
					if (selectProterties != null && !selectProterties.equals("")) {
						value = value + "（" + selectProterties + "）";
					}
					cell.setCellValue(value);
					cell.setCellStyle(leftStyle);
				} else if (key.equals("receiveDetailAddress")) {
					cell.setCellValue(item.getStr("receiveDetailAddress"));
					cell.setCellStyle(leftStyle);
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
		
		return getPath(name, wb);
	}
	
	public static String exportSaleCost(String name, List<Record> data, Record statistics, Map<String, String> searchMap) {
		HSSFWorkbook wb = new HSSFWorkbook(); // 创建了一个excel文件
		HSSFSheet sheet = wb.createSheet("Sheet1"); // 创建了一个工作簿
		Row row = null;
		Cell cell = null;
		
		// 创建表头
		ExcelUtil2.creatHeaderTitle(wb, sheet, "电商收入成本表（已收货）");
		
		// 设置查询行
		HSSFCellStyle style8 = ExcelUtil2.getStyle8(wb); 
		int indexRow = 1;
		row = sheet.createRow(indexRow);
		cell = row.createCell(0);
		cell.setCellValue("已收货对账时间：" + searchMap.get("startTime") + "至" + searchMap.get("endTime"));
		ExcelUtil2.setCellRange(sheet, indexRow, indexRow, 0, 1);
		cell.setCellStyle(style8);
		
		cell = row.createCell(10);
		cell.setCellValue("单位：元");
		cell.setCellStyle(style8);
		
		// 设置列宽
		ExcelUtil2.setColumnWidth(sheet, new int[] {256*8, 256*50, 256*20, 256*16, 256*15, 256*20, 256*20, 
				256*20, 256*10, 256*16, 256*16, 256*25});
		
		// 设置标题行
		indexRow++;
		Row row1 = sheet.createRow(indexRow);
		Row row2 = sheet.createRow(indexRow + 1);
		row1.setHeight((short) 500);
		row2.setHeight((short) 500);
		HSSFCellStyle style6 = ExcelUtil2.getStyle6(wb);
		
		cell = row1.createCell(0);
		cell.setCellValue("序号");
		CellRangeAddress region1 = ExcelUtil2.setCellRange(sheet, indexRow, indexRow + 1, 0, 0);
		cell.setCellStyle(style6);
		ExcelUtil2.setBorderStyle(1, region1, sheet, wb);
		
		cell = row1.createCell(1);
		cell.setCellValue("供应商名称");
		CellRangeAddress region2 = ExcelUtil2.setCellRange(sheet, indexRow, indexRow + 1, 1, 1);
		cell.setCellStyle(style6);
		ExcelUtil2.setBorderStyle(1, region2, sheet, wb);
		
		cell = row1.createCell(2);
		cell.setCellValue("支付方式");
		CellRangeAddress region3 = ExcelUtil2.setCellRange(sheet, indexRow, indexRow, 2, 6);
		cell.setCellStyle(style6);
		ExcelUtil2.setBorderStyle(1, region3, sheet, wb);
		
		cell = row2.createCell(2);
		cell.setCellValue("微信(网页、APP)");
		cell.setCellStyle(style6);
		
		cell = row2.createCell(3);
		cell.setCellValue("支付宝支付");
		cell.setCellStyle(style6);
		
		cell = row2.createCell(4);
		cell.setCellValue("银联支付");
		cell.setCellStyle(style6);
		
		cell = row2.createCell(5);
		cell.setCellValue("团购卡支付");
		cell.setCellStyle(style6);
		
		cell = row2.createCell(6);
		cell.setCellValue("积分兑换支付");
		cell.setCellStyle(style6);
		
		cell = row1.createCell(7);
		cell.setCellValue("收入合计");
		CellRangeAddress region4 = ExcelUtil2.setCellRange(sheet, indexRow, indexRow + 1, 7, 7);
		cell.setCellStyle(style6);
		ExcelUtil2.setBorderStyle(1, region4, sheet, wb);
		
		cell = row1.createCell(8);
		cell.setCellValue("成本");
		CellRangeAddress region5 = ExcelUtil2.setCellRange(sheet, indexRow, indexRow, 8, 10);
		cell.setCellStyle(style6);
		ExcelUtil2.setBorderStyle(1, region5, sheet, wb);
		
		cell = row2.createCell(8);
		cell.setCellValue("税率");
		cell.setCellStyle(style6);
		
		cell = row2.createCell(9);
		cell.setCellValue("未税");
		cell.setCellStyle(style6);
		
		cell = row2.createCell(10);
		cell.setCellValue("含税");
		cell.setCellStyle(style6);
		
		cell = row1.createCell(11);
		cell.setCellValue("备注");
		CellRangeAddress region6 = ExcelUtil2.setCellRange(sheet, indexRow, indexRow + 1, 11, 11);
		cell.setCellStyle(style6);
		ExcelUtil2.setBorderStyle(1, region6, sheet, wb);
		
		indexRow += 2;
		
		// 内容
		for (int i = 0; i < data.size(); i++) {
			Record item = data.get(i);
			row = sheet.createRow(indexRow);
			
			// 序号
			cell = row.createCell(0);
			cell.setCellValue(i+1);
			cell.setCellStyle(style6);
			row.setHeight((short) 500);
			
			// 供应商
			cell = row.createCell(1);
			cell.setCellValue(item.getStr("supplier_name"));
			cell.setCellStyle(style6);
			
			// 微信
			cell = row.createCell(2);
			cell.setCellValue(item.getBigDecimal("weixinpay").doubleValue());
			cell.setCellStyle(style6);
			
			// 支付宝
			cell = row.createCell(3);
			cell.setCellValue(item.getBigDecimal("alipay").doubleValue());
			cell.setCellStyle(style6);
			
			// 银联
			cell = row.createCell(4);
			cell.setCellValue(item.getBigDecimal("unionpay").doubleValue());
			cell.setCellStyle(style6);
			
			// 团购卡
			cell = row.createCell(5);
			cell.setCellValue(item.getBigDecimal("cardpay").doubleValue());
			cell.setCellStyle(style6);
			
			// 团购卡
			cell = row.createCell(6);
			cell.setCellValue(item.getBigDecimal("pointpay").doubleValue());
			cell.setCellStyle(style6);
			
			// 收入合计
			cell = row.createCell(7);
			cell.setCellValue(item.getBigDecimal("payable").doubleValue());
			cell.setCellStyle(style6);
			
			// 税率
			cell = row.createCell(8);
			cell.setCellValue(item.getBigDecimal("taxRate").doubleValue()+"%");
			cell.setCellStyle(style6);
			
			// 未税
			cell = row.createCell(9);
			cell.setCellValue(item.getBigDecimal("totalUnitCostNoTax").doubleValue());
			cell.setCellStyle(style6);
			
			// 含税
			cell = row.createCell(10);
			cell.setCellValue(item.getBigDecimal("totalUnitCost").doubleValue());
			cell.setCellStyle(style6);
			
			// 备注
			cell = row.createCell(11);
			cell.setCellValue(item.getStr("invoiceType"));
			cell.setCellStyle(style6);
			
			indexRow++;
		}
		
		// 合计
		row = sheet.createRow(indexRow);
		cell = row.createCell(0);
		cell.setCellValue("合计");
		CellRangeAddress region7 = ExcelUtil2.setCellRange(sheet, indexRow, indexRow, 0, 1);
		cell.setCellStyle(style6);
		ExcelUtil2.setBorderStyle(1, region7, sheet, wb);
		row.setHeight((short) 500);
		
		// 微信
		cell = row.createCell(2);
		cell.setCellValue(statistics.getDouble("totalWeixinpay"));
		cell.setCellStyle(style6);
		
		// 支付宝
		cell = row.createCell(3);
		cell.setCellValue(statistics.getDouble("totalAlipay"));
		cell.setCellStyle(style6);
		
		// 银联支付
		cell = row.createCell(4);
		cell.setCellValue(statistics.getDouble("totalUnionpay"));
		cell.setCellStyle(style6);
		
		// 团购卡
		cell = row.createCell(5);
		cell.setCellValue(statistics.getDouble("totalCardpay"));
		cell.setCellStyle(style6);
		
		// 积分兑换
		cell = row.createCell(6);
		cell.setCellValue(statistics.getDouble("totalPointpay"));
		cell.setCellStyle(style6);
		
		// 收入合计
		cell = row.createCell(7);
		cell.setCellValue(statistics.getDouble("totalPayable"));
		cell.setCellStyle(style6);
		
		// 税率
		cell = row.createCell(8);
		cell.setCellValue("");
		cell.setCellStyle(style6);
		
		// 未税
		cell = row.createCell(9);
		cell.setCellValue(statistics.getDouble("totalUnitCostNoTax"));
		cell.setCellStyle(style6);
		
		// 含税
		cell = row.createCell(10);
		cell.setCellValue(statistics.getDouble("totalUnitCost"));
		cell.setCellStyle(style6);
		
		// 备注
		cell = row.createCell(11);
		cell.setCellValue("");
		cell.setCellStyle(style6);
		
		HSSFCellStyle style7 = ExcelUtil2.getStyle7(wb);
		
		// 时间备注
		/*indexRow += 3;
		row = sheet.createRow(indexRow);
		cell = row.createCell(1);
		cell.setCellValue("备注：" + "对账时间为"+searchMap.get("startTime")+"至"+searchMap.get("endTime"));
		cell.setCellStyle(style7);*/
		
		// 复核人
		indexRow += 5;
		row = sheet.createRow(indexRow);
		cell = row.createCell(1);
		cell.setCellValue("复核人：");
		cell.setCellStyle(style7);
		
		// 制表人
		cell = row.createCell(5);
		cell.setCellValue("制表人：");
		cell.setCellStyle(style7);
		
		return getPath(name, wb);
	}
	
	private static void setHeaderTitle(HSSFWorkbook wb, HSSFSheet sheet, String headerTitle, int lastCol) {
		HSSFRow row0 = sheet.createRow(0);
		HSSFCell cell0 = row0.createCell(0);
		cell0.setCellValue(headerTitle);
		
		sheet.addMergedRegion(new CellRangeAddress(0,0,0,lastCol));
		
		HSSFFont fontStyle = wb.createFont();
		fontStyle.setBold(true);  //设置粗体
		fontStyle.setFontHeightInPoints((short)14);  //设置字体大小
		
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);  //居中
		
		style.setFont(fontStyle);
		cell0.setCellStyle(style);
	}
	
	/**
	 * 退款状态
	 * @param status
	 * @return
	 */
	private static String getRefundStatus(int status) {
		String value = "待审核";
		
		//0待审核；1退款中；2审核不通过；3退款成功；4退款失败
		switch (status) {
		case 0: value = "待审核";
				break;
		
		case 1:	value = "退款中";
				break;
		
		case 2: value = "审核不通过";
				break;
		
		case 3: value = "退款成功";
				break;
				
		case 4: value = "退款失败";
				break;
		}
		
		return value;
	}
	
	/**
	 * 退货状态
	 * @param status
	 * @return
	 */
	private static String getBackStatus(int status) {
		String value = "待审核";
		
		//0待审核；1退款中；2审核不通过；3已收货；4退款成功；5退款失败
		switch (status) {
		case 0: value = "待审核";
				break;
		
		case 1:	value = "退款中";
				break;
		
		case 2: value = "审核不通过";
				break;
		
		case 3: value = "已收货";
				break;
				
		case 4: value = "退款成功";
				break;
		case 5: value = "退款失败";
				break;
		}
		
		return value;
	}
	
	/**
	 * 订单明细状态
	 * @param status
	 * @return
	 */
	private static String getProductOrderStatus(Integer status) {
		String value = "正常";
		
		//1正常，2申请退款，3不通过，4通过，5退款成功，6已退货, 7已评价
		if(status != null) {
			switch (status) {
				case 1:	value = "正常";
						break;
				
				case 2: value = "申请退款中";
						break;
				
				case 3: value = "退款审核不通过";
						break;
						
				case 4: value = "退款审核通过";
						break;
						
				case 5: value = "退款成功";
						break;
						
				case 6: value = "已退货";
						break;
						
				case 7: value = "已评价";
						break;
						
				case 8: value = "退款失败";
						break;
			}
		}else {
			return "";
		}
		return value;
	}
	
	/**
	 * 订单状态
	 * @param status
	 * @return
	 */
	private static String getOrderStatus(int status) {
		String value = "正常";
		
		//1: 待付款,2: 待发货,3: 待收货,4: 待评价,5: 已评价,6: 已取消, 7订单完成
		switch (status) {
			case 1:	value = "待付款";
					break;
			
			case 2: value = "待发货";
					break;
			
			case 3: value = "待收货";
					break;
					
			case 4: value = "待评价";
					break;
					
			case 5: value = "已评价";
					break;
					
			case 6: value = "已取消";
					break;
					
			case 7: value = "订单完成";
			break;
		}
		
		return value;
	}
	
	/**
	 * 订单来源
	 * @param source
	 * @return
	 */
	private static String getOrderSource(int source) {
		String value = "pc";
		
		//1pc、2微信端、3android、4苹果
		switch (source) {
		case 1: value = "pc端";
				break;
				
		case 2: value = "微信端";
				break;
				
		case 3: value = "android";
				break;
				
		case 4: value = "苹果";
				break;
		}
		
		return value;
	}
	
	
	/**
	 * 订单支付状态
	 * @param payType
	 * @return
	 */
	public static String getOrderPayType(int payType) {
		String value = "未知";
		
		//1: 微信支付, 2: 支付宝, 3银联支付，4钱包支付
		switch (payType) {
		case 1: value = "微信支付";
				break;
				
		case 2: value = "支付宝支付";
				break;
				
		case 3: value = "银联支付";
				break;
				
		case 4: value = "余额支付";
				break;
				
		case 5: value = "团购卡支付";
				break;
				
		case 6: value = "积分兑换支付";
				break;		
		}
		
		return value;
	}
	
	/**
	 * 读取excel表
	 * @param path 文件路径
	 * @param sheetIndex 工作簿下标
	 * @return Sheet
	 * @throws IOException
	 */
	public static Sheet readerExcel(String path, int sheetIndex) throws IOException {
		path = PathKit.getWebRootPath() + "/" + path;
		FileInputStream fileIn = new FileInputStream(path);
		//根据指定的文件输入流导入Excel从而产生Workbook对象
		Workbook wb0 = new HSSFWorkbook(fileIn);  
		//获取Excel文档中的第一个表单  
		Sheet sht0 = wb0.getSheetAt(sheetIndex);  
        fileIn.close();
        return sht0;
	}
	
	
	public static String convertPayType(int payType) {
		String p = "";
		if (payType == 1) {
			p = "微信支付";
		} else if (payType == 2) {
			p = "支付宝";
		} else if (payType == 3) {
			p = "银联支付";
		} else if (payType == 4) {
			p = "余额支付";
		} else if (payType == 5) {
			p = "团购卡支付";
		} else if (payType == 6) {
			p = "积分兑换";
		}
		return p;
	}
	
	public static String convertPayType(int payType, int source) {
		String p = "";
		if (payType == 1 && (source == 1 || source == 2)) {
			p = "微信公众号";
		} else if (payType == 1 && source == 3) {
			p = "微信App";
		} else if (payType == 2) {
			p = "支付宝";
		} else if (payType == 3) {
			p = "银联支付";
		} else if (payType == 4) {
			p = "余额支付";
		} else if (payType == 5) {
			p = "团购卡支付";
		} else if (payType == 6) {
			p = "积分兑换";
		}
		return p;
	}
	
	public static String getPath(String name, HSSFWorkbook wb) {
		String dir = "upload/excels";
		FileOutputStream fileOut = null;
		try {
			String BaseDir = PathKit.getWebRootPath() + File.separator + dir;
			File file = new File(BaseDir);
			
			// 如果文件夹不存在则创建
			if (!file.exists()) {
				file.mkdirs();
			}

			String path = BaseDir + "/" + name + ".xls";
			fileOut = new FileOutputStream(path);
			wb.write(fileOut);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (fileOut != null) {
				try {
					fileOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return dir + "/" + name + ".xls";
	}
	
	private static String convertEvent(int event) {
		String s = "";
		switch (event) {
		case 3:	s = "支付宝充值";	break;
		case 4: s = "微信充值";	break;
		case 5: s = "银联充值";	break;
		}
		return s;
	}
	
	private static String orderStatus(int event) {
		String s = "";
		switch (event) {
		case 1:	s = "未发货";	break;
		case 2:	s = "在途";	break;
		case 3: s = "已收货";	break;
		case 4: s = "退货";	break;
		}
		return s;
	}

	
}
