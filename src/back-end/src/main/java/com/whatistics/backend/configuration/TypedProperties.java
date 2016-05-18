package com.whatistics.backend.configuration;

import java.util.Properties;

public class TypedProperties extends Properties {

    /**
     * Helper method to avoid having to call Integer.parseInt each time there is an int.
     * @param key
     * @return
     */
    public int getIntProp(String key){
        return Integer.parseInt(super.getProperty(key));
    }
}
