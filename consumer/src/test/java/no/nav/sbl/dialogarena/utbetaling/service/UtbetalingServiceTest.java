package no.nav.sbl.dialogarena.utbetaling.service;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.domain.WSUtbetalingTestData;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSMelding;
import no.nav.virksomhet.okonomi.utbetaling.v2.WSUtbetaling;
import no.nav.virksomhet.tjenester.utbetaling.meldinger.v2.WSHentUtbetalingListeRequest;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeBaksystemIkkeTilgjengelig;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeForMangeForekomster;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeMottakerIkkeFunnet;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeUgyldigDato;
import no.nav.virksomhet.tjenester.utbetaling.v2.UtbetalingPortType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UtbetalingServiceTest {

    @InjectMocks
    private UtbetalingService service = new UtbetalingService();
    @Mock
    private UtbetalingPortType utbetalingPortType;

    String fnr = "12345678900";
    WSUtbetalingTestData data = new WSUtbetalingTestData();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = ApplicationException.class)
    public void testExceptions_hentUtbetalingListeMottakerIkkeFunnet() throws Exception {
        when(utbetalingPortType.hentUtbetalingListe(any(WSHentUtbetalingListeRequest.class))).thenThrow(new HentUtbetalingListeMottakerIkkeFunnet());
        service.getWSUtbetalinger(fnr);
    }

    @Test(expected = ApplicationException.class)
    public void testExceptions_HentUtbetalingListeForMangeForekomster() throws Exception {
        when(utbetalingPortType.hentUtbetalingListe(any(WSHentUtbetalingListeRequest.class))).thenThrow(new HentUtbetalingListeForMangeForekomster());
        service.getWSUtbetalinger(fnr);
    }

    @Test(expected = ApplicationException.class)
    public void testExceptions_HentUtbetalingListeBaksystemIkkeTilgjengelig() throws Exception {
        when(utbetalingPortType.hentUtbetalingListe(any(WSHentUtbetalingListeRequest.class))).thenThrow(new HentUtbetalingListeBaksystemIkkeTilgjengelig());
        service.getWSUtbetalinger(fnr);
    }

    @Test(expected = ApplicationException.class)
    public void testExceptions_HentUtbetalingListeUgyldigDato() throws Exception {
        when(utbetalingPortType.hentUtbetalingListe(any(WSHentUtbetalingListeRequest.class))).thenThrow(new HentUtbetalingListeUgyldigDato());
        service.getWSUtbetalinger(fnr);
    }

    @Test
    public void transformerUtbetaling() throws Exception {
        WSUtbetaling wsUtbetaling = data.createUtbetaling1();
        DateTime fraDateTime = new DateTime(2010, 1, 23, 0, 0);
        DateTime tilDateTime = new DateTime(2011, 1, 24, 0, 0);
        DateTime utbDato = now().minusDays(4);
        String alderspensjon = "Alderspensjon";
        String skatt = "Skatt";
        String kontoNr = "12345678900";
        String meldingsTekst = "bilag1";
        WSMelding melding = new WSMelding().withMeldingtekst(meldingsTekst);

        Utbetaling u = service.transformUtbetalinger(Arrays.asList(wsUtbetaling)).get(0);

        int compareUtbetalingsDato = DateTimeComparator.getDateOnlyInstance().compare(utbDato, u.getUtbetalingsDato());
        assertThat(compareUtbetalingsDato, is(0));
        assertThat(u.getStartDate(), is(fraDateTime));
        assertThat(u.getEndDate(), is(tilDateTime));
        assertThat(u.getNettoBelop(), is(1000.0));
        assertThat(u.getBruttoBelop(), is(1000.0));
        assertThat(u.getBeskrivelse(), is(alderspensjon + ", " + skatt));
        assertThat(u.getStatuskode(), is("12"));
        assertThat(u.getKontoNr(), is(kontoNr));
        assertThat(u.getBilag().size(), is(2));
        assertThat(u.getBilag().get(0).getMelding(), is(equalTo(melding.getMeldingtekst())));
        assertThat(u.getBilag().get(0).getPosteringsDetaljer().size(), is(1));
        assertThat(u.getBilag().get(0).getPosteringsDetaljer().get(0).getHovedBeskrivelse(), is(alderspensjon));
        assertThat(u.getBilag().get(0).getPosteringsDetaljer().get(0).getKontoNr(), is(fnr));
        assertThat(u.getBilag().get(0).getPosteringsDetaljer().get(0).getHovedBeskrivelse(), is(alderspensjon));
        assertThat(u.getBilag().get(0).getPosteringsDetaljer().get(0).getKontoNr(), is(kontoNr));
        assertThat(u.getBilag().get(1).getPosteringsDetaljer().size(), is(1));
        assertThat(u.getBilag().get(1).getPosteringsDetaljer().get(0).getKontoNr(), is(kontoNr));
        assertThat(u.getBilag().get(1).getPosteringsDetaljer().get(0).getHovedBeskrivelse(), is(skatt));
        assertThat(u.getBilag().get(1).getPosteringsDetaljer().get(0).getHovedBeskrivelse(), is(skatt));

    }
}