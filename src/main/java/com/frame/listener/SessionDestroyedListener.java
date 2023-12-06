/*
 * Copyright (c) 2023 -Parker.
 * All rights reserved.
 */
package com.frame.listener;

import com.frame.model.User;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.stereotype.Component;

/**
 * Session 事件傾聽器定義
 *
 * @author Raven
 * @since 1.0.0
 */
@Slf4j
@Component
public class SessionDestroyedListener implements ApplicationListener<SessionDestroyedEvent> {

	@Override
	public void onApplicationEvent(SessionDestroyedEvent event) {
		log.info("session destroyed:{}", event.getId());
		List<SecurityContext> securityContexts = event.getSecurityContexts();

		for (SecurityContext securityContext : securityContexts) {
			Authentication auth = securityContext.getAuthentication();

			if (auth == null || !(auth.getPrincipal() instanceof User)) {
				continue;
			}
			User user = (User) auth.getPrincipal();

			// user登出後客制事件
		}
	}
}
