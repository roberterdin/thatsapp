package com.whatistics.backend.mail;

import org.apache.commons.lang3.StringUtils;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author robert
 */
public class MailUtilities {

    public static List<InputStream> getAttachments(Message message) {
        Object content;
        try {
            content = message.getContent();
            if (content instanceof String) {
                return null;
            }
            if (content instanceof Multipart) {
                Multipart multipart = (Multipart) content;
                List<InputStream> result = new ArrayList<InputStream>();
                for (int i = 0; i < multipart.getCount(); i++) {
                    result.addAll(getAttachments(multipart.getBodyPart(i)));
                }
                return result;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static List<InputStream> getAttachments(BodyPart part) throws Exception {
        List<InputStream> result = new ArrayList<InputStream>();
        Object content = part.getContent();
        if (content instanceof InputStream || content instanceof String) {
            if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()) || StringUtils.isNotBlank(part.getFileName())) {
                result.add(part.getInputStream());
                return result;
            } else {
                return new ArrayList<InputStream>();
            }
        }

        if (content instanceof Multipart) {
            Multipart multipart = (Multipart) content;
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                result.addAll(getAttachments(bodyPart));
            }
        }
        return result;
    }
}
