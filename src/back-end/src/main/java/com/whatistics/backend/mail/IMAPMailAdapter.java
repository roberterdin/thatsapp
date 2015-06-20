package com.whatistics.backend.mail;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.sun.mail.imap.IMAPFolder;
import com.whatistics.backend.configuration.GlobalConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * @author moritz, robert
 * TODO: disconnect dependency from global configuration
 */
@Singleton
public class IMAPMailAdapter implements MailAdapter {

    final Logger logger = LoggerFactory.getLogger(IMAPMailAdapter.class);

    private String host;
    private String email;
    private String pass;


    private Map<String, IMAPFolder> folders;

    Session session;
    Store store;

    Message messages[] = {};
    Transport transport;


    @Inject
    public IMAPMailAdapter(@Named("host") String host, @Named("email") String email, @Named("pass") String pass) {
        this.host = host;
        this.email = email;
        this.pass = pass;
        this.folders = new HashMap<>();
    }

    /**
     * Connect to the IMAP server and open folders.
     */
    public void connectToServer() {
        logger.debug("trying to connect to mail server...");

        /* Set the mail properties */
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");

        try {
            /* Create the session and get the store for read the mail. */
            session = Session.getDefaultInstance(props, null);
            store = session.getStore("imaps");
            store.connect(host, email, pass);

            this.openFolder(GlobalConfig.INBOX_NAME);
            this.openFolder(GlobalConfig.PROCESSED_FOLDER);
            this.openFolder(GlobalConfig.UNPROCESSABLE_FOLDER);

            transport = session.getTransport("smtps");
            transport.connect(host, email, pass);

            logger.debug("... connected to mail server...");

        } catch (NoSuchProviderException e) {
            logger.error("Can't connect to server", e);
        } catch (MessagingException e) {
            logger.error("Can't connect to server", e);
        }

        // set all read messages in the inbox to unread in case there were leftovers from the last session
        try {
            messages = folders.get(GlobalConfig.INBOX_NAME).search(new FlagTerm(new Flags(Flags.Flag.SEEN), true));
			/* Use a suitable FetchProfile */
            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            fp.add(FetchProfile.Item.CONTENT_INFO);
            fp.add(UIDFolder.FetchProfileItem.UID);
            folders.get(GlobalConfig.INBOX_NAME).fetch(messages, fp);

            // flag as seen
            folders.get(GlobalConfig.INBOX_NAME).setFlags(messages, new Flags(Flags.Flag.SEEN), false);

            logger.debug("setting all read, unprocessed messages to unread");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void fetchMails() {
		/* Get the messages which is unread in the Inbox */
        try {
            messages = folders.get(GlobalConfig.INBOX_NAME).search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
			/* Use a suitable FetchProfile */
            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            fp.add(FetchProfile.Item.CONTENT_INFO);
            fp.add(UIDFolder.FetchProfileItem.UID);
            folders.get(GlobalConfig.INBOX_NAME).fetch(messages, fp);

            // flag as seen
            folders.get(GlobalConfig.INBOX_NAME).setFlags(messages, new Flags(Flags.Flag.SEEN), true);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    public Message[] getMails() {
        return messages;
    }


    /* Print the envelope(FromAddress,ReceivedDate,Subject) */
    public void printEnvelope(Message message) {
        Address[] a;
        // FROM
        try {
            if ((a = message.getFrom()) != null) {
                for (int j = 0; j < a.length; j++) {
                    System.out.println("FROM: " + a[j].toString());
                }
            }
            // TO
            if ((a = message.getRecipients(Message.RecipientType.TO)) != null) {
                for (int j = 0; j < a.length; j++) {
                    // System.out.println("TO: " + a[j].toString());
                }
            }
            String subject = message.getSubject();
            Date receivedDate = message.getReceivedDate();
            String content = message.getContent().toString();
            System.out.println("Subject : " + subject);
            System.out.println("Received Date : " + receivedDate.toString());
            System.out.println("Content : " + content);
            // getContent(message);
        } catch (MessagingException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void sendMail(String[] to, String subject, String text) {
        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress("whatistics@gmail.com"));

            for (int i = 0; i < to.length; i++) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to[i]));
            }

            message.setSubject(subject);
            message.setText(text);

            transport.sendMessage(message, message.getAllRecipients());
        } catch (MessagingException e) {
            logger.error("Error sending mail", e);
        }


    }

    @Override
    public void closeOpenFolders() {
        try {
            for(IMAPFolder folder : folders.values()){
                folder.close(true);
            }
            store.close();
            transport.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void moveToFolder(Message message, String sourceFolder, String destFolder) {
        if(!folders.containsKey(destFolder)){
            try {
                openFolder(destFolder);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }

        try {
            folders.get(destFolder).addMessages(new Message[]{message});

            message.setFlag(Flags.Flag.DELETED, true);
            folders.get(sourceFolder).expunge();
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    /**
     * Return UID. According to RFC3501: Unique identifiers
     * are assigned in a strictly ascending fashion in the mailbox; as each
     * message is added to the mailbox it is assigned a higher UID than the
     * message(s) which were added previously.
     *
     * @param message
     * @return UID value
     */
    @Override
    public long getUID(Message message) {
        try {
            return folders.get(GlobalConfig.INBOX_NAME).getUID(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private IMAPFolder openFolder(String folderName) throws MessagingException {
        /* Mention the folder name which you want to read. */
        folders.put(folderName, (IMAPFolder) store.getFolder(folderName));
        /* Open the inbox using store. */
        folders.get(folderName).open(Folder.READ_WRITE);
        return folders.get(folderName);
    }
}