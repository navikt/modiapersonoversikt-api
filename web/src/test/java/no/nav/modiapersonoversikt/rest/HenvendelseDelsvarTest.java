package no.nav.modiapersonoversikt.rest;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.modiapersonoversikt.infrastructure.content.ContentRetriever;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollMock;
import no.nav.modiapersonoversikt.legacy.api.domain.Temagruppe;
import no.nav.modiapersonoversikt.legacy.api.service.OppgaveBehandlingService;
import no.nav.modiapersonoversikt.legacy.api.utils.http.HttpRequestUtil;
import no.nav.modiapersonoversikt.legacy.api.utils.http.SubjectHandlerUtil;
import no.nav.modiapersonoversikt.rest.dialog.apis.DelsvarRestRequest;
import no.nav.modiapersonoversikt.rest.dialog.henvendelse.HenvendelseDelsvar;
import no.nav.modiapersonoversikt.service.HenvendelseUtsendingServiceImpl;
import no.nav.modiapersonoversikt.service.henvendelse.DelsvarServiceImpl;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.SendUtHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.WSFerdigstillHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeResponse;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SPORSMAL_SKRIFTLIG;
import static no.nav.modiapersonoversikt.rest.persondata.PersondataTestdataKt.hentPersondataServiceMock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class HenvendelseDelsvarTest {

    private static final String SAKSBEHANDLERS_IDENT = "z999666";
    private static final String BRUKERS_FNR = "10108000398";
    private static final String TRAAD_ID = "trådID";
    private static final String HENVENDELSES_ID = "henvendelsesID";
    private static final String VALGT_ENHET = "0300";

    private OppgaveBehandlingService oppgaveBehandlingServiceMock = mock(OppgaveBehandlingService.class);
    private MockHttpServletRequest httpMockRequest;
    private HenvendelseDelsvar delsvarController;
    private SendUtHenvendelsePortType sendUtHenvendelsePortTypeMock;
    private Tilgangskontroll tilgangskontrollMock = TilgangskontrollMock.get();

    @BeforeEach
    void before() {
        httpMockRequest = HttpRequestUtil.mockHttpServletRequestMedCookie(SAKSBEHANDLERS_IDENT, VALGT_ENHET);
        delsvarController = new HenvendelseDelsvar(new DelsvarServiceImpl(setupHenvendelseUtsendingService(), oppgaveBehandlingServiceMock));
    }

    private HenvendelseUtsendingServiceImpl setupHenvendelseUtsendingService() {
        HenvendelsePortType henvendelsePortTypeMock = getHenvendelsePortTypeMock();
        ContentRetriever propertyResolver = mockPropertyResolver();
        CacheManager cacheManager = mock(CacheManager.class);
        when(cacheManager.getCache(anyString())).thenReturn(mock(Cache.class));
        sendUtHenvendelsePortTypeMock = mock(SendUtHenvendelsePortType.class);

        return new HenvendelseUtsendingServiceImpl(
            henvendelsePortTypeMock,
            sendUtHenvendelsePortTypeMock,
            null,
            null,
            null,
            tilgangskontrollMock,
            propertyResolver,
            hentPersondataServiceMock(),
            null,
            cacheManager
        );
    }

    private ContentRetriever mockPropertyResolver() {
        ContentRetriever propertyResolver = mock(ContentRetriever.class);
        when(propertyResolver.hentTekst(anyString())).thenReturn("asd");
        return propertyResolver;
    }

    private HenvendelsePortType getHenvendelsePortTypeMock() {
        HenvendelsePortType mock = mock(HenvendelsePortType.class);
        XMLHenvendelse xmlHenvendelse = lagXMLHenvendelse();
        when(mock.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class)))
            .thenReturn(new WSHentHenvendelseListeResponse().withAny(xmlHenvendelse));

        return mock;
    }

    private XMLHenvendelse lagXMLHenvendelse() {
        XMLHenvendelse xmlHenvendelse = new XMLHenvendelse();
        xmlHenvendelse.setBehandlingskjedeId(TRAAD_ID);
        xmlHenvendelse.setFnr(BRUKERS_FNR);
        xmlHenvendelse.setHenvendelseType(SPORSMAL_SKRIFTLIG.name());
        xmlHenvendelse.setGjeldendeTemagruppe(Temagruppe.ARBD.name());
        xmlHenvendelse.setMetadataListe(new XMLMetadataListe().withMetadata(new XMLMeldingFraBruker().withTemagruppe(Temagruppe.ARBD.name())));
        return xmlHenvendelse;
    }

    @Test
    @DisplayName("Svar delvis ferdigstiller henvendelse mot Henvendelse-tjenesten")
    void ferdigstillerHenvendelse() {
        ArgumentCaptor<WSFerdigstillHenvendelseRequest> argumentCaptor = ArgumentCaptor.forClass(WSFerdigstillHenvendelseRequest.class);

        SubjectHandlerUtil.withIdent(SAKSBEHANDLERS_IDENT, () -> delsvarController.svarDelvis(httpMockRequest, BRUKERS_FNR, lagDelsvarRequest()));

        verify(sendUtHenvendelsePortTypeMock).ferdigstillHenvendelse(argumentCaptor.capture());
        assertEquals(HENVENDELSES_ID, argumentCaptor.getValue().getBehandlingsId().get(0));
    }

    @Test
    @DisplayName("Tilknyttet enhet settes til den enhetet saksbehandler har valgt og er lagret i Cookie")
    void leserValgtEnhetFraCookie() {
        ArgumentCaptor<WSFerdigstillHenvendelseRequest> argumentCaptor = ArgumentCaptor.forClass(WSFerdigstillHenvendelseRequest.class);

        SubjectHandlerUtil.withIdent(SAKSBEHANDLERS_IDENT, () -> delsvarController.svarDelvis(httpMockRequest, BRUKERS_FNR, lagDelsvarRequest()));

        verify(sendUtHenvendelsePortTypeMock).ferdigstillHenvendelse(argumentCaptor.capture());
        XMLHenvendelse xmlHenvendelse = (XMLHenvendelse) argumentCaptor.getValue().getAny();
        assertEquals(VALGT_ENHET, xmlHenvendelse.getTilknyttetEnhet());
    }

    @Test
    @DisplayName("Delvis svar returnerer 200 OK")
    void ferdigstillHenvendelseReturer200OK() {
        ResponseEntity response = SubjectHandlerUtil.withIdent(SAKSBEHANDLERS_IDENT, () -> delsvarController.svarDelvis(httpMockRequest, BRUKERS_FNR, lagDelsvarRequest()));
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @NotNull
    private DelsvarRestRequest lagDelsvarRequest() {
        return new DelsvarRestRequest(null, "", TRAAD_ID, HENVENDELSES_ID, Temagruppe.ARBD.name(), "");
    }
}
