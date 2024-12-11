/*
 * Copyright (c) 2022 -Parker.
 * All rights reserved.
 */
package com.project.integration.mail;

import java.util.List;

/**
 * @author Parker Huang
 * @since 1.0.0
 */
public interface MailService {

	/**
	 * 寄送信件
	 *
	 * @param subject
	 * @param content
	 * @param recipient
	 * @throws Exception
	 */
	void sendMail(String subject, String content, List<String> recipient) throws Exception;
}
