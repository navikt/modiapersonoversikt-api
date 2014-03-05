package no.nav.sbl.dialogarena.modiabrukerdialog.web.util;

import static java.lang.Boolean.valueOf;
import static java.lang.System.getProperty;

public class PropertyUtils {

    private static final String VIS_UTBETALINGER = "visUtbetalinger";
    private static final String DEFAULT_VIS_UTBETALINGER = "true";

    public static boolean visUtbetalinger() {
        return valueOf(getProperty(VIS_UTBETALINGER, DEFAULT_VIS_UTBETALINGER));
    }
}
