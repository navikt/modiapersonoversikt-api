package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util;

import org.joda.time.LocalDate;
import org.junit.jupiter.api.Test;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.DateUtils.arbeidsdagerFraDato;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.joda.time.Days.daysBetween;

public class DateUtilsTest {

    @Test
    public void arbeidsdagerFraDatoHopperOverHelger() {
        LocalDate mandag = new LocalDate(2014, 9, 22);
        LocalDate nyDato = arbeidsdagerFraDato(5, mandag);

        assertThat(daysBetween(mandag, nyDato).getDays(), is(7));
    }

    @Test
    public void arbeidsdagerFraDatoHopperIkkeOverUkedager() {
        LocalDate mandag = new LocalDate(2014, 9, 22);
        LocalDate nyDato = arbeidsdagerFraDato(4, mandag);

        assertThat(daysBetween(mandag, nyDato).getDays(), is(4));
    }

    @Test
    public void arbeidsdagerFraDatoHopperOverHelligdager() {
        LocalDate onsdagForPaaske = new LocalDate(2035, 3, 21);
        LocalDate nyDato = arbeidsdagerFraDato(2, onsdagForPaaske);

        assertThat(daysBetween(onsdagForPaaske, nyDato).getDays(), is(7));
    }

}
