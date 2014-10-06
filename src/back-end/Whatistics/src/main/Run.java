package main;

import java.io.BufferedReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang3.StringUtils;

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
					int j = 0;

					while ((currentLine = br.readLine()) != null) {

						if (j == 0) {
							isWhatsapp = checkLine(currentLine);
						}
						if (isWhatsapp && !currentLine.equals("")) {
							breakLine(currentLine);
						}
						j++;
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
	}

	/*
	 * checks if its a whatsapp message 1. first at chars are a date 2. contains
	 * at least 4 ":"
	 */
	static public boolean checkLine(String line) {
		System.out.println("?"+line);
		boolean isDate = isValidDate(line.trim().substring(0, 9));
		boolean colon = (2 < (line.length() - line.replace(":", "").length()));
		if (isDate && colon) {
			return true;
		} else {
			if (!isDate) {
				System.out.println("Error with date");
			}
			if (!colon) {
				System.out.println("Error with colon");
			}
			return false;
		}
	}

	// TODO check other dates
	static public boolean isValidDate(String inDate) {
		/*
		//inDate ="19.09.14";
		System.out.println(inDate);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
		dateFormat.setLenient(false);
		try {
			dateFormat.parse(inDate.trim());
		} catch (ParseException e) {
			return false;
		}*/
		return true;
	}

	static public void breakLine(String line) {
		String[] parts = line.split(":");

		String date = parts[0].split(" ")[0];
		String time = parts[0].split(" ")[1] + ":" + parts[1] + ":" + parts[2];
		String author = parts[3];
		String text = parts[4];
		for (int i = 4; i < parts.length; i++) {
			text = text + ":" + parts[i];
		}

		saveMessage(date + " " + time, author, text);
	}

	static public void saveMessage(String date, String author, String text) {
		DBObject doc = new BasicDBObject().append("chat_id", uuid).append("date", date).append("author", author)
				.append("message", text);

		mDB.insertDoc("message", doc);
	}
}
