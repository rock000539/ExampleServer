/*
 * Copyright (c) 2018 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.bi.base.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.bi.base.handler.XssRequestWrapper;

/**
 * Filter request header and parameter. that avoids attacking by XSS.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class XssFilter implements Filter {

	FilterConfig filterConfig = null;

	@Override
	public void destroy() {
		this.filterConfig = null;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		chain.doFilter(new XssRequestWrapper((HttpServletRequest) request), response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

}
