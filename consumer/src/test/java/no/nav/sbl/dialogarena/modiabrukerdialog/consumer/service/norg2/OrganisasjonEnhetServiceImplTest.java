package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.norg2;

import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.Arbeidsfordeling;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.*;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.meldinger.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrganisasjonEnhetServiceImplTest {

    private final String SAKSBEHANDLERS_VALGTE_ENHET = "0447";

    @Mock
    private OrganisasjonEnhetV1 enhetWS;

    @InjectMocks
    private OrganisasjonEnhetServiceImpl organisasjonEnhetServiceImpl;

    @Test
    public void skalSortereEnheterIStigendeRekkefolge() {
        final WSHentFullstendigEnhetListeResponse response = new WSHentFullstendigEnhetListeResponse();
        final WSDetaljertEnhet navEnhet1 = new WSDetaljertEnhet();
        navEnhet1.setEnhetId("1111");
        navEnhet1.setNavn("Enhet");
        final WSDetaljertEnhet navEnhet2 = new WSDetaljertEnhet();
        navEnhet2.setEnhetId("2222");
        navEnhet2.setNavn("Enhet");
        final WSDetaljertEnhet navEnhet3 = new WSDetaljertEnhet();
        navEnhet3.setEnhetId("3333");
        navEnhet3.setNavn("Enhet");
        response.getEnhetListe().addAll(asList(navEnhet3, navEnhet2, navEnhet1));
        when(enhetWS.hentFullstendigEnhetListe(any(WSHentFullstendigEnhetListeRequest.class))).thenReturn(response);

        final List<AnsattEnhet> enheter = organisasjonEnhetServiceImpl.hentAlleEnheter();

        assertThat(enheter.get(0).enhetId, is(equalTo("1111")));
        assertThat(enheter.get(1).enhetId, is(equalTo("2222")));
        assertThat(enheter.get(2).enhetId, is(equalTo("3333")));
    }

    @Test
    public void hentEnhetGittGeografiskNedslagsfeltSkalReturnereEnkeltEnhetGittGeografiskNedslagsfelt() throws Exception {
        final WSDetaljertEnhet navEnhet = new WSDetaljertEnhet();
        navEnhet.setEnhetId("0219");
        navEnhet.setNavn("Nav Bærum");
        final WSFinnNAVKontorForGeografiskNedslagsfeltBolkResponse response = new WSFinnNAVKontorForGeografiskNedslagsfeltBolkResponse();
        final WSEnheterForGeografiskNedslagsfelt wsEnheterForGeografiskNedslagsfelt = new WSEnheterForGeografiskNedslagsfelt();
        wsEnheterForGeografiskNedslagsfelt.getEnhetListe().add(navEnhet);
        response.getEnheterForGeografiskNedslagsfeltListe().add(wsEnheterForGeografiskNedslagsfelt);
        when(enhetWS.finnNAVKontorForGeografiskNedslagsfeltBolk(any(WSFinnNAVKontorForGeografiskNedslagsfeltBolkRequest.class))).thenReturn(response);

        final Optional<AnsattEnhet> enhetFraTjenesten = organisasjonEnhetServiceImpl.hentEnhetGittGeografiskNedslagsfelt("0219");

        assertTrue(enhetFraTjenesten.isSome());
        assertThat(navEnhet.getEnhetId(), is(equalTo(enhetFraTjenesten.get().enhetId)));
        assertThat(navEnhet.getNavn(), is(equalTo(enhetFraTjenesten.get().enhetNavn)));
    }

    @Test
    public void hentEnhetGittEnhetIdSkalReturnereHenteEnkeltEnhetGittEnhetId() throws Exception {
        final WSDetaljertEnhet navEnhet = new WSDetaljertEnhet();
        navEnhet.setEnhetId("0100");
        navEnhet.setNavn("Nav Østfold");
        final WSHentEnhetBolkResponse response = new WSHentEnhetBolkResponse();
        response.getEnhetListe().add(navEnhet);
        when(enhetWS.hentEnhetBolk(any(WSHentEnhetBolkRequest.class))).thenReturn(response);

        final Optional<AnsattEnhet> enhetFraTjenesten = organisasjonEnhetServiceImpl.hentEnhetGittEnhetId("0100");

        assertTrue(enhetFraTjenesten.isSome());
        assertThat(navEnhet.getEnhetId(), is(equalTo(enhetFraTjenesten.get().enhetId)));
        assertThat(navEnhet.getNavn(), is(equalTo(enhetFraTjenesten.get().enhetNavn)));
    }

    @Test
    public void hentEnhetGittGeografiskNedslagsfeltSkalReturnereTomOptionalDersomGeografiskNedslagsfeltReturnererTomRespons() throws Exception {
        when(enhetWS.finnNAVKontorForGeografiskNedslagsfeltBolk(any(WSFinnNAVKontorForGeografiskNedslagsfeltBolkRequest.class))).thenReturn(new WSFinnNAVKontorForGeografiskNedslagsfeltBolkResponse());
        final Optional<AnsattEnhet> enhetFraTjenesten = organisasjonEnhetServiceImpl.hentEnhetGittGeografiskNedslagsfelt("0219");
        assertFalse(enhetFraTjenesten.isSome());
    }

    @Test
    public void hentEnhetGittGeografiskNedslagsfeltSkalReturnereTomOptionalDersomGeografiskNedslagsfeltInneholderUgyldigInput() throws Exception {
        when(enhetWS.finnNAVKontorForGeografiskNedslagsfeltBolk(any(WSFinnNAVKontorForGeografiskNedslagsfeltBolkRequest.class))).thenThrow(new FinnNAVKontorForGeografiskNedslagsfeltBolkUgyldigInput());
        final Optional<AnsattEnhet> enhetFraTjenesten = organisasjonEnhetServiceImpl.hentEnhetGittGeografiskNedslagsfelt("Ikke Gyldig Input");
        assertFalse(enhetFraTjenesten.isSome());
    }

    @Test
    public void hentEnhetGittEnhetIdSkalReturnereTomOptionalDersomEnhetIdReturnererTomRespons() throws Exception {
        when(enhetWS.hentEnhetBolk(any(WSHentEnhetBolkRequest.class))).thenReturn(new WSHentEnhetBolkResponse());
        final Optional<AnsattEnhet> enhetFraTjenesten = organisasjonEnhetServiceImpl.hentEnhetGittEnhetId("0100");
        assertFalse(enhetFraTjenesten.isSome());
    }

    @Test
    public void hentEnhetGittEnhetIdSkalReturnereTomOptionalDersomEnhetIdInneholderUgyldigInput() throws Exception {
        when(enhetWS.hentEnhetBolk(any(WSHentEnhetBolkRequest.class))).thenThrow(new HentEnhetBolkUgyldigInput());
        final Optional<AnsattEnhet> enhetFraTjenesten = organisasjonEnhetServiceImpl.hentEnhetGittEnhetId("0100");
        assertFalse(enhetFraTjenesten.isSome());
    }

    @Test
    public void hentArbeidsfordelingSkalReturnereListeAvArbeidsfordelinger() throws FinnArbeidsfordelingForEnhetBolkUgyldigInput {
        WSKriterier kriterier = new WSKriterier().withEnhetId(SAKSBEHANDLERS_VALGTE_ENHET);
        WSFinnArbeidsfordelingForEnhetBolkRequest request = new WSFinnArbeidsfordelingForEnhetBolkRequest()
                .withKriterierListe(kriterier);
        WSFinnArbeidsfordelingForEnhetBolkResponse mockResponse = createMockArbeidsfordelingForEnhet();
        when(enhetWS.finnArbeidsfordelingForEnhetBolk(request)).thenReturn(mockResponse);

        final List<Arbeidsfordeling> arbeidsfordelinger = organisasjonEnhetServiceImpl.hentArbeidsfordeling(SAKSBEHANDLERS_VALGTE_ENHET);

        assertThat(arbeidsfordelinger.size(), is(3));
    }

    @Test
    public void hentArbeidsfordelingSkalAkseptereArbeidsfordelingUtenGeografiskNedslagsfelt() throws FinnArbeidsfordelingForEnhetBolkUgyldigInput {
        WSKriterier kriterier = new WSKriterier().withEnhetId(SAKSBEHANDLERS_VALGTE_ENHET);
        WSFinnArbeidsfordelingForEnhetBolkRequest request = new WSFinnArbeidsfordelingForEnhetBolkRequest()
                .withKriterierListe(kriterier);
        WSFinnArbeidsfordelingForEnhetBolkResponse mockResponse = createWSArbeidsfordelingWithOneMissingGeografiskNedslagsfelt();
        when(enhetWS.finnArbeidsfordelingForEnhetBolk(request)).thenReturn(mockResponse);

        final List<Arbeidsfordeling> arbeidsfordelinger = organisasjonEnhetServiceImpl.hentArbeidsfordeling(SAKSBEHANDLERS_VALGTE_ENHET);

        assertThat(arbeidsfordelinger.size(), is(2));
    }

    @Test
    public void hentArbeidsfordelingForIkkeEksisterendeEnhetSkalReturnereTomListe() throws FinnArbeidsfordelingForEnhetBolkUgyldigInput {
        String ikkeEksisterendeEnhet = "6666";
        WSKriterier kriterier = new WSKriterier().withEnhetId(ikkeEksisterendeEnhet);
        WSFinnArbeidsfordelingForEnhetBolkRequest request = new WSFinnArbeidsfordelingForEnhetBolkRequest()
                .withKriterierListe(kriterier);
        WSFeiletEnhet feiletEnhet = new WSFeiletEnhet().withEnhetId(ikkeEksisterendeEnhet).withFeilmelding("Ingen enhet");
        WSFinnArbeidsfordelingForEnhetBolkResponse mockResponse =  new WSFinnArbeidsfordelingForEnhetBolkResponse()
                .withFeiletEnhetListe(feiletEnhet);
        when(enhetWS.finnArbeidsfordelingForEnhetBolk(request)).thenReturn(mockResponse);

        final List<Arbeidsfordeling> arbeidsfordelinger = organisasjonEnhetServiceImpl.hentArbeidsfordeling(ikkeEksisterendeEnhet);

        assertThat(arbeidsfordelinger.isEmpty(), is(true));
    }

    private WSFinnArbeidsfordelingForEnhetBolkResponse createWSArbeidsfordelingWithOneMissingGeografiskNedslagsfelt() {
        WSArkivtemaer arkivTeama = new WSArkivtemaer().withValue("BIL");
        WSArbeidsfordelingskriterier kriterie = new WSArbeidsfordelingskriterier()
                .withGeografiskNedslagsfelt("1337")
                .withArkivtema(arkivTeama);
        WSArbeidsfordelingskriterier arbeidsfordelingUtenGeografiskNedslagsfelt = new WSArbeidsfordelingskriterier()
                .withArkivtema(arkivTeama);
        WSArbeidsfordeling arbeidsfordeling1 = new WSArbeidsfordeling().withUnderliggendeArbeidsfordelingskriterier(kriterie);
        WSArbeidsfordeling arbeidsfordeling2 = new WSArbeidsfordeling().withUnderliggendeArbeidsfordelingskriterier(arbeidsfordelingUtenGeografiskNedslagsfelt);

        WSArbeidsfordelingerForEnhet arbeidsfordelingerForEnhet = new WSArbeidsfordelingerForEnhet()
                .withArbeidsfordelingListe(arbeidsfordeling1, arbeidsfordeling2);

        return new WSFinnArbeidsfordelingForEnhetBolkResponse()
                .withArbeidsfordelingerForEnhetListe(arbeidsfordelingerForEnhet);
    }

    private WSFinnArbeidsfordelingForEnhetBolkResponse createMockArbeidsfordelingForEnhet() {
        WSArkivtemaer  arkivTema = new WSArkivtemaer().withValue("BIL");
        WSArbeidsfordeling arbeidsfordeling1 = new WSArbeidsfordeling()
                .withUnderliggendeArbeidsfordelingskriterier(new WSArbeidsfordelingskriterier().withGeografiskNedslagsfelt("1800").withArkivtema(arkivTema));
        WSArbeidsfordeling arbeidsfordeling2 = new WSArbeidsfordeling()
                .withUnderliggendeArbeidsfordelingskriterier(new WSArbeidsfordelingskriterier().withGeografiskNedslagsfelt("1801").withArkivtema(arkivTema));
        WSArbeidsfordeling arbeidsfordeling3 = new WSArbeidsfordeling()
                .withUnderliggendeArbeidsfordelingskriterier(new WSArbeidsfordelingskriterier().withGeografiskNedslagsfelt("1802").withArkivtema(arkivTema));

        WSArbeidsfordelingerForEnhet arbeidsfordelingerForEnhet = new WSArbeidsfordelingerForEnhet()
                .withArbeidsfordelingListe(arbeidsfordeling1, arbeidsfordeling2, arbeidsfordeling3);

        return new WSFinnArbeidsfordelingForEnhetBolkResponse()
                .withArbeidsfordelingerForEnhetListe(arbeidsfordelingerForEnhet);
    }
}