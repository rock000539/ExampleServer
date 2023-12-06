/*
 * Copyright (c) 2021 -Parker.
 * All rights reserved.
 */
package com.frame.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * IP 資訊擷取工具
 *
 * @author Parker Huang
 * @since 1.0.0
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IpUtil {

	private static final String[] HEADER_IP_LIST = new String[]{
			"True-Client-IP",
			"X-Forwarded-For",
			"Proxy-Client-IP",
			"WL-Proxy-Client-IP",
			"HTTP_CLIENT_IP",
			"HTTP_X_FORWARDED_FOR",
			"X-Real-IP"
	};

	public static String getClientIp() {
		try {
			HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes()))
					.getRequest();
			String ip = null;
			for (String headerIp : HEADER_IP_LIST) {
				ip = request.getHeader(headerIp);
				if (StringUtils.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
					break;
				}
			}
			if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
				if ("0:0:0:0:0:0:0:1".equals(ip)) {
					ip = "127.0.0.1";
				}
			}
			return ip;
		} catch (Exception e) {
			return null;
		}
	}

	public static String getServerIp() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			return null;
		}
	}
}
