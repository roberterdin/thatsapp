package com.whatistics.backend.parser;

import com.whatistics.backend.mail.MailUtilities;
import com.whatistics.backend.model.Conversation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * @author robert
 */
public class ParserWorker implements Runnable {
    final Logger logger = LoggerFactory.getLogger(ParserWorker.class);

    private List<TimeFormat> timeFormats;

    private Message eMailMessage;
    private TimeFormat currentTimeFormat;


    public ParserWorker(Message eMailMessage, List<TimeFormat> timeFormats){
        this.eMailMessage = eMailMessage;
        this.timeFormats = timeFormats;

        try {
            logger.debug("Parser runnable created for message: " + ((InternetAddress)eMailMessage.getFrom()[0]).getAddress() + " writes " + eMailMessage.getSubject());
        } catch (MessagingException e) {
            logger.debug("Parser runnable created for message", eMailMessage);
        }
    }

    @Override
    public void run() {
        List<InputStream> attachments = MailUtilities.getAttachments(eMailMessage);

        BufferedReader reader = new BufferedReader(new InputStreamReader(attachments.get(0)));
        String currentLine;
        Conversation conversation = new Conversation();
        com.whatistics.backend.model.Message prevMessage = new com.whatistics.backend.model.Message();
        try {

            while ((currentLine = reader.readLine()) != null){
                com.whatistics.backend.model.Message message = new com.whatistics.backend.model.Message();

                // skip newlines
                if(currentLine.equals(""))
                    continue;

                // figure out current time format, if necessary
                if(currentTimeFormat == null | getDate(currentLine) == null)
                    // we need to figure out current time format
                    getTimeFormat(currentLine);

                LocalDateTime dateTime = getDate(currentLine);

                if (dateTime == null){
                    // add to previous message
                    prevMessage.setContent(prevMessage.getContent() + currentLine);
                }else {
                    // create new messaeg
                    message.setSendDate(dateTime);
                    message.setContent(currentLine.substring(currentTimeFormat.getLength()));
                }

                System.out.println(currentLine);

            }
        } catch (IOException e) {
            logger.error("Error while reading attachment", e);
            e.printStackTrace();
        }

    }

    private TimeFormat getTimeFormat(String line){

        String possibleDate;
        LocalDateTime dateTime = null;

        for(TimeFormat timeFormat : timeFormats){

            possibleDate = line.substring(0, timeFormat.getLength() - 1);

            try {
                dateTime = LocalDateTime.parse(possibleDate, timeFormat.asDateTimeFormatter());
            }catch (DateTimeParseException e){
                // can be ignored
            }
            if (dateTime != null)
                return timeFormat;
        }

        logger.error("Line can't be parsed: " + line);
        return null;
    }

    private LocalDateTime getDate(String line){
        return LocalDateTime.parse(line, currentTimeFormat.asDateTimeFormatter());
    }
}
