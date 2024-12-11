/*
 * Copyright (c) 2022 -Parker.
 * All rights reserved.
 */
package com.project.frame.interceptor;

import com.bi.base.database.annotation.BaseAutowired;
import com.bi.base.database.dao.BaseDao;
import com.bi.base.web.BaseController;
import com.project.business.adm.model.AdmUserAccessLogs;
import com.project.frame.model.StatusCaptureResponseWrapper;
import com.project.frame.model.User;
import com.project.frame.util.IpUtil;
import com.project.frame.util.UserUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 使用者使用紀錄
 *
 * @author Parker Huang
 * @since 1.0.0
 */
@Component
public class UserAccessLoggingInterceptor implements HandlerInterceptor {

	private final String[] ignorePath = {"/", "/sso", "/login", "/logoutSuccess", "/role", "/403"};

	@BaseAutowired
	private BaseDao<AdmUserAccessLogs> admUserAccessLogsBaseDao;

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
		if (HandlerMethod.class.equals(handler.getClass())) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			Class<?> clazz = handlerMethod.getBeanType().getSuperclass();
			if (response instanceof StatusCaptureResponseWrapper) {
				StatusCaptureResponseWrapper wrappedResponse = (StatusCaptureResponseWrapper) response;

				if (clazz.equals(BaseController.class) || clazz.equals(BaseController.class)) {
					String path = request.getServletPath();
					int status = wrappedResponse.getStatus();
					if (status == 200
							&& StringUtils.isNoneBlank(path)
							&& !ArrayUtils.contains(ignorePath, path)) {
						insertAccessLog(path, request, response);
					}
				}
			}
		}
	}

	private void insertAccessLog(String path, HttpServletRequest request, HttpServletResponse response) {
		User userInfo = UserUtil.getUser();
		if (userInfo == null) {
			return;
		}
		String userCode = userInfo.getUserCode();
		String branCode = userInfo.getBranCode();
		String deputyUserCode = userInfo.getDeputyUserCode();

		String ipAddress = IpUtil.getClientIp();

		AdmUserAccessLogs accessLog = new AdmUserAccessLogs();
		accessLog.setBranCode(branCode);
		accessLog.setUserCode(userCode);
		accessLog.setDeputyUserCode(deputyUserCode);
		accessLog.setProgClassName(path);
		accessLog.setMethodName("execute");
		accessLog.setRemoteIp(ipAddress);
		accessLog.setLogDt(new Date());
		accessLog.setCreateBy(userCode);
		admUserAccessLogsBaseDao.insert(accessLog);
	}
}
