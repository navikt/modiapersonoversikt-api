package no.nav.sbl.dialogarena.utbetaling.lamell;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterFormPanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.TotalOppsummeringPanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.unntak.UtbetalingerMessagePanel;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingsResultat;
import no.nav.sbl.dialogarena.utbetaling.wickettest.AbstractWicketTest;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.getBuilder;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UtbetalingLerretTest extends AbstractWicketTest{

    @Mock
    private UtbetalingService service;
    private UtbetalingLerret utbetalingLerret;

    @Override
    protected void setup() {
        applicationContext.putBean(service);

        utbetalingLerret = new UtbetalingLerret("lerret", "");
        wicketTester.goToPageWith(utbetalingLerret);
    }

    @Test
    public void skalInneholdeKorrekteKomponenter() {
        wicketTester.should().containComponent(withId("arenalink"));
        wicketTester.should().containComponent(ofType(FilterFormPanel.class));
        wicketTester.should().containComponent(ofType(TotalOppsummeringPanel.class));
        wicketTester.should().containComponent(withId("utbetalingslisteContainer"));
        wicketTester.should().containComponent(ofType(UtbetalingerMessagePanel.class));
    }

    @Test
    public void skalViseFeilmeldingpanelNaarTjenestenFeiler() {
        when(service.hentUtbetalinger(any(String.class), any(LocalDate.class), any(LocalDate.class))).thenThrow(new ApplicationException("OMG"));

        UtbetalingLerret utbetalingLerretMedException = new UtbetalingLerret("lerret", "");
        wicketTester.goToPageWith(utbetalingLerretMedException);

        wicketTester.tester.assertVisible("lerret:feilmelding");
    }

    @Test
    public void skalIkkeKalleServiceOmFilterintervallErInnenforCacheintervall() {
        verify(service, times(1)).hentUtbetalinger(any(String.class), any(LocalDate.class), any(LocalDate.class));
        utbetalingLerret.oppdaterCacheOmNodvendig();
        verifyNoMoreInteractions(service);
    }

    @Test
    public void beholderIkkeDuplikater() {
        Utbetaling idag = getBuilder("1").withUtbetalingsDato(DateTime.now()).build();
        Utbetaling igaar = getBuilder("2").withUtbetalingsDato(DateTime.now().minusDays(1)).build();
        Utbetaling forrigeUke = getBuilder("3").withUtbetalingsDato(DateTime.now().minusWeeks(1)).build();
        Utbetaling forrigeMaaned = getBuilder("4").withUtbetalingsDato(DateTime.now().minusMonths(1)).build();
        String fnr = "fnr";

        UtbetalingsResultat resultat = new UtbetalingsResultat(fnr, forrigeUke.getUtbetalingsdato().toLocalDate(), igaar.getUtbetalingsdato().toLocalDate(), asList(forrigeUke, igaar));
        when(service.hentUtbetalinger(fnr, forrigeMaaned.getUtbetalingsdato().toLocalDate(), idag.getUtbetalingsdato().toLocalDate()))
                .thenReturn(asList(forrigeMaaned, forrigeUke, igaar, idag));

        UtbetalingsResultat nyttResultat = utbetalingLerret.oppdaterCache(resultat, new Interval(forrigeMaaned.getUtbetalingsdato(), idag.getUtbetalingsdato()));

        assertEquals(2, nyttResultat.intervaller.size());
        assertThat(nyttResultat.utbetalinger, containsInAnyOrder(forrigeMaaned, forrigeUke, igaar, idag));
    }

}