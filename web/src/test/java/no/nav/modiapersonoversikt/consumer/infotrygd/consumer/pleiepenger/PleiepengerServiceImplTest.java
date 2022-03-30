package no.nav.modiapersonoversikt.consumer.infotrygd.consumer.pleiepenger;

import no.nav.modiapersonoversikt.consumer.infotrygd.consumer.pleiepenger.mapping.to.PleiepengerListeRequest;
import no.nav.modiapersonoversikt.consumer.infotrygd.consumer.pleiepenger.mapping.to.PleiepengerListeResponse;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.HentPleiepengerettighetSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.HentPleiepengerettighetUgyldigIdentNr;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.PleiepengerV1;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.informasjon.WSPerson;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.informasjon.WSPleiepengerettighet;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.meldinger.WSHentPleiepengerettighetRequest;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.meldinger.WSHentPleiepengerettighetResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PleiepengerServiceImplTest {

    private PleiepengerService pleiepengerService;
    private PleiepengerV1 pleiepengerV1;

    @Before
    public void setup() {
        pleiepengerV1 = mock(PleiepengerV1.class);
        pleiepengerService = new PleiepengerServiceImpl(pleiepengerV1);
    }

    @Test
    public void hentPleiepengerListeHenterTomListe() throws HentPleiepengerettighetUgyldigIdentNr, HentPleiepengerettighetSikkerhetsbegrensning {
        when(pleiepengerV1.hentPleiepengerettighet(any(WSHentPleiepengerettighetRequest.class)))
                .thenReturn(new WSHentPleiepengerettighetResponse());

        PleiepengerListeResponse response = pleiepengerService.hentPleiepengerListe(new PleiepengerListeRequest("10108000398"));

        assertThat(response.getPleieepengerettighetListe().size(), is(0));
    }

    @Test
    public void hentPleiepengerListeHenterListeMedElementer() throws HentPleiepengerettighetUgyldigIdentNr, HentPleiepengerettighetSikkerhetsbegrensning {
        when(pleiepengerV1.hentPleiepengerettighet(any(WSHentPleiepengerettighetRequest.class)))
                .thenReturn(new WSHentPleiepengerettighetResponse()
                        .withPleiepengerettighetListe(Collections.singletonList(new WSPleiepengerettighet()
                                .withOmsorgsperson(new WSPerson())
                                .withBarnet(new WSPerson()))));

        PleiepengerListeResponse response = pleiepengerService.hentPleiepengerListe(new PleiepengerListeRequest("10108000398"));

        assertThat(response.getPleieepengerettighetListe().size(), is(1));
    }

    @Test(expected = RuntimeException.class)
    public void hentPleiepengerListeSikkerhetsbegresning() throws HentPleiepengerettighetUgyldigIdentNr, HentPleiepengerettighetSikkerhetsbegrensning {
        when(pleiepengerV1.hentPleiepengerettighet(any(WSHentPleiepengerettighetRequest.class)))
                .thenThrow(new HentPleiepengerettighetSikkerhetsbegrensning());

        pleiepengerService.hentPleiepengerListe(new PleiepengerListeRequest("10108000398"));
    }

    @Test(expected = RuntimeException.class)
    public void hentPleiepengerListeUgyldigIdentNr() throws HentPleiepengerettighetUgyldigIdentNr, HentPleiepengerettighetSikkerhetsbegrensning {
        when(pleiepengerV1.hentPleiepengerettighet(any(WSHentPleiepengerettighetRequest.class)))
                .thenThrow(new HentPleiepengerettighetUgyldigIdentNr());

        pleiepengerService.hentPleiepengerListe(new PleiepengerListeRequest("10108000398"));
    }

}
