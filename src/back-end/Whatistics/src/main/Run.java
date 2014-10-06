package main;

import java.io.BufferedReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

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
							saveMessage(currentLine);
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

	static public boolean checkLine(String line) {
		return true;
	}

	static public void saveMessage(String line) {
		String[] strings = splitMessage(line);
		saveinDB(strings[0], strings[1], strings[2]);
	}

	/*
	 * Param: String line from file
	 * 
	 * Return: String[0] = Date String[1] = Author String[2] = text
	 */
	static public String[] splitMessage(String line) {
		String author = "", text = "";
		Date date = Calendar.getInstance().getTime();
	
		DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

		if (tryParse(line.substring(0, 18), "dd.MM.yy HH:mm:ss") != null) {
			date = tryParse(line.substring(0, 18), "dd.MM.yy HH:mm:ss");
			String rest = line.substring(18, line.length());
			author = rest.substring(0, rest.indexOf(":")).trim(); // TODO does
																	// not work
																	// if name
																	// has a ":"
			text = rest.substring(rest.indexOf(":") + 1, rest.length()).trim();
		} else if (line.indexOf(" - ") > 0 && tryParse(line.substring(0, line.indexOf(" - ")), "HH:mma, MMM dd") != null) {
			date = tryParse(line.substring(0, line.indexOf(" - ")) +" 14", "HH:mma, MMM dd yy");
			String rest = line.substring(line.indexOf(" - "), line.length());
			author = rest.substring(rest.indexOf("-") + 1, rest.indexOf(":")).trim();
			text = rest.substring(rest.indexOf(": ") + 1, rest.length()).trim();
		}

		String[] strings = { df.format(date), author, text };
		return strings;
	}

	static public Date tryParse(String dateString, String formatString) {
		try {
			return new SimpleDateFormat(formatString).parse(dateString);
		} catch (ParseException e) {
		}
		return null;
	}

	static public void saveinDB(String date, String author, String text) {
		DBObject doc = new BasicDBObject().append("chat_id", uuid).append("date", date).append("author", author)
				.append("message", text);

		mDB.insertDoc("message", doc);
	}
}
