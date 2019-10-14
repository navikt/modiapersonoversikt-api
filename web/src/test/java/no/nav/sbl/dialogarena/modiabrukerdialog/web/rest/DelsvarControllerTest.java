package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.kjerneinfo.domain.person.Person;
import no.nav.kjerneinfo.domain.person.Personfakta;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.modig.content.ContentRetriever;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.cache.CacheTestUtil;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http.HttpRequestUtil;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http.SubjectHandlerUtil;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse.DelsvarServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.henvendelse.DelsvarController;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.henvendelse.DelsvarRestRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.SendUtHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.WSFerdigstillHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeResponse;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.ws.rs.core.Response;
import java.util.Collections;

import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SPORSMAL_SKRIFTLIG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class DelsvarControllerTest {

    public static final String BRUKERS_FNR = "10108000398";
    public static final String TRAAD_ID = "trådID";
    public static final String HENVENDELSES_ID = "henvendelsesID";
    public static final String SAKSBEHANDLERS_IDENT = "z999666";
    private static final String VALGT_ENHET = "0300";

    private OppgaveBehandlingService oppgaveBehandlingServiceMock = mock(OppgaveBehandlingService.class);
    private MockHttpServletRequest httpMockRequest;
    private DelsvarController delsvarController;
    private SendUtHenvendelsePortType sendUtHenvendelsePortTypeMock;

    @BeforeAll
    static void beforeAll() {
//        SubjectHandlerUtil.medSaksbehandler(SAKSBEHANDLERS_IDENT);
        CacheTestUtil.setupCache(Collections.singletonList("endpointCache"));
    }

    @AfterAll
    static void afterAll() {
        CacheTestUtil.tearDown();
    }

    @BeforeEach
    void before() {
        httpMockRequest = HttpRequestUtil.mockHttpServletRequestMedCookie(SAKSBEHANDLERS_IDENT, VALGT_ENHET);
        delsvarController = new DelsvarController(new DelsvarServiceImpl(setupHenvendelseUtsendingService(), oppgaveBehandlingServiceMock));
    }

    private HenvendelseUtsendingServiceImpl setupHenvendelseUtsendingService() {
        HenvendelsePortType henvendelsePortTypeMock = getHenvendelsePortTypeMock();
        ContentRetriever propertyResolver = mockPropertyResolver();
        PersonKjerneinfoServiceBi kjerneinfoMock = mockPersonKjerneinfoService();
        sendUtHenvendelsePortTypeMock = mock(SendUtHenvendelsePortType.class);
        return new HenvendelseUtsendingServiceImpl(henvendelsePortTypeMock, sendUtHenvendelsePortTypeMock, null, null, null, null, propertyResolver, kjerneinfoMock, null);
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

    private PersonKjerneinfoServiceBi mockPersonKjerneinfoService() {
        PersonKjerneinfoServiceBi mock = mock(PersonKjerneinfoServiceBi.class);
        HentKjerneinformasjonResponse response = new HentKjerneinformasjonResponse();
        Person person = new Person();
        person.setPersonfakta(new Personfakta());
        response.setPerson(person);
        when(mock.hentKjerneinformasjon(any(HentKjerneinformasjonRequest.class))).thenReturn(response);
        return mock;
    }

    @Test
    @DisplayName("Svar delvis ferdigstiller henvendelse mot Henvendelse-tjenesten")
    void ferdigstillerHenvendelse() {
        ArgumentCaptor<WSFerdigstillHenvendelseRequest> argumentCaptor = ArgumentCaptor.forClass(WSFerdigstillHenvendelseRequest.class);

        delsvarController.svarDelvis(BRUKERS_FNR, httpMockRequest, lagDelsvarRequest());

        verify(sendUtHenvendelsePortTypeMock).ferdigstillHenvendelse(argumentCaptor.capture());
        assertEquals(HENVENDELSES_ID, argumentCaptor.getValue().getBehandlingsId().get(0));
    }

    @Test
    @DisplayName("Tilknyttet enhet settes til den enhetet saksbehandler har valgt og er lagret i Cookie")
    void leserValgtEnhetFraCookie() {
        ArgumentCaptor<WSFerdigstillHenvendelseRequest> argumentCaptor = ArgumentCaptor.forClass(WSFerdigstillHenvendelseRequest.class);

        delsvarController.svarDelvis(BRUKERS_FNR, httpMockRequest, lagDelsvarRequest());

        verify(sendUtHenvendelsePortTypeMock).ferdigstillHenvendelse(argumentCaptor.capture());
        XMLHenvendelse xmlHenvendelse = (XMLHenvendelse) argumentCaptor.getValue().getAny();
        assertEquals(VALGT_ENHET, xmlHenvendelse.getTilknyttetEnhet());
    }

    @Test
    @DisplayName("Delvis svar returnerer 200 OK")
    void ferdigstillHenvendelseReturer200OK() {
        Response response = delsvarController.svarDelvis(BRUKERS_FNR, httpMockRequest, lagDelsvarRequest());

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @NotNull
    private DelsvarRestRequest lagDelsvarRequest() {
        return new DelsvarRestRequest("", TRAAD_ID,HENVENDELSES_ID, Temagruppe.ARBD.name(), "");
    }
}