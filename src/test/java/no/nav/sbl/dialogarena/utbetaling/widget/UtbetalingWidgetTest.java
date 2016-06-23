package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.sbl.dialogarena.utbetaling.domain.Aktoer;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
import no.nav.sbl.dialogarena.utbetaling.wickettest.AbstractWicketTest;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.Arrays;
import java.util.List;

import static no.nav.sbl.dialogarena.utbetaling.widget.UtbetalingWidget.NUMBER_OF_DAYS_TO_SHOW;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UtbetalingWidgetTest extends AbstractWicketTest {

    private static final String FNR = "***REMOVED***";
    private static final int MIDNIGHT_AT_DAY_BEFORE = 1;

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
        List<Hovedytelse> hovedytelser = Arrays.asList(
            new Hovedytelse()
                .withId("1")
                .withNettoUtbetalt(0D)
                .withUtbetaltTil(dummyAktoer())
                .withHovedytelsedato(now().minusDays(MIDNIGHT_AT_DAY_BEFORE)),
            new Hovedytelse()
                    .withId("2")
                    .withNettoUtbetalt(0D)
                    .withUtbetaltTil(dummyAktoer())
                    .withHovedytelsedato(now().minusDays(NUMBER_OF_DAYS_TO_SHOW + MIDNIGHT_AT_DAY_BEFORE - 1)),
            new Hovedytelse()
                    .withId("3")
                    .withNettoUtbetalt(0D)
                    .withUtbetaltTil(dummyAktoer())
                    .withHovedytelsedato(now().minusDays(NUMBER_OF_DAYS_TO_SHOW + MIDNIGHT_AT_DAY_BEFORE)),
            new Hovedytelse()
                    .withId("4")
                    .withNettoUtbetalt(0D)
                    .withUtbetaltTil(dummyAktoer())
                    .withHovedytelsedato(now().minusDays(NUMBER_OF_DAYS_TO_SHOW + MIDNIGHT_AT_DAY_BEFORE).toDateMidnight().toDateTime().minusMillis(1)),
            new Hovedytelse()
                    .withId("5")
                    .withNettoUtbetalt(0D)
                    .withUtbetaltTil(dummyAktoer())
                    .withHovedytelsedato(now().minusDays(NUMBER_OF_DAYS_TO_SHOW + MIDNIGHT_AT_DAY_BEFORE + 1))
        );

        List<HovedytelseVM> hovedytelseVMs = UtbetalingWidget.transformUtbetalingToVM(hovedytelser);

        assertThat(hovedytelseVMs.size(), is(3));
        assertThat(hovedytelseVMs.get(0).getId(), is("1"));
        assertThat(hovedytelseVMs.get(1).getId(), is("2"));
        assertThat(hovedytelseVMs.get(2).getId(), is("3"));
    }

    private Aktoer dummyAktoer() {
        return new Aktoer()
                .withNavn("Ola Nordmann");
    }

    @Test
    public void viserFireUtbetalingerSisteTreMaanedeneMedFireAktuelle() throws Exception {
        List<Hovedytelse> list = Arrays.asList(
                new Hovedytelse()
                        .withId("1")
                        .withNettoUtbetalt(0D)
                        .withHovedytelsedato(now().minusDays(NUMBER_OF_DAYS_TO_SHOW))
                        .withUtbetaltTil(dummyAktoer())
                        .withYtelsesperiode(new Interval(now().minusDays(NUMBER_OF_DAYS_TO_SHOW), now())),
                new Hovedytelse()
                        .withId("2")
                        .withNettoUtbetalt(0D)
                        .withHovedytelsedato(now().minusDays(NUMBER_OF_DAYS_TO_SHOW))
                        .withUtbetaltTil(dummyAktoer())
                        .withYtelsesperiode(new Interval(now().minusDays(NUMBER_OF_DAYS_TO_SHOW), now())),
                new Hovedytelse()
                        .withId("3")
                        .withNettoUtbetalt(0D)
                        .withHovedytelsedato(now().minusDays(NUMBER_OF_DAYS_TO_SHOW))
                        .withUtbetaltTil(dummyAktoer())
                        .withYtelsesperiode(new Interval(now().minusDays(NUMBER_OF_DAYS_TO_SHOW), now())),
                new Hovedytelse()
                        .withId("4")
                        .withNettoUtbetalt(0D)
                        .withHovedytelsedato(now().minusDays(NUMBER_OF_DAYS_TO_SHOW))
                        .withUtbetaltTil(dummyAktoer())
                        .withYtelsesperiode(new Interval(now().minusDays(NUMBER_OF_DAYS_TO_SHOW), now()))
        );

        when(utbetalingService.hentUtbetalinger(Matchers.matches(FNR), Matchers.any(LocalDate.class), Matchers.any(LocalDate.class))).thenReturn(list);

        List<HovedytelseVM> feedItems = new UtbetalingWidget("utbetalingWidget", "initial", FNR).getFeedItems();

        assertThat(feedItems, hasSize(4));
    }

    @Test
    public void viserFemUtbetalingerSisteTreMaanedeneMedFemAktuelle() throws Exception {
        List<Hovedytelse> list = Arrays.asList(
                new Hovedytelse()
                        .withId("1")
                        .withNettoUtbetalt(0D)
                        .withHovedytelsedato(now().minusDays(NUMBER_OF_DAYS_TO_SHOW))
                        .withUtbetaltTil(dummyAktoer())
                        .withYtelsesperiode(new Interval(now().minusDays(NUMBER_OF_DAYS_TO_SHOW), now())),
                new Hovedytelse()
                        .withId("2")
                        .withNettoUtbetalt(0D)
                        .withHovedytelsedato(now().minusDays(NUMBER_OF_DAYS_TO_SHOW))
                        .withUtbetaltTil(dummyAktoer())
                        .withYtelsesperiode(new Interval(now().minusDays(NUMBER_OF_DAYS_TO_SHOW), now())),
                new Hovedytelse()
                        .withId("3")
                        .withNettoUtbetalt(0D)
                        .withHovedytelsedato(now().minusDays(NUMBER_OF_DAYS_TO_SHOW))
                        .withUtbetaltTil(dummyAktoer())
                        .withYtelsesperiode(new Interval(now().minusDays(NUMBER_OF_DAYS_TO_SHOW), now())),
                new Hovedytelse()
                        .withId("4")
                        .withNettoUtbetalt(0D)
                        .withHovedytelsedato(now().minusDays(NUMBER_OF_DAYS_TO_SHOW))
                        .withUtbetaltTil(dummyAktoer())
                        .withYtelsesperiode(new Interval(now().minusDays(NUMBER_OF_DAYS_TO_SHOW), now())),
                new Hovedytelse()
                        .withId("5")
                        .withNettoUtbetalt(0D)
                        .withHovedytelsedato(now().minusDays(NUMBER_OF_DAYS_TO_SHOW))
                        .withUtbetaltTil(dummyAktoer())
                        .withYtelsesperiode(new Interval(now().minusDays(NUMBER_OF_DAYS_TO_SHOW), now()))
        );

        when(utbetalingService.hentUtbetalinger(Matchers.matches(FNR), Matchers.any(LocalDate.class), Matchers.any(LocalDate.class))).thenReturn(list);

        List<HovedytelseVM> feedItems = new UtbetalingWidget("utbetalingWidget", "initial", FNR).getFeedItems();

        assertThat(feedItems, hasSize(5));
    }

}