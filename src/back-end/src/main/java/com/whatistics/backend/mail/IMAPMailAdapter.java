package com.whatistics.backend.mail;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.util.MailSSLSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * @author moritz, robert
 */
@Singleton
public class IMAPMailAdapter implements MailAdapter {

    final Logger logger = LoggerFactory.getLogger(IMAPMailAdapter.class);

    private final String imapHostName;
    private final String smtpHostName;
    private final String mailDomain;
    private final String email;
    private final String pass;
    private final String inboxName;
    private final String processedFolder;
    private final String unprocessableFolder;


    private final Map<String, IMAPFolder> folders = new HashMap<>();

    private Session session;
    private Store store;

    private Message messages[] = {};
    private Transport transport;

    private FetchProfile fetchProfile = new FetchProfile();


    @Inject
    public IMAPMailAdapter(@Named("email") String email,
                           @Named("smtpHostName") String smtpHostName,
                           @Named("imapHostName") String imapHostName,
                           @Named("password") String pass,
                           @Named("inboxName") String inboxName,
                           @Named("processedFolder") String processedFolder,
                           @Named("unprocessableFolder") String unprocessableFolder,
                           @Named("mailDomain") String mailDomain){
        this.smtpHostName = smtpHostName;
        this.imapHostName = imapHostName;
        this.mailDomain = mailDomain;
        this.email = email + "@" + mailDomain;
        this.pass = pass;
        this.inboxName = inboxName;
        this.processedFolder = processedFolder;
        this.unprocessableFolder = unprocessableFolder;

        this.fetchProfile.add(FetchProfile.Item.ENVELOPE);
        this.fetchProfile.add(FetchProfile.Item.CONTENT_INFO);
        this.fetchProfile.add(UIDFolder.FetchProfileItem.UID);
    }

    /**
     * Connect to the IMAP and SMTP server and open folders.
     */
    public void connectToServer() {
        logger.debug("trying to connect to mail server...");

        /* Set the mail properties */
        Properties props = System.getProperties();
        props.setProperty("mail.mime.decodefilename", "true");
        props.setProperty("mail.store.protocol", "imap");
        props.setProperty("mail.imap.starttls.enable", "true");
        props.setProperty("mail.imap.port", "143");
        props.setProperty("mail.smtp.starttls.enable", "true");
        props.setProperty("mail.smtp.port", "587");

        MailSSLSocketFactory sf = null;
        try {
            sf = new MailSSLSocketFactory();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        sf.setTrustAllHosts(true);
        props.put("mail.imap.ssl.trust", "*");
        props.put("mail.imap.ssl.socketFactory", sf);
        props.put("mail.smtp.ssl.trust", "*");
        props.put("mail.smtp.socketFactory.port", "587");
        props.put("mail.smtp.ssl.socketFactory", sf);


        try {
            /* Create the session and get the store for read the mail. */
            session = Session.getDefaultInstance(props, null);
            store = session.getStore("imap");
            store.connect(imapHostName, email, pass);

            this.openFolder(inboxName);
            this.openFolder(processedFolder);
            this.openFolder(unprocessableFolder);

            transport = session.getTransport("smtp");
            transport.connect(smtpHostName, email, pass);

            logger.debug("... connected to mail server...");

        } catch (NoSuchProviderException e) {
            logger.error("Can't connect to server", e);
        } catch (MessagingException e) {
            logger.error("Can't connect to server", e);
        }

        // set all read messages in the inbox to unread in case there were leftovers from the last session
        try {
            messages = folders.get(inboxName).search(new FlagTerm(new Flags(Flags.Flag.SEEN), true));

            folders.get(inboxName).fetch(messages, this.fetchProfile);

            // flag as unseen
            folders.get(inboxName).setFlags(messages, new Flags(Flags.Flag.SEEN), false);

            logger.debug("setting all read, unprocessed messages to unread");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void fetchMails() {
        if(folders.get(inboxName).isOpen()){
            try {
                messages = folders.get(inboxName).search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
                folders.get(inboxName).fetch(messages, fetchProfile);

                // flag as seen
                folders.get(inboxName).setFlags(messages, new Flags(Flags.Flag.SEEN), true);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            logger.info("Inbox not open, trying to reconnect...");
            connectToServer();
            if(folders.get(inboxName).isOpen()){
                logger.info("Reconnect successful, fetching mails");
                fetchMails();
            }else{
                logger.error("Reconnect failed, inbox still not open.");
            }
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
    /**
     * Uses the same address as is to retrieve the incoming mails
     */
    public void sendMail(String[] to, String subject, String text) {
        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(email, "ThatsApp"));

            for (int i = 0; i < to.length; i++) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to[i]));
            }

            message.setSubject(subject);
            message.setText(text);

            if( !transport.isConnected()){
                logger.info("Connection interrupted, trying to reconnect to server...");
                connectToServer();
            }

            transport.sendMessage(message, message.getAllRecipients());

        } catch (MessagingException e) {
            logger.error("Error sending mail", e);
        } catch (UnsupportedEncodingException e) {
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
        if(!folders.containsKey(destFolder) || !folders.get(destFolder).isOpen()){
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
            logger.error("Error moving mail from " + sourceFolder + " to " + destFolder, e);
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
            return folders.get(inboxName).getUID(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private IMAPFolder openFolder(String folderName) throws MessagingException {
        // check if folder exists
        if (!store.getFolder(folderName).exists()){
            // try to create it
            Folder newFolder = store.getDefaultFolder().getFolder(folderName);
            newFolder.create(Folder.HOLDS_MESSAGES);
        }

        folders.put(folderName, (IMAPFolder) store.getFolder(folderName));


        // Open the folder using store.
        folders.get(folderName).open(Folder.READ_WRITE);
        return folders.get(folderName);
    }
}
