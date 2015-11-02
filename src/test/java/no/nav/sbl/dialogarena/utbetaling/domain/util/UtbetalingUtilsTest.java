package no.nav.sbl.dialogarena.utbetaling.domain.util;

import org.joda.time.LocalDate;
import org.junit.Test;

import static no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingUtils.erUtbetalingsdatoISokeperioden;
import static no.nav.sbl.dialogarena.utbetaling.widget.UtbetalingWidget.NUMBER_OF_DAYS_TO_SHOW;
import static org.hamcrest.Matchers.is;
import static org.joda.time.LocalDate.now;
import static org.junit.Assert.assertThat;

public class UtbetalingUtilsTest {

    @Test
    public void utbetalingMedUtbetalingsDatoForStartDato() {
        LocalDate startDato = now().minusDays(NUMBER_OF_DAYS_TO_SHOW);
        LocalDate sluttDato = now();
        LocalDate testDato = now().minusDays(NUMBER_OF_DAYS_TO_SHOW).minusDays(1);

        assertThat(erUtbetalingsdatoISokeperioden(testDato, startDato, sluttDato), is(false));
    }

    @Test
    public void utbetalingMedUtbetalingsDatoEtterSluttDato() {
        LocalDate startDato = now().minusDays(2*NUMBER_OF_DAYS_TO_SHOW);
        LocalDate sluttDato = now().minusDays(NUMBER_OF_DAYS_TO_SHOW);
        LocalDate testDato = now();

        assertThat(erUtbetalingsdatoISokeperioden(testDato, startDato, sluttDato), is(false));
    }

    @Test
    public void utbetalingMedUtbetalingsDatoLikStartDato() {
        LocalDate startDato = now().minusDays(NUMBER_OF_DAYS_TO_SHOW);
        LocalDate sluttDato = now();
        LocalDate testDato = now().minusDays(NUMBER_OF_DAYS_TO_SHOW);

        assertThat(erUtbetalingsdatoISokeperioden(testDato, startDato, sluttDato), is(true));
    }

    @Test
    public void utbetalingMedUtbetalingsdatoLikSluttDato() {
        LocalDate startDato = now().minusDays(NUMBER_OF_DAYS_TO_SHOW);
        LocalDate sluttDato = now();
        LocalDate testDato = now();

        assertThat(erUtbetalingsdatoISokeperioden(testDato, startDato, sluttDato), is(true));
    }

    @Test
    public void utbetalingMedUtbetalingsdatoIMellomStartOgSluttdato() {
        LocalDate startDato = now().minusDays(2*NUMBER_OF_DAYS_TO_SHOW);
        LocalDate sluttDato = now();
        LocalDate testDato = now().minusDays(NUMBER_OF_DAYS_TO_SHOW);

        assertThat(erUtbetalingsdatoISokeperioden(testDato, startDato, sluttDato), is(true));
    }
}
