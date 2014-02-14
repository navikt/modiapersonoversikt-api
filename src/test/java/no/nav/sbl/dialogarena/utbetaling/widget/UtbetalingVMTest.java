package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.getBuilder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;

public class UtbetalingVMTest {

    @Before
    public void init() {
        Locale.setDefault(new Locale("nb", "no"));
    }

    @Test
    public void belopFormateres_medGruppering_medKomma_medToDesimaler() throws Exception {
        double belop = 67856565.6;
        Utbetaling utbetaling = getBuilder().withUtbetalt(belop).build();
        UtbetalingVM vm = new UtbetalingVM(utbetaling);

        String belop1 = vm.getBelop();

        String[] splittPaaKomma = belop1.split(",");
        assertThat(splittPaaKomma.length, is(equalTo(2)));
        assertThat(splittPaaKomma[1], is("60"));
    }

    @Test
    public void transformerWorksCorrectly(){
        Utbetaling utbetaling = getBuilder().withPeriode(new Interval(now().minusDays(7), now())).build();
        UtbetalingVM utbetalingVM = UtbetalingVM.TIL_UTBETALINGVM.transform(utbetaling);
        assertThat(utbetaling.getPeriode().getStart(), is(equalTo(utbetalingVM.getStartDato())));
    }
}
