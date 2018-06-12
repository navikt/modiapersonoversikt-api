package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.domain.OrganisasjonEnhetKontaktinformasjon;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.domain.Ukedag;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.service.OrganisasjonEnhetKontaktinformasjonServiceImpl;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.binding.HentKontaktinformasjonForEnhetBolkUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.binding.OrganisasjonEnhetKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.meldinger.HentKontaktinformasjonForEnhetBolkRequest;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.meldinger.HentKontaktinformasjonForEnhetBolkResponse;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrganisasjonEnhetKontaktinformasjonServiceImplTest {

    public static final String ENHET_ID = "2000";
    public static final String GATEADRESSE = "Islands gate";
    public static final String FEILMELDING_FEILET_ENHET = "Feilmelding om feilet enhet";

    private final OrganisasjonEnhetKontaktinformasjonV1 serviceMock = mock(OrganisasjonEnhetKontaktinformasjonV1.class);

    private OrganisasjonEnhetKontaktinformasjonServiceImpl service;

    @BeforeEach
    void beforeEach() throws HentKontaktinformasjonForEnhetBolkUgyldigInput {
        service = new OrganisasjonEnhetKontaktinformasjonServiceImpl(serviceMock);
        when(serviceMock.hentKontaktinformasjonForEnhetBolk(any())).thenReturn(mockResponse());
    }

    @Test
    @DisplayName("Setter opp requestobjekt med forespurt enhetsId")
    void setterOppRequestObjekt() throws HentKontaktinformasjonForEnhetBolkUgyldigInput {
        service.hentKontaktinformasjon(ENHET_ID);

        ArgumentCaptor<HentKontaktinformasjonForEnhetBolkRequest> argumentCaptor = ArgumentCaptor.forClass(HentKontaktinformasjonForEnhetBolkRequest.class);
        verify(serviceMock).hentKontaktinformasjonForEnhetBolk(argumentCaptor.capture());
        assertEquals(ENHET_ID, argumentCaptor.getValue().getEnhetIdListe().get(0));
    }

    @Test
    @DisplayName("Kontaktinformasjon returnerer data om forespurt enhet")
    void hentKontaktinformasjon() {
        OrganisasjonEnhetKontaktinformasjon kontaktinformasjon = service.hentKontaktinformasjon(ENHET_ID);

        assertEquals(ENHET_ID, kontaktinformasjon.getEnhetId());
    }

    @Test
    @DisplayName("Returnerer data om kontaktinformasjon")
    void returnererKontaktinformasjon() {
        OrganisasjonEnhetKontaktinformasjon kontaktinfo = service.hentKontaktinformasjon(ENHET_ID);

        assertEquals(GATEADRESSE, kontaktinfo.getKontaktinformasjon().getPublikumsmottak().get(0).getBesoeksadresse().getGatenavn());
    }

    @Nested
    @DisplayName("Feilhåndtering")
    class Feilhandtering {

        @Test
        @DisplayName("Kaster exception med feilmelding fra TPS ved feil")
        void enhetFeiler() throws HentKontaktinformasjonForEnhetBolkUgyldigInput {
            when(serviceMock.hentKontaktinformasjonForEnhetBolk(any())).thenReturn(mockResponsMedFeiletEnhet());

            RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> service.hentKontaktinformasjon(ENHET_ID));
            Assertions.assertTrue(runtimeException.getMessage().contains(FEILMELDING_FEILET_ENHET));
        }
    }

    @Nested
    @DisplayName("Spesialtilfeller")
    class Spesialtilfeller {

        @Test
        @DisplayName("Ingen elementer i listen av enheter som returneres")
        void ingenElementer() throws HentKontaktinformasjonForEnhetBolkUgyldigInput {
            when(serviceMock.hentKontaktinformasjonForEnhetBolk(any())).thenReturn(new HentKontaktinformasjonForEnhetBolkResponse());
            assertThrows(RuntimeException.class, () -> service.hentKontaktinformasjon(ENHET_ID));
        }

        @Test
        @DisplayName("Ingen publikumsmottak")
        void ingenPublikumsmottak() throws HentKontaktinformasjonForEnhetBolkUgyldigInput {
            when(serviceMock.hentKontaktinformasjonForEnhetBolk(any())).thenReturn(mockResponseUtenPublikumsmottak());

            OrganisasjonEnhetKontaktinformasjon enhet = service.hentKontaktinformasjon(ENHET_ID);

            assertTrue(enhet.getKontaktinformasjon().getPublikumsmottak().isEmpty());
        }

        private HentKontaktinformasjonForEnhetBolkResponse mockResponseUtenPublikumsmottak() {
            HentKontaktinformasjonForEnhetBolkResponse response = mockResponse();
            response.getEnhetListe().get(0).getKontaktinformasjon().getPublikumsmottakListe().clear();
            return response;
        }

        @Test
        @DisplayName("Ingen besøksadresse for publikumsmottak")
        void ingenBesoksadresse() throws HentKontaktinformasjonForEnhetBolkUgyldigInput {
            when(serviceMock.hentKontaktinformasjonForEnhetBolk(any())).thenReturn(mockResponseMedPublikumsmottakUtenBesoksadresse());

            OrganisasjonEnhetKontaktinformasjon enhet = service.hentKontaktinformasjon(ENHET_ID);

            assertEquals(null, enhet.getKontaktinformasjon().getPublikumsmottak().get(0).getBesoeksadresse());
        }

        private HentKontaktinformasjonForEnhetBolkResponse mockResponseMedPublikumsmottakUtenBesoksadresse() {
            HentKontaktinformasjonForEnhetBolkResponse response = mockResponse();
            response.getEnhetListe().get(0).getKontaktinformasjon().getPublikumsmottakListe().get(0).setBesoeksadresse(null);
            return response;
        }

        @Test
        @DisplayName("Ingen åpningstid på mandag")
        void ingenApningstidMandag() throws HentKontaktinformasjonForEnhetBolkUgyldigInput {
            when(serviceMock.hentKontaktinformasjonForEnhetBolk(any())).thenReturn(mockResponseMedPublikumsmottakUtenApningstidMandag());

            OrganisasjonEnhetKontaktinformasjon enhet = service.hentKontaktinformasjon(ENHET_ID);

            assertFalse(enhet.getKontaktinformasjon().getPublikumsmottak().get(0).getApningstider().getApningstid(Ukedag.MANDAG).isPresent());
        }

        private HentKontaktinformasjonForEnhetBolkResponse mockResponseMedPublikumsmottakUtenApningstidMandag() {
            HentKontaktinformasjonForEnhetBolkResponse response = mockResponse();
            response.getEnhetListe().get(0).getKontaktinformasjon().getPublikumsmottakListe().get(0)
                    .getAapningstider().setMandag(null);
            return response;
        }

    }

    private HentKontaktinformasjonForEnhetBolkResponse mockResponse() {
        HentKontaktinformasjonForEnhetBolkResponse response = new HentKontaktinformasjonForEnhetBolkResponse();
        Organisasjonsenhet organisasjonsenhet = new Organisasjonsenhet();
        organisasjonsenhet.setEnhetId(ENHET_ID);
        organisasjonsenhet.setKontaktinformasjon(mockKontaktinformasjon());
        response.getEnhetListe().add(organisasjonsenhet);
        return response;
    }

    private KontaktinformasjonForOrganisasjonsenhet mockKontaktinformasjon() {
        KontaktinformasjonForOrganisasjonsenhet kontaktinformasjonForOrganisasjonsenhet = new KontaktinformasjonForOrganisasjonsenhet();
        kontaktinformasjonForOrganisasjonsenhet.getPublikumsmottakListe().add(mockPublikumsmottak());
        return kontaktinformasjonForOrganisasjonsenhet;
    }

    private Publikumsmottak mockPublikumsmottak() {
        Publikumsmottak publikumsmottak = new Publikumsmottak();
        publikumsmottak.setBesoeksadresse(mockGateadresse());
        publikumsmottak.setAapningstider(new Aapningstider());
        return publikumsmottak;
    }

    private Gateadresse mockGateadresse() {
        Gateadresse gateadresse = new Gateadresse();
        gateadresse.setGatenavn(GATEADRESSE);
        gateadresse.setPoststed(new Postnummer());
        return gateadresse;
    }

    private HentKontaktinformasjonForEnhetBolkResponse mockResponsMedFeiletEnhet() {
        HentKontaktinformasjonForEnhetBolkResponse response = new HentKontaktinformasjonForEnhetBolkResponse();
        FeiletEnhet feiletEnhet = new FeiletEnhet();
        feiletEnhet.setFeilmelding(FEILMELDING_FEILET_ENHET);
        response.getFeiletEnhetListe().add(feiletEnhet);
        return response;
    }

}