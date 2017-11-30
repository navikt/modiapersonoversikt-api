package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.domain.OrganisasjonEnhetKontaktinformasjon;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.service.OrganisasjonEnhetKontaktinformasjonServiceImpl;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.binding.HentKontaktinformasjonForEnhetBolkUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.binding.OrganisasjonEnhetKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.meldinger.HentKontaktinformasjonForEnhetBolkRequest;
import no.nav.tjeneste.virksomhet.organisasjonenhetkontaktinformasjon.v1.meldinger.HentKontaktinformasjonForEnhetBolkResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

class OrganisasjonEnhetKontaktinformasjonServiceImplTest {

    public static final String ENHET_ID = "2000";
    public static final String GATEADRESSE = "Islands gate";

    private final OrganisasjonEnhetKontaktinformasjonV1 serviceMock = mock(OrganisasjonEnhetKontaktinformasjonV1.class);

    @BeforeEach
    void beforeEach() throws HentKontaktinformasjonForEnhetBolkUgyldigInput {
        when(serviceMock.hentKontaktinformasjonForEnhetBolk(any())).thenReturn(mockResponse());
    }

    @Test
    @DisplayName("Setter opp request objekt riktig")
    void setterOppRequestObjekt() throws HentKontaktinformasjonForEnhetBolkUgyldigInput {
        OrganisasjonEnhetKontaktinformasjonServiceImpl service = new OrganisasjonEnhetKontaktinformasjonServiceImpl(serviceMock);

        service.hentKontaktinformasjon(ENHET_ID);

        ArgumentCaptor<HentKontaktinformasjonForEnhetBolkRequest> argumentCaptor = ArgumentCaptor.forClass(HentKontaktinformasjonForEnhetBolkRequest.class);
        verify(serviceMock).hentKontaktinformasjonForEnhetBolk(argumentCaptor.capture());
        assertEquals(ENHET_ID, argumentCaptor.getValue().getEnhetIdListe().get(0));
    }

    @Test
    @DisplayName("Kontaktinformasjon returnerer data om forespurt enhet")
    void hentKontaktinformasjon() {
        OrganisasjonEnhetKontaktinformasjonServiceImpl service = new OrganisasjonEnhetKontaktinformasjonServiceImpl(serviceMock);

        OrganisasjonEnhetKontaktinformasjon kontaktinformasjon = service.hentKontaktinformasjon(ENHET_ID);

        assertEquals(ENHET_ID, kontaktinformasjon.getEnhetId());
    }

    @Test
    @DisplayName("Returnerer data om kontaktinformasjon")
    void returnererKontaktinformasjon() {
        OrganisasjonEnhetKontaktinformasjonServiceImpl service = new OrganisasjonEnhetKontaktinformasjonServiceImpl(serviceMock);

        OrganisasjonEnhetKontaktinformasjon kontaktinfo = service.hentKontaktinformasjon(ENHET_ID);

        assertEquals(GATEADRESSE, kontaktinfo.getKontaktinformasjon().getPublikumsmottak().get(0).getBesoeksadresse().getGatenavn());
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
}