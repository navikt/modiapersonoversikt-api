package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.norg2;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.Arbeidsfordeling;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.FinnArbeidsfordelingForEnhetBolkUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.OrganisasjonEnhetV1;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.meldinger.WSFinnArbeidsfordelingForEnhetBolkRequest;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.meldinger.WSFinnArbeidsfordelingForEnhetBolkResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
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
        WSArbeidsfordeling arbeidsfordelingUtenGeografiskNedslagsfelt = createArbeidsfordeling("1337", null);

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