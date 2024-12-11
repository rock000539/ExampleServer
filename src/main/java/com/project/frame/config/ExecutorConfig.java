/*
 * Copyright (c) 2022 -Parker.
 * All rights reserved.
 */
package com.project.frame.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author Parker Huang
 * @since 1.0.0
 */
@Configuration
@EnableAsync
@Slf4j
public class ExecutorConfig {

	@Value("${async.executor.thread.core_pool_size}")
	private int corePoolSize;

	@Value("${async.executor.thread.max_pool_size}")
	private int maxPoolSize;

	@Value("${async.executor.thread.queue_capacity}")
	private int queueCapacity;

	@Value("${async.executor.thread.name.prefix}")
	private String namePrefix;

	@Bean(name = "asyncServiceExecutor")
	public Executor asyncServiceExecutor() {
		log.info("Start asyncServiceExecutor");
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setQueueCapacity(queueCapacity);
		executor.setThreadNamePrefix(namePrefix);

		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executor.initialize();
		return executor;
	}
}
