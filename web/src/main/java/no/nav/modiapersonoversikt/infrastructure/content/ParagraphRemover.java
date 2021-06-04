package no.nav.modiapersonoversikt.infrastructure.content;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

public class ParagraphRemover {
    public static final Pattern TAG_PATTERN = compile("</?p>");

    public static String remove(String input) {
        String text = input;
        if (text == null) {
            return null;
        }

        Matcher matcher = TAG_PATTERN.matcher(text);
        text = matcher.replaceAll("").trim();

        return text;
    }
}
