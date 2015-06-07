package com.whatistics.backend.mail;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.sun.mail.imap.IMAPFolder;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

@Singleton
public class IMAPMailAdapter implements MailAdapter {

    private String host;
    private String email;
    private String pass;


    IMAPFolder inbox;
    Folder processed;

    Session session;
    Store store;

    Message messages[]={};
    Transport transport;



    @Inject
    public IMAPMailAdapter(@Named("host") String host, @Named("email") String email, @Named("pass") String pass) {
        this.host = host;
        this.email = email;
        this.pass = pass;
    }

    /**
     * Connect to the IMAP server and open folders.
     */
    public void connectToServer(){

        /* Set the mail properties */
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");

        try {
			/* Create the session and get the store for read the mail. */
            session = Session.getDefaultInstance(props, null);
            store = session.getStore("imaps");
            store.connect(host, email, pass);

			/* Mention the folder name which you want to read. */
            inbox = (IMAPFolder)store.getFolder("Inbox");
            System.out.println("Nr. of Unread Messages : " + inbox.getUnreadMessageCount());
			/* Open the inbox using store. */
            inbox.open(Folder.READ_WRITE);

            transport = session.getTransport("smtps");
            transport.connect(host, email, pass);

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.exit(2);
        }

        // set all read messages in the inbox to unread in case there were leftovers from the last session
        try {
            messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), true));
			/* Use a suitable FetchProfile */
            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            fp.add(FetchProfile.Item.CONTENT_INFO);
            fp.add(UIDFolder.FetchProfileItem.UID);
            inbox.fetch(messages, fp);

            // flag as seen
            inbox.setFlags(messages, new Flags(Flags.Flag.SEEN), false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void fetchMails() {
		/* Get the messages which is unread in the Inbox */
        try {
            messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
			/* Use a suitable FetchProfile */
            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
            fp.add(FetchProfile.Item.CONTENT_INFO);
            fp.add(UIDFolder.FetchProfileItem.UID);
            inbox.fetch(messages, fp);

            // flag as seen
            inbox.setFlags(messages, new Flags(Flags.Flag.SEEN), true);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    @Override
    public Message[] getMails() {
        return messages;
    }


    public void printAllMessages(Message[] msgs) throws Exception {
        for (int i = 0; i < msgs.length; i++) {
            System.out.println("MESSAGE #" + (i + 1) + ":");
            printEnvelope(msgs[i]);
        }
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
            message.setFrom(new InternetAddress("ffjfj@gmail.com"));
            InternetAddress[] toAddress = new InternetAddress[1];

            // To get the array of addresses
            for (int i = 0; i < to.length; i++) {
                toAddress[i] = new InternetAddress("scmo@zhaw.ch");
            }

            for (int i = 0; i < toAddress.length; i++) {
                message.addRecipient(Message.RecipientType.TO, toAddress[i]);
            }

            message.setSubject(subject);
            message.setText(text);

            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    @Override
    public void closeInbox() {
        try {
            inbox.close(true);
            store.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    /**
     * Return UID. According to RFC3501: Unique identifiers
     * are assigned in a strictly ascending fashion in the mailbox; as each
     * message is added to the mailbox it is assigned a higher UID than the
     * message(s) which were added previously.
     * @param message
     * @return UID value
     */
    @Override
    public long getUID(Message message){
        try {
            return inbox.getUID(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            return -1;
        }
    }
}