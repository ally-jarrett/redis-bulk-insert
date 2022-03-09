package io.redis.utils;

import org.springframework.stereotype.Component;

@Component
public class SearchUtil {

    /**
     * Add escape symbols for 'Special Characters' in Strings
     * @param inputString
     * @return
     */
    public String escapeMetaCharacters(String inputString){
        final String[] metaCharacters = {"\\","^","$","{","}","[","]","(",")",".","*","+","?","<",">","-","&","%"};

        for (int i = 0 ; i < metaCharacters.length ; i++){
            if(inputString.contains(metaCharacters[i])){
                inputString = inputString.replace(metaCharacters[i],"\\"+metaCharacters[i]);
            }
        }
        return inputString;
    }

}
