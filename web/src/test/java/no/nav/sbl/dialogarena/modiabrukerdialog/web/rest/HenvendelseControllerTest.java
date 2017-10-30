package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSNAVAnsatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNAVEnhetListe;
import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navorgenhet.ASBOGOSYSNavEnhet;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.*;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.kjerneinfo.domain.person.Person;
import no.nav.kjerneinfo.domain.person.Personfakta;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.*;
import no.nav.modig.content.PropertyResolver;
import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.FeatureToggle;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.*;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse.HenvendelseServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.henvendelse.FerdigstillHenvendelseRestRequest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.henvendelse.HenvendelseController;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.SendUtHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.WSFerdigstillHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeResponse;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.ws.rs.core.Response;

import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SPORSMAL_SKRIFTLIG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

class HenvendelseControllerTest {

    public static final String BRUKERS_FNR = "10108000398";
    public static final String TRAAD_ID = "trådID";
    public static final String HENVENDELSES_ID = "henvendelsesID";

    private HenvendelseController henvendelseController;
    private SendUtHenvendelsePortType sendUtHenvendelsePortTypeMock;

    @BeforeAll
    static void beforeAll() {
        FeatureToggle.enableDelviseSvarFunksjonalitet();
    }

    @AfterAll
    static void afterAll() {
        FeatureToggle.disableDelviseSvarFunksjonalitet();
    }

    @BeforeEach
    void before() {
        henvendelseController = new HenvendelseController(new HenvendelseServiceImpl(setupHenvendelseUtsendingService()));
        setupSubjectHandler();
    }

    private HenvendelseUtsendingServiceImpl setupHenvendelseUtsendingService() {
        SaksbehandlerInnstillingerServiceImpl saksbehandlerInnstillingerService = setupSaksbehandlerInnstillingerService();
        HenvendelsePortType henvendelsePortTypeMock = getHenvendelsePortTypeMock();
        PropertyResolver propertyResolver = mockPropertyResolver();
        PersonKjerneinfoServiceBi kjerneinfoMock = mockPersonKjerneinfoService();
        sendUtHenvendelsePortTypeMock = mock(SendUtHenvendelsePortType.class);
        return new HenvendelseUtsendingServiceImpl(henvendelsePortTypeMock, sendUtHenvendelsePortTypeMock, null, null, null, null, saksbehandlerInnstillingerService, propertyResolver, kjerneinfoMock, null);
    }

    private PropertyResolver mockPropertyResolver() {
        PropertyResolver propertyResolver = mock(PropertyResolver.class);
        when(propertyResolver.getProperty(anyString())).thenReturn("asd");
        return propertyResolver;
    }

    private SaksbehandlerInnstillingerServiceImpl setupSaksbehandlerInnstillingerService() {
        GOSYSNAVansatt gosysnavAnsatt = mockGosysNavAnsatt();
        return new SaksbehandlerInnstillingerServiceImpl(new AnsattServiceImpl(gosysnavAnsatt));
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

    private GOSYSNAVansatt mockGosysNavAnsatt() {
        GOSYSNAVansatt gosysnavAnsatt = mock(GOSYSNAVansatt.class);
        try {
            ASBOGOSYSNAVEnhetListe ansattListe = new ASBOGOSYSNAVEnhetListe();
            ansattListe.getNAVEnheter().add(new ASBOGOSYSNavEnhet());
            when(gosysnavAnsatt.hentNAVAnsattEnhetListe(any(ASBOGOSYSNAVAnsatt.class))).thenReturn(ansattListe);
        } catch (HentNAVAnsattEnhetListeFaultGOSYSNAVAnsattIkkeFunnetMsg | HentNAVAnsattEnhetListeFaultGOSYSGeneriskMsg e) {
            throw new RuntimeException(e);
        }
        return gosysnavAnsatt;
    }

    private void setupSubjectHandler() {
        System.setProperty(SubjectHandler.SUBJECTHANDLER_KEY, ThreadLocalSubjectHandler.class.getCanonicalName());
    }

    @Test
    @DisplayName("Ferdigstill henvendelse ferdigstiller henvendelse mot Henvendelse-applikasjonen")
    void ferdigstillerHenvendelse() {
        ArgumentCaptor<WSFerdigstillHenvendelseRequest> argumentCaptor = ArgumentCaptor.forClass(WSFerdigstillHenvendelseRequest.class);

        henvendelseController.ferdigstill(BRUKERS_FNR, TRAAD_ID, HENVENDELSES_ID, new MockHttpServletRequest(), new FerdigstillHenvendelseRestRequest());

        verify(sendUtHenvendelsePortTypeMock).ferdigstillHenvendelse(argumentCaptor.capture());
        assertEquals(argumentCaptor.getValue().getBehandlingsId().get(0), HENVENDELSES_ID);
    }

    @Test
    @DisplayName("Ferdigstill henvendelse returnerer 200 OK")
    void ferdigstillHenvendelseReturer200OK() {
        Response response = henvendelseController.ferdigstill(BRUKERS_FNR, TRAAD_ID, HENVENDELSES_ID, new MockHttpServletRequest(), new FerdigstillHenvendelseRestRequest());

        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
    }

}