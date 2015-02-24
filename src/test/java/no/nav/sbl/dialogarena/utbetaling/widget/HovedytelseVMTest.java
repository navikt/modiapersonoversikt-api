package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.utbetaling.domain.Aktoer;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Konto;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static java.util.Collections.sort;
import static no.nav.sbl.dialogarena.utbetaling.widget.HovedytelseVM.UtbetalingVMComparator;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;

public class HovedytelseVMTest {

    private static final String ID = "id";

    @Before
    public void init() {
        Locale.setDefault(new Locale("nb", "no"));
    }

    @Test
    public void belopFormateres_medGruppering_medKomma_medToDesimaler() throws Exception {
        double belop = 67856565.6;
        Record<Hovedytelse> ytelse = new Record<Hovedytelse>()
                .with(Hovedytelse.id, ID)
                .with(Hovedytelse.utbetaltTil, dummyAktoer())
                .with(Hovedytelse.nettoUtbetalt, belop);
        HovedytelseVM vm = new HovedytelseVM(ytelse);

        String belop1 = vm.getBelop();

        String[] splittPaaKomma = belop1.split(",");
        assertThat(splittPaaKomma.length, is(equalTo(2)));
        assertThat(splittPaaKomma[1], is("60"));
    }

    @Test
    public void transformerWorksCorrectly(){
        Record<Hovedytelse> ytelse = new Record<Hovedytelse>()
                .with(Hovedytelse.id, ID)
                .with(Hovedytelse.utbetaltTil, dummyAktoer())
                .with(Hovedytelse.ytelsesperiode, new Interval(now().minusDays(7), now()));

        HovedytelseVM hovedytelseVM = HovedytelseVM.TIL_HOVEDYTELSEVM.transform(ytelse);
        assertThat(ytelse.get(Hovedytelse.ytelsesperiode).getStart(), is(equalTo(hovedytelseVM.getStartDato())));
    }


    @Test
    public void shouldSortereNyesteUtbetalingForst() {
        HovedytelseVM hovedytelseVM2Jan = lagUtbetalingVM(new DateTime(2013, 1, 2, 0, 0));
        HovedytelseVM hovedytelseVM1Jan = lagUtbetalingVM(new DateTime(2013, 1, 1, 0, 0));
        HovedytelseVM hovedytelseVM3Jan = lagUtbetalingVM(new DateTime(2013, 1, 3, 0, 0));
        List<HovedytelseVM> utbetalinger = asList(hovedytelseVM2Jan, hovedytelseVM1Jan, hovedytelseVM3Jan);
        sort(utbetalinger, new UtbetalingVMComparator());

        assertThat(utbetalinger, contains(hovedytelseVM3Jan, hovedytelseVM2Jan, hovedytelseVM1Jan));
    }

    @Test
    public void shouldSortereUtbetalingerUtenUtbetalingsdatoForst() {
        HovedytelseVM hovedytelseVM2Jan = lagUtbetalingVM(new DateTime(2013, 1, 2, 0, 0));
        HovedytelseVM hovedytelseVM3Jan = lagUtbetalingVM(new DateTime(2013, 1, 3, 0, 0));
        HovedytelseVM hovedytelseVMUtenUtbetalingsdato = lagUtbetalingVM(null);

        List<HovedytelseVM> utbetalinger = asList(hovedytelseVM2Jan, hovedytelseVMUtenUtbetalingsdato, hovedytelseVM3Jan);
        sort(utbetalinger, new UtbetalingVMComparator());

        assertThat(utbetalinger, contains(hovedytelseVMUtenUtbetalingsdato, hovedytelseVM3Jan, hovedytelseVM2Jan));
    }

    @Test
    public void shouldHandtereManglendeUtbetalingsdato() {
        HovedytelseVM hovedytelseVMUtenUtbetalingsdato1 = lagUtbetalingVM(null);
        HovedytelseVM hovedytelseVMUtenUtbetalingsdato2 = lagUtbetalingVM(null);

        List<HovedytelseVM> utbetalinger = asList(hovedytelseVMUtenUtbetalingsdato1, hovedytelseVMUtenUtbetalingsdato2);
        sort(utbetalinger, new UtbetalingVMComparator());

        assertThat(utbetalinger, contains(hovedytelseVMUtenUtbetalingsdato1, hovedytelseVMUtenUtbetalingsdato2));
    }

    private HovedytelseVM lagUtbetalingVM(DateTime utbetalingsDato) {
        return new HovedytelseVM(
                new Record<Hovedytelse>()
                    .with(Hovedytelse.id, ID)
                    .with(Hovedytelse.hovedytelsedato, utbetalingsDato)
                    .with(Hovedytelse.utbetaltTil, dummyAktoer())
                    .with(Hovedytelse.utbetaltTilKonto, new Record<Konto>().with(Konto.kontonummer, "123"))
                    .with(Hovedytelse.utbetaltTil, new Record<Aktoer>().with(Aktoer.aktoerId, "***REMOVED***"))
        );
    }

    private Record<Aktoer> dummyAktoer() {
        return new Record<Aktoer>()
                .with(Aktoer.navn, "Ola Nordmann");
    }

}
