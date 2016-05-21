package com.whatistics.backend.mail;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.QuotedPrintableCodec;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.MimeUtility;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author robert
 */
public class MailUtilities {
    private static final Logger logger = LoggerFactory.getLogger(MailUtilities.class);
    // todo: the =2Etxt part is just a workaround. Proper quoted-printable decoding would be preferable
    private static Pattern extPattern = Pattern.compile("(.+?)(\\.txt|=2Etxt\\?=)$");

    public static TreeMap<String, InputStream> getAttachments(Message message) {
        Object content;
        try {
            content = message.getContent();
            if (content instanceof String) {
                return null;
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
        TreeMap<String, InputStream> result= new TreeMap<>();
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

    static boolean isValid(Message message){
        TreeMap<String, InputStream> attachments = MailUtilities.getAttachments(message);

        // remove all non .txt messages
        attachments.entrySet().removeIf(entry -> ! extPattern.matcher(entry.getKey()).matches());

        if (attachments.size() == 1){
            return true;
        }else if (attachments.size() == 0){
            logger.info("Number of attachments is 0");
        }
        return false;
    }
}
