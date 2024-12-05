/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.bi.base.i18n.model;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;

import com.bi.base.util.SpringUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * Provides internationalization message.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
@Slf4j
public class LocaleMessage {

	/**
	 * Get message.
	 *
	 * @param code mapping key
	 * @return mapping value
	 */
	public String getMessage(String code) {
		return getMessage(code, new Object[]{});
	}

	/**
	 * Get message, otherwise default value.
	 *
	 * @param code mapping key
	 * @param defaultMessage return value when miss mapping
	 * @return mapping value
	 */
	public String getMessage(String code, String defaultMessage) {
		return getMessage(code, null, defaultMessage);
	}

	/**
	 * Get message that auto-mapping arguments to string.
	 *
	 * @param code mapping key
	 * @param args set value into mapping value text
	 * @return mapping value
	 */
	public String getMessage(String code, Object[] args) {
		String result = null;
		try {
			MessageSource messageSource = SpringUtil.getBean("messageSource");
			result = messageSource.getMessage(code, args, getLocale());
		} catch (NoSuchMessageException e) {
			log.debug("I18n not found {}", code);
		}
		return result != null ? result : code;
	}

	/**
	 * Get message that auto-mapping arguments to string, otherwise default value.
	 *
	 * @param code mapping key
	 * @param args set value into mapping value text
	 * @param defaultMessage return value when miss mapping
	 * @return mapping value
	 */
	public String getMessage(String code, Object[] args, String defaultMessage) {
		String result = code;
		try {
			MessageSource messageSource = SpringUtil.getBean("messageSource");
			result = messageSource.getMessage(code, args, defaultMessage, getLocale());
		} catch (NoSuchMessageException e) {
			log.debug("I18n not found {}", code);
		}
		return result;
	}

	/**
	 * Get current locale.
	 *
	 * @return current locale
	 */
	public Locale getLocale() {
		return LocaleContextHolder.getLocale();
	}

}
