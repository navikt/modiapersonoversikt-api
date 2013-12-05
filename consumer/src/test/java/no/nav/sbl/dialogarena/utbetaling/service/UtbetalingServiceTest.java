package no.nav.sbl.dialogarena.utbetaling.service;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSUtbetaling;
import no.nav.virksomhet.tjenester.utbetaling.meldinger.v2.WSHentUtbetalingListeRequest;
import no.nav.virksomhet.tjenester.utbetaling.meldinger.v2.WSHentUtbetalingListeResponse;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeBaksystemIkkeTilgjengelig;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeForMangeForekomster;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeMottakerIkkeFunnet;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeUgyldigDato;
import no.nav.virksomhet.tjenester.utbetaling.v2.UtbetalingPortType;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData.createUtbetaling1;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UtbetalingServiceTest {

    public static final String FNR = "12345678900";
    @InjectMocks
    private UtbetalingService service = new UtbetalingService();
    @Mock
    private UtbetalingPortType utbetalingPortType;

    @Test(expected = ApplicationException.class)
    public void testExceptions_hentUtbetalingListeMottakerIkkeFunnet() throws Exception {
        when(utbetalingPortType.hentUtbetalingListe(any(WSHentUtbetalingListeRequest.class))).thenThrow(new HentUtbetalingListeMottakerIkkeFunnet());
        service.hentUtbetalinger(FNR, new DateTime(), new DateTime());
    }

    @Test(expected = ApplicationException.class)
    public void testExceptions_HentUtbetalingListeForMangeForekomster() throws Exception {
        when(utbetalingPortType.hentUtbetalingListe(any(WSHentUtbetalingListeRequest.class))).thenThrow(new HentUtbetalingListeForMangeForekomster());
        service.hentUtbetalinger(FNR, new DateTime(), new DateTime());
    }

    @Test(expected = ApplicationException.class)
    public void testExceptions_HentUtbetalingListeBaksystemIkkeTilgjengelig() throws Exception {
        when(utbetalingPortType.hentUtbetalingListe(any(WSHentUtbetalingListeRequest.class))).thenThrow(new HentUtbetalingListeBaksystemIkkeTilgjengelig());
        service.hentUtbetalinger(FNR, new DateTime(), new DateTime());
    }

    @Test(expected = ApplicationException.class)
    public void testExceptions_HentUtbetalingListeUgyldigDato() throws Exception {
        when(utbetalingPortType.hentUtbetalingListe(any(WSHentUtbetalingListeRequest.class))).thenThrow(new HentUtbetalingListeUgyldigDato());
        service.hentUtbetalinger(FNR, new DateTime(), new DateTime());
    }

    @Test(expected = ApplicationException.class)
    public void testExceptions_UkjentFeil() throws Exception {
        when(utbetalingPortType.hentUtbetalingListe(any(WSHentUtbetalingListeRequest.class))).thenThrow(new RuntimeException());
        service.hentUtbetalinger(FNR, new DateTime(), new DateTime());
    }

    @Test
    public void skalTransformereUtbetaling() throws Exception {
        WSUtbetaling wsUtbetaling = createUtbetaling1();
        String alderspensjon = "Dagpenger";

        when(utbetalingPortType.hentUtbetalingListe(any(WSHentUtbetalingListeRequest.class))).thenReturn(new WSHentUtbetalingListeResponse().withUtbetalingListe(wsUtbetaling));
        Utbetaling u = service.hentUtbetalinger(FNR, new DateTime(), new DateTime()).get(0);

        assertThat(u.getUtbetalingsDato(), is(wsUtbetaling.getUtbetalingDato()));
        assertThat(u.getStartDate(), is(wsUtbetaling.getUtbetalingsPeriode().getPeriodeFomDato()));
        assertThat(u.getEndDate(), is(wsUtbetaling.getUtbetalingsPeriode().getPeriodeTomDato()));
        assertThat(u.getNettoBelop(), is(wsUtbetaling.getNettobelop()));
        assertThat(u.getBruttoBelop(), is(wsUtbetaling.getBruttobelop()));
        assertThat(u.getBeskrivelse(), is(alderspensjon));
    }
}