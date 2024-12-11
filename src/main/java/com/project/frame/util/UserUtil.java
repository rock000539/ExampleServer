/*
 * Copyright (c) 2021 -Parker.
 * All rights reserved.
 */
package com.project.frame.util;

import com.project.frame.model.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Session User 取得工具
 *
 * @author Parker Huang
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserUtil {

	public static User getUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication != null ? authentication.getPrincipal() : null;
		return principal instanceof User ? (User) principal : null;
	}
}
