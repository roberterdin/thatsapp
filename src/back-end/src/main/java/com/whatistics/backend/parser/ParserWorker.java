package com.whatistics.backend.parser;

import com.whatistics.backend.model.Conversation;
import com.whatistics.backend.model.Message;
import com.whatistics.backend.model.Person;
import com.whatistics.meta.TimeFormatCounter;
import org.apache.commons.io.input.BOMInputStream;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

/**
 * @author robert
 *         Receives an InputStream of the chat conversation file, parses it and creates a
 *         {@link Conversation} object which will then be stored in the database.
 *         Callable is used instead of Runnable to make testin easier.
 */
public class ParserWorker implements Callable<Conversation> {
    final Logger logger = LoggerFactory.getLogger(ParserWorker.class);

    private Datastore ds;

    public static final DateTimeFormatter logFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_");

    private List<TimeFormat> timeFormats;

    private Conversation conversation;

    private InputStream inputStream;
    private TimeFormat currentTimeFormat;

    private Map<String, Person> senderMap = new HashMap<>();

    private Pattern nullPattern = Pattern.compile("\\u0000");

    // A time format like M/d/yy can be
    // 1/1/14
    // 14/1/14
    // 14/10/14
    // thus the potential margin from the length of the time format M/d/yy
    private int margin;

    public ParserWorker(InputStream inputStream,
                        List<TimeFormat> timeFormats,
                        Datastore ds) {
        this.inputStream = inputStream;
        this.timeFormats = timeFormats;
        this.conversation = new Conversation();
        this.ds = ds;
    }

    @Override
    public Conversation call() {
        long startTime = System.nanoTime();

        // prepare to persist information to which time formats are used
        UpdateOperations<TimeFormatCounter> ops = ds.createUpdateOperations(TimeFormatCounter.class);
        Query<TimeFormatCounter> updateQuery = ds.createQuery(TimeFormatCounter.class)
                .field(Mapper.ID_KEY).equal(1);

        // ignore Unicode BOM in input stream (breaks time parsing)
        BufferedReader reader = new BufferedReader(new InputStreamReader(new BOMInputStream(inputStream)));
        String currentLine;

        try {
            Message message = null;
            while ((currentLine = reader.readLine()) != null) {
                // skip newlines
                if (currentLine.equals(""))
                    continue;

                this.conversation.incrementLineCount();

                // remove leading and trailing white spaces
                currentLine = currentLine.trim();

                // remove null characters
                currentLine = nullPattern.matcher(currentLine).replaceAll("");

                // figure out current time format, if necessary
                if (currentTimeFormat == null || getDate(currentLine) == null) {
                    // we need to figure out current time format
                    TimeFormat tmp = getTimeFormat(currentLine);
                    if (tmp != null) {
                        currentTimeFormat = tmp;
                        ops.disableValidation().inc(currentTimeFormat.getRawFormat().replaceAll("\\.", "\uFFFD"));
                    }
                }


                LocalDateTime dateTime = getDate(currentLine);

                if (dateTime == null) {
                    // add to previous {
                    if (message != null) {
                        message.setContent(message.getContent() + System.lineSeparator() + currentLine);
                    } else {
                        File errorFile = new File("/var/log/thatsapp/unparseable/" + LocalDateTime.now().format(logFormatter) + conversation.getSubmittedBy() + ".txt");
                        logger.error("First line can't be part of multiline message, will write file to " + errorFile.getAbsolutePath() + " for further investigation.",
                                new IllegalStateException("First line can't be part of multiline message"));

                        // this means that the file can't be parsed. Save output to a file to investigate
                        Files.copy(inputStream, errorFile.toPath());
                        break;
                    }

                } else {
                    // add old message to conversation:
                    if (message != null)
                        conversation.addMessage(message);

                    // create new message;
                    message = new com.whatistics.backend.model.Message();

                    // remove date from message
                    currentLine = currentLine.substring(currentTimeFormat.getLength() + this.margin);

                    message.setSendDate(dateTime);

                    //todo handle participant names containing ": "
                    String[] senderAndContent = currentLine.split(": ", 2);
                    if (senderAndContent.length == 2) {
                        // normal line

                        if (!senderMap.containsKey(senderAndContent[0]))
                            senderMap.put(senderAndContent[0], new Person(senderAndContent[0]));

                        message.setSender(senderMap.get(senderAndContent[0]));
                        message.setContent(senderAndContent[1]);
                    } else if (senderAndContent.length == 1) {
                        // "system message", like person added or icon changed, etc.
                        if (!senderMap.containsKey("_dummy"))
                            senderMap.put("_dummy", new Person("_dummy"));

                        message.setSender(senderMap.get("_dummy"));
                        message.setContent(senderAndContent[0]);
                    }
                }

            }

            // add last message
            if (message != null)
                conversation.addMessage(message);

        } catch (IOException e) {
            logger.error("Error while reading attachment", e);
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                logger.error("Error closing stream", e);
            }
        }

        // persist time format meta data
        ds.update(updateQuery, ops, true);

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
        logger.info("Time to parse message: " + duration / 1000000 + "ms");

        return conversation;
    }

    /**
     * Returns the TimeFormat of the line or null if it can't be parsed.
     * Not being able to parse the line is an indicator for a multi line message
     *
     * @param line
     * @return
     */
    private TimeFormat getTimeFormat(String line) {

        LocalDateTime dateTime = null;

        // if the line is shorter than the shortest time format, it has to be a multi line
        // this is an optimisation measure
        if (line.length() < this.timeFormats.get(this.timeFormats.size() - 1).getLength()) {
            return null;
        }

        for (TimeFormat timeFormat : timeFormats) {

            String possibleDate;
            for (int margin = 0; margin <= 2; margin++) {
                try {
                    possibleDate = line.substring(0, timeFormat.getLength() + margin);
                    dateTime = LocalDateTime.parse(possibleDate, timeFormat.asDateTimeFormatter());

                    if (dateTime != null) {
                        return timeFormat;
                    }

                } catch (StringIndexOutOfBoundsException e) {
                    // potential multiline
                    return null;
                } catch (DateTimeParseException e2) {
                    // can be ignored
                }
            }

        }

        logger.trace("Line can't be parsed: " + line); // nothing to worry per se. It's probably a multi line message
        this.conversation.incrementUnparseableCount();
        return null;
    }

    private LocalDateTime getDate(String line) {
        if (currentTimeFormat != null && currentTimeFormat.getLength() < line.length()) {

            for (int margin = 0; margin <= 2; margin++) {
                try {
                    String possibleDate = line.substring(0, currentTimeFormat.getLength() + margin);
                    this.margin = margin;
                    return LocalDateTime.parse(possibleDate, currentTimeFormat.asDateTimeFormatter());
                } catch (StringIndexOutOfBoundsException e) {
                    return null;
                } catch (DateTimeParseException e2) {
                    // handled outside
                }
            }
        }
        return null;
    }
}
