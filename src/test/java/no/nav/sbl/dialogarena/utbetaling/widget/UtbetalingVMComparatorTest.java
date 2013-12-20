package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;

public class UtbetalingVMComparatorTest {

    @Test
    public void shouldSortereNyesteUtbetalingForst() {
        UtbetalingVM utbetalingVM2Jan = lagUtbetalingVM(new DateTime(2013, 1, 2, 0, 0));
        UtbetalingVM utbetalingVM1Jan = lagUtbetalingVM(new DateTime(2013, 1, 1, 0, 0));
        UtbetalingVM utbetalingVM3Jan = lagUtbetalingVM(new DateTime(2013, 1, 3, 0, 0));
        List<UtbetalingVM> utbetalinger = asList(utbetalingVM2Jan, utbetalingVM1Jan, utbetalingVM3Jan);
        Collections.sort(utbetalinger, new UtbetalingVMComparator());

        assertThat(utbetalinger, contains(utbetalingVM3Jan, utbetalingVM2Jan, utbetalingVM1Jan));
    }

    @Test
    public void shouldSortereUtbetalingerUtenUtbetalingsdatoForst() {
        UtbetalingVM utbetalingVM2Jan = lagUtbetalingVM(new DateTime(2013, 1, 2, 0, 0));
        UtbetalingVM utbetalingVM3Jan = lagUtbetalingVM(new DateTime(2013, 1, 3, 0, 0));
        UtbetalingVM utbetalingVMUtenUtbetalingsdato = lagUtbetalingVM(null);

        List<UtbetalingVM> utbetalinger = asList(utbetalingVM2Jan, utbetalingVMUtenUtbetalingsdato, utbetalingVM3Jan);
        Collections.sort(utbetalinger, new UtbetalingVMComparator());

        assertThat(utbetalinger, contains(utbetalingVMUtenUtbetalingsdato, utbetalingVM3Jan, utbetalingVM2Jan));
    }

    @Test
    public void shouldHandtereManglendeUtbetalingsdato() {
        UtbetalingVM utbetalingVMUtenUtbetalingsdato1 = lagUtbetalingVM(null);
        UtbetalingVM utbetalingVMUtenUtbetalingsdato2 = lagUtbetalingVM(null);

        List<UtbetalingVM> utbetalinger = asList(utbetalingVMUtenUtbetalingsdato1, utbetalingVMUtenUtbetalingsdato2);
        Collections.sort(utbetalinger, new UtbetalingVMComparator());

        assertThat(utbetalinger, contains(utbetalingVMUtenUtbetalingsdato1, utbetalingVMUtenUtbetalingsdato2));
    }

    private UtbetalingVM lagUtbetalingVM(DateTime utbetalingsDato) {
        return new UtbetalingVM(
                Utbetaling.getBuilder()
                        .withValuta("kr")
                        .withMottakerId("Arbeidsgiver")
                        .withKontonr("123")
                        .withUtbetalingsDato(utbetalingsDato)
                        .createUtbetaling()
        );
    }
}
