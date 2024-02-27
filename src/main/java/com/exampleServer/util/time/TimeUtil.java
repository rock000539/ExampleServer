/*
 * Copyright (c) 2023 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.exampleServer.util.time;

import java.text.ParseException;
import java.util.Date;

/** 時間產生以及運算工具 */
public interface TimeUtil {

	/**
	 * 工具初始化
	 *
	 * @return 回傳初始化
	 */
	static TimeUtilBuilder builder() {
		return new TimeUtilBuilder();
	}

	/**
	 * 工具初始化
	 *
	 * @param date 輸入固定時間
	 * @return 回傳初始化
	 */
	static TimeUtilBuilder builder(Date date) {
		return new TimeUtilBuilder(date);
	}

	/**
	 * 工具初始化
	 *
	 * @param date 輸入固定時間
	 * @param dateFormat 讀取格式
	 * @return 回傳初始化
	 */
	static TimeUtilBuilder builder(String date, String dateFormat) throws ParseException {
		return new TimeUtilBuilder(date, dateFormat);
	}

	/**
	 * 為查詢時間加上每日的結尾時間 23:59:59 999
	 *
	 * @return
	 */
	TimeUtilBuilder atEndOfDay();

	/**
	 * 為查詢時間加上每日的結尾時間 00:00:00 00
	 *
	 * @return
	 */
	TimeUtilBuilder atStartOfDay();

	/**
	 * 根據查詢時間取得該月的第一天
	 *
	 * @return
	 */
	TimeUtilBuilder atStartOfMonth();

	/**
	 * 設定為該年第一個月
	 *
	 * @return
	 */
	TimeUtilBuilder atStartOfYear();

	/**
	 * 設定為該年最後一個月
	 *
	 * @return
	 */
	TimeUtilBuilder atEndOfYear();

	/**
	 * 取得年份
	 *
	 * @return
	 */
	long getYear();

	/**
	 * 取得月份
	 *
	 * @return
	 */
	long getMonth();

	/**
	 * 取得天
	 *
	 * @return
	 */
	long getDay();

	/**
	 * 根據查詢時間取得該月的最後一天
	 *
	 * @return
	 */
	TimeUtilBuilder atEndOfMonth();

	/**
	 * 月計算
	 *
	 * @param days 加上得日數
	 * @return
	 */
	TimeUtilBuilder addDays(int days);

	/**
	 * 月計算
	 *
	 * @param month 加上得月數
	 * @return
	 */
	TimeUtilBuilder addMonths(int month);

	/**
	 * 年份計算
	 *
	 * @param year 加上得年數
	 * @return
	 */
	TimeUtilBuilder addYears(int year);

	/**
	 * @param compare 比較時間
	 * @return 差異天數
	 */
	long diffDays(Date compare);

	/**
	 * 計算月份間隔 ex: 202201~202202 =2 202301~202212 =2
	 *
	 * @param dt 比較時間
	 * @return
	 */
	int betweenMonth(Date dt);

	/**
	 * 取得時間
	 *
	 * @return 時間
	 */
	Date getTime();

	String toString(String format);
}
