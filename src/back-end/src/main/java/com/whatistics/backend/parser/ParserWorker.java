package com.whatistics.backend.parser;

import com.whatistics.backend.model.Conversation;
import com.whatistics.backend.model.Person;
import org.apache.commons.io.input.BOMInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author robert
 *         Receives an InputStream of the chat conversation file, parses it and creates a
 *         {@link Conversation} object which will then be stored in the database.
 *         Callable is used instead of Runnable to make testin easier.
 */
public class ParserWorker implements Callable<Conversation> {
    final Logger logger = LoggerFactory.getLogger(ParserWorker.class);

    private List<TimeFormat> timeFormats;
    private ParserService parserService;

    private InputStream inputStream;
    private TimeFormat currentTimeFormat;

    private boolean doubleDigitFlag = false;


    public ParserWorker(InputStream inputStream, List<TimeFormat> timeFormats, ParserService parserService) {
        this.inputStream = inputStream;
        this.timeFormats = timeFormats;
        this.parserService = parserService;
    }

    @Override
    public Conversation call() {

        // ignore Unicode BOM in input stream (breaks time parsing)
        BufferedReader reader = new BufferedReader(new InputStreamReader(new BOMInputStream(inputStream)));
        String currentLine;
        Conversation conversation = new Conversation();
        com.whatistics.backend.model.Message message = new com.whatistics.backend.model.Message();
        try {

            while ((currentLine = reader.readLine()) != null) {

                // skip newlines
                if (currentLine.equals(""))
                    continue;

                // remove leading and trailing white spaces
                currentLine = currentLine.trim();

                // figure out current time format, if necessary
                if (currentTimeFormat == null | getDate(currentLine) == null) {
                    // we need to figure out current time format
                    currentTimeFormat = getTimeFormat(currentLine);
                }


                LocalDateTime dateTime = getDate(currentLine);

                if (dateTime == null) {
                    // add to previous message
                    message.setContent(message.getContent() + System.lineSeparator() + currentLine);
                } else {
                    // add old message to conversation:
                    conversation.getMessages().add(message);

                    // create new message;
                    message = new com.whatistics.backend.model.Message();

                    // remove date from message
                    if (!doubleDigitFlag) {
                        currentLine = currentLine.substring(currentTimeFormat.getLength());
                    } else {
                        // date is one character longer due to double digits in
                        currentLine = currentLine.substring(currentTimeFormat.getLength() + 1);
                        this.doubleDigitFlag = false;
                    }

                    message.setSendDate(dateTime);

                    String[] senderAndContent = currentLine.split(":", 2);
                    if (senderAndContent.length == 2) {
                        // normal line
                        // get sender
                        message.setSender(new Person(senderAndContent[0]));
                        message.setContent(senderAndContent[1]);
                    } else if (senderAndContent.length == 1) {
                        // "system message"
                        message.setSender(new Person("_dummy"));
                        message.setContent(senderAndContent[0]);
                    }
                }

            }

            // add last message to conversation
            conversation.getMessages().add(message);

            // clean up first empty message
            conversation.getMessages().remove(0);

        } catch (IOException e) {
            logger.error("Error while reading attachment", e);
            e.printStackTrace();
        }


        return conversation;
    }

    private TimeFormat getTimeFormat(String line) {

        String possibleDate1;
        String possibleDate2;
        LocalDateTime dateTime1 = null;
        LocalDateTime dateTime2 = null;

        for (TimeFormat timeFormat : timeFormats) {

            possibleDate1 = line.substring(0, timeFormat.getLength());
            possibleDate2 = line.substring(0, timeFormat.getLength() + 1);

            try {
                dateTime1 = LocalDateTime.parse(possibleDate1, timeFormat.asDateTimeFormatter());
            } catch (DateTimeParseException e) {
                try {
                    dateTime2 = LocalDateTime.parse(possibleDate2, timeFormat.asDateTimeFormatter());
                    this.doubleDigitFlag = true;
                } catch (DateTimeParseException e2) {
                    // can be ignored
                }
            }
            if (dateTime1 != null | dateTime2 != null)
                return timeFormat;
        }

        logger.info("Line can't be parsed: " + line);
        return null;
    }

    private LocalDateTime getDate(String line) {
        if (currentTimeFormat != null) {
            try {
                String possibleDate = line.substring(0, currentTimeFormat.getLength());
                return LocalDateTime.parse(possibleDate, currentTimeFormat.asDateTimeFormatter());
            } catch (DateTimeParseException e) {
                try {
                    String possibleDate = line.substring(0, currentTimeFormat.getLength() + 1);
                    LocalDateTime result = LocalDateTime.parse(possibleDate, currentTimeFormat.asDateTimeFormatter());
                    this.doubleDigitFlag = true;
                    return result;

                } catch (DateTimeParseException e1) {
                    // handled outside
                }
            }
        }
        return null;
    }
}
