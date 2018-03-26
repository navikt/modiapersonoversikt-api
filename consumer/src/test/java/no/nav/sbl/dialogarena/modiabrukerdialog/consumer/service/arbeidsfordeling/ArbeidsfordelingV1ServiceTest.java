package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.arbeidsfordeling;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.kjerneinfo.domain.person.*;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling.ArbeidsfordelingV1Service;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling.FinnBehandlendeEnhetException;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.KodeverksmapperService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.domain.Behandling;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.ArbeidsfordelingV1;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.FinnBehandlendeEnhetListeUgyldigInput;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.informasjon.WSOrganisasjonsenhet;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.meldinger.WSFinnBehandlendeEnhetListeRequest;
import no.nav.tjeneste.virksomhet.arbeidsfordeling.v1.meldinger.WSFinnBehandlendeEnhetListeResponse;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

class ArbeidsfordelingV1ServiceTest {

    private static final String FAGOMRADE = "BAR";
    private static final String OPPGAVETYPE = "SPM_OG_SVAR";
    private static final String UNDERKATEGORI = "KNA_BAR";
    private static final String ENHETSNUMMER = "4100";
    private static final String ENHETSNAVN = "NAV Styringsenhet Kontaktsenter";
    private static final String BEHANDLINGSTEMA = "ab0311";
    private static final String BEHANDLINGSTYPE = "ae0106";
    private static final String MAPPET_OPPGAVETYPE = "JFR";
    private static final String GEOGRAFISK_TILKNYTNING = "0219";
    private static final String PERSON = "11111111111";

    private ArbeidsfordelingV1Service arbeidsfordelingService;
    private ArbeidsfordelingV1 arbeidsfordeling;
    private PersonKjerneinfoServiceBi personService;
    private KodeverksmapperService kodeverksmapper;

    @BeforeEach
    void setupMocks() throws FinnBehandlendeEnhetListeUgyldigInput, IOException {
        arbeidsfordeling = lagArbeidsfordelingMock();
        personService = lagPersonServiceMock();
        kodeverksmapper = lagKodeverksmapperMock();
        arbeidsfordelingService = new ArbeidsfordelingV1ServiceImpl(arbeidsfordeling, personService, kodeverksmapper);
    }

    private PersonKjerneinfoServiceBi lagPersonServiceMock() {
        PersonKjerneinfoServiceBi personServiceMock = mock(PersonKjerneinfoServiceBi.class);
        when(personServiceMock.hentKjerneinformasjon(any(HentKjerneinformasjonRequest.class))).thenReturn(lagHentKjerneinfoResponse());
        return personServiceMock;
    }

    private HentKjerneinformasjonResponse lagHentKjerneinfoResponse() {
        Personfakta personfakta = new Personfakta();
        personfakta.setGeografiskTilknytning(new GeografiskTilknytning().withValue(GEOGRAFISK_TILKNYTNING));

        Person person = new Person();
        person.setPersonfakta(personfakta);

        HentKjerneinformasjonResponse response = new HentKjerneinformasjonResponse();
        response.setPerson(person);
        return response;
    }

    private KodeverksmapperService lagKodeverksmapperMock() throws IOException {
        KodeverksmapperService kodeverksmapperMock = mock(KodeverksmapperService.class);
        when(kodeverksmapperMock.mapOppgavetype(anyString())).thenReturn(MAPPET_OPPGAVETYPE);
        when(kodeverksmapperMock.mapUnderkategori(anyString())).thenReturn(Optional.of(new Behandling().withBehandlingstema(BEHANDLINGSTEMA).withBehandlingstype(BEHANDLINGSTYPE)));
        return kodeverksmapperMock;
    }

    private ArbeidsfordelingV1 lagArbeidsfordelingMock() throws FinnBehandlendeEnhetListeUgyldigInput {
        ArbeidsfordelingV1 arbeidsfordeling = mock(ArbeidsfordelingV1.class);
        when(arbeidsfordeling.finnBehandlendeEnhetListe(any(WSFinnBehandlendeEnhetListeRequest.class))).thenReturn(lagArbeidsfordelingResponse());
        return arbeidsfordeling;
    }

