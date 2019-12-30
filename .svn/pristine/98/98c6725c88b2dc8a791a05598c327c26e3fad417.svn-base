package com.kuaidi100.util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;

public class ExcelUtil2 {

	
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
