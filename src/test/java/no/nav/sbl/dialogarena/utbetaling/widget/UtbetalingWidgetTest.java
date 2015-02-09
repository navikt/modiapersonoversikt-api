package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static no.nav.sbl.dialogarena.utbetaling.widget.UtbetalingWidget.NUMBER_OF_MONTHS_TO_SHOW;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;

public class UtbetalingWidgetTest  {

    @Test
    public void transformererKorrekteUtbetalingerTilVMer() {
        List<Record<Hovedytelse>> hovedytelser = Arrays.asList(
            new Record<Hovedytelse>()
                .with(Hovedytelse.id, "1")
                .with(Hovedytelse.nettoUtbetalt, 0D)
                .with(Hovedytelse.hovedytelsedato, now()),
            new Record<Hovedytelse>()
                    .with(Hovedytelse.id, "2")
                    .with(Hovedytelse.nettoUtbetalt, 0D)
                    .with(Hovedytelse.hovedytelsedato, now().minusMonths(NUMBER_OF_MONTHS_TO_SHOW-1)),
            new Record<Hovedytelse>()
                    .with(Hovedytelse.id, "3")
                    .with(Hovedytelse.nettoUtbetalt, 0D)
                    .with(Hovedytelse.hovedytelsedato, now().minusMonths(NUMBER_OF_MONTHS_TO_SHOW)),
            new Record<Hovedytelse>()
                    .with(Hovedytelse.id, "4")
                    .with(Hovedytelse.nettoUtbetalt, 0D)
                    .with(Hovedytelse.hovedytelsedato, now().minusMonths(NUMBER_OF_MONTHS_TO_SHOW).toDateMidnight().toDateTime().minusMillis(1)),
            new Record<Hovedytelse>()
                    .with(Hovedytelse.id, "5")
                    .with(Hovedytelse.nettoUtbetalt, 0D)
                    .with(Hovedytelse.hovedytelsedato, now().minusMonths(NUMBER_OF_MONTHS_TO_SHOW + 1))
        );

        List<HovedytelseVM> hovedytelseVMs = UtbetalingWidget.transformUtbetalingToVM(hovedytelser);

        assertThat(hovedytelseVMs.size(), is(3));
        assertThat(hovedytelseVMs.get(0).getId(), is("1"));
        assertThat(hovedytelseVMs.get(1).getId(), is("2"));
        assertThat(hovedytelseVMs.get(2).getId(), is("3"));
    }


}