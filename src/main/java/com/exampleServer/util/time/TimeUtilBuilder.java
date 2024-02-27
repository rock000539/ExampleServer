/*
 * Copyright (c) 2023 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.exampleServer.util.time;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;
import lombok.Setter;
import org.springframework.util.Assert;

public class TimeUtilBuilder implements TimeUtil {

	private Calendar calendar;

	@Setter
	private static boolean sitMode = false;

	@Setter
	private static Date mockDate;

	protected TimeUtilBuilder() {
		calendar = Calendar.getInstance();
		if (sitMode) {
			calendar.setTime(mockDate);
		}
	}

	protected TimeUtilBuilder(Date date) {
		calendar = Calendar.getInstance();
		if (sitMode) {
			calendar.setTime(mockDate);
		} else {
			calendar.setTime(date);
		}
	}

	protected TimeUtilBuilder(String date, String dateFormat) {
		calendar = Calendar.getInstance();
		if (sitMode) {
			calendar.setTime(mockDate);
		} else {
			DateTimeFormatter formatter = new DateTimeFormatterBuilder()
					.appendPattern(dateFormat)
					.parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
					.parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
					.parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
					.parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
					.parseDefaulting(ChronoField.MILLI_OF_SECOND, 0)
					.toFormatter();
			LocalDate localDateTime = LocalDate.parse(date, formatter);
			calendar.set(Calendar.YEAR, localDateTime.getYear());
			calendar.set(Calendar.MONTH, localDateTime.getMonthValue() - 1);
			calendar.set(Calendar.DAY_OF_MONTH, localDateTime.getDayOfMonth());
		}
	}

	@Override
	public TimeUtilBuilder atEndOfDay() {
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return this;
	}

	@Override
	public TimeUtilBuilder atStartOfDay() {
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return this;
	}

	@Override
	public TimeUtilBuilder atStartOfMonth() {
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return this;
	}

	@Override
	public TimeUtilBuilder atStartOfYear() {
		calendar.set(Calendar.MONTH, 1);
		return this;
	}

	@Override
	public TimeUtilBuilder atEndOfYear() {
		calendar.set(Calendar.MONTH, 12);
		return this;
	}

	@Override
	public long getYear() {
		return this.calendar.get(Calendar.YEAR);
	}

	@Override
	public long getMonth() {
		return this.calendar.get(Calendar.MONTH) + 1;
	}

	@Override
	public long getDay() {
		return this.calendar.get(Calendar.DAY_OF_MONTH);
	}

	@Override
	public TimeUtilBuilder atEndOfMonth() {
		int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		calendar.set(Calendar.DAY_OF_MONTH, lastDay);
		return this;
	}

	@Override
	public TimeUtilBuilder addDays(int days) {
		calendar.add(Calendar.DAY_OF_MONTH, days);
		return this;
	}

	@Override
	public TimeUtilBuilder addMonths(int month) {
		calendar.add(Calendar.MONTH, month);
		return this;
	}

	@Override
	public TimeUtilBuilder addYears(int year) {
		calendar.add(Calendar.YEAR, year);
		return this;
	}

	@Override
	public long diffDays(Date compare) {
		long diff = calendar.getTimeInMillis() - compare.getTime();
		return diff / (24 * 60 * 60 * 1000);
	}

	@Override
	public int betweenMonth(Date dt) {
		Assert.notNull(dt, "data should not be null.");
		Calendar tmpCal = Calendar.getInstance();
		tmpCal.setTime(dt);
		int curYear = calendar.get(Calendar.YEAR);
		int curMonth = calendar.get(Calendar.MONTH) + 1;
		int tmpYear = tmpCal.get(Calendar.YEAR);
		int tmpMonth = tmpCal.get(Calendar.MONTH) + 1;
		if (curYear == tmpYear) {
			return Math.abs(tmpMonth - curMonth) + 1;
		}
		int smallerM = curYear > tmpYear ? tmpMonth : curMonth;
		int biggerM = curYear > tmpYear ? curMonth : tmpMonth;
		return biggerM + 12 - smallerM + 1;
	}

	@Override
	public Date getTime() {
		return calendar.getTime();
	}

	@Override
	public String toString(String format) {
		SimpleDateFormat simDtFormat = new SimpleDateFormat(format);
		return simDtFormat.format(calendar.getTime());
	}
}
