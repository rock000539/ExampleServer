/*
 * Copyright (c) 2021 -Parker.
 * All rights reserved.
 */
package com.frame.web.model;

import com.bi.base.web.model.ResultEntity;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.frame.model.RestTracer;
import com.frame.model.SqlTracer;
import com.frame.model.enums.ReturnStatus;
import java.util.List;
import lombok.Data;

/**
 * API 資訊顯示
 *
 * @author Parker Huang
 * @since 1.0.0
 */
@Data
public class ApiResultEntity extends ResultEntity {

	private static final long serialVersionUID = 1L;

	@JsonUnwrapped
	private ReturnStatus returnStatus;

	private List<SqlTracer> sqlTracer;

	private List<RestTracer> restTracer;
}
