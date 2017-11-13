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
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.FeatureToggle;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.*;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse.SvarDelvisServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.henvendelse.SvarDelvisRESTRequest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.henvendelse.SvarDelvisController;
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
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature.DELVISE_SVAR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

class SvarDelvisControllerTest {

    public static final String BRUKERS_FNR = "10108000398";
    public static final String TRAAD_ID = "trådID";
    public static final String HENVENDELSES_ID = "henvendelsesID";

    private SvarDelvisController svarDelvisController;
    private SendUtHenvendelsePortType sendUtHenvendelsePortTypeMock;

    @BeforeAll
    static void beforeAll() {
        FeatureToggle.toggleFeature(DELVISE_SVAR);
    }

    @AfterAll
    static void afterAll() {
        FeatureToggle.disableFeature(DELVISE_SVAR);
    }

    @BeforeEach
    void before() {
        svarDelvisController = new SvarDelvisController(new SvarDelvisServiceImpl(setupHenvendelseUtsendingService(),
                mock(SaksbehandlerInnstillingerServiceImpl.class)));
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
    @DisplayName("Svar delvis ferdigstiller henvendelse mot Henvendelse-tjenesten")
    void ferdigstillerHenvendelse() {
        ArgumentCaptor<WSFerdigstillHenvendelseRequest> argumentCaptor = ArgumentCaptor.forClass(WSFerdigstillHenvendelseRequest.class);

        svarDelvisController.svarDelvis(BRUKERS_FNR, TRAAD_ID, HENVENDELSES_ID, new MockHttpServletRequest(), new SvarDelvisRESTRequest());

        verify(sendUtHenvendelsePortTypeMock).ferdigstillHenvendelse(argumentCaptor.capture());
        assertEquals(argumentCaptor.getValue().getBehandlingsId().get(0), HENVENDELSES_ID);
    }

    @Test
    @DisplayName("Delvis svar returnerer 200 OK")
    void ferdigstillHenvendelseReturer200OK() {
        Response response = svarDelvisController.svarDelvis(BRUKERS_FNR, TRAAD_ID, HENVENDELSES_ID, new MockHttpServletRequest(), new SvarDelvisRESTRequest());

        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
    }

}