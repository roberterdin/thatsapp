package com.whatistics.backend.parser;

import com.whatistics.backend.Service;

import javax.mail.Message;
import java.util.Observable;

/**
 * @author robert
 */
public abstract class ParserService extends Observable implements Service {
    public abstract void parseMessage(Message message);
}