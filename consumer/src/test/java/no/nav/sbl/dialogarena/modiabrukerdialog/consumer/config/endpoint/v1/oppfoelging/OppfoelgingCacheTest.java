package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.oppfoelging;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.OppfoelgingPortType;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.informasjon.WSOppfoelgingskontrakt;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.meldinger.WSHentOppfoelgingskontraktListeRequest;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.meldinger.WSHentOppfoelgingskontraktListeResponse;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.meldinger.WSHentOppfoelgingsstatusRequest;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.meldinger.WSHentOppfoelgingsstatusResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OppfoelgingCacheTest extends CacheTest {

    private static final String OPPFOELGING_CACHE = "oppfolgingCache";
    private static final String FODSELSNUMMER_AREMARK = "10108000398";
    private static final String FODSELSNUMMER_TROGSTAD = "06128074978";
    private static final String OPPFOELGINGSENHET_1 = "0118";
    private static final String OPPFOELGINGSENHET_2 = "0119";

    OppfoelgingCacheTest() {
        super(OPPFOELGING_CACHE);
    }

    @Inject
    private OppfoelgingPortType oppfolgingPortType;

    @BeforeEach
    void setUpMock() throws Exception {
        OppfoelgingPortType unwrapped = (OppfoelgingPortType) unwrapProxy(oppfolgingPortType);
        reset(unwrapped);

        WSHentOppfoelgingsstatusResponse response1 = new WSHentOppfoelgingsstatusResponse();
        response1.setNavOppfoelgingsenhet(OPPFOELGINGSENHET_1);
        WSHentOppfoelgingsstatusResponse response2 = new WSHentOppfoelgingsstatusResponse();
        response2.setNavOppfoelgingsenhet(OPPFOELGINGSENHET_2);

        when(unwrapped.hentOppfoelgingsstatus(any(WSHentOppfoelgingsstatusRequest.class)))
                .thenReturn(response1, response2);

        WSHentOppfoelgingskontraktListeResponse response3 = new WSHentOppfoelgingskontraktListeResponse();
        response3.getOppfoelgingskontraktListe().add(new WSOppfoelgingskontrakt());
        WSHentOppfoelgingskontraktListeResponse response4 = new WSHentOppfoelgingskontraktListeResponse();
        response4.getOppfoelgingskontraktListe().add(new WSOppfoelgingskontrakt());
        response4.getOppfoelgingskontraktListe().add(new WSOppfoelgingskontrakt());

        when(unwrapped.hentOppfoelgingskontraktListe(any(WSHentOppfoelgingskontraktListeRequest.class)))
                .thenReturn(response3, response4);
    }

    @Test
    void toKallTilHentOppfoelgingsstatusMedSammeIdentGirBareEttTjenestekall() throws Exception {
        WSHentOppfoelgingsstatusRequest request1 = new WSHentOppfoelgingsstatusRequest();
        request1.setPersonidentifikator(FODSELSNUMMER_AREMARK);

        WSHentOppfoelgingsstatusResponse response1 = oppfolgingPortType.hentOppfoelgingsstatus(request1);
        WSHentOppfoelgingsstatusResponse response2 = oppfolgingPortType.hentOppfoelgingsstatus(request1);

        OppfoelgingPortType unwrappedOppfolgingsinfo = (OppfoelgingPortType) unwrapProxy(oppfolgingPortType);
        verify(unwrappedOppfolgingsinfo, times(1)).hentOppfoelgingsstatus(any());

        assertThat(response1.getNavOppfoelgingsenhet(), is(response2.getNavOppfoelgingsenhet()));
    }

    @Test
    void toKallTilHentOppfoelgingsstatusMedForskjelligeIdenterGirToTjenestekall() throws Exception {
        WSHentOppfoelgingsstatusRequest request1 = new WSHentOppfoelgingsstatusRequest();
        request1.setPersonidentifikator(FODSELSNUMMER_AREMARK);
        WSHentOppfoelgingsstatusRequest request2 = new WSHentOppfoelgingsstatusRequest();
        request1.setPersonidentifikator(FODSELSNUMMER_TROGSTAD);

        WSHentOppfoelgingsstatusResponse response1 = oppfolgingPortType.hentOppfoelgingsstatus(request1);
        WSHentOppfoelgingsstatusResponse response2 = oppfolgingPortType.hentOppfoelgingsstatus(request2);

        OppfoelgingPortType unwrappedOppfolgingsinfo = (OppfoelgingPortType) unwrapProxy(oppfolgingPortType);
        verify(unwrappedOppfolgingsinfo, times(2)).hentOppfoelgingsstatus(any());

        assertThat(response1.getNavOppfoelgingsenhet(), is(not(response2.getNavOppfoelgingsenhet())));
    }

    @Test
    void toKallTilHentOppfoelgingskontraktListeMedSammeIdentGirBareEttTjenestekall() throws Exception {
        WSHentOppfoelgingskontraktListeRequest request1 = new WSHentOppfoelgingskontraktListeRequest();
        request1.setPersonidentifikator(FODSELSNUMMER_AREMARK);

        WSHentOppfoelgingskontraktListeResponse response1 = oppfolgingPortType.hentOppfoelgingskontraktListe(request1);
        WSHentOppfoelgingskontraktListeResponse response2 = oppfolgingPortType.hentOppfoelgingskontraktListe(request1);

        OppfoelgingPortType unwrappedOppfolgingsinfo = (OppfoelgingPortType) unwrapProxy(oppfolgingPortType);
        verify(unwrappedOppfolgingsinfo, times(1)).hentOppfoelgingskontraktListe(any());

        assertThat(response1.getOppfoelgingskontraktListe(), is(response2.getOppfoelgingskontraktListe()));
    }

    @Test
    void toKallTilHentOppfoelgingskontraktListeMedForskjelligeIdenterGirToTjenestekall() throws Exception {
        WSHentOppfoelgingskontraktListeRequest request1 = new WSHentOppfoelgingskontraktListeRequest();
        request1.setPersonidentifikator(FODSELSNUMMER_AREMARK);
        WSHentOppfoelgingskontraktListeRequest request2 = new WSHentOppfoelgingskontraktListeRequest();
        request1.setPersonidentifikator(FODSELSNUMMER_TROGSTAD);

        WSHentOppfoelgingskontraktListeResponse response1 = oppfolgingPortType.hentOppfoelgingskontraktListe(request1);
        WSHentOppfoelgingskontraktListeResponse response2 = oppfolgingPortType.hentOppfoelgingskontraktListe(request2);

        OppfoelgingPortType unwrappedOppfolgingsinfo = (OppfoelgingPortType) unwrapProxy(oppfolgingPortType);
        verify(unwrappedOppfolgingsinfo, times(2)).hentOppfoelgingskontraktListe(any());

        assertThat(response1.getOppfoelgingskontraktListe(), is(not(response2.getOppfoelgingskontraktListe())));
    }

}
