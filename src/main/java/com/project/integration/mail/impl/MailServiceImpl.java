/*
 * Copyright (c) 2022 -Parker.
 * All rights reserved.
 */
package com.project.integration.mail.impl;

import com.bi.base.model.enums.YesNo;
import com.project.integration.mail.MailService;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * @author Parker Huang
 * @since 1.0.0
 */
@Service
public class MailServiceImpl implements MailService {

	@Value("${spring.mail.sender}")
	private String sender;

	@Autowired
	private JavaMailSender mailSender;

	@Override
	public void sendMail(String subject, String content, List<String> recipient) throws Exception {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
		helper.setFrom(sender, sender);
		helper.setTo(recipient.toArray(new String[recipient.size()]));
		helper.setSubject(subject);
		helper.setText(content, true);
		YesNo sentYn = YesNo.N;
		String failMessage = null;
		try {
			mailSender.send(mimeMessage);
		} catch (Exception e) {
			failMessage = e.getMessage();
			throw e;
		} finally {
			// loggingSendMail(sender, subject, content, recipient, sentYn, failMessage);
		}
	}
}
