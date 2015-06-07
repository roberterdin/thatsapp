package com.whatistics.backend.mail.model;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;

import javax.mail.Session;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author robert
 */
public class UniqueIMAPMessage extends IMAPMessage {
    private static final Logger log = Logger.getLogger(UniqueIMAPMessage.class.getName());

    protected UniqueIMAPMessage(IMAPFolder folder, int msgnum) {
        super(folder, msgnum);
    }

    protected UniqueIMAPMessage(Session session){
        super(session);
    }


    @Override
    public int hashCode(){
        if( super.getUID() != -1){
            return (int) (super.getUID()^(super.getUID()>>>32));
        } else {
            log.log(Level.WARNING, "Message contains no UID, fallback to super.hashCode. This may result in multiple instances of the same message");
            return super.hashCode();
        }
    }

    public boolean equals(Object object){
        if (!(object instanceof UniqueIMAPMessage))
            return false;
        if (object == this)
            return true;

        if(super.getUID() != -1){
            return ((UniqueIMAPMessage)object).getUID() == super.getUID();
        }else {
            log.log(Level.WARNING, "Message contains no UID, fallback to super.hashCode. This may result in multiple instances of the same message");
            return super.equals(object);
        }
    }
}
