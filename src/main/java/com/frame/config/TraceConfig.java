/*
 * Copyright (c) 2023 -Parker.
 * All rights reserved.
 */
package com.frame.config;

import com.frame.model.RestTracer;
import com.frame.model.SqlTracer;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

/**
 * API 資訊追蹤設定
 *
 * @author Parker Huang
 * @since 1.0.0
 */
@Configuration
public class TraceConfig {

	@RequestScope
	@Bean
	public List<SqlTracer> sqlTracers() {
		return new ArrayList<>();
	}

	@RequestScope
	@Bean
	public List<RestTracer> restTracers() {
		return new ArrayList<>();
	}
}
