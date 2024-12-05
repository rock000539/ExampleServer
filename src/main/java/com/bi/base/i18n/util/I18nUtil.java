/*
 * Copyright (c) 2018 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.bi.base.i18n.util;

import com.bi.base.i18n.model.LocaleMessage;
import com.bi.base.i18n.model.MessageProvider;
import com.bi.base.web.model.ResultEntity;

/**
 * Provides internationalization utility functions.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
public final class I18nUtil {

	private static final LocaleMessage localeMessage = new LocaleMessage();

	/**
	 * Get internationalization message.
	 *
	 * @param code mapping key
	 * @return mapping value
	 */
	public static String getMessage(String code) {
		return localeMessage.getMessage(code);
	}

	/**
	 * Get internationalization message.
	 *
	 * @param code mapping key
	 * @param defaultMessage return value when miss mapping
	 * @return mapping value
	 */
	public static String getMessage(String code, String defaultMessage) {
		return localeMessage.getMessage(code, defaultMessage);
	}

	/**
	 * Get internationalization message.
	 *
	 * @param code mapping key
	 * @param args set value into mapping value text
	 * @return mapping value
	 */
	public static String getMessage(String code, Object[] args) {
		return localeMessage.getMessage(code, args);
	}

	/**
	 * Get internationalization message.
	 *
	 * @param code mapping key
	 * @param args set value into mapping value text
	 * @param defaultMessage return value when miss mapping
	 * @return mapping value
	 */
	public static String getMessage(String code, Object[] args, String defaultMessage) {
		return localeMessage.getMessage(code, args, defaultMessage);
	}

	/**
	 * Get ResultEntity from enum.
	 *
	 * @param obj enum value
	 * @return mapping value
	 */
	public static ResultEntity<Object> resultEntity(Object obj) {
		if (obj instanceof MessageProvider) {
			MessageProvider messageProvider = (MessageProvider) obj;
			return new ResultEntity<>(messageProvider.getMessage(), ((Enum<?>) obj).name());
		}
		return new ResultEntity<>(obj);
	}

}
