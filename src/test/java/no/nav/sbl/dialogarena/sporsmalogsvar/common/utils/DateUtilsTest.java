package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;

import org.joda.time.LocalDate;
import org.junit.Test;

import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.DateUtils.ukedagerFraDato;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.DateUtils.erHelg;
import static org.hamcrest.CoreMatchers.is;
import static org.joda.time.Days.daysBetween;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class DateUtilsTest {

    @Test
    public void datoFraUkedagerHopperOverHelger() {
        LocalDate mandag = new LocalDate(2014, 9, 22);
        LocalDate nyDato = ukedagerFraDato(5, mandag);

        assertThat(daysBetween(mandag, nyDato).getDays(), is(7));
    }

    @Test
    public void datoFraUkedagerHopperIkkeOverUkedager() {
        LocalDate mandag = new LocalDate(2014, 9, 22);
        LocalDate nyDato = ukedagerFraDato(4, mandag);

        assertThat(daysBetween(mandag, nyDato).getDays(), is(4));
    }

    @Test
    public void erHelgErTrueKunForHelgedager() {
        LocalDate mandag = new LocalDate(2014, 9, 22);
        LocalDate tirsdag = new LocalDate(2014, 9, 23);
        LocalDate onsdag = new LocalDate(2014, 9, 24);
        LocalDate torsdag = new LocalDate(2014, 9, 25);
        LocalDate fredag = new LocalDate(2014, 9, 26);
        LocalDate lordag = new LocalDate(2014, 9, 27);
        LocalDate sondag = new LocalDate(2014, 9, 28);

        assertFalse(erHelg(mandag));
        assertFalse(erHelg(tirsdag));
        assertFalse(erHelg(onsdag));
        assertFalse(erHelg(torsdag));
        assertFalse(erHelg(fredag));
        assertTrue(erHelg(lordag));
        assertTrue(erHelg(sondag));
    }
}