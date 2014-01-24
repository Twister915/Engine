package net.tbnr.gearz.chat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Censored word
 */
class CensoredWord {
    /**
     * Stores the compiled pattern for the string
     */
    private final Pattern pattern;

    /**
     * Generates the compiled pattern for the string
     *
     * @param word The word to get the pattern of
     */
    public CensoredWord(String word) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            builder.append(String.format("[%c%c]", Character.toUpperCase(c), Character.toLowerCase(c)));
            if (i == word.length() - 1) {
                builder.append("\\S{0,3}");
            } else {
                builder.append("\\S{0,1}");
            }
        }
        pattern = Pattern.compile(builder.toString());
    }

    /**
     * Censors a string and outputs it
     *
     * @param s String to censor, should be the message
     * @return The message with *s in place of the censored words.
     */
    public String censorString(String s) {
        StringBuffer buffer = new StringBuffer(s);
        Matcher matcher = this.pattern.matcher(buffer);
        while (matcher.find()) {
            for (int i = matcher.start(); i < matcher.end(); i++) {
                if (buffer.charAt(i) == ' ') continue;
                buffer.setCharAt(i, '*');
            }
        }
        return buffer.toString();
    }
}
