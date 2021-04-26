package no.nav.sbl.dialogarena.utbetaling.domain.util;

import org.joda.time.LocalDate;
import org.junit.Test;

import static no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingUtils.erDatoISokeperioden;
import static org.hamcrest.Matchers.is;
import static org.joda.time.LocalDate.now;
import static org.hamcrest.MatcherAssert.assertThat;

public class UtbetalingUtilsTest {
    public static final int NUMBER_OF_DAYS_TO_SHOW = 30;

    @Test
    public void utbetalingMedUtbetalingsDatoForStartDato() {
        LocalDate startDato = now().minusDays(NUMBER_OF_DAYS_TO_SHOW);
        LocalDate sluttDato = now();
        LocalDate testDato = now().minusDays(NUMBER_OF_DAYS_TO_SHOW).minusDays(1);

        assertThat(erDatoISokeperioden(testDato, startDato, sluttDato), is(false));
    }

    @Test
    public void utbetalingMedUtbetalingsDatoEtterSluttDato() {
        LocalDate startDato = now().minusDays(2*NUMBER_OF_DAYS_TO_SHOW);
        LocalDate sluttDato = now().minusDays(NUMBER_OF_DAYS_TO_SHOW);
        LocalDate testDato = now();

        assertThat(erDatoISokeperioden(testDato, startDato, sluttDato), is(false));
    }

    @Test
    public void utbetalingMedUtbetalingsDatoLikStartDato() {
        LocalDate startDato = now().minusDays(NUMBER_OF_DAYS_TO_SHOW);
        LocalDate sluttDato = now();
        LocalDate testDato = now().minusDays(NUMBER_OF_DAYS_TO_SHOW);

        assertThat(erDatoISokeperioden(testDato, startDato, sluttDato), is(true));
    }

    @Test
    public void utbetalingMedUtbetalingsdatoLikSluttDato() {
        LocalDate startDato = now().minusDays(NUMBER_OF_DAYS_TO_SHOW);
        LocalDate sluttDato = now();
        LocalDate testDato = now();

        assertThat(erDatoISokeperioden(testDato, startDato, sluttDato), is(true));
    }

    @Test
    public void utbetalingMedUtbetalingsdatoIMellomStartOgSluttdato() {
        LocalDate startDato = now().minusDays(2*NUMBER_OF_DAYS_TO_SHOW);
        LocalDate sluttDato = now();
        LocalDate testDato = now().minusDays(NUMBER_OF_DAYS_TO_SHOW);

        assertThat(erDatoISokeperioden(testDato, startDato, sluttDato), is(true));
    }
}
