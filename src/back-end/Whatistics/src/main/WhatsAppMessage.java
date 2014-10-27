package main;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class WhatsAppMessage {
	private UUID uuid;
	private String rawMessage = "";
	private String text = "";
	private String author = "";
	private Date date;
	private boolean infomessage = false;
	private SimpleDateFormat usedDateFormat;

	public SimpleDateFormat getUsedDateFormat() {
		return usedDateFormat;
	}

	public WhatsAppMessage(UUID uuid, String rawString) {
		this.uuid = uuid;
		this.rawMessage = rawString;
		this.splitDateFromRawMessage(rawString);

	}

	public String getRawMessage() {
		return rawMessage;
	}

	public void setRawMessage(String rawMessage) {
		this.rawMessage = rawMessage;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Date getDate() {
		return date;
	}

	public String getFormatedDate() {
		DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		return df.format(this.date);
	}

	public DBObject getDBObject() {
		DBObject doc = new BasicDBObject().append("chat_id", this.uuid).append("date", this.getFormatedDate())
				.append("author", this.author).append("message", this.text);
		return doc;
	}

	public void setDate(String date) {
		this.date = tryParse(date);

		if (tryParse(date) != null) {

			String authorText = this.rawMessage.substring((date.length() + 1), (this.rawMessage.length())).trim();
			if (authorText.indexOf(": ") == -1) {
				this.setText(authorText);
				this.infomessage = true;
			} else {
				this.setAuthor(authorText.substring(0, (authorText.indexOf(": "))).trim());
				this.setText(authorText.substring((authorText.indexOf(": ") + 1), authorText.length()).trim());
			}
		} else {
			System.out.println("Date " + date + " not defined");
		}

	}

	public Date tryParse(String dateString) {
		String[] formats = { "HH:mma, MMM dd yy", "dd.MM.yy HH:mm:ss", "dd. MMM yyyy HH:mm", "dd. MMM., HH:mm",
				"dd MMM yyyy HH:mm", "dd. MMM. HH:mm", "HH:mma, MMM dd" };
		for (String format : formats) {
			try {
					this.usedDateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
				return this.usedDateFormat.parse(dateString);
			} catch (ParseException e) {
			}
			try {
				this.usedDateFormat = new SimpleDateFormat(format, Locale.GERMAN);
				return this.usedDateFormat.parse(dateString);
			} catch (ParseException e) {
			}

		}
		return null;
	}

	public void splitDateFromRawMessage(String rawMessage) {
		String[] firstSplit = rawMessage.split("- ");
		if (firstSplit[0].length() > 20) {
			String[] secondSplit = rawMessage.split(": ");
			this.setDate(secondSplit[0]);
		} else {
			this.setDate(firstSplit[0]);
		}

	}
}
