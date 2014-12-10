package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.*;
import no.nav.modig.core.context.StaticSubjectHandler;
import no.nav.modig.lang.option.Optional;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Henvendelse;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.SendUtHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.WSSendUtHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseResponse;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Henvendelse.Henvendelsetype.*;
import static org.hamcrest.Matchers.*;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HenvendelseTestConfig.class})
public class HenvendelseUtsendingServiceTest {

    private final static String FNR = "fnr";
    private final static String TRAAD_ID = "id";
    private final static String FRITEKST = "fritekst";
    private final static String TEMAGRUPPE = "temagruppe";

    private final static String NYESTE_HENVENDELSE_ID = "Nyeste henvendelse";
    private final static String ELDSTE_HENVENDELSE = "Eldste henvendelse";

    public static final String[] SVAR_TYPER = {
            XMLHenvendelseType.SVAR_SKRIFTLIG.name(),
            XMLHenvendelseType.SVAR_OPPMOTE.name(),
            XMLHenvendelseType.SVAR_TELEFON.name()};

    public static final String JOURNALFORT_TEMA = "tema jobb";

    @Captor
    ArgumentCaptor<WSSendUtHenvendelseRequest> wsSendHenvendelseRequestCaptor;
    @Captor
    ArgumentCaptor<WSHentHenvendelseListeRequest> hentHenvendelseListeRequestCaptor;

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
    }

    @Test
    public void henterSporsmal() {
        System.setProperty(StaticSubjectHandler.SUBJECTHANDLER_KEY, StaticSubjectHandler.class.getName());
        when(henvendelsePortType.hentHenvendelse(any(WSHentHenvendelseRequest.class))).thenReturn(mockWSHentHenvendelseResponse());
        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(new WSHentHenvendelseListeResponse());

        Henvendelse sporsmal = henvendelseUtsendingService.hentTraad("fnr", TRAAD_ID).get(0);

        verify(henvendelsePortType).hentHenvendelse(any(WSHentHenvendelseRequest.class));
        assertThat(sporsmal.id, is(TRAAD_ID));
        assertThat(sporsmal.fritekst, is(FRITEKST));
        assertThat(sporsmal.temagruppe, is(TEMAGRUPPE));
    }

    @Test
    public void skalSendeSvar() throws HenvendelseUtsendingService.OppgaveErFerdigstilt {
        henvendelseUtsendingService.sendHenvendelse(new Henvendelse().withFnr(FNR).withFritekst(FRITEKST).withType(SVAR_SKRIFTLIG), Optional.<String>none());

        verify(sendUtHenvendelsePortType).sendUtHenvendelse(wsSendHenvendelseRequestCaptor.capture());
        assertThat(wsSendHenvendelseRequestCaptor.getValue().getType(), is(XMLHenvendelseType.SVAR_SKRIFTLIG.name()));
    }

    @Test
    public void skalSendeReferat() throws HenvendelseUtsendingService.OppgaveErFerdigstilt {
        henvendelseUtsendingService.sendHenvendelse(new Henvendelse().withFnr(FNR).withFritekst(FRITEKST).withType(REFERAT_OPPMOTE), Optional.<String>none());

        verify(sendUtHenvendelsePortType).sendUtHenvendelse(wsSendHenvendelseRequestCaptor.capture());
        assertThat(wsSendHenvendelseRequestCaptor.getValue().getType(), is(XMLHenvendelseType.REFERAT_OPPMOTE.name()));
    }

    @Test
    public void skalHenteSvarlisteTilhorendeSporsmal() {
        WSHentHenvendelseListeResponse wsHentHenvendelseListeResponse =
                new WSHentHenvendelseListeResponse().withAny(
                        createXMLMeldingTilBruker(TRAAD_ID),
                        createXMLMeldingTilBruker(TRAAD_ID),
                        createXMLMeldingTilBruker("annenId"),
                        createXMLMeldingTilBruker("endaEnAnnenId")
                );
        when(henvendelsePortType.hentHenvendelse(any(WSHentHenvendelseRequest.class))).thenReturn(new WSHentHenvendelseResponse().withAny(createXMLMeldingFraBruker("", "")));
        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(wsHentHenvendelseListeResponse);

        List<Henvendelse> traad = henvendelseUtsendingService.hentTraad(FNR, TRAAD_ID);

        assertThat(traad, hasSize(3));
        assertThat(traad.get(1).traadId, is(TRAAD_ID));
        assertThat(traad.get(2).traadId, is(TRAAD_ID));
    }

    @Test
    public void skalHenteSvarlisteMedRiktigTypeSpesifisert() {
        WSHentHenvendelseListeResponse wsHentHenvendelseListeResponse = new WSHentHenvendelseListeResponse().withAny(createXMLMeldingTilBruker(TRAAD_ID));

        when(henvendelsePortType.hentHenvendelse(any(WSHentHenvendelseRequest.class))).thenReturn(new WSHentHenvendelseResponse().withAny(createXMLMeldingFraBruker("", "")));
        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(wsHentHenvendelseListeResponse);

        henvendelseUtsendingService.hentTraad(FNR, TRAAD_ID);

        verify(henvendelsePortType).hentHenvendelseListe(hentHenvendelseListeRequestCaptor.capture());
        assertThat(hentHenvendelseListeRequestCaptor.getValue().getTyper(), is(not(empty())));
        assertThat(hentHenvendelseListeRequestCaptor.getValue().getTyper(), containsInAnyOrder(SVAR_TYPER));
        assertThat(hentHenvendelseListeRequestCaptor.getValue().getTyper(), not(contains(XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name())));
    }

    @Test
    public void skalHenteSortertListeAvSvarEllerReferatForSporsmalMedEldsteForst() {
        WSHentHenvendelseListeResponse wsHentHenvendelseListeResponse =
                new WSHentHenvendelseListeResponse().withAny(createToXMLMeldingTilBrukerSomSvarerPaaSporsmalsIdMedNyesteForst(TRAAD_ID));

        when(henvendelsePortType.hentHenvendelse(any(WSHentHenvendelseRequest.class))).thenReturn(new WSHentHenvendelseResponse().withAny(createXMLMeldingFraBruker("", "")));
        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(wsHentHenvendelseListeResponse);

        List<Henvendelse> traad = henvendelseUtsendingService.hentTraad(FNR, TRAAD_ID);

        assertThat(traad, hasSize(3));
        assertThat(traad.get(1).type, is(SVAR_TELEFON));
        assertThat(traad.get(2).type, is(SVAR_OPPMOTE));
    }

    @Test
    public void skalHenteListeAvSvarEllerReferatMedBlankFritekstOmManIkkeHarTilgang() {
        WSHentHenvendelseListeResponse resp =
                new WSHentHenvendelseListeResponse().withAny(createToXMLMeldingerTilBrukerSomSvarerPaSporsmalMedJournalfortTemaGruppe(TRAAD_ID, JOURNALFORT_TEMA));

        when(henvendelsePortType.hentHenvendelse(any(WSHentHenvendelseRequest.class))).thenReturn(new WSHentHenvendelseResponse().withAny(createXMLMeldingFraBruker("", "")));
        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(resp);
        when(pep.hasAccess(any(PolicyRequest.class))).thenReturn(true, false);

        List<Henvendelse> traad = henvendelseUtsendingService.hentTraad(FNR, TRAAD_ID);

        assertThat(traad, hasSize(3));
        assertThat(traad.get(1).fritekst, isEmptyString());
        assertThat(traad.get(2).fritekst, not(isEmptyString()));
    }

    private WSHentHenvendelseResponse mockWSHentHenvendelseResponse() {
        return new WSHentHenvendelseResponse().withAny(
                new XMLHenvendelse()
                        .withBehandlingsId(TRAAD_ID)
                        .withOpprettetDato(now())
                        .withHenvendelseType(XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name())
                        .withMetadataListe(new XMLMetadataListe().withMetadata(
                                new XMLMeldingFraBruker().withFritekst(FRITEKST).withTemagruppe(TEMAGRUPPE)))
        );
    }

    private XMLHenvendelse createXMLMeldingFraBruker(String oppgaveId, String fritekst) {
        return new XMLHenvendelse()
                .withOppgaveIdGsak(oppgaveId)
                .withOpprettetDato(now().minusDays(2))
                .withHenvendelseType(XMLHenvendelseType.SPORSMAL_SKRIFTLIG.value())
                .withMetadataListe(new XMLMetadataListe()
                        .withMetadata(new XMLMeldingFraBruker()
                                .withFritekst(fritekst)));
    }

    private XMLHenvendelse createXMLMeldingTilBruker(String sporsmalId) {
        return new XMLHenvendelse()
                .withFnr("")
                .withBehandlingskjedeId(sporsmalId)
                .withOpprettetDato(DateTime.now())
                .withHenvendelseType(XMLHenvendelseType.SVAR_SKRIFTLIG.name())
                .withMetadataListe(new XMLMetadataListe().withMetadata(new XMLMeldingTilBruker().withNavident("")));
    }

    private List<Object> createToXMLMeldingTilBrukerSomSvarerPaaSporsmalsIdMedNyesteForst(String sporsmalId) {
        return new ArrayList<Object>(asList(
                new XMLHenvendelse()
                        .withFnr("")
                        .withBehandlingsId(NYESTE_HENVENDELSE_ID)
                        .withBehandlingskjedeId(sporsmalId)
                        .withHenvendelseType(XMLHenvendelseType.SVAR_OPPMOTE.name())
                        .withOpprettetDato(DateTime.now())
                        .withMetadataListe(new XMLMetadataListe().withMetadata(
                                new XMLMeldingTilBruker().withNavident(""))),
                new XMLHenvendelse()
                        .withFnr("")
                        .withBehandlingsId(ELDSTE_HENVENDELSE)
                        .withBehandlingskjedeId(sporsmalId)
                        .withOpprettetDato(DateTime.now().minusDays(1))
                        .withHenvendelseType(XMLHenvendelseType.SVAR_TELEFON.name())
                        .withMetadataListe(new XMLMetadataListe().withMetadata(
                                new XMLMeldingTilBruker().withNavident("")))
        ));
    }

    private List<Object> createToXMLMeldingerTilBrukerSomSvarerPaSporsmalMedJournalfortTemaGruppe(String sporsmalsId, String temagruppe) {
        List<Object> lst = createToXMLMeldingTilBrukerSomSvarerPaaSporsmalsIdMedNyesteForst(sporsmalsId);
        for (Object obj : lst) {
            XMLHenvendelse henvendelse = (XMLHenvendelse) obj;
            henvendelse.withMetadataListe(new XMLMetadataListe().withMetadata(
                    new XMLMeldingTilBruker()
                            .withSporsmalsId(sporsmalsId)
                            .withNavident("")
                            .withFritekst("Fritekst")
            )).withJournalfortInformasjon(new XMLJournalfortInformasjon().withJournalfortTema(temagruppe));
        }
        return lst;
    }

}
