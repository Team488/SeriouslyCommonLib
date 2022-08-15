package xbot.common.logging;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.log4j.Logger;


public class WordGenerator {
    
    String[] wordArray;
    private Logger log = Logger.getLogger("WordGenerator");
    Random rand = new Random();

    @Inject
    public WordGenerator() {
        String words = getResourceFileAsString("10k.txt");
        if (words != null) {
            wordArray = words.split(System.lineSeparator());
            log.info("Successfully loaded english dictionary.");
            log.info("Dictionary contains " + wordArray.length + " words.");
        } else {
            log.warn("Could not load dictionary file");
        }
    }

    public String getResourceFileAsString(String fileName) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
        if (is != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
        return null;
    }

    public String getRandomWordChain(int length, String separator) {
        StringBuilder b = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            int r = rand.nextInt(wordArray.length-1);
            b.append(wordArray[r]).append(separator);
        }

        String precandidate = b.toString();
        String candidate = precandidate.substring(0, precandidate.length()-1);
        return candidate;
    }
}