package test;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.Test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.CalendarException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;

public class EmailListenerTest {

    private String IMAP_HOST = "mail.softbi.com";

    private int IMAP_PORT = 143;

    private String SENDER_EMAIL = "";

    private String SENDER_EMAIL_PASSWORD = "";

    @Test
    public void mailTest() {
        // Connect to the email account
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imap");
        properties.put("mail.imap.host", IMAP_HOST);
        properties.put("mail.imap.port", String.valueOf(IMAP_PORT));
        List<String> lastProcessedMessageIDs = new ArrayList<>(); 

        try {
            Session session = Session.getDefaultInstance(properties);
            Store store = session.getStore();
            store.connect(IMAP_HOST, IMAP_PORT, SENDER_EMAIL, SENDER_EMAIL_PASSWORD);

            // Open the inbox folder
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            FetchProfile fetchProfile = new FetchProfile();
            fetchProfile.add(FetchProfile.Item.ENVELOPE);
            fetchProfile.add(FetchProfile.Item.CONTENT_INFO);
            fetchProfile.add(FetchProfile.Item.FLAGS);
            fetchProfile.add(FetchProfile.Item.CONTENT_INFO);

            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.scheduleAtFixedRate(() -> {
                try {
                    // Check for new messages
                    inbox.fetch(new Message[] { inbox.getMessage(inbox.getMessageCount()) }, fetchProfile);

                    // Process the new messages
                    Message[] messages = inbox.getMessages();
                    for (Message message : messages) {
                        String messageID = ((MimeMessage) message).getMessageID();
                        
                        if (!lastProcessedMessageIDs.contains(messageID)) {
                            if (isMeetingResponse(message)) {
                                handleMeetingResponse(message);
                            }
                            lastProcessedMessageIDs.add(messageID);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, 0, 1, TimeUnit.MINUTES);

            // Keep the program running
            while (true) {
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isMeetingResponse(Message message) throws MessagingException {
        // Check if the message is a meeting response
        String subject = message.getSubject();
        return subject != null && (subject.startsWith("Accepted:") || subject.startsWith("Declined:"));
    }

    private void handleMeetingResponse(Message message) throws Exception {
        MailResponse mailResponse = extractResponse(message);
        // Store the event response in the database
        saveEventResponseToDatabase(mailResponse);

        // Mark the message as read
        message.setFlag(Flags.Flag.SEEN, true);
    }

    private MailResponse extractResponse(Message message) throws Exception {
        String subject = message.getSubject();
        Address[] fromAddresses = message.getFrom();
        String sender = fromAddresses[0].toString();
        Address[] toAddresses = message.getRecipients(Message.RecipientType.TO);
        String recipient = toAddresses[0].toString();
        Date sentDate = message.getSentDate();

        Object content = message.getContent();
        String mailContent = null;
        message.isMimeType("");
        
        if (content instanceof Multipart) {
            Multipart multipart = (Multipart) content;
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                
                if (bodyPart.getContentType().contains("text/calendar") || bodyPart.isMimeType("text/calendar")) {
                    // 郵件內容中的 iCalendar 數據
                    String iCalendarContent = bodyPart.getContent().toString();
                    CalendarBuilder calendarBuilder = new CalendarBuilder();
                    Calendar calendar = calendarBuilder.build(new StringReader(iCalendarContent));

                    // 遍歷 iCalendar 組件
                    for (Component component : calendar.getComponents()) {
                        if (component.getName().equals("VEVENT")) {
                            // 處理事件組件
                            Optional<Property> summary = component.getProperty(Property.SUMMARY);
                            Optional<Property> startDate = component.getProperty(Property.DTSTART);
                            Optional<Property> endDate = component.getProperty(Property.DTEND);

                            String eventSummary = summary.isPresent()? summary.get().getValue() : null;
                            String eventStartDate =startDate.isPresent()? startDate.get().getValue() : null;
                            String eventEndDate = endDate.isPresent()? endDate.get().getValue(): null;
                        }
                    }
                    // TODO 進一步處理 iCalendar 數據
                } else {
                    // TODO 其他郵件內容部分，根據需要進行處理
                }
            }
        } else if(message.isMimeType("text/calendar")) {
            InputStream inputStream = (InputStream) content;
            // 解析 iCalendar 数据
            CalendarBuilder calendarBuilder = new CalendarBuilder();
            try {
                Calendar calendar = calendarBuilder.build(inputStream);

                // 遍歷 iCalendar 組件
                for (Component component : calendar.getComponents()) {
                    if (component.getName().equals("VEVENT")) {
                        // 處理事件組件
                        Optional<Property> summary = component.getProperty(Property.SUMMARY);
                        Optional<Property> startDate = component.getProperty(Property.DTSTART);
                        Optional<Property> endDate = component.getProperty(Property.DTEND);

                        String eventSummary = summary.isPresent()? summary.get().getValue() : null;
                        String eventStartDate =startDate.isPresent()? startDate.get().getValue() : null;
                        String eventEndDate = endDate.isPresent()? endDate.get().getValue(): null;

                        // 根據需要處理事件組件的其他屬性
                        System.out.println("eventSummary == "+ eventSummary);
                        System.out.println("eventStartDate == "+ eventStartDate);
                        System.out.println("eventEndDate == "+ eventEndDate);
                        // ...
                    }
                }
            } catch (CalendarException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } 
        else if (content instanceof String) {
            // 郵件內容為純文本
            mailContent = (String) content;
            // 進一步處理文本內容
        } else {
            System.out.println("Unknow content type!!");
        }

        MailResponse mailResponse = new MailResponse(subject, fromAddresses, sender, toAddresses, recipient, sentDate,
                mailContent);
        return mailResponse;
    }

    private void saveEventResponseToDatabase(MailResponse mailResponse) throws SQLException {
        //TODO 資料存儲
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public class MailResponse {

        private String subject;

        private Address[] fromAddresses;

        private String sender;

        private Address[] toAddresses;

        private String recipient;

        private Date sentDate;

        private String mailContent;
    }
}
