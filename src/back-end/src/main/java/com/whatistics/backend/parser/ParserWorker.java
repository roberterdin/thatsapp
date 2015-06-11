package com.whatistics.backend.parser;

import com.whatistics.backend.mail.MailUtilities;
import com.whatistics.backend.model.Conversation;
import com.whatistics.backend.model.Person;
import org.apache.commons.io.input.BOMInputStream;
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
import java.time.format.DateTimeFormatter;
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



        BufferedReader reader = new BufferedReader(new InputStreamReader(new BOMInputStream(attachments.get(0))));
        String currentLine;
        Conversation conversation = new Conversation();
        com.whatistics.backend.model.Message message = new com.whatistics.backend.model.Message();
        try {

            while ((currentLine = reader.readLine()) != null){

                // skip newlines
                if(currentLine.equals(""))
                    continue;

                // remove leading and trailing white spaces
                currentLine = currentLine.trim();

                // figure out current time format, if necessary
                if(currentTimeFormat == null | getDate(currentLine) == null){
                    // we need to figure out current time format
                    currentTimeFormat = getTimeFormat(currentLine);
                }


                LocalDateTime dateTime = getDate(currentLine);

                if (dateTime == null){
                    // add to previous message
                    message.setContent(message.getContent() + currentLine);
                }else {
                    // add old message to conversation:
                    conversation.getMessages().add(message);

                    // create new message;
                    message = new com.whatistics.backend.model.Message();

                    // remove date from message
                    currentLine = currentLine.substring(currentTimeFormat.getLength());

                    // get sender
                    message.setSender(new Person(currentLine.split(":", 2)[0]));

                    message.setSendDate(dateTime);
                    message.setContent(currentLine.split(":", 2)[1]);
                }

                System.out.println(currentLine);

            }

            // add last message to conversation
            conversation.getMessages().add(message);

            // clean up first empty message
            conversation.getMessages().remove(0);

        } catch (IOException e) {
            logger.error("Error while reading attachment", e);
            e.printStackTrace();
        }

    }

    private TimeFormat getTimeFormat(String line){

        String possibleDate;
        LocalDateTime dateTime = null;

        for(TimeFormat timeFormat : timeFormats){

            possibleDate = line.substring(0, timeFormat.getLength());

            try {
                dateTime = LocalDateTime.parse(possibleDate, timeFormat.asDateTimeFormatter());
            }catch (DateTimeParseException e){
                System.out.println(e);
                // can be ignored
            }
            if (dateTime != null)
                return timeFormat;
        }

        logger.error("Line can't be parsed: " + line);
        return null;
    }

    private LocalDateTime getDate(String line){
        if (currentTimeFormat != null) {
            String possibleDate = line.substring(0, currentTimeFormat.getLength());
            return LocalDateTime.parse(possibleDate, currentTimeFormat.asDateTimeFormatter());
        }
        return null;
    }
}
