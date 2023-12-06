/*
 * Copyright (c) 2023 -Parker.
 * All rights reserved.
 */
package com.frame.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * App 啟動傾聽器定義
 *
 * @author Parker
 * @since 1.0.0
 */
@Slf4j
@Component
public class ApplicationReadyEventListener implements ApplicationListener<ApplicationReadyEvent> {

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		// AP 啟動時，自訂事件
	}
}
