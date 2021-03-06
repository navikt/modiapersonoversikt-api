package no.nav.modiapersonoversikt.utils;

import org.joda.time.LocalDate;

import static no.bekk.bekkopen.date.NorwegianDateUtil.addWorkingDaysToDate;

public class DateUtils {
    public static LocalDate arbeidsdagerFraDato(int ukedager, LocalDate startDato) {
        return LocalDate.fromDateFields(addWorkingDaysToDate(startDato.toDate(), ukedager));
    }
}
