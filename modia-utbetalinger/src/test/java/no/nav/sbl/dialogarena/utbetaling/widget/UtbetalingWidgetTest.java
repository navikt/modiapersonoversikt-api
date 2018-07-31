package no.nav.sbl.dialogarena.utbetaling.widget;

import no.nav.sbl.dialogarena.utbetaling.domain.Aktoer;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedutbetaling;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
import no.nav.sbl.dialogarena.utbetaling.wickettest.AbstractWicketTest;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSUtbetaling;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData.createUtbetalingMedValgtUtbetalingsdatoForfallsdatoOgPosteringsdato;
import static no.nav.sbl.dialogarena.utbetaling.widget.UtbetalingWidget.NUMBER_OF_DAYS_TO_SHOW;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UtbetalingWidgetTest extends AbstractWicketTest {

    private static final String FNR = "5";
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
        List<Hovedutbetaling> hovedutbetalinger = asList(
                new Hovedutbetaling()
                        .withId("1")
                        .withHovedytelsesdato(now().minusDays(MIDNIGHT_AT_DAY_BEFORE))
                        .withHovedytelser(singletonList(
                                new Hovedytelse()
                                        .withId("1")
                                        .withNettoUtbetalt(0D)
                                        .withUtbetaltTil(dummyAktoer())
                                        .withHovedytelsedato(now().minusDays(MIDNIGHT_AT_DAY_BEFORE))
                                        .withNettoUtbetalt(123.4)
                                        .withYtelse("testytelse"))),
                new Hovedutbetaling()
                        .withId("2")
                        .withHovedytelsesdato(now().minusDays(NUMBER_OF_DAYS_TO_SHOW + MIDNIGHT_AT_DAY_BEFORE - 1))
                        .withHovedytelser(singletonList(
                                new Hovedytelse()
                                        .withId("2")
                                        .withNettoUtbetalt(0D)
                                        .withUtbetaltTil(dummyAktoer())
                                        .withHovedytelsedato(now().minusDays(NUMBER_OF_DAYS_TO_SHOW + MIDNIGHT_AT_DAY_BEFORE - 1))
                                        .withNettoUtbetalt(123.4)
                                        .withYtelse("testytelse"))),
        new Hovedutbetaling()
                        .withId("3")
                        .withHovedytelsesdato(now().minusDays(NUMBER_OF_DAYS_TO_SHOW + MIDNIGHT_AT_DAY_BEFORE))
                        .withHovedytelser(singletonList(
                                new Hovedytelse()
                                        .withId("3")
                                        .withNettoUtbetalt(0D)
                                        .withUtbetaltTil(dummyAktoer())
                                        .withHovedytelsedato(now().minusDays(NUMBER_OF_DAYS_TO_SHOW + MIDNIGHT_AT_DAY_BEFORE))
                                        .withNettoUtbetalt(123.4)
                                        .withYtelse("testytelse"))),
                new Hovedutbetaling()
                        .withId("4")
                        .withHovedytelsesdato(now().minusDays(NUMBER_OF_DAYS_TO_SHOW + MIDNIGHT_AT_DAY_BEFORE).toDateMidnight().toDateTime().minusMillis(1))
                        .withHovedytelser(singletonList(
                                new Hovedytelse()
                                        .withId("4")
                                        .withNettoUtbetalt(0D)
                                        .withUtbetaltTil(dummyAktoer())
                                        .withHovedytelsedato(now().minusDays(NUMBER_OF_DAYS_TO_SHOW + MIDNIGHT_AT_DAY_BEFORE).toDateMidnight().toDateTime().minusMillis(1))
                                        .withNettoUtbetalt(123.4)
                                        .withYtelse("testytelse"))),
                new Hovedutbetaling()
                        .withId("5")
                        .withHovedytelsesdato(now().minusDays(NUMBER_OF_DAYS_TO_SHOW + MIDNIGHT_AT_DAY_BEFORE + 1))
                        .withHovedytelser(singletonList(
                                new Hovedytelse()
                                        .withId("5")
                                        .withNettoUtbetalt(0D)
                                        .withUtbetaltTil(dummyAktoer())
                                        .withHovedytelsedato(now().minusDays(NUMBER_OF_DAYS_TO_SHOW + MIDNIGHT_AT_DAY_BEFORE + 1))
                                        .withNettoUtbetalt(123.4)
                                        .withYtelse("testytelse")))
                );

        List<HovedutbetalingVM> hovedutbetalingVMs = UtbetalingWidget.transformUtbetalingToVM(hovedutbetalinger);

        assertThat(hovedutbetalingVMs.size(), is(3));
        assertThat(hovedutbetalingVMs.get(0).getId(), is("1"));
        assertThat(hovedutbetalingVMs.get(1).getId(), is("2"));
        assertThat(hovedutbetalingVMs.get(2).getId(), is("3"));
    }

    private Aktoer dummyAktoer() {
        return new Aktoer()
                .withNavn("Ola Nordmann");
    }

    @Test
    public void viserToUtbetalingerSisteTrettiDagerMedToAktuelle() throws Exception {
        List<WSUtbetaling> liste = asList(
                createUtbetalingMedValgtUtbetalingsdatoForfallsdatoOgPosteringsdato(now().minusDays(NUMBER_OF_DAYS_TO_SHOW + 10), null, now().minusDays(NUMBER_OF_DAYS_TO_SHOW + 10)),
                createUtbetalingMedValgtUtbetalingsdatoForfallsdatoOgPosteringsdato(now().minusDays(NUMBER_OF_DAYS_TO_SHOW), null, now().minusDays(NUMBER_OF_DAYS_TO_SHOW))
        );

        when(utbetalingService.hentWSUtbetalinger(Matchers.matches(FNR), any(LocalDate.class), any(LocalDate.class))).thenReturn(liste);

        List<HovedutbetalingVM> feedItems = new UtbetalingWidget("utbetalingWidget", "initial", FNR).getFeedItems();

        assertThat(feedItems, hasSize(1));
    }

    @Test
    public void viserToUtbetalingerSisteTreTrettiDagerMedToAktuelle() throws Exception {

        List<WSUtbetaling> liste = asList(
                createUtbetalingMedValgtUtbetalingsdatoForfallsdatoOgPosteringsdato(now().minusDays(NUMBER_OF_DAYS_TO_SHOW), null, now().minusDays(NUMBER_OF_DAYS_TO_SHOW)),
                createUtbetalingMedValgtUtbetalingsdatoForfallsdatoOgPosteringsdato(now().minusDays(NUMBER_OF_DAYS_TO_SHOW), null, now().minusDays(NUMBER_OF_DAYS_TO_SHOW))
        );

        when(utbetalingService.hentWSUtbetalinger(Matchers.matches(FNR), any(LocalDate.class), any(LocalDate.class))).thenReturn(liste);

        List<HovedutbetalingVM> feedItems = new UtbetalingWidget("utbetalingWidget", "initial", FNR).getFeedItems();

        assertThat(feedItems, hasSize(2));
    }

}