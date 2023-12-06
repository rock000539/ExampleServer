package test;

import java.util.Properties;
import java.util.UUID;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import org.junit.jupiter.api.Test;

public class EmailICalendarTest {

	private String SMTP_HOST = "www.softbi.com";

	private String SMTP_PORT = "587";

	private String SENDER_EMAIL = "";

	private String SENDER_EMAIL_PASSWORD = "";

	private String recipient_email = "";

	@Test
	public void mailTest() {

		// 配置電子郵件發送參數
		Properties properties = new Properties();
		properties.put("mail.smtp.host", SMTP_HOST);
		properties.put("mail.smtp.port", SMTP_PORT);
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");

		try {
			Session session = Session.getInstance(properties, new Authenticator() {

				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(SENDER_EMAIL, SENDER_EMAIL_PASSWORD);
				}
			});

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(SENDER_EMAIL));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient_email));
			message.setSubject("Meeting Invitation");

			String uid = UUID.randomUUID().toString();
			// 創建iCalendar文件内容
			String iCalendarContent = "BEGIN:VCALENDAR\n"
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
                    + "UID:"+uid+"\n"
                    + " 000002FF5448EC190848BF9815D3F0100000000\n"
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

			// 創建正文
			MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setText("Please see the attached calendar invitation.");
			// 創建附件
			BodyPart calendarPart = new MimeBodyPart();
			calendarPart.setDataHandler(new DataHandler(new ByteArrayDataSource(iCalendarContent.getBytes(), "text/calendar;method=REQUEST;charset=\"UTF-8\"")));
			calendarPart.setHeader("Content-Class", "urn:content-classes:calendarmessage");
			calendarPart.setHeader("Content-Disposition", "inline; filename=meeting.ics"); // 設置自動添加到Outlook行事歷的標誌

			Multipart multipart = new MimeMultipart();
			//添加正文
			multipart.addBodyPart(mimeBodyPart);
			multipart.addBodyPart(calendarPart);
			message.setContent(multipart);
			message.saveChanges();

			// 發送會議通知
			Transport.send(message);

			System.out.println("Meeting invitation sent successfully.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
