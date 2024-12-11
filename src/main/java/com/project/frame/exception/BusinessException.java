/*
 * Copyright (c) 2018 -Parker.
 * All rights reserved.
 */
package com.project.frame.exception;

import com.project.frame.model.enums.ReturnStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 系統自訂義錯誤
 *
 * @author Parker Huang
 * @since 1.0.0
 */
public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private BusinessReturnStatus returnStatus;

	public BusinessException(ReturnStatus returnStatus, Object... args) {
		super(returnStatus.getReturnDesc(args), null, false, false);
		this.returnStatus = new BusinessReturnStatus(returnStatus.getReturnCode(), returnStatus.getReturnType(), returnStatus.getReturnDesc(args));
	}

	public BusinessException(String returnCode, String returnType, String returnDesc) {
		super(returnDesc, null, false, false);
		this.returnStatus = new BusinessReturnStatus(returnCode, returnType, returnDesc);
	}

	@AllArgsConstructor
	@Data
	public static class BusinessReturnStatus implements ReturnStatus {

		private String returnCode;

		private String returnType;

		private String returnDesc;
	}
}
