package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLJournalfortInformasjon;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingTilBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.modig.core.context.StaticSubjectHandler;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Sporsmal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.SvarEllerReferat;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.SendUtHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.WSSendUtHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseResponse;
import no.nav.tjeneste.virksomhet.oppgave.v3.HentOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOptimistiskLasing;
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
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.SvarEllerReferat.Henvendelsetype.REFERAT_OPPMOTE;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.SvarEllerReferat.Henvendelsetype.SVAR_SKRIFTLIG;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
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
    private final static String SPORSMAL_ID_1 = "id";
    private final static String FRITEKST = "fritekst";
    private final static String TEMAGRUPPE = "temagruppe";

    private final static String NYESTE_HENVENDELSE_ID = "Nyeste henvendelse";
    private final static String ELDSTE_HENVENDELSE = "Eldste henvendelse";
    public static final String OPPGAVE_ID_1 = "id1";
    public static final String OPPGAVE_ID_2 = "id2";
    public static final String OPPGAVE_ID_3 = "id3";

    public static final String[] UTGAAENDE_TYPER = {
            XMLHenvendelseType.SVAR_SKRIFTLIG.name(),
            XMLHenvendelseType.SVAR_OPPMOTE.name(),
            XMLHenvendelseType.SVAR_TELEFON.name(),
            XMLHenvendelseType.REFERAT_OPPMOTE.name(),
            XMLHenvendelseType.REFERAT_TELEFON.name()};
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
    public void skalHenteSporsmaalOgTilordneIGsak() throws HentOppgaveOppgaveIkkeFunnet, LagreOppgaveOppgaveIkkeFunnet, LagreOppgaveOptimistiskLasing {
        System.setProperty(StaticSubjectHandler.SUBJECTHANDLER_KEY, StaticSubjectHandler.class.getName());
        when(henvendelsePortType.hentHenvendelse(any(WSHentHenvendelseRequest.class))).thenReturn(mockWSHentHenvendelseResponse());

        Sporsmal sporsmal = henvendelseUtsendingService.getSporsmal(SPORSMAL_ID_1);

        assertThat(sporsmal.id, is(SPORSMAL_ID_1));
        assertThat(sporsmal.fritekst, is(FRITEKST));
        assertThat(sporsmal.temagruppe, is(TEMAGRUPPE));
    }

    @Test
    public void skalSendeSvar() {
        henvendelseUtsendingService.sendSvarEllerReferat(new SvarEllerReferat().withFnr(FNR).withFritekst(FRITEKST).withType(SVAR_SKRIFTLIG));

        verify(sendUtHenvendelsePortType).sendUtHenvendelse(wsSendHenvendelseRequestCaptor.capture());
        assertThat(wsSendHenvendelseRequestCaptor.getValue().getType(), is(XMLHenvendelseType.SVAR_SKRIFTLIG.name()));
    }

    @Test
    public void skalSendeReferat() {
        henvendelseUtsendingService.sendSvarEllerReferat(new SvarEllerReferat().withFnr(FNR).withFritekst(FRITEKST).withType(REFERAT_OPPMOTE));

        verify(sendUtHenvendelsePortType).sendUtHenvendelse(wsSendHenvendelseRequestCaptor.capture());
        assertThat(wsSendHenvendelseRequestCaptor.getValue().getType(), is(XMLHenvendelseType.REFERAT_OPPMOTE.name()));
    }

    @Test
    public void skalHenteSporsmalFraOppgaveId() {
        WSHentHenvendelseListeResponse wsHentHenvendelseListeResponse =
                new WSHentHenvendelseListeResponse().withAny(
                        createXMLMeldingFraBruker(OPPGAVE_ID_1, "fritekst1"),
                        createXMLMeldingFraBruker(OPPGAVE_ID_2, "fritekst2"),
                        createXMLMeldingFraBruker(OPPGAVE_ID_3, "fritekst3")
                );

        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(wsHentHenvendelseListeResponse);

        Sporsmal sporsmal = henvendelseUtsendingService.getSporsmalFromOppgaveId(FNR, OPPGAVE_ID_2);

        assertThat(sporsmal.fritekst, is("fritekst2"));
    }

    @Test
    public void skalHenteSporsmalMedRiktigTypeSpesifisert() {
        WSHentHenvendelseListeResponse wsHentHenvendelseListeResponse = new WSHentHenvendelseListeResponse().withAny(createXMLMeldingFraBruker(OPPGAVE_ID_2, "fritekst1"));
        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(wsHentHenvendelseListeResponse);

        henvendelseUtsendingService.getSporsmalFromOppgaveId(FNR, OPPGAVE_ID_2);

        verify(henvendelsePortType).hentHenvendelseListe(hentHenvendelseListeRequestCaptor.capture());
        assertThat(hentHenvendelseListeRequestCaptor.getValue().getTyper(), is(not(empty())));
        assertThat(hentHenvendelseListeRequestCaptor.getValue().getTyper(), contains(XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name()));
        assertThat(hentHenvendelseListeRequestCaptor.getValue().getTyper(), not(contains(UTGAAENDE_TYPER)));
    }

    @Test(expected = RuntimeException.class)
    public void skalKasteExceptionDersomDetIkkeFinnesNoeSporsmalMedDenOppgaveIden() {
        WSHentHenvendelseListeResponse wsHentHenvendelseListeResponse = new WSHentHenvendelseListeResponse().withAny(createXMLMeldingFraBruker(OPPGAVE_ID_1, "fritekst1"));
        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(wsHentHenvendelseListeResponse);

        henvendelseUtsendingService.getSporsmalFromOppgaveId(FNR, "random oppgaveid");
    }

    @Test
    public void skalHenteSvarlisteTilhorendeSporsmal() {
        WSHentHenvendelseListeResponse wsHentHenvendelseListeResponse =
                new WSHentHenvendelseListeResponse().withAny(
                        createXMLMeldingTilBruker(SPORSMAL_ID_1),
                        createXMLMeldingTilBruker(SPORSMAL_ID_1),
                        createXMLMeldingTilBruker("annenId"),
                        createXMLMeldingTilBruker("endaEnAnnenId")
                );
        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(wsHentHenvendelseListeResponse);

        List<SvarEllerReferat> svarliste = henvendelseUtsendingService.getSvarEllerReferatForSporsmal(FNR, SPORSMAL_ID_1);

        assertThat(svarliste, hasSize(2));
        assertThat(svarliste.get(0).sporsmalsId, is(SPORSMAL_ID_1));
        assertThat(svarliste.get(1).sporsmalsId, is(SPORSMAL_ID_1));
    }

    @Test
    public void skalHenteSvarlisteMedRiktigTypeSpesifisert() {
        WSHentHenvendelseListeResponse wsHentHenvendelseListeResponse =
                new WSHentHenvendelseListeResponse().withAny(createXMLMeldingTilBruker(SPORSMAL_ID_1));
        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(wsHentHenvendelseListeResponse);

        henvendelseUtsendingService.getSvarEllerReferatForSporsmal(FNR, SPORSMAL_ID_1);

        verify(henvendelsePortType).hentHenvendelseListe(hentHenvendelseListeRequestCaptor.capture());
        assertThat(hentHenvendelseListeRequestCaptor.getValue().getTyper(), is(not(empty())));
        assertThat(hentHenvendelseListeRequestCaptor.getValue().getTyper(), contains(UTGAAENDE_TYPER));
        assertThat(hentHenvendelseListeRequestCaptor.getValue().getTyper(), not(contains(XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name())));
    }

    @Test
    public void skalHenteSortertListeAvSvarEllerReferatForSporsmalMedEldsteForst() {
        WSHentHenvendelseListeResponse wsHentHenvendelseListeResponse =
                new WSHentHenvendelseListeResponse().withAny(createToXMLMeldingTilBrukerSomSvarerPaaSporsmalsIdMedNyesteForst(SPORSMAL_ID_1));
        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(wsHentHenvendelseListeResponse);

        List<SvarEllerReferat> svarEllerReferatForSporsmal = henvendelseUtsendingService.getSvarEllerReferatForSporsmal(FNR, SPORSMAL_ID_1);

        assertThat(svarEllerReferatForSporsmal.get(0).type, is(SvarEllerReferat.Henvendelsetype.REFERAT_OPPMOTE));
        assertThat(svarEllerReferatForSporsmal.get(1).type, is(SvarEllerReferat.Henvendelsetype.SVAR_OPPMOTE));
    }

    @Test
    public void skalHenteListeAvSvarEllerReferatMedBlankFritekstOmManIkkeHarTilgang() {
        WSHentHenvendelseListeResponse resp =
                new WSHentHenvendelseListeResponse().withAny(createToXMLMeldingerTilBrukerSomSvarerPaSporsmalMedJournalfortTemaGruppe(SPORSMAL_ID_1, JOURNALFORT_TEMA));
        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(resp);
        when(pep.hasAccess(any(PolicyRequest.class))).thenReturn(true, false);

        List<SvarEllerReferat> svarEllerReferatList = henvendelseUtsendingService.getSvarEllerReferatForSporsmal(FNR, SPORSMAL_ID_1);

        assertThat(svarEllerReferatList.get(0).fritekst, isEmptyString());
        assertThat(svarEllerReferatList.get(1).fritekst, not(isEmptyString()));
    }

    private WSHentHenvendelseResponse mockWSHentHenvendelseResponse() {
        return new WSHentHenvendelseResponse().withAny(
                new XMLHenvendelse()
                        .withBehandlingsId(SPORSMAL_ID_1)
                        .withOpprettetDato(now())
                        .withHenvendelseType(XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name())
                        .withMetadataListe(new XMLMetadataListe().withMetadata(
                                new XMLMeldingFraBruker().withFritekst(FRITEKST).withTemagruppe(TEMAGRUPPE)))
        );
    }

    private XMLHenvendelse createXMLMeldingFraBruker(String oppgaveId, String fritekst) {
        return new XMLHenvendelse().withOppgaveIdGsak(oppgaveId).withMetadataListe(new XMLMetadataListe()
                .withMetadata(new XMLMeldingFraBruker().withFritekst(fritekst)));
    }

    private XMLHenvendelse createXMLMeldingTilBruker(String sporsmalId) {
        return new XMLHenvendelse()
                .withFnr("")
                .withOpprettetDato(DateTime.now())
                .withHenvendelseType(XMLHenvendelseType.SVAR_SKRIFTLIG.name())
                .withMetadataListe(new XMLMetadataListe().withMetadata(new XMLMeldingTilBruker().withSporsmalsId(sporsmalId).withNavident("")));
    }

    private List<Object> createToXMLMeldingTilBrukerSomSvarerPaaSporsmalsIdMedNyesteForst(String sporsmalId) {
        return new ArrayList<Object>(asList(
                new XMLHenvendelse()
                        .withFnr("")
                        .withBehandlingsId(NYESTE_HENVENDELSE_ID)
                        .withHenvendelseType(XMLHenvendelseType.SVAR_OPPMOTE.name())
                        .withOpprettetDato(DateTime.now())
                        .withMetadataListe(new XMLMetadataListe().withMetadata(
                                new XMLMeldingTilBruker().withSporsmalsId(sporsmalId).withNavident(""))),
                new XMLHenvendelse()
                        .withFnr("")
                        .withBehandlingsId(ELDSTE_HENVENDELSE)
                        .withOpprettetDato(DateTime.now().minusDays(1))
                        .withHenvendelseType(XMLHenvendelseType.REFERAT_OPPMOTE.name())
                        .withMetadataListe(new XMLMetadataListe().withMetadata(
                                new XMLMeldingTilBruker().withSporsmalsId(sporsmalId).withNavident("")))
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
