package com.whatistics.backend.statistics;

import com.google.inject.Provider;

import java.util.regex.Pattern;

/**
 * @author robert
 *
 * @see {http://unicode.org/emoji/charts/full-emoji-list.html}
 * @see {http://apps.timwhitlock.info/emoji/tables/unicode}
 */
public class EmojiPatternProvider implements Provider<Pattern> {

    private Pattern result;

    public EmojiPatternProvider(){
        result = Pattern.compile("[\uD83C-\uDBFF\uDC00-\uDFFF]");
    }

    @Override
    public Pattern get() {
        return result;
    }
}
