/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.bi.base.model.enums;

import com.bi.base.i18n.model.MessageProvider;
import com.bi.base.i18n.util.I18nUtil;

import lombok.AllArgsConstructor;

/**
 * Provides strict Yes No values with internationalization language.
 * 
 * @author Allen Lin
 * @since 1.0.0
 */
@AllArgsConstructor
public enum YesNo implements MessageProvider {

	Y("global.text.YesNo.yes"),

	N("global.text.YesNo.no");

	private final String value;

	@Override
	public String getMessage() {
		return I18nUtil.getMessage(value);
	}
}
