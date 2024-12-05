/*
 * Copyright (c) 2022 -Parker.
 * All rights reserved.
 */
package com.frame.handler;

import com.bi.base.database.annotation.BaseAutowired;
import com.bi.base.database.service.BaseService;
import com.bi.base.database.service.BaseSpService;
import com.exampleServer.adm.AdmErrorLog;
import com.exampleServer.adm.SpGetSerialNo;
import com.exampleServer.adm.enums.SerialName;
import com.frame.exception.BusinessException;
import com.frame.model.User;
import com.frame.util.IpUtil;
import com.frame.util.UserUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author Parker Huang
 * @since 1.0.0
 */
@ControllerAdvice
public class ExceptionHandler extends ResponseEntityExceptionHandler {

	@Autowired
	private HttpServletRequest httpServletRequest;

	@BaseAutowired
	private BaseService<AdmErrorLog> admErrorLogBaseService;

	@BaseAutowired
	private BaseSpService<SpGetSerialNo> spGetSerialNoBaseSpService;

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
		if (HttpStatus.INTERNAL_SERVER_ERROR.equals(statusCode)) {
			loggingError(ex);
		}
		return super.handleExceptionInternal(ex, body, headers, statusCode, request);
	}

	@org.springframework.web.bind.annotation.ExceptionHandler({Exception.class})
	public void handlerException(Exception e) throws Exception {
		if (!(e instanceof BusinessException) && !(e instanceof AccessDeniedException)) {
			loggingError(e);
		}
		throw e;
	}

	private void loggingError(Exception e) {
		User user = UserUtil.getUser();
		SpGetSerialNo spGetSerialNo = SpGetSerialNo.builder().iSerialCode(SerialName.ERROR_ID.name()).build();
		String errorId = spGetSerialNoBaseSpService.execute(spGetSerialNo).getResults().get(0);
		AdmErrorLog admErrorLog = new AdmErrorLog();
		admErrorLog.setErrorId(errorId);
		admErrorLog.setErrorMessage(e.getMessage());
		admErrorLog.setErrorBody(ExceptionUtils.getStackTrace(e));
		admErrorLog.setHostIp(IpUtil.getClientIp());
		if (user != null) {
			admErrorLog.setLoginId(user.getLoginId());
		}
		admErrorLogBaseService.insert(admErrorLog);
	}
}