    private WSFinnBehandlendeEnhetListeResponse lagArbeidsfordelingResponse() {
        return new WSFinnBehandlendeEnhetListeResponse()
                .withBehandlendeEnhetListe(new WSOrganisasjonsenhet()
                                .withEnhetId(ENHETSNUMMER)
                                .withEnhetNavn(ENHETSNAVN),
                        new WSOrganisasjonsenhet()
                                .withEnhetId(GEOGRAFISK_TILKNYTNING)
                                .withEnhetNavn("NAV Bærum"));
    }

    @Test
    @DisplayName("Kaster exception hvis kall mot Kodeverksmapperen feiler")
    void kasterExceptionHvisKallMotKodeverksmapperFeiler() throws IOException {
        when(kodeverksmapper.mapOppgavetype(anyString())).thenThrow(new IOException());
        when(kodeverksmapper.mapUnderkategori(anyString())).thenThrow(new IOException());

        assertThrows(FinnBehandlendeEnhetException.class, () -> arbeidsfordelingService.finnBehandlendeEnhetListe(PERSON, FAGOMRADE, OPPGAVETYPE, UNDERKATEGORI));
    }

    @Test
    @DisplayName("Kaster exception hvis kall mot PersonService feiler")
    void kasterExceptionHvisKallMotPersonServiceFeiler() {
        when(personService.hentKjerneinformasjon(any(HentKjerneinformasjonRequest.class))).thenThrow(new RuntimeException());

        assertThrows(FinnBehandlendeEnhetException.class, () -> arbeidsfordelingService.finnBehandlendeEnhetListe(PERSON, FAGOMRADE, OPPGAVETYPE, UNDERKATEGORI));
    }

    @Test
    @DisplayName("Kaster exception hvis kall mot Arbeidsfordeling feiler")
    void kasterExceptionHvisKallMotArbeidsfordelingFeiler() throws FinnBehandlendeEnhetListeUgyldigInput {
        when(arbeidsfordeling.finnBehandlendeEnhetListe(any(WSFinnBehandlendeEnhetListeRequest.class))).thenThrow(new FinnBehandlendeEnhetListeUgyldigInput());

        assertThrows(FinnBehandlendeEnhetException.class, () -> arbeidsfordelingService.finnBehandlendeEnhetListe(PERSON, FAGOMRADE, OPPGAVETYPE, UNDERKATEGORI));
    }

    @Test
    @DisplayName("Arbeidsfordeling kalles med riktige argumenter")
    void kallerArbeidsfordelingMedRiktigeArgumenter() throws FinnBehandlendeEnhetListeUgyldigInput {
        arbeidsfordelingService.finnBehandlendeEnhetListe(PERSON, FAGOMRADE, OPPGAVETYPE, UNDERKATEGORI);

        ArgumentCaptor<WSFinnBehandlendeEnhetListeRequest> captor = ArgumentCaptor.forClass(WSFinnBehandlendeEnhetListeRequest.class);
        verify(arbeidsfordeling).finnBehandlendeEnhetListe(captor.capture());

        WSFinnBehandlendeEnhetListeRequest request = captor.getValue();
        assertAll("request",
                () -> assertEquals(BEHANDLINGSTEMA, request.getArbeidsfordelingKriterier().getBehandlingstema().getValue()),
                () -> assertEquals(BEHANDLINGSTYPE, request.getArbeidsfordelingKriterier().getBehandlingstype().getValue()),
                () -> assertEquals(GEOGRAFISK_TILKNYTNING, request.getArbeidsfordelingKriterier().getGeografiskTilknytning().getValue()),
                () -> assertEquals(MAPPET_OPPGAVETYPE, request.getArbeidsfordelingKriterier().getOppgavetype().getValue()),
                () -> assertEquals(FAGOMRADE, request.getArbeidsfordelingKriterier().getTema().getValue()));
    }
}
