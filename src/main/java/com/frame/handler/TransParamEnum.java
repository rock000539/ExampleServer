/*
 * Copyright (c) 2020 -Parker.
 * All rights reserved.
 */
package com.frame.handler;

import com.bi.base.i18n.util.I18nUtil;

/**
 * i18n文字轉換邏輯
 *
 * @author Parker Huang
 * @since 1.0.0
 */
public interface TransParamEnum extends TransParam {

	String getValue();

	String getDescription();

	default String getMessage() {
		return I18nUtil.getMessage(getDescription());
	}

	default boolean equal(String value) {
		return getValue() != null && getValue().equals(value);
	}
}
