/*
 * Copyright (c) 2022 -Parker.
 * All rights reserved.
 */
package com.frame.util;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

/**
 * Excel生成工具
 *
 * @author Parker Huang
 * @since 1.0.0
 */
@Slf4j
public class ExportExcelUtils {

	/**
	 * 導出Excel
	 *
	 * @param excelName 要導出的excel名稱
	 * @param list 要導出的數據集合
	 * @param fieldMap 中英文欄位對應Map，即要導出的excel表頭
	 * @param response 使用response可以導出到瀏覽器
	 * @param <T>
	 */
	public static <T> void export(String excelName, List<T> list, LinkedHashMap<String, String> fieldMap, HttpServletResponse response) {

		// 設置默認文件名為當前時間：年月日時分秒
		if (excelName == null || excelName == "") {
			excelName = DateFormatUtils.format(new Date(), "yyyyMMddhhmmss");
		}
		// 設置response頭資訊
		response.reset();
		response.setContentType("application/vnd.ms-excel"); // 改成輸出excel文件
		try {
			response.setHeader("Content-disposition", "attachment; filename=" + new String(excelName.getBytes("UTF-8")) + ".xls");
		} catch (UnsupportedEncodingException e1) {
			log.info("Set Response Head Failed!! : " + e1.getMessage());
		}

		try {
			try (
					// 創建一個WorkBook,對應一個Excel文件
					HSSFWorkbook wb = new HSSFWorkbook();
					OutputStream ouputStream = response.getOutputStream();) {
				// 在Workbook中，創建一個sheet，對應Excel中的工作薄（sheet）
				HSSFSheet sheet = wb.createSheet(excelName);
				// 創建單元格，並設置值表頭 設置表頭居中
				HSSFCellStyle style = wb.createCellStyle();
				// 創建一個居中格式
				style.setAlignment(HorizontalAlignment.CENTER);
				// 填充工作表
				fillSheet(sheet, list, fieldMap, style);

				// 將文件輸出
				wb.write(ouputStream);
			}
		} catch (Exception e) {
			log.info("導出Excel失敗！");
			log.error(e.getMessage());
		}
	}

	/**
	 * 根據欄位名獲取欄位對象
	 *
	 * @param fieldName 欄位名
	 * @param clazz 包含該欄位的類
	 * @return 欄位
	 */
	public static Field getFieldByName(String fieldName, Class<?> clazz) {
		// 拿到本類的所有欄位
		Field[] selfFields = clazz.getDeclaredFields();
		// 如果本類中存在該欄位，則返回
		for (Field field : selfFields) {
			// 如果本類中存在該欄位，則返回
			if (field.getName().equals(fieldName)) {
				return field;
			}
		}

		// 否則，查看父類中是否存在此欄位，如果有則返回
		Class<?> superClazz = clazz.getSuperclass();
		if (superClazz != null && superClazz != Object.class) {
			// 遞歸
			return getFieldByName(fieldName, superClazz);
		}

		// 如果本類和父類都沒有，則返回空
		return null;
	}

	/**
	 * 根據欄位名獲取欄位值
	 *
	 * @param fieldName 欄位名
	 * @param o 對象
	 * @return 欄位值
	 * @throws Exception 異常
	 */
	public static Object getFieldValueByName(String fieldName, Object o) throws Exception {
		Object value = null;
		// 根據欄位名得到欄位對象
		Field field = getFieldByName(fieldName, o.getClass());

		// 如果該欄位存在，則取出該欄位的值
		if (field != null) {
			field.setAccessible(true); // 類中的成員變數為private,在類外邊使用屬性值，故必須進行此操作
			value = field.get(o); // 獲取當前對象中當前Field的value
		} else {
			throw new Exception(o.getClass().getSimpleName() + "類不存在欄位名 " + fieldName);
		}

		return value;
	}

	/**
	 * 根據帶路徑或不帶路徑的屬性名獲取屬性值,即接受簡單屬性名， 如userName等，又接受帶路徑的屬性名，如student.department.name等
	 *
	 * @param fieldNameSequence 帶路徑的屬性名或簡單屬性名
	 * @param o 對象
	 * @return 屬性值
	 * @throws Exception 異常
	 */
	public static Object getFieldValueByNameSequence(String fieldNameSequence, Object o) throws Exception {
		Object value = null;

		// 將fieldNameSequence進行拆分
		String[] attributes = fieldNameSequence.split("\\.");
		if (attributes.length == 1) {
			value = getFieldValueByName(fieldNameSequence, o);
		} else {
			// 根據數組中第一個連接屬性名獲取連接屬性對象，如student.department.name
			Object fieldObj = getFieldValueByName(attributes[0], o);
			// 截取除第一個屬性名之後的路徑
			String subFieldNameSequence = fieldNameSequence.substring(fieldNameSequence.indexOf(".") + 1);
			// 遞歸得到最終的屬性對象的值
			value = getFieldValueByNameSequence(subFieldNameSequence, fieldObj);
		}
		return value;
	}

	/**
	 * 向工作表中填充數據
	 *
	 * @param sheet excel的工作表名稱
	 * @param list 數據源
	 * @param fieldMap 中英文欄位對應關係的Map
	 * @param style 表格中的格式
	 * @throws Exception 異常
	 */
	public static <T> void fillSheet(HSSFSheet sheet, List<T> list, LinkedHashMap<String, String> fieldMap, HSSFCellStyle style) throws Exception {
		log.info("向工作表中填充數據:fillSheet()");
		// 定義存放英文欄位名和中文欄位名的數組
		String[] enFields = new String[fieldMap.size()];
		String[] cnFields = new String[fieldMap.size()];

		// 填充數組
		int count = 0;
		for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
			enFields[count] = entry.getKey();
			cnFields[count] = entry.getValue();
			count++;
		}

		// 在sheet中添加表頭第0行,注意老版本poi對Excel的行數列數有限制short
		HSSFRow row = sheet.createRow((int) 0);

		// 填充表頭
		for (int i = 0; i < cnFields.length; i++) {
			HSSFCell cell = row.createCell(i);
			cell.setCellValue(cnFields[i]);
			cell.setCellStyle(style);
			sheet.autoSizeColumn(i);
		}

		// 填充內容
		for (int index = 0; index < list.size(); index++) {
			row = sheet.createRow(index + 1);
			// 獲取單個對象
			T item = list.get(index);
			for (int i = 0; i < enFields.length; i++) {
				Object objValue = getFieldValueByNameSequence(enFields[i], item);
				String fieldValue = objValue == null ? "" : objValue.toString();

				row.createCell(i).setCellValue(fieldValue);
			}
		}
	}
}
