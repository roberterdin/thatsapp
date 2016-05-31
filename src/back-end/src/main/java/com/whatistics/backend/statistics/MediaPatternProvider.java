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
                , "<\u200EBild weggelassen>"
        );

        StringBuilder pattern = new StringBuilder("(" + mediaPattern.get(0));
        for (int i = 1; i < mediaPattern.size(); i++){
            pattern.append("|" + mediaPattern.get(i));
        }
        pattern.append(")");

        result = Pattern.compile(pattern.toString());
    }


    @Override
    public Pattern get() {
        return result;
    }
}
