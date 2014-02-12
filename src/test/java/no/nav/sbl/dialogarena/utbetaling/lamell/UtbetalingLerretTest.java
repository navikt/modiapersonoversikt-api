package no.nav.sbl.dialogarena.utbetaling.lamell;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterFormPanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.TotalOppsummeringPanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.unntak.FeilmeldingPanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.unntak.UtbetalingerMessagePanel;
import no.nav.sbl.dialogarena.utbetaling.service.UtbetalingService;
import no.nav.sbl.dialogarena.utbetaling.wickettest.AbstractWicketTest;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
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
        wicketTester.should().containComponent(ofType(FeilmeldingPanel.class));
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
}
