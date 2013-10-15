package no.nav.sbl.dialogarena.utbetaling;

import org.apache.commons.collections15.Factory;
import org.apache.wicket.Session;

import java.util.Locale;

/**
 * Henter Locale som er satt på Wicket {@link org.apache.wicket.Session}.
 */
public final class LocaleFromWicketSession implements Factory<Locale> {

    public static final Factory<Locale> INSTANCE = new LocaleFromWicketSession();

    private LocaleFromWicketSession() {
    }

    @Override
    public Locale create() {
        return Session.get().getLocale();
    }
}