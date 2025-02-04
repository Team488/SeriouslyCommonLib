package xbot.common.logging;

// logic copilot suggestion code from from apache.commons.text
public class Pluralizer {
    public static String pluralize(String word) {
        // Basic pluralization logic
        if (word.endsWith("y")) {
            return word.substring(0, word.length() - 1) + "ies";
        } else if (word.endsWith("s") || word.endsWith("x") || word.endsWith("z") || word.endsWith("ch") || word.endsWith("sh")) {
            return word + "es";
        } else {
            return word + "s";
        }
    }
}