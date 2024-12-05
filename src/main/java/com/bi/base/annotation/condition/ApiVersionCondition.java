/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.bi.base.annotation.condition;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

/**
 * Request for mapping actually API version.
 *
 * @author Allen Lin
 * @since 1.0.0
 */
public class ApiVersionCondition implements RequestCondition<ApiVersionCondition> {

	/**
	 * Extract the version part from url. example [v0-9]
	 */
	private final static Pattern VERSION_PREFIX_PATTERN = Pattern.compile("v(\\d+)/");

	@Getter
	private final int apiVersion;

	public ApiVersionCondition(int apiVersion) {
		this.apiVersion = apiVersion;
	}

	@Override
	public ApiVersionCondition combine(ApiVersionCondition other) {
		// Latest defined would be take effect, that means, methods definition with
		// override the classes definition
		return new ApiVersionCondition(other.getApiVersion());
	}

	@Override
	public ApiVersionCondition getMatchingCondition(jakarta.servlet.http.HttpServletRequest request) {
		Matcher matcher = VERSION_PREFIX_PATTERN.matcher(request.getRequestURI());
		try {
			if (matcher.find()) {
				int version = Integer.parseInt(matcher.group(1));
				if (version >= this.apiVersion) {// when applying version number bigger than configuration, then it will take effect
					return this;
				}
			}
		} catch (Exception e) {
			// Exception forward to 404
		}
		return null;
	}

	/**
	 * When more than one configured version number passed the match rule,
	 * then only the biggest one will take effect.
	 */
	@Override
	public int compareTo(ApiVersionCondition other, jakarta.servlet.http.HttpServletRequest request) {
		return other.getApiVersion() - this.apiVersion;
	}
}
