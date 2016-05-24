package com.whatistics.backend.mail;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import java.io.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author robert
 */
public class MailUtilities {
    private static final Logger logger = LoggerFactory.getLogger(MailUtilities.class);

    private static Pattern txtPattern = Pattern.compile("(.+?)(\\.txt)$");
    private static Pattern zipPattern = Pattern.compile("(.+?)(\\.zip)$");


    public static TreeMap<String, InputStream> getCleanAttachments(Message message) {

        TreeMap<String, InputStream> attachments = MailUtilities.getAttachments(message);

        // unzip potential .zip files
        for (Map.Entry<String, InputStream> entry : attachments.entrySet()) {
            if (zipPattern.matcher(entry.getKey()).matches()) {

                ZipInputStream zis = new ZipInputStream(entry.getValue());
                ZipEntry zipEntry;

                try {
                    while ((zipEntry = zis.getNextEntry()) != null) {
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                        // consume all the data from this entry
                        while (zis.available() > 0)
                            outputStream.write(zis.read());

                        // add file to attachments
                        // todo: don't do in memory
                        attachments.put(zipEntry.getName(), new ByteArrayInputStream(outputStream.toByteArray()));

                    }
                } catch (IOException e) {
                    logger.error("Failed reading .zip file", e);
                }
            }
        }


        // remove all non .txt or .zip messages
        attachments.entrySet().removeIf(entry -> !txtPattern.matcher(entry.getKey()).matches());

        return attachments;
    }

    public static TreeMap<String, InputStream> getAttachments(Message message) {
        Object content;
        try {
            content = message.getContent();
            if (content instanceof String) {
                return new TreeMap<String, InputStream>();
            }
            if (content instanceof Multipart) {
                Multipart multipart = (Multipart) content;
                TreeMap<String, InputStream> result = new TreeMap<>();
                for (int i = 0; i < multipart.getCount(); i++) {
                    result.putAll(getAttachments(multipart.getBodyPart(i)));
                }
                return result;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static TreeMap<String, InputStream> getAttachments(BodyPart part) throws Exception {
        TreeMap<String, InputStream> result = new TreeMap<>();
        Object content = part.getContent();
        if (content instanceof InputStream || content instanceof String) {
            if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()) || StringUtils.isNotBlank(part.getFileName())) {
                result.put(part.getFileName(), part.getInputStream());
                return result;
            } else {
                return new TreeMap<String, InputStream>();
            }
        }

        if (content instanceof Multipart) {
            Multipart multipart = (Multipart) content;
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                result.putAll(getAttachments(bodyPart));
            }
        }
        return result;
    }

    static boolean isValid(Message message) {
        Map attachments = getCleanAttachments(message);

        if (attachments.size() == 1) {
            return true;
        } else if (attachments.size() == 0) {
            logger.info("Number of attachments is 0");
        }
        return false;
    }
}
