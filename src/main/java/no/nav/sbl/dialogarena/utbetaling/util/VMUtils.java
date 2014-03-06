package no.nav.sbl.dialogarena.utbetaling.util;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;

public class VMUtils {

    public static boolean erDefinertPeriode(DateTime startDato, DateTime sluttDato) {
        final DateTime unixEpoch = new DateTime(0, ISOChronology.getInstance());
        return !(startDato.isEqual(unixEpoch) && sluttDato.isEqual(unixEpoch));
    }
}
