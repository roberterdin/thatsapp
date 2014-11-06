package main;

import java.io.BufferedReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import mail.MailAdapter;
import mongodb.MongoDB;

public class Run {
	private static final String DB_NAME = "whatistics";
	private static final String HOST = "imap.gmail.com";
	private static final String EMAIL = "whatistics@gmail.com";
	private static final String PASSWORD = "#uu^b4{fe-XS-!Z";

	private static MongoDB mDB;
	private static UUID uuid;
	private static List<WhatsAppMessage> mlist = new ArrayList<WhatsAppMessage>();

	public static void main(String[] args) throws MessagingException {
		mDB = new MongoDB(DB_NAME);

		MailAdapter mr = new MailAdapter(HOST, EMAIL, PASSWORD);

		mr.fetchMails();
		for (int i = 0; i < mr.getMails().length; i++) {
			uuid = UUID.randomUUID();
			boolean isWhatsapp = false;
			Message message = mr.getMails()[i];
			System.out.println("MESSAGE #" + (i + 1) + ":");
			mr.printEnvelope(message);

			for (InputStream inputstream : mr.getAttachments(message)) {

				try {
					String currentLine;
					BufferedReader br = new BufferedReader(new InputStreamReader(inputstream));

					while ((currentLine = br.readLine()) != null) {

						if (!currentLine.equals("")) {
							if (isNewLine(currentLine)) {

								int BUFFER_SIZE = 1000;
								br.mark(BUFFER_SIZE);
								isWhatsapp = true;
								WhatsAppMessage waMessage = new WhatsAppMessage(uuid, currentLine);
								
								System.out.println(waMessage.getFormatedDate());
								String nextLine = br.readLine();
								if (!isNewLine(nextLine)) {
									
									waMessage.setText(waMessage.getText() +" " + nextLine);
								}
								mlist.add(waMessage);
								mDB.insertDoc("message", waMessage.getDBObject());
								br.reset();
							} 
						}
					}
				} catch (IOException e) {
					System.err.println("Error: " + e);
				}
			}
			if (isWhatsapp) {
				String text = "Here is you link for Whatistics: " + uuid;
				String from = ((InternetAddress) message.getFrom()[0]).getAddress();
				String[] to = { from };
				mr.sendMail(to, "Whatistics", text);
			} else {
				System.out.println("Error Reading File");
			}
		}

		mr.closeInbox();
		System.out.println(mlist.size());
	}

	static public boolean isNewLine(String line) {
		if (line != null && !line.equals("")) {
			return ((String) line.subSequence(0, 1)).matches("[-+]?\\d*\\.?\\d+");
		} else {
			return false;
		}
	}

	static public void saveinDB(String date, String author, String text) {
		DBObject doc = new BasicDBObject().append("chat_id", uuid).append("date", date).append("author", author)
				.append("message", text);

		mDB.insertDoc("message", doc);
	}
}
