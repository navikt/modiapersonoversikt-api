package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingTilBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.modig.core.context.StaticSubjectHandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Sporsmal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.SvarEllerReferat;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SPORSMAL;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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
    private final static String SPORSMAL_ID = "id";
    private final static String FRITEKST = "fritekst";
    private final static String TEMAGRUPPE = "temagruppe";

    private final static String NYESTE_HENVENDELSE_ID = "Nyeste henvendelse";
    private final static String ELDSTE_HENVENDELSE = "Eldste henvendelse";

    @Captor
    ArgumentCaptor<WSSendUtHenvendelseRequest> wsSendHenvendelseRequestCaptor;
    @Captor
    ArgumentCaptor<WSHentHenvendelseListeRequest> hentHenvendelseListeRequestCaptor;

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

        Sporsmal sporsmal = henvendelseUtsendingService.getSporsmal(SPORSMAL_ID);

        assertThat(sporsmal.id, is(SPORSMAL_ID));
        assertThat(sporsmal.fritekst, is(FRITEKST));
        assertThat(sporsmal.temagruppe, is(TEMAGRUPPE));
    }

    @Test
    public void skalSendeSvar() {
        henvendelseUtsendingService.sendSvar(new SvarEllerReferat().withFnr(FNR).withFritekst(FRITEKST));

        verify(sendUtHenvendelsePortType).sendUtHenvendelse(wsSendHenvendelseRequestCaptor.capture());
        assertThat(wsSendHenvendelseRequestCaptor.getValue().getType(), is(SVAR.name()));
    }

    @Test
    public void skalSendeReferat() {
        henvendelseUtsendingService.sendReferat(new SvarEllerReferat().withFnr(FNR).withFritekst(FRITEKST));

        verify(sendUtHenvendelsePortType).sendUtHenvendelse(wsSendHenvendelseRequestCaptor.capture());
        assertThat(wsSendHenvendelseRequestCaptor.getValue().getType(), is(REFERAT.name()));
    }

    @Test
    public void skalHenteSporsmalFraOppgaveId() {
        WSHentHenvendelseListeResponse wsHentHenvendelseListeResponse =
                new WSHentHenvendelseListeResponse().withAny(
                        createXMLMeldingFraBruker("id1", "fritekst1"),
                        createXMLMeldingFraBruker("id2", "fritekst2"),
                        createXMLMeldingFraBruker("id3", "fritekst3")
                );

        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(wsHentHenvendelseListeResponse);

        Sporsmal sporsmal = henvendelseUtsendingService.getSporsmalFromOppgaveId("fnr", "id2");

        assertThat(sporsmal.fritekst, is("fritekst2"));
    }

    @Test
    public void skalHenteSporsmalMedRiktigTypeSpesifisert() {
        WSHentHenvendelseListeResponse wsHentHenvendelseListeResponse = new WSHentHenvendelseListeResponse().withAny(createXMLMeldingFraBruker("id1", "fritekst1"));
        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(wsHentHenvendelseListeResponse);

        henvendelseUtsendingService.getSporsmalFromOppgaveId("fnr", "id2");

        verify(henvendelsePortType).hentHenvendelseListe(hentHenvendelseListeRequestCaptor.capture());
        assertThat(hentHenvendelseListeRequestCaptor.getValue().getTyper(), is(not(empty())));
        assertThat(hentHenvendelseListeRequestCaptor.getValue().getTyper(), contains(SPORSMAL.name()));
        assertThat(hentHenvendelseListeRequestCaptor.getValue().getTyper(), not(contains(SVAR.name(), REFERAT.name())));
    }

    @Test
    public void skalHenteSvarlisteTilhorendeSporsmal() {
        String sporsmalId = "sporsmalId";
        WSHentHenvendelseListeResponse wsHentHenvendelseListeResponse =
                new WSHentHenvendelseListeResponse().withAny(
                        createXMLMeldingTilBruker(sporsmalId),
                        createXMLMeldingTilBruker(sporsmalId),
                        createXMLMeldingTilBruker("annenId"),
                        createXMLMeldingTilBruker("endaEnAnnenId")
                );
        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(wsHentHenvendelseListeResponse);

        List<SvarEllerReferat> svarliste = henvendelseUtsendingService.getSvarEllerReferatForSporsmal("fnr", sporsmalId);

        assertThat(svarliste, hasSize(2));
        assertThat(svarliste.get(0).sporsmalsId, is(sporsmalId));
        assertThat(svarliste.get(1).sporsmalsId, is(sporsmalId));
    }

    @Test
    public void skalHenteSvarlisteMedRiktigTypeSpesifisert() {
        WSHentHenvendelseListeResponse wsHentHenvendelseListeResponse =
                new WSHentHenvendelseListeResponse().withAny(createXMLMeldingTilBruker("sporsmalId"));
        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(wsHentHenvendelseListeResponse);

        henvendelseUtsendingService.getSvarEllerReferatForSporsmal("fnr", "sporsmalId");

        verify(henvendelsePortType).hentHenvendelseListe(hentHenvendelseListeRequestCaptor.capture());
        assertThat(hentHenvendelseListeRequestCaptor.getValue().getTyper(), is(not(empty())));
        assertThat(hentHenvendelseListeRequestCaptor.getValue().getTyper(), contains(SVAR.name(), REFERAT.name()));
        assertThat(hentHenvendelseListeRequestCaptor.getValue().getTyper(), not(contains(SPORSMAL.name())));
    }

    @Test
    public void skalHenteSortertListeAvSvarEllerReferatForSporsmalMedEldsteForst() {
        String sporsmalId = "sporsmalId";
        WSHentHenvendelseListeResponse wsHentHenvendelseListeResponse =
                new WSHentHenvendelseListeResponse().withAny(createToXMLMeldingTilBrukerSomSvarerPaaSporsmalsIdMedNyesteForst(sporsmalId));
        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(wsHentHenvendelseListeResponse);

        List<SvarEllerReferat> svarEllerReferatForSporsmal = henvendelseUtsendingService.getSvarEllerReferatForSporsmal("fnr", sporsmalId);

        assertThat(svarEllerReferatForSporsmal.get(0).type, is(SvarEllerReferat.Henvendelsetype.REFERAT));
        assertThat(svarEllerReferatForSporsmal.get(1).type, is(SvarEllerReferat.Henvendelsetype.SVAR));
    }

    private WSHentHenvendelseResponse mockWSHentHenvendelseResponse() {
        return new WSHentHenvendelseResponse().withAny(
                new XMLHenvendelse()
                        .withBehandlingsId(SPORSMAL_ID)
                        .withOpprettetDato(now())
                        .withHenvendelseType(SPORSMAL.name())
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
                .withHenvendelseType(SVAR.name())
                .withMetadataListe(new XMLMetadataListe().withMetadata(new XMLMeldingTilBruker().withSporsmalsId(sporsmalId).withNavident("")));
    }

    private List<Object> createToXMLMeldingTilBrukerSomSvarerPaaSporsmalsIdMedNyesteForst(String sporsmalId) {
        return new ArrayList<Object>(Arrays.asList(
                new XMLHenvendelse()
                        .withFnr("")
                        .withBehandlingsId(NYESTE_HENVENDELSE_ID)
                        .withHenvendelseType(SVAR.name())
                        .withOpprettetDato(DateTime.now())
                        .withMetadataListe(new XMLMetadataListe().withMetadata(
                                new XMLMeldingTilBruker().withSporsmalsId(sporsmalId).withNavident(""))),
                new XMLHenvendelse()
                        .withFnr("")
                        .withBehandlingsId(ELDSTE_HENVENDELSE)
                        .withOpprettetDato(DateTime.now().minusDays(1))
                        .withHenvendelseType(REFERAT.name())
                        .withMetadataListe(new XMLMetadataListe().withMetadata(
                                new XMLMeldingTilBruker().withSporsmalsId(sporsmalId).withNavident("")))
        ));
    }

}
