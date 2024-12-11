/*
 * Copyright (c) 2021 -Parker.
 * All rights reserved.
 */
package com.project.frame.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
			// 從 RequestContextHolder 獲取當前請求
			ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			if (attributes == null) {
				return null; // 確保在非 Web 環境中不崩潰
			}
			HttpServletRequest request = attributes.getRequest();

			// 遍歷可能的頭部來提取客戶端 IP
			for (String header : HEADER_IP_LIST) {
				String ip = request.getHeader(header);
				if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
					// 若包含多個 IP 地址，取第一個
					return ip.split(",")[0].trim();
				}
			}

			// 如果沒有從頭部獲得 IP，使用 getRemoteAddr
			String ip = request.getRemoteAddr();
			if ("0:0:0:0:0:0:0:1".equals(ip)) {
				ip = "127.0.0.1"; // 處理 IPv6 本地地址
			}
			return ip;
		} catch (Exception e) {
			// 捕獲異常並返回 null
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
