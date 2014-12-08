package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;

import org.joda.time.LocalDate;

import static org.joda.time.DateTimeConstants.SATURDAY;
import static org.joda.time.DateTimeConstants.SUNDAY;

public class DateUtils {

    public static LocalDate ukedagerFraDato(int ukedager, LocalDate startDato) {
        LocalDate dato = new LocalDate(startDato);
        for (int dagerIgjen = ukedager; dagerIgjen > 0; dagerIgjen--) {
            dato = dato.plusDays(1);
            while (erHelg(dato)) {
                dato = dato.plusDays(1);
            }
        }
        return dato;
    }

    public static boolean erHelg(LocalDate dato) {
        return dato.getDayOfWeek() == SATURDAY || dato.getDayOfWeek() == SUNDAY;
    }
}
