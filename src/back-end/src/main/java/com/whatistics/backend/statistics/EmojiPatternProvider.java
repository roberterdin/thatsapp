package com.whatistics.backend.statistics;

import com.google.inject.Provider;

import java.util.regex.Pattern;

/**
 * @author robert
 */
public class EmojiPatternProvider implements Provider<Pattern> {

    private Pattern result;

    public EmojiPatternProvider(){
        result = Pattern.compile("[\\uD800-\\uDBFF][\\uDC00-\\uDFFF]");
    }

    @Override
    public Pattern get() {
        return result;
    }
}
