/*
 * Copyright (c) 2021 -Parker.
 * All rights reserved.
 */
package com.frame.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * System process result status code.
 *
 * @author Parker Huang
 * @since 1.0.0
 */
@AllArgsConstructor
public enum ReturnCode implements ReturnStatus {

	SUCCESS("0000"),
	UNAUTHORIZED("9998"),
	EXCEPTION("9999");

	@Getter
	private final String returnCode;

	@Override
	public String getReturnType() {
		return "FRAME";
	}
}
