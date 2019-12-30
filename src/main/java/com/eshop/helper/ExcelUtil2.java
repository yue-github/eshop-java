package com.eshop.helper;


import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;


public class ExcelUtil2 {
	
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
	public static void getTogetherAndaddBorder(int firstCol,int lastCol,int firstRow,int lastRow ,HSSFSheet sheet,HSSFWorkbook wb) {
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
	public static void addBorder(/*HSSFCellStyle style*/CellRangeAddress cra, HSSFSheet sheet, HSSFWorkbook wb) {
		// 使用RegionUtil类为合并后的单元格添加边框  
        RegionUtil.setBorderBottom(1, cra, sheet, wb); // 下边框  
        RegionUtil.setBorderLeft(1, cra, sheet, wb); // 左边框  
        RegionUtil.setBorderRight(1, cra, sheet, wb); // 右边框  
        RegionUtil.setBorderTop(1, cra, sheet, wb); // 上边框
	}
	
	/**
	 * 设置表格的列宽
	 * @param sheet
	 * @param columnWidth
	 */
	public static void setColumnWidth(HSSFSheet sheet, int[] columnWidths) {
		for(int i=0; i<columnWidths.length; i ++) {
			sheet.setColumnWidth(i, columnWidths[i]);
		}
	}
	
	/**
	 * 合并单元格
	 * @param sheet
	 * @param firstRow
	 * @param lastRow
	 * @param firstCol
	 * @param lastCol
	 */
	public static CellRangeAddress setCellRange(HSSFSheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
		CellRangeAddress region = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
		sheet.addMergedRegion(region);
		return region;
	}
	
	/**
	 * 设置列宽
	 * @param sheet
	 * @param columnIndex
	 * @param width
	 */
	public static void setColumnWidth(HSSFSheet sheet, int columnIndex, int width) {
		sheet.setColumnWidth(columnIndex, width);
	}
	
	/**
	 * 设置行高
	 * @param row
	 * @param height
	 */
	public static void setRowHeight(Row row, int height) {
		row.setHeight((short) height);
	}
	
	/**
	 * 设置样式居中
	 * @param style
	 */
	public static void setAlignmentCenter(HSSFCellStyle style) {
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
	}
	
	/**
	 * 设置字体
	 * @param font
	 * @param style
	 * @param size
	 * @param isBold
	 */
	public static void setFontStyle(HSSFFont font, HSSFCellStyle style, int size, boolean isBold) {
		setFontStyle(font, style, size, isBold, "Arial");
	}
	
	public static void setFontStyle(HSSFFont font, HSSFCellStyle style, int size, boolean isBold, String fontName) {
		font.setFontHeightInPoints((short) size);
		if (isBold) {
			font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		}
		font.setFontName(fontName);
		style.setFont(font);
	}
	
	/**
	 * 创建表头
	 * @param wb
	 * @param sheet
	 * @param title
	 */
	public static void creatHeaderTitle(HSSFWorkbook wb, HSSFSheet sheet, String title) {
		HSSFRow row0 = sheet.createRow(0);
		HSSFCell cell0 = row0.createCell(0);
		cell0.setCellValue(title);
		
		HSSFCellStyle style = wb.createCellStyle();
		HSSFFont font = wb.createFont();
		
		setBorder(style);
		setCellRange(sheet, 0, 0, 0, 12);
		setRowHeight(row0, 800);
		setAlignmentCenter(style);
		setFontStyle(font, style, 20, true, "宋体");
		
		cell0.setCellStyle(style);
	}
	
	public static void creatHeaderTitle(HSSFWorkbook wb, HSSFSheet sheet, String title, int firstRow,
			int lastRow, int firstCol, int lastCol, boolean isBorder) {
		
		HSSFRow row0 = sheet.createRow(firstRow);
		HSSFCell cell0 = row0.createCell(firstCol);
		cell0.setCellValue(title);
		
		HSSFCellStyle style = wb.createCellStyle();
		HSSFFont font = wb.createFont();
		
		CellRangeAddress region = setCellRange(sheet, firstRow, lastRow, firstCol, lastCol);
		
		if (isBorder) {
			addBorder(region, sheet, wb);
		}
		
		setRowHeight(row0, 800);
		setAlignmentCenter(style);
		setFontStyle(font, style, 20, true, "宋体");
		
		cell0.setCellStyle(style);
	}
	
	/**
	 * 设置表格标题行
	 * @param sheet
	 * @param columnWidth
	 */
	public static void setTitle(HSSFRow row, String[] titles, HSSFCellStyle style) {
		HSSFCell cell = null;
		for(int i=0; i<titles.length; i ++) {
			cell = row.getCell(i);
			cell.setCellValue(titles[i]);
			cell.setCellStyle(style);
		}
	}
	
	public static void setTitle(HSSFRow row, Map<String, String> titles, HSSFCellStyle style) {
		HSSFCell cell = null;
		int k = 0;
		for (Map.Entry<String, String> entry : titles.entrySet()) {
			cell = row.createCell((short) k);
			cell.setCellValue(entry.getValue());
			cell.setCellStyle(style);
			k++;
		}
	}
	
	/**
	 * 设置单元格边框，字体，水平居中，垂直居中
	 * @param workbook
	 * @return
	 */
	public static HSSFCellStyle getStyle(HSSFWorkbook workbook){
		HSSFCellStyle style = workbook.createCellStyle();
        style = workbook.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        return style;
	}
	/**
	 * 设置单元格边框，字体，水平居中，垂直居中
	 * @param workbook
	 * @return
	 */
	public static HSSFCellStyle getStyle1(HSSFWorkbook workbook){
		HSSFCellStyle style = workbook.createCellStyle();
		setBorder(style);//设置边框
		style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 13);
        style.setFont(font);
        return style;
	}
	
