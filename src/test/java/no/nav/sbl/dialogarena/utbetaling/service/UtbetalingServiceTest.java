package no.nav.sbl.dialogarena.utbetaling.service;

import no.nav.modig.core.exception.SystemException;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.virksomhet.tjenester.utbetaling.meldinger.v2.WSHentUtbetalingListeRequest;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeBaksystemIkkeTilgjengelig;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeForMangeForekomster;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeMottakerIkkeFunnet;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeUgyldigDato;
import no.nav.virksomhet.tjenester.utbetaling.v2.UtbetalingPortType;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

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

    public void skalReturnereTomListeMedUtbetalingerHvisPersonIkkeFinnes() throws Exception {
        when(utbetalingPortType.hentUtbetalingListe(any(WSHentUtbetalingListeRequest.class))).thenThrow(new HentUtbetalingListeMottakerIkkeFunnet());
        List<Utbetaling> utbetalinger = service.hentUtbetalinger(FNR, new LocalDate(), new LocalDate());
        assertThat(utbetalinger.isEmpty(), is(true));
    }

    @Test(expected = SystemException.class)
    public void testExceptions_HentUtbetalingListeForMangeForekomster() throws Exception {
        when(utbetalingPortType.hentUtbetalingListe(any(WSHentUtbetalingListeRequest.class))).thenThrow(new HentUtbetalingListeForMangeForekomster());
        service.hentUtbetalinger(FNR, new LocalDate(), new LocalDate());
    }

    @Test(expected = SystemException.class)
    public void testExceptions_HentUtbetalingListeBaksystemIkkeTilgjengelig() throws Exception {
        when(utbetalingPortType.hentUtbetalingListe(any(WSHentUtbetalingListeRequest.class))).thenThrow(new HentUtbetalingListeBaksystemIkkeTilgjengelig());
        service.hentUtbetalinger(FNR, new LocalDate(), new LocalDate());
    }

    @Test(expected = SystemException.class)
    public void testExceptions_HentUtbetalingListeUgyldigDato() throws Exception {
        when(utbetalingPortType.hentUtbetalingListe(any(WSHentUtbetalingListeRequest.class))).thenThrow(new HentUtbetalingListeUgyldigDato());
        service.hentUtbetalinger(FNR, new LocalDate(), new LocalDate());
    }

    @Test(expected = SystemException.class)
    public void testExceptions_UkjentFeil() throws Exception {
        when(utbetalingPortType.hentUtbetalingListe(any(WSHentUtbetalingListeRequest.class))).thenThrow(new RuntimeException());
        service.hentUtbetalinger(FNR, new LocalDate(), new LocalDate());
    }

}