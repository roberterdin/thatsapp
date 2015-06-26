package com.whatistics.backend.statistics;

import com.google.inject.Provider;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author robert
 */
public class MediaPatternProvider implements Provider<Pattern> {

    private List<String> mediaPattern;
    private Pattern result;

    public MediaPatternProvider(){
        mediaPattern = Arrays.asList(
                "<Media omitted>"
                , "<Mediendatei entfernt>"
                , "<Medien weggelassen>"
        );

        String pattern = "(" + mediaPattern.get(0);
        for (int i = 1; i<mediaPattern.size(); i++){
            pattern += ("|" + mediaPattern.get(i));
        }
        pattern += ")";

        result = Pattern.compile(pattern);
    }


    @Override
    public Pattern get() {
        return result;
    }
}
