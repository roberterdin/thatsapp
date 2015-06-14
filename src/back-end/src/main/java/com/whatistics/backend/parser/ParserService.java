package com.whatistics.backend.parser;

import com.whatistics.backend.Service;
import com.whatistics.backend.model.Conversation;

import java.io.InputStream;

/**
 * @author robert
 */
public interface ParserService extends Service {
    void parseMessage(InputStream inputStream);

    void storeConversation(Conversation conversation);
}
