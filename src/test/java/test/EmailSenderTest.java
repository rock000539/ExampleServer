/*
 * Copyright (c) 2023 -Parker.
 * All rights reserved.
 */
package test;

import java.util.UUID;
import javax.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {com.bi.base.ApplicationMain.class})
public class EmailSenderTest {

	private String SENDER_EMAIL = "parker.huang@softbi.com";

	private String SMTP_HOST = "smtp.gmail.com";

	private String SMTP_PORT = "587";

	private String recipient_email = "parker.huang@softbi.com";

	@Autowired
	private JavaMailSender mailSender;

	@Test
	public void testMailSender() {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setFrom(SENDER_EMAIL);
			helper.setTo(recipient_email);
			helper.setSubject("Meeting Invitation");

			// 设置会议内容
			String uid = UUID.randomUUID().toString();
			// 創建iCalendar文件内容
			String meetingContent = "BEGIN:VCALENDAR\n"
					+ "PRODID:-//Microsoft Corporation//Outlook 16.0 MIMEDIR//EN\n"
					+ "VERSION:2.0\n"
					+ "METHOD:REQUEST\n"
					+ "BEGIN:VEVENT\n"
					+ "ATTENDEE;CN=Recipient Name;RSVP=TRUE:mailto:"
					+ recipient_email
					+ "\n"
					+ "ORGANIZER;CN=Your Name:mailto:"
					+ SENDER_EMAIL
					+ "\n"
					+ "DTSTART:20230726T090000Z\n"
					+ "DTEND:20230726T100000Z\n"
					+ "LOCATION:Meeting Room\n"
					+ "UID:"
					+ uid
					+ "\n"
					+ "SEQUENCE:0\n"
					+ "PRIORITY:5\n"
					+ "CLASS:PUBLIC\n"
					+ "DESCRIPTION:This is a meeting invitation.\n\n"
					+ "Please confirm your attendance.\n"
					+ "SUMMARY:Meeting\n"
					+ "TRANSP:OPAQUE\n"
					+ "BEGIN:VALARM\n"
					+ "TRIGGER:-PT15M\n"
					+ "ACTION:DISPLAY\n"
					+ "DESCRIPTION:Reminder\n"
					+ "END:VALARM\n"
					+ "END:VEVENT\n"
					+ "END:VCALENDAR";

			// 设置会议内容类型和附件
			helper.setText(meetingContent, true);
			helper.getMimeMessage().addHeader("Content-Class", "urn:content-classes:calendarmessage");

			helper.setText(meetingContent);
			// 发送会议通知
			mailSender.send(message);

			System.out.println("Meeting invitation sent successfully.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
