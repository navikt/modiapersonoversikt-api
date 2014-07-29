package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.utils;

import org.apache.commons.collections15.Factory;
import org.apache.wicket.Session;

import java.util.Locale;

import static org.apache.wicket.Session.get;

/**
 * Henter Locale som er satt på Wicket {@link Session}.
 */
public final class LocaleFromWicketSession implements Factory<Locale> {

    public static final Factory<Locale> INSTANCE = new LocaleFromWicketSession();

    @Override
    public Locale create() {
        return get().getLocale();
    }

    private LocaleFromWicketSession() {
    }
}