package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.modig.modia.widget.panels.GenericListing;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.utbetaling.domain.Aktoer;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
import no.nav.sbl.dialogarena.utbetaling.wickettest.AbstractWicketTest;
import org.apache.wicket.model.util.ListModel;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.YtelseUtils.defaultSluttDato;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.YtelseUtils.defaultStartDato;
import static no.nav.sbl.dialogarena.utbetaling.widget.UtbetalingWidget.NUMBER_OF_MONTHS_TO_SHOW;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UtbetalingWidgetTest extends AbstractWicketTest {

    private static final String FNR = "11223312345";

    private UtbetalingService utbetalingService;
    private UtbetalingWidget utbetalingWidget;

    @Override
    protected void setup() {
        utbetalingService = mock(UtbetalingService.class);
        applicationContext.putBean("utbetalingService", utbetalingService);
        utbetalingWidget = new UtbetalingWidget("utbetalingWidget", "U", FNR);
        wicketTester.goToPageWith(utbetalingWidget);
    }

    @Test
    public void transformererKorrekteUtbetalingerTilVMer() {
        List<Record<Hovedytelse>> hovedytelser = Arrays.asList(
            new Record<Hovedytelse>()
                .with(Hovedytelse.id, "1")
                .with(Hovedytelse.nettoUtbetalt, 0D)
                .with(Hovedytelse.utbetaltTil, dummyAktoer())
                .with(Hovedytelse.hovedytelsedato, now()),
            new Record<Hovedytelse>()
                    .with(Hovedytelse.id, "2")
                    .with(Hovedytelse.nettoUtbetalt, 0D)
                    .with(Hovedytelse.utbetaltTil, dummyAktoer())
                    .with(Hovedytelse.hovedytelsedato, now().minusMonths(NUMBER_OF_MONTHS_TO_SHOW - 1)),
            new Record<Hovedytelse>()
                    .with(Hovedytelse.id, "3")
                    .with(Hovedytelse.nettoUtbetalt, 0D)
                    .with(Hovedytelse.utbetaltTil, dummyAktoer())
                    .with(Hovedytelse.hovedytelsedato, now().minusMonths(NUMBER_OF_MONTHS_TO_SHOW)),
            new Record<Hovedytelse>()
                    .with(Hovedytelse.id, "4")
                    .with(Hovedytelse.nettoUtbetalt, 0D)
                    .with(Hovedytelse.utbetaltTil, dummyAktoer())
                    .with(Hovedytelse.hovedytelsedato, now().minusMonths(NUMBER_OF_MONTHS_TO_SHOW).toDateMidnight().toDateTime().minusMillis(1)),
            new Record<Hovedytelse>()
                    .with(Hovedytelse.id, "5")
                    .with(Hovedytelse.nettoUtbetalt, 0D)
                    .with(Hovedytelse.utbetaltTil, dummyAktoer())
                    .with(Hovedytelse.hovedytelsedato, now().minusMonths(NUMBER_OF_MONTHS_TO_SHOW + 1))
        );

        List<HovedytelseVM> hovedytelseVMs = UtbetalingWidget.transformUtbetalingToVM(hovedytelser);

        assertThat(hovedytelseVMs.size(), is(3));
        assertThat(hovedytelseVMs.get(0).getId(), is("1"));
        assertThat(hovedytelseVMs.get(1).getId(), is("2"));
        assertThat(hovedytelseVMs.get(2).getId(), is("3"));
    }

    @Test
    public void ingenUtbetalingerModellHvisDetIkkeFinnesUtbetalinger() {
        List<Record<Hovedytelse>> emptyList = new ArrayList<>();
        when(utbetalingService.hentUtbetalinger(FNR, defaultStartDato(), defaultSluttDato())).thenReturn(emptyList);

        ListModel<?> listModel = utbetalingWidget.lagModell(FNR);

        assertThat(listModel.getObject().size(), is(1));
        assertThat(listModel.getObject().get(0), is(instanceOf(GenericListing.class)));
    }


    @Test
    public void utbetalingeneLagtTilModellenHvisDetFinnesUtbetalinger() {
        List<Record<Hovedytelse>> list = Arrays.asList(
                new Record<Hovedytelse>()
                        .with(Hovedytelse.hovedytelsedato, now())
                        .with(Hovedytelse.nettoUtbetalt, 0D)
                        .with(Hovedytelse.utbetaltTil, dummyAktoer())
                        .with(Hovedytelse.id, "1"),
                new Record<Hovedytelse>()
                        .with(Hovedytelse.hovedytelsedato, now())
                        .with(Hovedytelse.utbetaltTil, dummyAktoer())
                        .with(Hovedytelse.nettoUtbetalt, 0D)
                        .with(Hovedytelse.id, "2")
        );

        when(utbetalingService.hentUtbetalinger(FNR, defaultStartDato(), defaultSluttDato())).thenReturn(list);

        ListModel<?> listModel = utbetalingWidget.lagModell(FNR);

        assertThat(listModel.getObject().size(), is(2));
        assertThat(listModel.getObject().get(0), is(instanceOf(HovedytelseVM.class)));
    }

    private Record<Aktoer> dummyAktoer() {
        return new Record<Aktoer>()
                .with(Aktoer.navn, "Ola Nordmann");
    }

    @Test
    public void viserFireUtbetalingerSisteTreMaanedeneMedFireAktuelle() {
        List<Record<Hovedytelse>> list = Arrays.asList(
                new Record<Hovedytelse>()
                        .with(Hovedytelse.id, "1")
                        .with(Hovedytelse.nettoUtbetalt, 0D)
                        .with(Hovedytelse.hovedytelsedato, now().minusMonths(1))
                        .with(Hovedytelse.utbetaltTil, dummyAktoer())
                        .with(Hovedytelse.ytelsesperiode, new Interval(now().minusMonths(1), now())),
                new Record<Hovedytelse>()
                        .with(Hovedytelse.id, "2")
                        .with(Hovedytelse.nettoUtbetalt, 0D)
                        .with(Hovedytelse.hovedytelsedato, now().minusMonths(1))
                        .with(Hovedytelse.utbetaltTil, dummyAktoer())
                        .with(Hovedytelse.ytelsesperiode, new Interval(now().minusMonths(1), now())),
                new Record<Hovedytelse>()
                        .with(Hovedytelse.id, "3")
                        .with(Hovedytelse.nettoUtbetalt, 0D)
                        .with(Hovedytelse.hovedytelsedato, now().minusMonths(1))
                        .with(Hovedytelse.utbetaltTil, dummyAktoer())
                        .with(Hovedytelse.ytelsesperiode, new Interval(now().minusMonths(1), now())),
                new Record<Hovedytelse>()
                        .with(Hovedytelse.id, "4")
                        .with(Hovedytelse.nettoUtbetalt, 0D)
                        .with(Hovedytelse.hovedytelsedato, now().minusMonths(1))
                        .with(Hovedytelse.utbetaltTil, dummyAktoer())
                        .with(Hovedytelse.ytelsesperiode, new Interval(now().minusMonths(1), now()))
        );

        when(utbetalingService.hentUtbetalinger(Matchers.matches(FNR), Matchers.any(LocalDate.class), Matchers.any(LocalDate.class))).thenReturn(list);

        UtbetalingWidget utbetalingWidget = new UtbetalingWidget("utbetalingWidget", "initial", FNR);
        wicketTester.goToPageWith(utbetalingWidget);

        wicketTester.should().containComponents(4, is(ofType(UtbetalingWidgetPanel.class)));
        wicketTester.should().notContainPatterns("Vis alle utbetalingene");
    }

    @Test
    public void viserFemUtbetalingerSisteTreMaanedeneMedFemAktuelle(){
        List<Record<Hovedytelse>> list = Arrays.asList(
                new Record<Hovedytelse>()
                        .with(Hovedytelse.id, "1")
                        .with(Hovedytelse.nettoUtbetalt, 0D)
                        .with(Hovedytelse.hovedytelsedato, now().minusMonths(1))
                        .with(Hovedytelse.utbetaltTil, dummyAktoer())
                        .with(Hovedytelse.ytelsesperiode, new Interval(now().minusMonths(1), now())),
                new Record<Hovedytelse>()
                        .with(Hovedytelse.id, "2")
                        .with(Hovedytelse.nettoUtbetalt, 0D)
                        .with(Hovedytelse.hovedytelsedato, now().minusMonths(1))
                        .with(Hovedytelse.utbetaltTil, dummyAktoer())
                        .with(Hovedytelse.ytelsesperiode, new Interval(now().minusMonths(1), now())),
                new Record<Hovedytelse>()
                        .with(Hovedytelse.id, "3")
                        .with(Hovedytelse.nettoUtbetalt, 0D)
                        .with(Hovedytelse.hovedytelsedato, now().minusMonths(1))
                        .with(Hovedytelse.utbetaltTil, dummyAktoer())
                        .with(Hovedytelse.ytelsesperiode, new Interval(now().minusMonths(1), now())),
                new Record<Hovedytelse>()
                        .with(Hovedytelse.id, "4")
                        .with(Hovedytelse.nettoUtbetalt, 0D)
                        .with(Hovedytelse.hovedytelsedato, now().minusMonths(1))
                        .with(Hovedytelse.utbetaltTil, dummyAktoer())
                        .with(Hovedytelse.ytelsesperiode, new Interval(now().minusMonths(1), now())),
                new Record<Hovedytelse>()
                        .with(Hovedytelse.id, "5")
                        .with(Hovedytelse.nettoUtbetalt, 0D)
                        .with(Hovedytelse.hovedytelsedato, now().minusMonths(1))
                        .with(Hovedytelse.utbetaltTil, dummyAktoer())
                        .with(Hovedytelse.ytelsesperiode, new Interval(now().minusMonths(1), now()))
        );

        when(utbetalingService.hentUtbetalinger(Matchers.matches(FNR), Matchers.any(LocalDate.class), Matchers.any(LocalDate.class))).thenReturn(list);

        UtbetalingWidget utbetalingWidget = new UtbetalingWidget("utbetalingWidget","initial", FNR);
        wicketTester.goToPageWith(utbetalingWidget);

        wicketTester.should().containComponents(5,is(ofType(UtbetalingWidgetPanel.class)));
        wicketTester.should().notContainPatterns("Vis alle utbetalingene");
    }

    @Test
    public void viserFireUtbetalingerSisteTreMaanedeneMedSyvAktuelle(){
        List<Record<Hovedytelse>> list = Arrays.asList(
                new Record<Hovedytelse>()
                        .with(Hovedytelse.id, "1")
                        .with(Hovedytelse.nettoUtbetalt, 0D)
                        .with(Hovedytelse.hovedytelsedato, now().minusMonths(1))
                        .with(Hovedytelse.utbetaltTil, dummyAktoer())
                        .with(Hovedytelse.ytelsesperiode, new Interval(now().minusMonths(1), now())),
                new Record<Hovedytelse>()
                        .with(Hovedytelse.id, "2")
                        .with(Hovedytelse.nettoUtbetalt, 0D)
                        .with(Hovedytelse.hovedytelsedato, now().minusMonths(1))
                        .with(Hovedytelse.utbetaltTil, dummyAktoer())
                        .with(Hovedytelse.ytelsesperiode, new Interval(now().minusMonths(1), now())),
                new Record<Hovedytelse>()
                        .with(Hovedytelse.id, "3")
                        .with(Hovedytelse.nettoUtbetalt, 0D)
                        .with(Hovedytelse.hovedytelsedato, now().minusMonths(1))
                        .with(Hovedytelse.utbetaltTil, dummyAktoer())
                        .with(Hovedytelse.ytelsesperiode, new Interval(now().minusMonths(1), now())),
                new Record<Hovedytelse>()
                        .with(Hovedytelse.id, "4")
                        .with(Hovedytelse.nettoUtbetalt, 0D)
                        .with(Hovedytelse.hovedytelsedato, now().minusMonths(1))
                        .with(Hovedytelse.utbetaltTil, dummyAktoer())
                        .with(Hovedytelse.ytelsesperiode, new Interval(now().minusMonths(1), now())), new Record<Hovedytelse>()
                        .with(Hovedytelse.id, "5")
                        .with(Hovedytelse.nettoUtbetalt, 0D)
                        .with(Hovedytelse.hovedytelsedato, now().minusMonths(1))
                        .with(Hovedytelse.utbetaltTil, dummyAktoer())
                        .with(Hovedytelse.ytelsesperiode, new Interval(now().minusMonths(1), now())), new Record<Hovedytelse>()
                        .with(Hovedytelse.id, "6")
                        .with(Hovedytelse.nettoUtbetalt, 0D)
                        .with(Hovedytelse.hovedytelsedato, now().minusMonths(1))
                        .with(Hovedytelse.utbetaltTil, dummyAktoer())
                        .with(Hovedytelse.ytelsesperiode, new Interval(now().minusMonths(1), now())),
                new Record<Hovedytelse>()
                        .with(Hovedytelse.id, "7")
                        .with(Hovedytelse.nettoUtbetalt, 0D)
                        .with(Hovedytelse.hovedytelsedato, now().minusMonths(1))
                        .with(Hovedytelse.utbetaltTil, dummyAktoer())
                        .with(Hovedytelse.ytelsesperiode, new Interval(now().minusMonths(1), now()))
        );

        when(utbetalingService.hentUtbetalinger(Matchers.matches(FNR), Matchers.any(LocalDate.class), Matchers.any(LocalDate.class))).thenReturn(list);

        UtbetalingWidget utbetalingWidget = new UtbetalingWidget("utbetalingWidget","initial", FNR);
        wicketTester.goToPageWith(utbetalingWidget);

        wicketTester.should().containComponents(4,is(ofType(UtbetalingWidgetPanel.class)));
        wicketTester.should().containPatterns("Vis alle utbetalingene");
    }
}