/*
 * Copyright (c) 2020 -Parker.
 * All rights reserved.
 */
package com.project.frame.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 日期格式
 *
 * @author Parker Huange
 * @since 1.0.0
 */
@AllArgsConstructor
public enum DateType {

	STAND_DATE("yyyy/MM/dd"),
	STAND_DATE_TIME("yyyy/MM/dd HH:mm:ss"),
	INPUT_DATE_TIME("yyyy-MM-dd HH:mm"),
	INPUT_STAND_DATE("yyyy-MM-dd"),
	QUERY_TIME("HH:mm");

	@Getter
	private final String value;
}
