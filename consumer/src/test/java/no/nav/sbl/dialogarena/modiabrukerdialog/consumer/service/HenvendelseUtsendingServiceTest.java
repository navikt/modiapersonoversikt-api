package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.*;
import no.nav.modig.core.context.StaticSubjectHandler;
import no.nav.modig.lang.option.Optional;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.*;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.SendUtHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.WSSendUtHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.WSSendUtHenvendelseResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeResponse;
import no.nav.virksomhet.tjenester.ruting.v1.Ruting;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.*;
import static org.hamcrest.Matchers.*;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HenvendelseTestConfig.class, OppgaveTestConfig.class})
public class HenvendelseUtsendingServiceTest {

    private final static String FNR = "fnr";
    private final static String TRAAD_ID = "id";
    private final static String FRITEKST = "fritekst";
    private final static String TEMAGRUPPE = "temagruppe";

    private final static String NYESTE_HENVENDELSE_ID = "Nyeste henvendelse";
    private final static String ELDSTE_HENVENDELSE = "Eldste henvendelse";

    public static final String JOURNALFORT_TEMA = "tema jobb";

    @Captor
    ArgumentCaptor<WSSendUtHenvendelseRequest> wsSendHenvendelseRequestCaptor;
    @Captor
    ArgumentCaptor<WSHentHenvendelseListeRequest> hentHenvendelseListeRequestCaptor;
    @Captor
    ArgumentCaptor<Sak> sakArgumentCaptor;
    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;

    @Mock
    public SakerService sakerService;
    @Mock
    public OppgaveBehandlingService oppgaveBehandlingService;
    @Mock
    public SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Mock
    public AnsattService ansattWS;
    @Mock
    public Ruting ruting;

    @Inject
    @Named("pep")
    private EnforcementPoint pep;
    @Inject
    private HenvendelsePortType henvendelsePortType;
    @Inject
    protected SendUtHenvendelsePortType sendUtHenvendelsePortType;

