package com.whatistics.backend.mail;

import javax.mail.Message;
import java.io.InputStream;
import java.util.List;

/**
 * @author robert
 */
public interface MailAdapter {
    void fetchMails();

    /**
     * Returns all fetched messages. No order is guaranteed.
     * If this becomes an issue, check out the
     * <a href="https://javamail.java.net/nonav/docs/api/com/sun/mail/imap/IMAPFolder.html#getSortedMessages(com.sun.mail.imap.SortTerm[],%20javax.mail.search.SearchTerm)">getSortedMessages</a>
     * interface.
     */
    Message[] getMails();

    void connectToServer();

    void sendMail(String[] to, String subject, String text);

    void closeOpenFolders();

    void moveToFolder(Message message, String sourceFolder, String destFolder);

    long getUID(Message message);
}
