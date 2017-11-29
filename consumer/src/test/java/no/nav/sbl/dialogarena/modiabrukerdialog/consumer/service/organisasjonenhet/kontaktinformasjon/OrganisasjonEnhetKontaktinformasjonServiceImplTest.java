package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.domain.OrganisasjonEnhetKontaktinformasjon;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.service.OrganisasjonEnhetKontaktinformasjonServiceImpl;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.binding.HentKontaktinformasjonForEnhetBolkUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.binding.OrganisasjonEnhetKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.meldinger.HentKontaktinformasjonForEnhetBolkRequest;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.meldinger.HentKontaktinformasjonForEnhetBolkResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
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
    @DisplayName("Setter opp requestobjekt riktig")
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

    @Test
    @DisplayName("Kaster exception med feilmelding fra TPS ved feil")
    void enhetFeiler() throws HentKontaktinformasjonForEnhetBolkUgyldigInput {
        when(serviceMock.hentKontaktinformasjonForEnhetBolk(any())).thenReturn(mockResponsMedFeiletEnhet());

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> service.hentKontaktinformasjon(ENHET_ID));
        Assertions.assertTrue(runtimeException.getMessage().contains(FEILMELDING_FEILET_ENHET));
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
        Gateadresse gateadresse = new Gateadresse();
        gateadresse.setGatenavn(GATEADRESSE);
        publikumsmottak.setBesoeksadresse(gateadresse);
        publikumsmottak.setAapningstider(new Aapningstider());
        return publikumsmottak;
    }

    private HentKontaktinformasjonForEnhetBolkResponse mockResponsMedFeiletEnhet() {
        HentKontaktinformasjonForEnhetBolkResponse response = new HentKontaktinformasjonForEnhetBolkResponse();
        FeiletEnhet feiletEnhet = new FeiletEnhet();
        feiletEnhet.setFeilmelding(FEILMELDING_FEILET_ENHET);
        response.getFeiletEnhetListe().add(feiletEnhet);
        return response;
    }

}