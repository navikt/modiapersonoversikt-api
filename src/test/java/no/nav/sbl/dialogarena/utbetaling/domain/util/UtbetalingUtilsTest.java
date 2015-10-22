package no.nav.sbl.dialogarena.utbetaling.domain.util;

import org.joda.time.LocalDate;
import org.junit.Test;

import static no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingUtils.erUtbetalingsdatoISokeperioden;
import static org.hamcrest.Matchers.is;
import static org.joda.time.LocalDate.now;
import static org.junit.Assert.assertThat;

public class UtbetalingUtilsTest {

    @Test
    public void utbetalingMedUtbetalingsDatoForStartDato() {
        LocalDate startDato = now().minusMonths(1);
        LocalDate sluttDato = now();
        LocalDate testDato = now().minusMonths(1).minusDays(1);

        assertThat(erUtbetalingsdatoISokeperioden(testDato, startDato, sluttDato), is(false));
    }

    @Test
    public void utbetalingMedUtbetalingsDatoEtterSluttDato() {
        LocalDate startDato = now().minusMonths(2);
        LocalDate sluttDato = now().minusMonths(1);
        LocalDate testDato = now();

        assertThat(erUtbetalingsdatoISokeperioden(testDato, startDato, sluttDato), is(false));
    }

    @Test
    public void utbetalingMedUtbetalingsDatoLikStartDato() {
        LocalDate startDato = now().minusMonths(1);
        LocalDate sluttDato = now();
        LocalDate testDato = now().minusMonths(1);

        assertThat(erUtbetalingsdatoISokeperioden(testDato, startDato, sluttDato), is(true));
    }

    @Test
    public void utbetalingMedUtbetalingsdatoLikSluttDato() {
        LocalDate startDato = now().minusMonths(1);
        LocalDate sluttDato = now();
        LocalDate testDato = now();

        assertThat(erUtbetalingsdatoISokeperioden(testDato, startDato, sluttDato), is(true));
    }

    @Test
    public void utbetalingMedUtbetalingsdatoIMellomStartOgSluttdato() {
        LocalDate startDato = now().minusMonths(2);
        LocalDate sluttDato = now();
        LocalDate testDato = now().minusMonths(1);

        assertThat(erUtbetalingsdatoISokeperioden(testDato, startDato, sluttDato), is(true));
    }
}