    @InjectMocks
    private HenvendelseUtsendingService henvendelseUtsendingService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        when(sendUtHenvendelsePortType.sendUtHenvendelse(any(WSSendUtHenvendelseRequest.class))).thenReturn(
                new WSSendUtHenvendelseResponse().withBehandlingsId("ID_1")
        );
    }

    @Test
    public void henterSporsmal() {
        System.setProperty(StaticSubjectHandler.SUBJECTHANDLER_KEY, StaticSubjectHandler.class.getName());
        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(mockWSHentHenvendelseResponse());

        Melding sporsmal = henvendelseUtsendingService.hentTraad("fnr", TRAAD_ID).get(0);

        verify(henvendelsePortType).hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class));
        assertThat(sporsmal.id, is(TRAAD_ID));
        assertThat(sporsmal.fritekst, is(FRITEKST));
        assertThat(sporsmal.temagruppe, is(TEMAGRUPPE));
    }

    @Test
    public void skalSendeSvar() throws Exception {
        Melding melding = new Melding().withFnr(FNR).withFritekst(FRITEKST).withType(SVAR_SKRIFTLIG);
        henvendelseUtsendingService.sendHenvendelse(melding, Optional.<String>none(), Optional.<Sak>none());

        verify(sendUtHenvendelsePortType).sendUtHenvendelse(wsSendHenvendelseRequestCaptor.capture());
        assertThat(wsSendHenvendelseRequestCaptor.getValue().getType(), is(XMLHenvendelseType.SVAR_SKRIFTLIG.name()));
    }

    @Test
    public void skalSendeReferat() throws Exception {
        Melding melding = new Melding().withFnr(FNR).withFritekst(FRITEKST).withType(SAMTALEREFERAT_OPPMOTE);
        henvendelseUtsendingService.sendHenvendelse(melding, Optional.<String>none(), Optional.<Sak>none());

        verify(sendUtHenvendelsePortType).sendUtHenvendelse(wsSendHenvendelseRequestCaptor.capture());
        assertThat(wsSendHenvendelseRequestCaptor.getValue().getType(), is(XMLHenvendelseType.REFERAT_OPPMOTE.name()));
    }

    @Test
    public void skalSendeSporsmal() throws Exception {
        Melding melding = new Melding().withFnr(FNR).withFritekst(FRITEKST).withType(SPORSMAL_MODIA_UTGAAENDE);
        henvendelseUtsendingService.sendHenvendelse(melding, Optional.<String>none(), Optional.<Sak>none());

        verify(sendUtHenvendelsePortType).sendUtHenvendelse(wsSendHenvendelseRequestCaptor.capture());
        assertThat(wsSendHenvendelseRequestCaptor.getValue().getType(), is(XMLHenvendelseType.SPORSMAL_MODIA_UTGAAENDE.name()));
    }

    @Test
    public void skalJournalforeHenvendelseDersomSakErSatt() throws Exception {
        Sak sak = new Sak();
        sak.saksId = optional("sakid");
        Melding melding = new Melding().withFnr(FNR).withFritekst(FRITEKST).withType(SPORSMAL_MODIA_UTGAAENDE);
        henvendelseUtsendingService.sendHenvendelse(melding, Optional.<String>none(), optional(sak));

        verify(sakerService).knyttBehandlingskjedeTilSak(anyString(), anyString(), sakArgumentCaptor.capture());

        Sak sendtSak = sakArgumentCaptor.getValue();
        assertThat(sendtSak, is(sak));
    }

    @Test
    public void skalFerdigstilleOppgaveDersomDenneErSatt() throws Exception {
        String oppgaveId = "oppgaveId";
        Melding melding = new Melding().withFnr(FNR).withFritekst(FRITEKST).withType(SPORSMAL_MODIA_UTGAAENDE);
        henvendelseUtsendingService.sendHenvendelse(melding, optional(oppgaveId), Optional.<Sak>none());

        verify(oppgaveBehandlingService).ferdigstillOppgaveIGsak(stringArgumentCaptor.capture());

        String sendtOppgaveId = stringArgumentCaptor.getValue();
        assertThat(sendtOppgaveId, is(oppgaveId));
    }

    @Test
    public void skalHenteTraad() {
        WSHentHenvendelseListeResponse wsHentHenvendelseListeResponse =
                new WSHentHenvendelseListeResponse().withAny(
                        createXMLMeldingFraBruker(),
                        createXMLMeldingTilBruker(TRAAD_ID),
                        createXMLMeldingTilBruker(TRAAD_ID),
                        createXMLMeldingTilBruker("annenId"),
                        createXMLMeldingTilBruker("endaEnAnnenId")
                );

        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(wsHentHenvendelseListeResponse);

        List<Melding> traad = henvendelseUtsendingService.hentTraad(FNR, TRAAD_ID);

        assertThat(traad, hasSize(3));
        assertThat(traad.get(0).traadId, is(TRAAD_ID));
        assertThat(traad.get(1).traadId, is(TRAAD_ID));
        assertThat(traad.get(2).traadId, is(TRAAD_ID));
    }

    @Test
    public void skalHenteSvarlisteMedRiktigTypeSpesifisert() {
        WSHentHenvendelseListeResponse wsHentHenvendelseListeResponse = new WSHentHenvendelseListeResponse().withAny(createXMLMeldingTilBruker(TRAAD_ID));

        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(wsHentHenvendelseListeResponse);

        henvendelseUtsendingService.hentTraad(FNR, TRAAD_ID);

        verify(henvendelsePortType).hentHenvendelseListe(hentHenvendelseListeRequestCaptor.capture());
        assertThat(hentHenvendelseListeRequestCaptor.getValue().getTyper(), is(not(empty())));
        assertThat(hentHenvendelseListeRequestCaptor.getValue().getTyper(), containsInAnyOrder(
                XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name(),
                XMLHenvendelseType.SVAR_SKRIFTLIG.name(),
                XMLHenvendelseType.SVAR_OPPMOTE.name(),
                XMLHenvendelseType.SVAR_TELEFON.name(),
                XMLHenvendelseType.REFERAT_OPPMOTE.name(),
                XMLHenvendelseType.REFERAT_TELEFON.name(),
                XMLHenvendelseType.SPORSMAL_MODIA_UTGAAENDE.name(),
                XMLHenvendelseType.SVAR_SBL_INNGAAENDE.name()));
        assertThat(hentHenvendelseListeRequestCaptor.getValue().getTyper(), not(contains(XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name())));
    }

    @Test
    public void skalHenteSortertListeAvSvarEllerReferatForSporsmalMedEldsteForst() {
        WSHentHenvendelseListeResponse wsHentHenvendelseListeResponse =
                new WSHentHenvendelseListeResponse().withAny(createTraad(TRAAD_ID).toArray());

        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(wsHentHenvendelseListeResponse);

        List<Melding> traad = henvendelseUtsendingService.hentTraad(FNR, TRAAD_ID);

        assertThat(traad, hasSize(2));
        assertThat(traad.get(0).meldingstype, is(SPORSMAL_SKRIFTLIG));
        assertThat(traad.get(1).meldingstype, is(SVAR_TELEFON));
    }

    @Test
    public void skalHenteTraadMedBlankFritekstOmManIkkeHarTilgang() {
        WSHentHenvendelseListeResponse resp =
                new WSHentHenvendelseListeResponse().withAny(createTraadMedJournalfortTemaGruppe(TRAAD_ID, JOURNALFORT_TEMA).toArray());

        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(resp);
        when(pep.hasAccess(any(PolicyRequest.class))).thenReturn(false).thenReturn(false).thenReturn(true);

        List<Melding> traad = henvendelseUtsendingService.hentTraad(FNR, TRAAD_ID);

        assertThat(traad, hasSize(3));
        assertThat(traad.get(1).fritekst, isEmptyString());
        assertThat(traad.get(2).fritekst, not(isEmptyString()));
    }

    private WSHentHenvendelseListeResponse mockWSHentHenvendelseResponse() {
        return new WSHentHenvendelseListeResponse().withAny(
                new XMLHenvendelse()
                        .withBehandlingsId(TRAAD_ID)
                        .withBehandlingskjedeId(TRAAD_ID)
                        .withOpprettetDato(now())
                        .withHenvendelseType(XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name())
                        .withMetadataListe(new XMLMetadataListe().withMetadata(
                                new XMLMeldingFraBruker().withFritekst(FRITEKST).withTemagruppe(TEMAGRUPPE)))
        );
    }

    private XMLHenvendelse createXMLMeldingFraBruker() {
        return new XMLHenvendelse()
                .withOppgaveIdGsak("")
                .withBehandlingskjedeId(TRAAD_ID)
                .withOpprettetDato(now().minusDays(2))
                .withHenvendelseType(XMLHenvendelseType.SPORSMAL_SKRIFTLIG.value())
                .withMetadataListe(new XMLMetadataListe()
                        .withMetadata(new XMLMeldingFraBruker()
                                .withFritekst("")));
    }

    private XMLHenvendelse createXMLMeldingTilBruker(String sporsmalId) {
        return new XMLHenvendelse()
                .withFnr("")
                .withBehandlingskjedeId(sporsmalId)
                .withOpprettetDato(now())
                .withHenvendelseType(XMLHenvendelseType.SVAR_SKRIFTLIG.name())
                .withMetadataListe(new XMLMetadataListe().withMetadata(new XMLMeldingTilBruker().withNavident("")));
    }

    private List<XMLHenvendelse> createTraad(String sporsmalId) {
        return asList(
                new XMLHenvendelse()
                        .withFnr("")
                        .withBehandlingsId(NYESTE_HENVENDELSE_ID)
                        .withBehandlingskjedeId(sporsmalId)
                        .withHenvendelseType(XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name())
                        .withOpprettetDato(now().minusDays(1))
                        .withMetadataListe(new XMLMetadataListe().withMetadata(
                                new XMLMeldingFraBruker().withFritekst("").withTemagruppe(""))),
                new XMLHenvendelse()
                        .withFnr("")
                        .withBehandlingsId(ELDSTE_HENVENDELSE)
                        .withBehandlingskjedeId(sporsmalId)
                        .withOpprettetDato(now())
                        .withHenvendelseType(XMLHenvendelseType.SVAR_TELEFON.name())
                        .withMetadataListe(new XMLMetadataListe().withMetadata(
                                new XMLMeldingTilBruker().withNavident("")))
        );
    }

    private List<XMLHenvendelse> createTraadMedJournalfortTemaGruppe(String sporsmalsId, String temagruppe) {
        List<XMLHenvendelse> svar = asList(createXMLMeldingTilBruker(sporsmalsId), createXMLMeldingTilBruker(sporsmalsId));
        for (XMLHenvendelse henvendelse : svar) {
            henvendelse.withMetadataListe(new XMLMetadataListe().withMetadata(
                    new XMLMeldingTilBruker()
                            .withSporsmalsId(sporsmalsId)
                            .withNavident("")
                            .withFritekst("Fritekst")
            )).withJournalfortInformasjon(new XMLJournalfortInformasjon().withJournalfortTema(temagruppe));
        }

        XMLHenvendelse xmlMeldingFraBruker = createXMLMeldingFraBruker();
        xmlMeldingFraBruker.withJournalfortInformasjon(new XMLJournalfortInformasjon().withJournalfortTema(temagruppe));

        List<XMLHenvendelse> xmlHenvendelser = new ArrayList<>(asList(xmlMeldingFraBruker));
        xmlHenvendelser.addAll(svar);
        return xmlHenvendelser;
    }

}
