/*
 * Copyright (c) 2021 -Parker.
 * All rights reserved.
 */
package com.project.frame.model;

import java.io.Serializable;
import lombok.Data;

/**
 * SQL 監控資訊
 *
 * @author Parker Huang
 * @since 1.0.0
 */
@Data
public class SqlTracer implements Serializable {

	private static final long serialVersionUID = 1L;

	private String sql;

	private Object parameter;

	private Object data;
}
