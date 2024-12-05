/*
 * Copyright (c) 2018 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.bi.base.handler;

import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Provides an implementation of XSS protector.that avoids
 * attacking by XSS.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
public class XssRequestWrapper extends HttpServletRequestWrapper {

	public XssRequestWrapper(HttpServletRequest servletRequest) {
		super(servletRequest);
	}

	@Override
	public String[] getParameterValues(String parameter) {
		String[] values = super.getParameterValues(parameter);
		return values == null ? null : Stream.of(values).map(this::cleanXss).toArray(String[]::new);
	}

	@Override
	public String getParameter(String parameter) {
		String value = super.getParameter(parameter);
		return value == null ? null : cleanXss(value);
	}

	@Override
	public String getHeader(String name) {
		String value = super.getHeader(name);
		return value == null ? null : cleanXss(value);
	}

	private String cleanXss(String value) {
		// You'll need to remove the spaces from the html entities below
		value = value.replaceAll("<", "& lt;").replaceAll(">", "& gt;");
		value = value.replaceAll("\\(", "& #40;").replaceAll("\\)", "& #41;");
		value = value.replaceAll("'", "& #39;");
		value = value.replaceAll("eval\\((.*)\\)", "");
		value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
		value = value.replaceAll("script", "");
		return value;
	}

}
