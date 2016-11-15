package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.sbl.dialogarena.utbetaling.domain.Aktoer;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedutbetaling;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Collections.sort;
import static no.nav.sbl.dialogarena.utbetaling.widget.HovedutbetalingVM.UtbetalingVMComparator;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;

public class HovedutbetalingVMTest {

    private static final String ID = "id";

    @Before
    public void init() {
        Locale.setDefault(new Locale("nb", "no"));
    }

    @Test
    public void belopFormateres_medGruppering_medKomma_medToDesimaler() throws Exception {
        double belop = 67856565.6;
        Hovedytelse ytelse = new Hovedytelse()
                .withId(ID)
                .withUtbetaltTil(dummyAktoer())
                .withNettoUtbetalt(belop);

        Hovedutbetaling hovedutbetaling = new Hovedutbetaling()
                .withHovedytelser(singletonList(ytelse))
                .settUtbetaltSum();

        HovedutbetalingVM vm = new HovedutbetalingVM(hovedutbetaling);

        String belop1 = vm.getBelop();

        String[] splittPaaKomma = belop1.split(",");
        assertThat(splittPaaKomma.length, is(equalTo(2)));
        assertThat(splittPaaKomma[1], is("60"));
    }

    @Test
    public void transformerWorksCorrectly(){
        Hovedytelse ytelse = new Hovedytelse()
                .withId(ID)
                .withUtbetaltTil(dummyAktoer())
                .withYtelsesperiode(new Interval(now().minusDays(7), now()));

        Hovedutbetaling hovedutbetaling = new Hovedutbetaling()
                .withHovedytelsesdato(now())
                .withHovedytelser(singletonList(ytelse));

        HovedutbetalingVM hovedutbetalingVM = HovedutbetalingVM.TIL_HOVEDUTBETALINGVM.apply(hovedutbetaling);
        assertThat(ytelse.getYtelsesperiode().getStart(), is(equalTo(hovedutbetalingVM.getStartDato())));
    }


    @Test
    public void shouldSortereNyesteUtbetalingForst() {
        HovedutbetalingVM hovedutbetalingVM2Jan = lagHovedutbetaling(new DateTime(2013, 1, 2, 0, 0));
        HovedutbetalingVM hovedutbetalingVM1Jan = lagHovedutbetaling(new DateTime(2013, 1, 1, 0, 0));
        HovedutbetalingVM hovedutbetalingVM3Jan = lagHovedutbetaling(new DateTime(2013, 1, 3, 0, 0));
        List<HovedutbetalingVM> utbetalinger = asList(hovedutbetalingVM2Jan, hovedutbetalingVM1Jan, hovedutbetalingVM3Jan);
        sort(utbetalinger, new UtbetalingVMComparator());

        assertThat(utbetalinger, contains(hovedutbetalingVM3Jan, hovedutbetalingVM2Jan, hovedutbetalingVM1Jan));
    }

    @Test
    public void shouldSortereUtbetalingerUtenUtbetalingsdatoForst() {
        HovedutbetalingVM hovedutbetalingVM2Jan = lagHovedutbetaling(new DateTime(2013, 1, 2, 0, 0));
        HovedutbetalingVM hovedutbetalingVM3Jan = lagHovedutbetaling(new DateTime(2013, 1, 3, 0, 0));
        HovedutbetalingVM hovedutbetalingVMUtenUtbetalingsdato = lagHovedutbetaling(null);

        List<HovedutbetalingVM> utbetalinger = asList(hovedutbetalingVM2Jan, hovedutbetalingVMUtenUtbetalingsdato, hovedutbetalingVM3Jan);
        sort(utbetalinger, new UtbetalingVMComparator());

        assertThat(utbetalinger, contains(hovedutbetalingVMUtenUtbetalingsdato, hovedutbetalingVM3Jan, hovedutbetalingVM2Jan));
    }

    @Test
    public void shouldHandtereManglendeUtbetalingsdato() {
        HovedutbetalingVM hovedutbetalingVMUtenUtbetalingsdato1 = lagHovedutbetaling(null);
        HovedutbetalingVM hovedutbetalingVMUtenUtbetalingsdato2 = lagHovedutbetaling(null);

        List<HovedutbetalingVM> utbetalinger = asList(hovedutbetalingVMUtenUtbetalingsdato1, hovedutbetalingVMUtenUtbetalingsdato2);
        sort(utbetalinger, new UtbetalingVMComparator());

        assertThat(utbetalinger, contains(hovedutbetalingVMUtenUtbetalingsdato1, hovedutbetalingVMUtenUtbetalingsdato2));
    }

    private HovedutbetalingVM lagHovedutbetaling(DateTime utbetalingsdato){
        return new HovedutbetalingVM(
                new Hovedutbetaling()
                        .withId("123")
                        .withHovedytelsesdato(utbetalingsdato)
                        .withHovedytelser(singletonList(lagHovedytelse(utbetalingsdato)))
                        .withIsUtbetalt(true)
                        .withUtbetalingStatus("Utbetalt")
                        .settUtbetaltSum()
        );
    }

    private Hovedytelse lagHovedytelse(DateTime utbetalingsdato) {
        return new Hovedytelse()
                .withId("123")
                .withHovedytelsedato(utbetalingsdato)
                .withYtelsesperiode(new Interval(
                        new DateTime().withDate(2015,2,1),
                        new DateTime().withDate(2015,3,1)))
                .withUtbetaltTilKonto("123")
                .withUtbetaltTil(new Aktoer().withAktoerId("***REMOVED***"));
    }

    private Aktoer dummyAktoer() {
        return new Aktoer().withNavn("Ola Nordmann");
    }

}
