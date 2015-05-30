package com.whatistics.backend.mail;

import javax.mail.Message;
import java.io.InputStream;
import java.util.List;

/**
 * @author robert
 */
public interface MailAdapterService {
    void fetchMails();

    Message[] getMails();

    List<InputStream> getAttachments(Message message);

    void sendMail(String[] to, String subject, String text);

    void closeInbox();

    long getUID(Message message);
}
