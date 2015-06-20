package com.whatistics.backend.parser;

import com.whatistics.backend.ObservableService;
import com.whatistics.backend.Service;
import com.whatistics.backend.model.Conversation;

import javax.mail.Message;
import java.io.InputStream;

/**
 * @author robert
 */
public abstract class ParserService extends ObservableService {
    public abstract void parseMessage(Message message);
}