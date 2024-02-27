/*
 * Copyright (c) 2024 -Parker.
 * All rights reserved.
 */
package com.frame.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Configuration;

// 排除自动加载的配置
// @Profile("!prod") // 也可以指定啟動哪個環境是目前配置生效
@Configuration
@ConditionalOnProperty(value = "spring.redis.enabled", havingValue = "false")
@EnableAutoConfiguration(exclude = {RedisAutoConfiguration.class})
@Slf4j
public class ExcludeAutoConfiguration {}
