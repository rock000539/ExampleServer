/*
 * Copyright (c) 2023 -Parker.
 * All rights reserved.
 */
package com.frame.config;

import com.bi.base.web.model.ResultEntity;
import com.frame.web.model.ApiResultEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 錯誤訊息設定
 *
 * @author Parker Huang
 * @since 1.0.0
 */
@Configuration
public class WebConfig {

	@Bean
	public Class<? extends ResultEntity> resultEntity() {
		return ApiResultEntity.class;
	}
}
