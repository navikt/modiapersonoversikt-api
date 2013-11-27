package no.nav.sbl.dialogarena.utbetaling.service;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSUtbetaling;
import no.nav.virksomhet.tjenester.utbetaling.meldinger.v2.WSHentUtbetalingListeRequest;
import no.nav.virksomhet.tjenester.utbetaling.meldinger.v2.WSHentUtbetalingListeResponse;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeBaksystemIkkeTilgjengelig;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeForMangeForekomster;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeMottakerIkkeFunnet;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeUgyldigDato;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UtbetalingServiceTest {

    @InjectMocks
    private UtbetalingService service = new UtbetalingService();
    @Mock
    private no.nav.virksomhet.tjenester.utbetaling.v2.Utbetaling utbetaling;

    public static final String fnr = "12345678900";
    WSUtbetalingTestData data = new WSUtbetalingTestData();

    @Test(expected = ApplicationException.class)
    public void testExceptions_hentUtbetalingListeMottakerIkkeFunnet() throws Exception {
        when(utbetaling.hentUtbetalingListe(any(WSHentUtbetalingListeRequest.class))).thenThrow(new HentUtbetalingListeMottakerIkkeFunnet());
        service.hentUtbetalinger(fnr, new DateTime(), new DateTime());
    }

    @Test(expected = ApplicationException.class)
    public void testExceptions_HentUtbetalingListeForMangeForekomster() throws Exception {
        when(utbetaling.hentUtbetalingListe(any(WSHentUtbetalingListeRequest.class))).thenThrow(new HentUtbetalingListeForMangeForekomster());
        service.hentUtbetalinger(fnr, new DateTime(), new DateTime());
    }

    @Test(expected = ApplicationException.class)
    public void testExceptions_HentUtbetalingListeBaksystemIkkeTilgjengelig() throws Exception {
        when(utbetaling.hentUtbetalingListe(any(WSHentUtbetalingListeRequest.class))).thenThrow(new HentUtbetalingListeBaksystemIkkeTilgjengelig());
        service.hentUtbetalinger(fnr, new DateTime(), new DateTime());
    }

    @Test(expected = ApplicationException.class)
    public void testExceptions_HentUtbetalingListeUgyldigDato() throws Exception {
        when(utbetaling.hentUtbetalingListe(any(WSHentUtbetalingListeRequest.class))).thenThrow(new HentUtbetalingListeUgyldigDato());
        service.hentUtbetalinger(fnr, new DateTime(), new DateTime());
    }

    @Test(expected = ApplicationException.class)
    public void testExceptions_UkjentFeil() throws Exception {
        when(utbetaling.hentUtbetalingListe(any(WSHentUtbetalingListeRequest.class))).thenThrow(new RuntimeException());
        service.hentUtbetalinger(fnr, new DateTime(), new DateTime());
    }

    @Test
    public void skalTransformereUtbetaling() throws Exception {
        WSUtbetaling wsUtbetaling = data.createUtbetaling1();
        String alderspensjon = "Alderspensjon";

        when(utbetaling.hentUtbetalingListe(any(WSHentUtbetalingListeRequest.class))).thenReturn(new WSHentUtbetalingListeResponse().withUtbetalingListe(wsUtbetaling));
        Utbetaling u = service.hentUtbetalinger(fnr, new DateTime(), new DateTime()).get(0);

        assertThat(u.getUtbetalingsDato(), is(wsUtbetaling.getUtbetalingDato()));
        assertThat(u.getStartDate(), is(wsUtbetaling.getUtbetalingsPeriode().getPeriodeFomDato()));
        assertThat(u.getEndDate(), is(wsUtbetaling.getUtbetalingsPeriode().getPeriodeTomDato()));
        assertThat(u.getNettoBelop(), is(wsUtbetaling.getNettobelop()));
        assertThat(u.getBruttoBelop(), is(wsUtbetaling.getBruttobelop()));
        assertThat(u.getBeskrivelse(), is(alderspensjon));
    }
}