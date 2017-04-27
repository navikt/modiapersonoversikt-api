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
        String ikkeEksisterendeEnhetId = "6666";
        WSKriterier kriterier = new WSKriterier().withEnhetId(ikkeEksisterendeEnhetId);
        WSFinnArbeidsfordelingForEnhetBolkRequest request = new WSFinnArbeidsfordelingForEnhetBolkRequest()
                .withKriterierListe(kriterier);
        WSFeiletEnhet feiletEnhet = new WSFeiletEnhet().withEnhetId(ikkeEksisterendeEnhetId).withFeilmelding("Ingen enhet");
        WSFinnArbeidsfordelingForEnhetBolkResponse mockResponse =  new WSFinnArbeidsfordelingForEnhetBolkResponse()
                .withFeiletEnhetListe(feiletEnhet);
        when(enhetWS.finnArbeidsfordelingForEnhetBolk(request)).thenReturn(mockResponse);

        final List<Arbeidsfordeling> arbeidsfordelinger = organisasjonEnhetServiceImpl.hentArbeidsfordeling(ikkeEksisterendeEnhetId);

        assertThat(arbeidsfordelinger.isEmpty(), is(true));
    }

    @Test
    public void hentArbeidsfordelingSomReturnererArbeidsfordelingKriterieUtenArkivTema() throws FinnArbeidsfordelingForEnhetBolkUgyldigInput {
        WSKriterier kriterier = new WSKriterier().withEnhetId(SAKSBEHANDLERS_VALGTE_ENHET);
        WSFinnArbeidsfordelingForEnhetBolkRequest request = new WSFinnArbeidsfordelingForEnhetBolkRequest()
                .withKriterierListe(kriterier);
        WSFinnArbeidsfordelingForEnhetBolkResponse mockResponse = createWSArbeidsfordelingWithMissingArkivtema();
        when(enhetWS.finnArbeidsfordelingForEnhetBolk(request)).thenReturn(mockResponse);

        final List<Arbeidsfordeling> arbeidsfordelinger = organisasjonEnhetServiceImpl.hentArbeidsfordeling(SAKSBEHANDLERS_VALGTE_ENHET);

        assertThat(arbeidsfordelinger.size(), is(2));
    }

    @Test
    public void hentArbeidsfordelingMedUgyldigInputReturnererTomListe() throws FinnArbeidsfordelingForEnhetBolkUgyldigInput {
        when(enhetWS.finnArbeidsfordelingForEnhetBolk(any())).thenThrow(new FinnArbeidsfordelingForEnhetBolkUgyldigInput());

        List<Arbeidsfordeling> arbeidsfordelinger = organisasjonEnhetServiceImpl.hentArbeidsfordeling(null);

        assertThat(arbeidsfordelinger.isEmpty(), is(true));
    }

    private WSFinnArbeidsfordelingForEnhetBolkResponse createWSArbeidsfordelingWithOneMissingGeografiskNedslagsfelt() {
        WSArbeidsfordeling arbeidsfordeling = createArbeidsfordeling("1337", new WSArkivtemaer().withValue("BIL"));
        WSArbeidsfordeling arbeidsfordelingUtenGeografiskNedslagsfelt = createArbeidsfordeling(null, new WSArkivtemaer().withValue("BIL"));

        WSArbeidsfordelingerForEnhet arbeidsfordelingerForEnhet = new WSArbeidsfordelingerForEnhet()
                .withArbeidsfordelingListe(arbeidsfordeling, arbeidsfordelingUtenGeografiskNedslagsfelt);

        return new WSFinnArbeidsfordelingForEnhetBolkResponse()
                .withArbeidsfordelingerForEnhetListe(arbeidsfordelingerForEnhet);
    }

    private WSFinnArbeidsfordelingForEnhetBolkResponse createWSArbeidsfordelingWithMissingArkivtema() {
        WSArbeidsfordeling arbeidsfordeling = createArbeidsfordeling("1337", new WSArkivtemaer().withValue("BIL"));
        WSArkivtemaer arkivTema = null;
        WSArbeidsfordeling arbeidsfordelingUtenGeografiskNedslagsfelt = createArbeidsfordeling("1337", arkivTema);

        WSArbeidsfordelingerForEnhet arbeidsfordelingerForEnhet = new WSArbeidsfordelingerForEnhet()
                .withArbeidsfordelingListe(arbeidsfordeling, arbeidsfordelingUtenGeografiskNedslagsfelt);

        return new WSFinnArbeidsfordelingForEnhetBolkResponse()
                .withArbeidsfordelingerForEnhetListe(arbeidsfordelingerForEnhet);
    }

    private WSFinnArbeidsfordelingForEnhetBolkResponse createMockArbeidsfordelingForEnhet() {
        String arkivtema = "BIL";
        WSArbeidsfordeling arbeidsfordeling1 = createArbeidsfordeling("1800", new WSArkivtemaer().withValue(arkivtema));
        WSArbeidsfordeling arbeidsfordeling2 = createArbeidsfordeling("1801", new WSArkivtemaer().withValue(arkivtema));
        WSArbeidsfordeling arbeidsfordeling3 = createArbeidsfordeling("1802", new WSArkivtemaer().withValue(arkivtema));

        WSArbeidsfordelingerForEnhet arbeidsfordelingerForEnhet = new WSArbeidsfordelingerForEnhet()
                .withArbeidsfordelingListe(arbeidsfordeling1, arbeidsfordeling2, arbeidsfordeling3);

        return new WSFinnArbeidsfordelingForEnhetBolkResponse()
                .withArbeidsfordelingerForEnhetListe(arbeidsfordelingerForEnhet);
    }

    private WSArbeidsfordeling createArbeidsfordeling(String geografiskNedslagsfelt, WSArkivtemaer arkivtema) {
        return new WSArbeidsfordeling()
                .withUnderliggendeArbeidsfordelingskriterier(new WSArbeidsfordelingskriterier()
                        .withGeografiskNedslagsfelt(geografiskNedslagsfelt)
                        .withArkivtema(arkivtema));
    }
}