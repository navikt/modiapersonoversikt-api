package no.nav.sbl.dialogarena.sporsmalogsvar;

import org.apache.wicket.Session;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

/**
 * Ulike tekstlige formateringer av datoer og tid, ref:
 * <a href="http://confluence.adeo.no/display/Modernisering/Datoformat">http://confluence.adeo.no/display/Modernisering/Datoformat</a>
 */
public final class Datoformat {

    public static final String LANGT = "EEEEE d. MMMM yyyy, 'kl' HH:mm";
    public static final String MEDIUM = "d. MMM yyyy, 'kl' HH:mm";
    public static final String KORT = "dd.MM.yyyy 'kl' HH.mm";

    public static String lang(DateTime dateTime) { return format(dateTime, LANGT); }
    public static String medium(DateTime dateTime) { return format(dateTime, MEDIUM); }
    public static String kort(DateTime dateTime) { return format(dateTime, KORT); }


    static String format(DateTime dateTime, String pattern) {
        return format(dateTime, pattern, Session.get().getLocale());
    }

    static String format(DateTime dateTime, String pattern, Locale locale) {
        return forPattern(pattern, locale).print(dateTime);
    }

    private static DateTimeFormatter forPattern(String pattern, Locale locale) {
        return DateTimeFormat.forPattern(pattern).withLocale(locale);
    }

    private Datoformat() {}
}