	/**
	 * 设置单元格边框，字体，垂直居中
	 * @param workbook
	 * @return
	 */
	public static HSSFCellStyle getStyle2(HSSFWorkbook workbook){
		HSSFCellStyle style = workbook.createCellStyle();
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 13);
        style.setFont(font);
        return style;
	}
	
	/**
	 * 设置单元格字体颜色，大小，垂直居中
	 * @param workbook
	 * @return
	 */
	public static HSSFCellStyle getStyle3(HSSFWorkbook workbook){
		HSSFCellStyle style = workbook.createCellStyle();
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 13);
        font.setColor(HSSFColor.BLACK.index);  
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style.setFont(font);
        return style;
	}
	
	/**
	 * 设置单元格边框，垂直居中,主要给表格中的数据设置样式
	 * @param workbook
	 * @return
	 */
	public static HSSFCellStyle getStyle4(HSSFWorkbook workbook){
		HSSFCellStyle style = workbook.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        setBorder(style);
        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        return style;
	}
	
	/**
	 * 设置字体
	 * @param workbook
	 * @return
	 */
	public static HSSFCellStyle getStyle5(HSSFWorkbook workbook) {
		HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        return style;
	}
	
	public static HSSFCellStyle getStyle6(HSSFWorkbook workbook) {
		HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        
        setAlignmentCenter(style);
        setFontStyle(font, style, 12, false, "宋体");
        setBorder(style);
        
        return style;
	}
	
	public static HSSFCellStyle getStyle7(HSSFWorkbook workbook) {
		HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        
        setFontStyle(font, style, 12, false, "宋体");
        
        return style;
	}
	
	public static HSSFCellStyle getStyle8(HSSFWorkbook workbook) {
		HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        
        setFontStyle(font, style, 12, false, "宋体");
        
        return style;
	}
	
	public static HSSFCellStyle getStyle8(HSSFWorkbook workbook, int fontSize, boolean isBold,
			String fontFamily, boolean isSetBorder, boolean isAlignCenter, boolean isVerticalCenter) {
		HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        
        setFontStyle(font, style, fontSize, isBold, fontFamily);
        
        if (isSetBorder) {
        	setBorder(style);
        }
        
        if (isAlignCenter) {
        	style.setAlignment(CellStyle.ALIGN_CENTER);
        }
        
        if (isVerticalCenter) {
        	style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        }
        
        return style;
	}
	
	public static HSSFCellStyle getStyle9(HSSFWorkbook workbook, boolean isAlignCenter, boolean isVerticalCenter, int fontSize, String fontStyle){
		HSSFCellStyle style = workbook.createCellStyle();
		setBorder(style);//设置边框
		if (isAlignCenter)
			style.setAlignment(CellStyle.ALIGN_CENTER);
		if (isVerticalCenter)
			style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) fontSize);
        font.setFontName(fontStyle);
        style.setFont(font);
        return style;
	}
	
	/**
	 * 设置单元格边框
	 * @param style
	 */
	public static void setBorder(HSSFCellStyle style) {
		style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setBorderTop(CellStyle.BORDER_THIN);
	}
	
	/**
	 * 给合并单元格的设置边框
	 * @param border
	 * @param region
	 * @param sheet
	 * @param wb
	 */
	public static void setBorderStyle(int border, CellRangeAddress region, HSSFSheet sheet, HSSFWorkbook wb){
        RegionUtil.setBorderBottom(border, region, sheet, wb);//下边框
        RegionUtil.setBorderLeft(border, region, sheet, wb);//左边框
        RegionUtil.setBorderRight(border, region, sheet, wb);//右边框
        RegionUtil.setBorderTop(border, region, sheet, wb); //上边框
   }

}
