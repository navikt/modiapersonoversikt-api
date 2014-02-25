package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static java.util.Collections.sort;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.getBuilder;
import static no.nav.sbl.dialogarena.utbetaling.widget.UtbetalingVM.UtbetalingVMComparator;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;

public class UtbetalingVMTest {

    private static final String ID = "id";

    @Before
    public void init() {
        Locale.setDefault(new Locale("nb", "no"));
    }

    @Test
    public void belopFormateres_medGruppering_medKomma_medToDesimaler() throws Exception {
        double belop = 67856565.6;
        Utbetaling utbetaling = getBuilder(ID).withUtbetalt(belop).build();
        UtbetalingVM vm = new UtbetalingVM(utbetaling);

        String belop1 = vm.getBelop();

        String[] splittPaaKomma = belop1.split(",");
        assertThat(splittPaaKomma.length, is(equalTo(2)));
        assertThat(splittPaaKomma[1], is("60"));
    }

    @Test
    public void transformerWorksCorrectly(){
        Utbetaling utbetaling = getBuilder(ID).withPeriode(new Interval(now().minusDays(7), now())).build();
        UtbetalingVM utbetalingVM = UtbetalingVM.TIL_UTBETALINGVM.transform(utbetaling);
        assertThat(utbetaling.getPeriode().getStart(), is(equalTo(utbetalingVM.getStartDato())));
    }


    @Test
    public void shouldSortereNyesteUtbetalingForst() {
        UtbetalingVM utbetalingVM2Jan = lagUtbetalingVM(new DateTime(2013, 1, 2, 0, 0));
        UtbetalingVM utbetalingVM1Jan = lagUtbetalingVM(new DateTime(2013, 1, 1, 0, 0));
        UtbetalingVM utbetalingVM3Jan = lagUtbetalingVM(new DateTime(2013, 1, 3, 0, 0));
        List<UtbetalingVM> utbetalinger = asList(utbetalingVM2Jan, utbetalingVM1Jan, utbetalingVM3Jan);
        sort(utbetalinger, new UtbetalingVMComparator());

        assertThat(utbetalinger, contains(utbetalingVM3Jan, utbetalingVM2Jan, utbetalingVM1Jan));
    }

    @Test
    public void shouldSortereUtbetalingerUtenUtbetalingsdatoForst() {
        UtbetalingVM utbetalingVM2Jan = lagUtbetalingVM(new DateTime(2013, 1, 2, 0, 0));
        UtbetalingVM utbetalingVM3Jan = lagUtbetalingVM(new DateTime(2013, 1, 3, 0, 0));
        UtbetalingVM utbetalingVMUtenUtbetalingsdato = lagUtbetalingVM(null);

        List<UtbetalingVM> utbetalinger = asList(utbetalingVM2Jan, utbetalingVMUtenUtbetalingsdato, utbetalingVM3Jan);
        sort(utbetalinger, new UtbetalingVMComparator());

        assertThat(utbetalinger, contains(utbetalingVMUtenUtbetalingsdato, utbetalingVM3Jan, utbetalingVM2Jan));
    }

    @Test
    public void shouldHandtereManglendeUtbetalingsdato() {
        UtbetalingVM utbetalingVMUtenUtbetalingsdato1 = lagUtbetalingVM(null);
        UtbetalingVM utbetalingVMUtenUtbetalingsdato2 = lagUtbetalingVM(null);

        List<UtbetalingVM> utbetalinger = asList(utbetalingVMUtenUtbetalingsdato1, utbetalingVMUtenUtbetalingsdato2);
        sort(utbetalinger, new UtbetalingVMComparator());

        assertThat(utbetalinger, contains(utbetalingVMUtenUtbetalingsdato1, utbetalingVMUtenUtbetalingsdato2));
    }

    private UtbetalingVM lagUtbetalingVM(DateTime utbetalingsDato) {
        return new UtbetalingVM(
                Utbetaling.getBuilder(ID)
                        .withValuta("kr")
                        .withMottakerId("***REMOVED***")
                        .withKontonr("123")
                        .withUtbetalingsDato(utbetalingsDato)
                        .build()
        );
    }
}
