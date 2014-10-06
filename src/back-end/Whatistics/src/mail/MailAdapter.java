package mail;

import java.io.*;
import java.util.*;

import javax.mail.*;
import javax.mail.Flags.Flag;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;

import org.apache.commons.lang3.StringUtils;

public class MailAdapter {
	Folder inbox;

	Session session;
	Store store;

	Message messages[]={};
	Transport transport;
	public MailAdapter(String host, String email, String pass) {
		/* Set the mail properties */
		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");
		try {
			/* Create the session and get the store for read the mail. */
			session = Session.getDefaultInstance(props, null);
			store = session.getStore("imaps");
			store.connect(host, email, pass);

			/* Mention the folder name which you want to read. */
			inbox = store.getFolder("Inbox");
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
	}

	public void fetchMails() {
		/* Get the messages which is unread in the Inbox */
		try {
			messages = inbox.search(new FlagTerm(new Flags(Flag.SEEN), false));
			/* Use a suitable FetchProfile */
			FetchProfile fp = new FetchProfile();
			fp.add(FetchProfile.Item.ENVELOPE);
			fp.add(FetchProfile.Item.CONTENT_INFO);
			inbox.fetch(messages, fp);

			// flag as seen
			inbox.setFlags(messages, new Flags(Flags.Flag.SEEN), true);
			// printAllMessages(messages);

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

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

	public List<InputStream> getAttachments(Message message) {
		Object content;
		try {
			content = message.getContent();
			if (content instanceof String) {
				return null;
			}
			if (content instanceof Multipart) {
				Multipart multipart = (Multipart) content;
				List<InputStream> result = new ArrayList<InputStream>();
				for (int i = 0; i < multipart.getCount(); i++) {
					result.addAll(getAttachments(multipart.getBodyPart(i)));
				}
				return result;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	private List<InputStream> getAttachments(BodyPart part) throws Exception {
		List<InputStream> result = new ArrayList<InputStream>();
		Object content = part.getContent();
		if (content instanceof InputStream || content instanceof String) {
			if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()) || StringUtils.isNotBlank(part.getFileName())) {
				result.add(part.getInputStream());
				return result;
			} else {
				return new ArrayList<InputStream>();
			}
		}

		if (content instanceof Multipart) {
			Multipart multipart = (Multipart) content;
			for (int i = 0; i < multipart.getCount(); i++) {
				BodyPart bodyPart = multipart.getBodyPart(i);
				result.addAll(getAttachments(bodyPart));
			}
		}
		return result;
	}

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

	public void closeInbox() {
		try {
			inbox.close(true);
			store.close();
		} catch (MessagingException e) {
			e.printStackTrace();
		}

	}
}