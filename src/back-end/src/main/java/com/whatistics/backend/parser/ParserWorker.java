package com.whatistics.backend.parser;

import com.whatistics.backend.mail.MailUtilities;

import javax.mail.Message;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author robert
 */
public class ParserWorker implements Runnable {

    private Message eMailMessage;

    public ParserWorker(Message eMailMessage){
        this.eMailMessage = eMailMessage;
    }

    @Override
    public void run() {
        List<InputStream> attachments = MailUtilities.getAttachments(eMailMessage);

        BufferedReader reader = new BufferedReader(new InputStreamReader(attachments.get(0)));
        String currentLine;

        try {
            while ((currentLine = reader.readLine()) != null){
                System.out.println(currentLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
