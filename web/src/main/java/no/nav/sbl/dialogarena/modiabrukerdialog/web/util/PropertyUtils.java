package no.nav.sbl.dialogarena.modiabrukerdialog.web.util;

import java.util.List;

import static java.lang.Boolean.valueOf;
import static java.lang.System.getProperty;
import static java.util.Arrays.asList;

public class PropertyUtils {

    protected static final String VIS_UTBETALINGER = "visUtbetalinger";
    protected static final String DEFAULT_VIS_UTBETALINGER = "true";
    protected static final String UTBETALING_TILGANG_LISTE = "utbetalingTilgang";
    protected static final String DEFAULT_TILGANG_UTBETALING_LISTE = "[]";

    public static boolean visUtbetalinger(String enhetsId) {
        return utbetalingerErPaa() && enhetHarTilgangTilUtbetaling(enhetsId);
    }

    protected static boolean utbetalingerErPaa() {
        return valueOf(getProperty(VIS_UTBETALINGER, DEFAULT_VIS_UTBETALINGER));
    }

    protected static boolean enhetHarTilgangTilUtbetaling(String enhetsId) {
        String listeOverTilgjengeligeEnheterMedKlammer = getProperty(UTBETALING_TILGANG_LISTE, DEFAULT_TILGANG_UTBETALING_LISTE);
        String listeOverTilgjengeligeEnheter = listeOverTilgjengeligeEnheterMedKlammer.substring(1, listeOverTilgjengeligeEnheterMedKlammer.length() - 1);
        List<String> mylist = asList(listeOverTilgjengeligeEnheter.split(","));
        return listeOverTilgjengeligeEnheter.isEmpty() || mylist.contains(enhetsId);
    }
}
