/*
 * Copyright (c) 2022 -Parker.
 * All rights reserved.
 */
package com.project.frame.model;

import java.io.Serializable;
import lombok.Data;

/**
 * API 監控資訊
 *
 * @author Parker Huang
 * @since 1.0.0
 */
@Data
public class RestTracer implements Serializable {

	private static final long serialVersionUID = 1L;

	private String url;

	private Object request;

	private Object response;
}
