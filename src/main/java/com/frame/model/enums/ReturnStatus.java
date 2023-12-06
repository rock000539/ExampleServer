/*
 * Copyright (c) 2021 -Parker.
 * All rights reserved.
 */
package com.frame.model.enums;

import com.bi.base.i18n.util.I18nUtil;
import java.text.MessageFormat;

/**
 * 自訂義訊息回傳
 *
 * @author Parker Huang
 * @since 1.0.0
 */
public interface ReturnStatus {

	/**
	 * Get system response code.
	 *
	 * @return
	 */
	String getReturnCode();

	/**
	 * Get system response code type.
	 *
	 * @return
	 */
	String getReturnType();

	/**
	 * Get system response description.
	 *
	 * @param args
	 * @return
	 */
	default String getReturnDesc(Object... args) {
		return I18nUtil.getMessage(new MessageFormat("ReturnStatus.{0}.{1}")
				.format(new Object[]{getReturnType(), getReturnCode()}), args, "");
	}

	/**
	 * Compare code with return code.
	 *
	 * @param code
	 * @return
	 */
	default boolean equalsCode(String code) {
		return getReturnCode().equals(code);
	}
}
