package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.kjerneinfo.domain.person.Person;
import no.nav.kjerneinfo.domain.person.Personfakta;
import no.nav.kjerneinfo.domain.person.fakta.AnsvarligEnhet;
import no.nav.kjerneinfo.domain.person.fakta.Organisasjonsenhet;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.*;
import no.nav.modig.content.PropertyResolver;
import no.nav.modig.core.context.StaticSubjectHandler;
import no.nav.modig.lang.collections.PredicateUtils;
import no.nav.modig.lang.option.Optional;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.modig.security.tilgangskontroll.policy.request.attributes.ActionId;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.SakerService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.SendUtHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.WSFerdigstillHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.WSSendUtHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.WSSendUtHenvendelseResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeResponse;
import no.nav.virksomhet.tjenester.ruting.v1.Ruting;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.TransformerUtils.castTo;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.*;
import static org.hamcrest.Matchers.*;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class HenvendelseUtsendingServiceImplTest {

    private static final String BEHANDLINGS_ID = "ID_1";
    private static final String FNR = "fnr";
    private static final String TRAAD_ID = "id";
    private static final String FRITEKST = "fritekst";
    private static final String TEMAGRUPPE = "ARBD";

    private static final String NYESTE_HENVENDELSE_ID = "Nyeste henvendelse";
    private static final String ELDSTE_HENVENDELSE = "Eldste henvendelse";
    private static final String ENHET = "1234";

    public static final String JOURNALFORT_TEMA = "tema jobb";

    @Captor
    ArgumentCaptor<WSSendUtHenvendelseRequest> wsSendHenvendelseRequestCaptor;
    @Captor
    ArgumentCaptor<WSHentHenvendelseListeRequest> hentHenvendelseListeRequestCaptor;
    @Captor
    ArgumentCaptor<WSFerdigstillHenvendelseRequest> wsFerdigstillHenvendelseRequestCaptor;
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
    @Mock
    public PropertyResolver propertyResolver;
    @Mock
    private EnforcementPoint pep;
    @Mock
    private HenvendelsePortType henvendelsePortType;
    @Mock
    protected SendUtHenvendelsePortType sendUtHenvendelsePortType;
    @Mock
    private BehandleHenvendelsePortType behandleHenvendelsePortType;
    @Mock
    private PersonKjerneinfoServiceBi kjerneinfo;
    @Mock
    private LDAPService ldapService;

    @InjectMocks
    private HenvendelseUtsendingServiceImpl henvendelseUtsendingService;

    @Before
    public void init() {
        when(sendUtHenvendelsePortType.sendUtHenvendelse(any(WSSendUtHenvendelseRequest.class))).thenReturn(
                new WSSendUtHenvendelseResponse().withBehandlingsId(BEHANDLINGS_ID)
        );
        HentKjerneinformasjonResponse kjerneinformasjonResponse = new HentKjerneinformasjonResponse();
        Person person = new Person();
        Personfakta personfakta = new Personfakta();
        AnsvarligEnhet ansvarligEnhet = new AnsvarligEnhet();
        Organisasjonsenhet organisasjonsenhet = new Organisasjonsenhet();
        organisasjonsenhet.setOrganisasjonselementId(ENHET);
        ansvarligEnhet.setOrganisasjonsenhet(organisasjonsenhet);
        personfakta.setHarAnsvarligEnhet(ansvarligEnhet);
        person.setPersonfakta(personfakta);
        person.getPersonfakta().getHarAnsvarligEnhet().getOrganisasjonsenhet().getOrganisasjonselementId();
        kjerneinformasjonResponse.setPerson(person);
        when(kjerneinfo.hentKjerneinformasjon(any(HentKjerneinformasjonRequest.class))).thenReturn(kjerneinformasjonResponse);
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
        Melding melding = new Melding().withFnr(FNR).withFritekst(FRITEKST).withType(SVAR_SKRIFTLIG).withTemagruppe(TEMAGRUPPE);
        henvendelseUtsendingService.sendHenvendelse(melding, Optional.<String>none(), Optional.<Sak>none());

        verify(sendUtHenvendelsePortType).sendUtHenvendelse(wsSendHenvendelseRequestCaptor.capture());
        assertThat(wsSendHenvendelseRequestCaptor.getValue().getType(), is(XMLHenvendelseType.SVAR_SKRIFTLIG.name()));
    }

    @Test
    public void skalSendeReferat() throws Exception {
        Melding melding = new Melding().withFnr(FNR).withFritekst(FRITEKST).withType(SAMTALEREFERAT_OPPMOTE).withTemagruppe(TEMAGRUPPE);
        henvendelseUtsendingService.sendHenvendelse(melding, Optional.<String>none(), Optional.<Sak>none());

        verify(sendUtHenvendelsePortType).sendUtHenvendelse(wsSendHenvendelseRequestCaptor.capture());
        assertThat(wsSendHenvendelseRequestCaptor.getValue().getType(), is(XMLHenvendelseType.REFERAT_OPPMOTE.name()));
    }

    @Test
    public void skalSendeSporsmal() throws Exception {
        Melding melding = new Melding().withFnr(FNR).withFritekst(FRITEKST).withType(SPORSMAL_MODIA_UTGAAENDE).withTemagruppe(TEMAGRUPPE);
        henvendelseUtsendingService.sendHenvendelse(melding, Optional.<String>none(), Optional.<Sak>none());

        verify(sendUtHenvendelsePortType).sendUtHenvendelse(wsSendHenvendelseRequestCaptor.capture());
        assertThat(wsSendHenvendelseRequestCaptor.getValue().getType(), is(XMLHenvendelseType.SPORSMAL_MODIA_UTGAAENDE.name()));
    }

    @Test
    public void skalOppretteHenvendelse() {
        henvendelseUtsendingService.opprettHenvendelse(SVAR_SKRIFTLIG.name(), FNR, BEHANDLINGS_ID);

        verify(sendUtHenvendelsePortType).opprettHenvendelse(SVAR_SKRIFTLIG.name(), FNR,  BEHANDLINGS_ID);
    }

    @Test
    public void skalFerdigstilleHenvendelse() throws Exception {
        Melding melding = new Melding().withFnr(FNR).withFritekst(FRITEKST).withType(SPORSMAL_MODIA_UTGAAENDE).withTemagruppe(TEMAGRUPPE);
        henvendelseUtsendingService.ferdigstillHenvendelse(melding, Optional.<String>none(), Optional.<Sak>none(), BEHANDLINGS_ID);

        verify(sendUtHenvendelsePortType).ferdigstillHenvendelse(wsFerdigstillHenvendelseRequestCaptor.capture());
        assertThat(wsFerdigstillHenvendelseRequestCaptor.getValue().getBehandlingsId(), is(BEHANDLINGS_ID));
    }

    @Test
    public void skalJournalforeHenvendelseDersomSakErSatt() throws Exception {
        Sak sak = new Sak();
        sak.saksId = optional("sakid");
        Melding melding = new Melding().withFnr(FNR).withFritekst(FRITEKST).withType(SPORSMAL_MODIA_UTGAAENDE).withTemagruppe(TEMAGRUPPE);
        henvendelseUtsendingService.sendHenvendelse(melding, Optional.<String>none(), optional(sak));

        verify(sakerService).knyttBehandlingskjedeTilSak(anyString(), anyString(), sakArgumentCaptor.capture());

        Sak sendtSak = sakArgumentCaptor.getValue();
        assertThat(sendtSak, is(sak));
    }

    @Test
    public void skalFerdigstilleOppgaveDersomDenneErSatt() throws Exception {
        String oppgaveId = "oppgaveId";
        Melding melding = new Melding().withFnr(FNR).withFritekst(FRITEKST).withType(SPORSMAL_MODIA_UTGAAENDE).withTemagruppe(Temagruppe.ARBD.toString());
        henvendelseUtsendingService.sendHenvendelse(melding, optional(oppgaveId), Optional.<Sak>none());
        ArgumentCaptor<Temagruppe> temagruppeCaptor = ArgumentCaptor.forClass(Temagruppe.class);

        verify(oppgaveBehandlingService).ferdigstillOppgaveIGsak(stringArgumentCaptor.capture(), temagruppeCaptor.capture());

        String sendtOppgaveId = stringArgumentCaptor.getValue();
        assertThat(sendtOppgaveId, is(oppgaveId));
        assertThat(temagruppeCaptor.getValue(), is(Temagruppe.ARBD));
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
        assertThat(hentHenvendelseListeRequestCaptor.getValue().getTyper(), not(Matchers.contains(XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name())));
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

    @Test
    public void kontorsperrerHenvendelsePaaAndreSosialeTjenester() throws Exception {
        Melding melding = new Melding().withFnr(FNR).withFritekst(FRITEKST).withType(SAMTALEREFERAT_OPPMOTE).withTemagruppe(Temagruppe.ANSOS.toString());
        henvendelseUtsendingService.sendHenvendelse(melding, Optional.<String>none(), Optional.<Sak>none());

        verify(behandleHenvendelsePortType).oppdaterKontorsperre(ENHET, singletonList(BEHANDLINGS_ID));
    }

    @Test
    public void kontorsperrerIkkeHenvendelsePaaOkonomiskSosialhjelp() throws Exception {
        Melding melding = new Melding().withFnr(FNR).withFritekst(FRITEKST).withType(SAMTALEREFERAT_OPPMOTE).withTemagruppe(Temagruppe.OKSOS.toString());
        henvendelseUtsendingService.sendHenvendelse(melding, Optional.<String>none(), Optional.<Sak>none());

        verify(behandleHenvendelsePortType, never()).oppdaterKontorsperre(anyString(), anyList());
    }

    @Test
    public void knyttetHenvendelsenTilBrukersEnhetFraTPS() throws Exception {
        Melding melding = new Melding().withFnr(FNR).withFritekst(FRITEKST).withType(SAMTALEREFERAT_OPPMOTE).withTemagruppe(Temagruppe.ARBD.toString());
        henvendelseUtsendingService.sendHenvendelse(melding, Optional.<String>none(), Optional.<Sak>none());

        verify(sendUtHenvendelsePortType).sendUtHenvendelse(wsSendHenvendelseRequestCaptor.capture());
        verify(kjerneinfo, times(1)).hentKjerneinformasjon(any(HentKjerneinformasjonRequest.class));
        XMLHenvendelse xmlHenvendelse = (XMLHenvendelse) wsSendHenvendelseRequestCaptor.getValue().getAny();
        assertThat(xmlHenvendelse.getBrukersEnhet(), is(ENHET));
    }

    @Test
    public void knyttetHenvendelsenTilBrukersEnhetFraMelding() throws Exception {
        String brukersEnhet = "0123";

        Melding melding = new Melding()
                .withFnr(FNR)
                .withFritekst(FRITEKST)
                .withType(SAMTALEREFERAT_OPPMOTE)
                .withTemagruppe(Temagruppe.ARBD.toString())
                .withBrukersEnhet(brukersEnhet);
        henvendelseUtsendingService.sendHenvendelse(melding, Optional.<String>none(), Optional.<Sak>none());

        verify(sendUtHenvendelsePortType).sendUtHenvendelse(wsSendHenvendelseRequestCaptor.capture());
        verify(kjerneinfo, never()).hentKjerneinformasjon(any(HentKjerneinformasjonRequest.class));
        XMLHenvendelse xmlHenvendelse = (XMLHenvendelse) wsSendHenvendelseRequestCaptor.getValue().getAny();
        assertThat(xmlHenvendelse.getBrukersEnhet(), is(brukersEnhet));
    }

    @Test
    public void sjekkerTilgangPaaOkonomiskSosialhjelp() {
        String valgtEnhet = "1234";
        when(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()).thenReturn(valgtEnhet);
        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(new WSHentHenvendelseListeResponse().withAny(
                new XMLHenvendelse()
                        .withBehandlingsId(TRAAD_ID)
                        .withBehandlingskjedeId(TRAAD_ID)
                        .withOpprettetDato(now())
                        .withHenvendelseType(XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name())
                        .withBrukersEnhet("5678")
                        .withGjeldendeTemagruppe("OKSOS")
                        .withMetadataListe(new XMLMetadataListe().withMetadata(
                                new XMLMeldingFraBruker().withFritekst(FRITEKST).withTemagruppe(TEMAGRUPPE)))
        ));

        henvendelseUtsendingService.hentTraad(FNR, TRAAD_ID);
        ArgumentCaptor<PolicyRequest> captor = ArgumentCaptor.forClass(PolicyRequest.class);
        verify(pep).assertAccess(captor.capture());

        PolicyRequest policyRequest = captor.getValue();
        ActionId actionId = on(policyRequest.getAttributes()).filter(PredicateUtils.isA(ActionId.class)).map(castTo(ActionId.class)).head().get();

        assertThat((String) actionId.getAttributeValue().getValue(), is("oksos"));
    }

    private WSHentHenvendelseListeResponse mockWSHentHenvendelseResponse() {
        return new WSHentHenvendelseListeResponse().withAny(
                new XMLHenvendelse()
                        .withBehandlingsId(TRAAD_ID)
                        .withBehandlingskjedeId(TRAAD_ID)
                        .withOpprettetDato(now())
                        .withHenvendelseType(XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name())
                        .withGjeldendeTemagruppe("ARBD")
                        .withMetadataListe(new XMLMetadataListe().withMetadata(
                                new XMLMeldingFraBruker().withFritekst(FRITEKST).withTemagruppe(TEMAGRUPPE)))
        );
    }

    private XMLHenvendelse createXMLMeldingFraBruker() {
        return new XMLHenvendelse()
                .withOppgaveIdGsak("")
                .withBehandlingskjedeId(TRAAD_ID)
                .withOpprettetDato(now().minusDays(2))
                .withGjeldendeTemagruppe("ARBD")
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
                .withGjeldendeTemagruppe("ARBD")
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
                        .withGjeldendeTemagruppe("ARBD")
                        .withMetadataListe(new XMLMetadataListe().withMetadata(
                                new XMLMeldingFraBruker().withFritekst("").withTemagruppe(""))),
                new XMLHenvendelse()
                        .withFnr("")
                        .withBehandlingsId(ELDSTE_HENVENDELSE)
                        .withBehandlingskjedeId(sporsmalId)
                        .withOpprettetDato(now())
                        .withGjeldendeTemagruppe("FMLI")
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
