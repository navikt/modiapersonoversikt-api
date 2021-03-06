package no.nav.modiapersonoversikt.service;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.Person;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.Personfakta;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.fakta.AnsvarligEnhet;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.fakta.Organisasjonsenhet;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.*;
import no.nav.modiapersonoversikt.infrastructure.content.ContentRetriever;
import no.nav.modiapersonoversikt.legacy.api.domain.Saksbehandler;
import no.nav.modiapersonoversikt.legacy.api.domain.Temagruppe;
import no.nav.modiapersonoversikt.legacy.api.domain.saker.Sak;
import no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Fritekst;
import no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Melding;
import no.nav.modiapersonoversikt.legacy.api.service.OppgaveBehandlingService;
import no.nav.modiapersonoversikt.legacy.api.service.saker.SakerService;
import no.nav.modiapersonoversikt.legacy.api.service.ldap.LDAPService;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollContext;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.SendUtHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.WSFerdigstillHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.WSSendUtHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.WSSendUtHenvendelseResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeResponse;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Meldingstype.*;
import static org.hamcrest.Matchers.*;
import static org.joda.time.DateTime.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HenvendelseUtsendingServiceImplTest {

    private static final String BEHANDLINGS_ID = "ID_1";
    private static final String FNR = "fnr";
    private static final String TRAAD_ID = "id";
    private static final String FRITEKST = "fritekst";
    private static final String TEMAGRUPPE = "ARBD";

    private static final String NYESTE_HENVENDELSE_ID = "Nyeste henvendelse";
    private static final String ELDSTE_HENVENDELSE = "Eldste henvendelse";
    private static final String ENHET = "1234";

    private static final String JOURNALFORT_TEMA = "tema jobb";
    private static final String SAKSBEHANDLERS_VALGTE_ENHET = "4300";
    private static final String SAKSBEHANDLERS_IDENT = "z123456";

    private ArgumentCaptor<WSSendUtHenvendelseRequest> wsSendHenvendelseRequestCaptor = ArgumentCaptor.forClass(WSSendUtHenvendelseRequest.class);
    private ArgumentCaptor<WSHentHenvendelseListeRequest> hentHenvendelseListeRequestCaptor = ArgumentCaptor.forClass(WSHentHenvendelseListeRequest.class);
    private ArgumentCaptor<WSFerdigstillHenvendelseRequest> wsFerdigstillHenvendelseRequestCaptor = ArgumentCaptor.forClass(WSFerdigstillHenvendelseRequest.class);
    private ArgumentCaptor<Sak> sakArgumentCaptor = ArgumentCaptor.forClass(Sak.class);
    private ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

    private SakerService sakerService = mock(SakerService.class);
    private OppgaveBehandlingService oppgaveBehandlingService = mock(OppgaveBehandlingService.class);
    private ContentRetriever propertyResolver = mock(ContentRetriever.class);
    private TilgangskontrollContext tilgangskontrollContext = mock(TilgangskontrollContext.class);
    private Tilgangskontroll tilgangskontroll = new Tilgangskontroll(tilgangskontrollContext);
    private HenvendelsePortType henvendelsePortType = mock(HenvendelsePortType.class);
    private SendUtHenvendelsePortType sendUtHenvendelsePortType = mock(SendUtHenvendelsePortType.class);
    private BehandleHenvendelsePortType behandleHenvendelsePortType = mock(BehandleHenvendelsePortType.class);
    private PersonKjerneinfoServiceBi kjerneinfo = mock(PersonKjerneinfoServiceBi.class);
    private LDAPService ldapService = mock(LDAPService.class);
    private CacheManager cacheManager = mock(CacheManager.class);

    private HenvendelseUtsendingServiceImpl henvendelseUtsendingService = new HenvendelseUtsendingServiceImpl(
            henvendelsePortType,
            sendUtHenvendelsePortType,
            behandleHenvendelsePortType,
            oppgaveBehandlingService,
            sakerService,
            tilgangskontroll,
            propertyResolver,
            kjerneinfo,
            ldapService,
            cacheManager
    );

    @BeforeEach
    void init() {
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
        personfakta.setAnsvarligEnhet(ansvarligEnhet);
        person.setPersonfakta(personfakta);
        person.getPersonfakta().getAnsvarligEnhet().getOrganisasjonsenhet().getOrganisasjonselementId();
        kjerneinformasjonResponse.setPerson(person);
        when(kjerneinfo.hentKjerneinformasjon(any(HentKjerneinformasjonRequest.class))).thenReturn(kjerneinformasjonResponse);
        when(cacheManager.getCache(anyString())).thenReturn(mock(Cache.class));
    }

    @Test
    void henterSporsmal() {
        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(mockWSHentHenvendelseResponse());

        Melding sporsmal = henvendelseUtsendingService.hentTraad("fnr", TRAAD_ID, SAKSBEHANDLERS_VALGTE_ENHET).get(0);

        verify(henvendelsePortType).hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class));
        assertThat(sporsmal.id, is(TRAAD_ID));
        assertThat(sporsmal.getFritekst(), is(FRITEKST));
        assertThat(sporsmal.temagruppe, is(TEMAGRUPPE));
    }

    @Test
    void skalSendeSvar() {
        Melding melding = new Melding()
                .withFnr(FNR)
                .withFritekst(mockFritekst())
                .withType(SVAR_SKRIFTLIG)
                .withTemagruppe(TEMAGRUPPE);
        henvendelseUtsendingService.sendHenvendelse(melding, Optional.empty(), Optional.empty(), SAKSBEHANDLERS_VALGTE_ENHET);

        verify(sendUtHenvendelsePortType).sendUtHenvendelse(wsSendHenvendelseRequestCaptor.capture());
        assertThat(wsSendHenvendelseRequestCaptor.getValue().getType(), is(XMLHenvendelseType.SVAR_SKRIFTLIG.name()));
    }

    @Test
    void skalSendeReferat() {
        Melding melding = new Melding()
                .withFnr(FNR)
                .withFritekst(mockFritekst())
                .withType(SAMTALEREFERAT_OPPMOTE)
                .withTemagruppe(TEMAGRUPPE);
        henvendelseUtsendingService.sendHenvendelse(melding, Optional.empty(), Optional.empty(), SAKSBEHANDLERS_VALGTE_ENHET);

        verify(sendUtHenvendelsePortType).sendUtHenvendelse(wsSendHenvendelseRequestCaptor.capture());
        assertThat(wsSendHenvendelseRequestCaptor.getValue().getType(), is(XMLHenvendelseType.REFERAT_OPPMOTE.name()));
    }

    @Test
    void skalSendeSporsmal() {
        Melding melding = new Melding()
                .withFnr(FNR)
                .withFritekst(mockFritekst())
                .withType(SPORSMAL_MODIA_UTGAAENDE)
                .withTemagruppe(TEMAGRUPPE);
        henvendelseUtsendingService.sendHenvendelse(melding, Optional.empty(), Optional.empty(), SAKSBEHANDLERS_VALGTE_ENHET);

        verify(sendUtHenvendelsePortType).sendUtHenvendelse(wsSendHenvendelseRequestCaptor.capture());
        assertThat(wsSendHenvendelseRequestCaptor.getValue().getType(), is(XMLHenvendelseType.SPORSMAL_MODIA_UTGAAENDE.name()));
    }

    @Test
    void skalOppretteHenvendelse() {
        henvendelseUtsendingService.opprettHenvendelse(SVAR_SKRIFTLIG.name(), FNR, BEHANDLINGS_ID);

        verify(sendUtHenvendelsePortType).opprettHenvendelse(SVAR_SKRIFTLIG.name(), FNR, BEHANDLINGS_ID);
    }

    @Test
    void skalFerdigstilleHenvendelse() throws Exception {
        Melding melding = new Melding()
                .withFnr(FNR)
                .withFritekst(mockFritekst())
                .withType(SPORSMAL_MODIA_UTGAAENDE)
                .withTemagruppe(TEMAGRUPPE);

        henvendelseUtsendingService.ferdigstillHenvendelse(melding, Optional.empty(), Optional.empty(), BEHANDLINGS_ID, SAKSBEHANDLERS_VALGTE_ENHET);

        verify(sendUtHenvendelsePortType).ferdigstillHenvendelse(wsFerdigstillHenvendelseRequestCaptor.capture());
        assertThat(wsFerdigstillHenvendelseRequestCaptor.getValue().getBehandlingsId(), is(singletonList(BEHANDLINGS_ID)));
    }

    @Test
    void skalJournalforeHenvendelseDersomSakErSatt() throws Exception {
        Sak sak = new Sak();
        sak.saksId = "sakid";
        Melding melding = new Melding()
                .withFnr(FNR)
                .withFritekst(mockFritekst())
                .withType(SPORSMAL_MODIA_UTGAAENDE)
                .withTemagruppe(TEMAGRUPPE);
        henvendelseUtsendingService.sendHenvendelse(melding, Optional.empty(), Optional.of(sak), SAKSBEHANDLERS_VALGTE_ENHET);

        verify(sakerService).knyttBehandlingskjedeTilSak(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), sakArgumentCaptor.capture(), ArgumentMatchers.anyString());

        Sak sendtSak = sakArgumentCaptor.getValue();
        assertThat(sendtSak, is(sak));
    }

    @Test
    void skalFerdigstilleOppgaveDersomDenneErSatt() {
        String oppgaveId = "oppgaveId";
        Melding melding = new Melding()
                .withFnr(FNR)
                .withFritekst(mockFritekst())
                .withType(SPORSMAL_MODIA_UTGAAENDE)
                .withTemagruppe(Temagruppe.ARBD.toString());

        henvendelseUtsendingService.sendHenvendelse(melding, Optional.of(oppgaveId), Optional.empty(), SAKSBEHANDLERS_VALGTE_ENHET);
        ArgumentCaptor<Temagruppe> temagruppeCaptor = ArgumentCaptor.forClass(Temagruppe.class);

        verify(oppgaveBehandlingService).ferdigstillOppgaveIGsak(stringArgumentCaptor.capture(), temagruppeCaptor.capture(), any());

        String sendtOppgaveId = stringArgumentCaptor.getValue();
        assertThat(sendtOppgaveId, is(oppgaveId));
        assertThat(temagruppeCaptor.getValue(), is(Temagruppe.ARBD));
    }

    @Test
    void skalHenteTraad() {
        WSHentHenvendelseListeResponse wsHentHenvendelseListeResponse =
                new WSHentHenvendelseListeResponse().withAny(
                        createXMLMeldingFraBruker(),
                        createXMLMeldingTilBruker(TRAAD_ID),
                        createXMLMeldingTilBruker(TRAAD_ID),
                        createXMLMeldingTilBruker("annenId"),
                        createXMLMeldingTilBruker("endaEnAnnenId")
                );

        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(wsHentHenvendelseListeResponse);

        List<Melding> traad = henvendelseUtsendingService.hentTraad(FNR, TRAAD_ID, SAKSBEHANDLERS_VALGTE_ENHET);

        assertThat(traad, hasSize(3));
        assertThat(traad.get(0).traadId, is(TRAAD_ID));
        assertThat(traad.get(1).traadId, is(TRAAD_ID));
        assertThat(traad.get(2).traadId, is(TRAAD_ID));
    }

    @Test
    void skalHenteSvarlisteMedRiktigTypeSpesifisert() {
        WSHentHenvendelseListeResponse wsHentHenvendelseListeResponse = new WSHentHenvendelseListeResponse()
                .withAny(
                        createXMLMeldingTilBruker(TRAAD_ID),
                        createXMLMeldingTilBruker("id2").withHenvendelseType(XMLHenvendelseType.INFOMELDING_MODIA_UTGAAENDE.name())
                );

        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(wsHentHenvendelseListeResponse);


        henvendelseUtsendingService.hentTraad(FNR, TRAAD_ID, SAKSBEHANDLERS_VALGTE_ENHET);

        verify(henvendelsePortType).hentHenvendelseListe(hentHenvendelseListeRequestCaptor.capture());
        assertThat(hentHenvendelseListeRequestCaptor.getValue().getTyper(), is(not(empty())));
        assertThat(hentHenvendelseListeRequestCaptor.getValue().getTyper(), containsInAnyOrder(
                XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name(),
                XMLHenvendelseType.SPORSMAL_SKRIFTLIG_DIREKTE.name(),
                XMLHenvendelseType.SVAR_SKRIFTLIG.name(),
                XMLHenvendelseType.SVAR_OPPMOTE.name(),
                XMLHenvendelseType.SVAR_TELEFON.name(),
                XMLHenvendelseType.DELVIS_SVAR_SKRIFTLIG.name(),
                XMLHenvendelseType.REFERAT_OPPMOTE.name(),
                XMLHenvendelseType.REFERAT_TELEFON.name(),
                XMLHenvendelseType.SPORSMAL_MODIA_UTGAAENDE.name(),
                XMLHenvendelseType.INFOMELDING_MODIA_UTGAAENDE.name(),
                XMLHenvendelseType.SVAR_SBL_INNGAAENDE.name(),
                XMLHenvendelseType.DOKUMENT_VARSEL.name(),
                XMLHenvendelseType.OPPGAVE_VARSEL.name()

        ));
        assertThat(hentHenvendelseListeRequestCaptor.getValue().getTyper(), not(Matchers.contains(XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name())));
    }

    @Test
    void skalHenteSortertListeAvSvarEllerReferatForSporsmalMedEldsteForst() {
        WSHentHenvendelseListeResponse wsHentHenvendelseListeResponse =
                new WSHentHenvendelseListeResponse().withAny(createTraad(TRAAD_ID).toArray());

        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(wsHentHenvendelseListeResponse);

        List<Melding> traad = henvendelseUtsendingService.hentTraad(FNR, TRAAD_ID, SAKSBEHANDLERS_VALGTE_ENHET);

        assertThat(traad, hasSize(2));
        assertThat(traad.get(0).meldingstype, is(SPORSMAL_SKRIFTLIG));
        assertThat(traad.get(1).meldingstype, is(SVAR_TELEFON));
    }

    @Test
    void skalHenteTraadMedBlankFritekstOmManIkkeHarTilgang() {
        WSHentHenvendelseListeResponse resp =
                new WSHentHenvendelseListeResponse().withAny(createTraadMedJournalfortTemaGruppe(TRAAD_ID, JOURNALFORT_TEMA).toArray());
        ((XMLHenvendelse) resp.getAny().get(1)).getJournalfortInformasjon().setJournalfortTema("Noe annet");

        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(resp);
        when(tilgangskontrollContext.hentTemagrupperForSaksbehandler(ArgumentMatchers.anyString())).thenReturn(new TreeSet<>(asList(JOURNALFORT_TEMA)));

        List<Melding> traad = henvendelseUtsendingService.hentTraad(FNR, TRAAD_ID, SAKSBEHANDLERS_VALGTE_ENHET);

        assertThat(traad, hasSize(3));
        assertThat(traad.get(1).getFritekst(), emptyString());
        assertThat(traad.get(2).getFritekst(), not(emptyString()));
    }

    @Test
    void kontorsperrerHenvendelsePaaAndreSosialeTjenester() {
        Melding melding = new Melding().withFnr(FNR).withFritekst(mockFritekst()).withType(SAMTALEREFERAT_OPPMOTE).withTemagruppe(Temagruppe.ANSOS.toString());
        henvendelseUtsendingService.sendHenvendelse(melding, Optional.empty(), Optional.empty(), SAKSBEHANDLERS_VALGTE_ENHET);

        verify(behandleHenvendelsePortType).oppdaterKontorsperre(ENHET, singletonList(BEHANDLINGS_ID));
    }

    @Test
    void kontorsperrerIkkeHenvendelsePaaOkonomiskSosialhjelp() {
        Melding melding = new Melding().withFnr(FNR).withFritekst(mockFritekst()).withType(SAMTALEREFERAT_OPPMOTE).withTemagruppe(Temagruppe.OKSOS.toString());
        henvendelseUtsendingService.sendHenvendelse(melding, Optional.empty(), Optional.empty(), SAKSBEHANDLERS_VALGTE_ENHET);

        verify(behandleHenvendelsePortType, never()).oppdaterKontorsperre(ArgumentMatchers.anyString(), ArgumentMatchers.anyList());
    }

    @Test
    void knyttetHenvendelsenTilBrukersEnhetFraTPS() {
        Melding melding = new Melding().withFnr(FNR).withFritekst(mockFritekst()).withType(SAMTALEREFERAT_OPPMOTE).withTemagruppe(Temagruppe.ARBD.toString());
        henvendelseUtsendingService.sendHenvendelse(melding, Optional.empty(), Optional.empty(), SAKSBEHANDLERS_VALGTE_ENHET);

        verify(sendUtHenvendelsePortType).sendUtHenvendelse(wsSendHenvendelseRequestCaptor.capture());
        verify(kjerneinfo, times(1)).hentKjerneinformasjon(any(HentKjerneinformasjonRequest.class));
        XMLHenvendelse xmlHenvendelse = (XMLHenvendelse) wsSendHenvendelseRequestCaptor.getValue().getAny();
        assertThat(xmlHenvendelse.getBrukersEnhet(), is(ENHET));
    }

    @Test
    void knyttetHenvendelsenTilBrukersEnhetFraMelding() {
        String brukersEnhet = "0123";

        Melding melding = new Melding()
                .withFnr(FNR)
                .withFritekst(mockFritekst())
                .withType(SAMTALEREFERAT_OPPMOTE)
                .withTemagruppe(Temagruppe.ARBD.toString())
                .withBrukersEnhet(brukersEnhet);
        henvendelseUtsendingService.sendHenvendelse(melding, Optional.empty(), Optional.empty(), SAKSBEHANDLERS_VALGTE_ENHET);

        verify(sendUtHenvendelsePortType).sendUtHenvendelse(wsSendHenvendelseRequestCaptor.capture());
        verify(kjerneinfo, never()).hentKjerneinformasjon(any(HentKjerneinformasjonRequest.class));
        XMLHenvendelse xmlHenvendelse = (XMLHenvendelse) wsSendHenvendelseRequestCaptor.getValue().getAny();
        assertThat(xmlHenvendelse.getBrukersEnhet(), is(brukersEnhet));
    }

    @Test
    void knyttetHenvendelsenTilTomEnhetDersomBrukerIkkeHarNavkontor() {
        String brukersEnhet = null;

        HentKjerneinformasjonResponse kjerneinformasjonResponse = new HentKjerneinformasjonResponse();
        Person person = new Person();
        Personfakta personfakta = new Personfakta();
        personfakta.setAnsvarligEnhet(null);
        person.setPersonfakta(personfakta);
        kjerneinformasjonResponse.setPerson(person);
        when(kjerneinfo.hentKjerneinformasjon(any(HentKjerneinformasjonRequest.class))).thenReturn(kjerneinformasjonResponse);

        Melding melding = new Melding()
                .withFnr(FNR)
                .withFritekst(mockFritekst())
                .withType(SAMTALEREFERAT_OPPMOTE)
                .withTemagruppe(Temagruppe.ARBD.toString())
                .withBrukersEnhet(brukersEnhet);
        henvendelseUtsendingService.sendHenvendelse(melding, Optional.empty(), Optional.empty(), SAKSBEHANDLERS_VALGTE_ENHET);

        verify(sendUtHenvendelsePortType).sendUtHenvendelse(wsSendHenvendelseRequestCaptor.capture());
        XMLHenvendelse xmlHenvendelse = (XMLHenvendelse) wsSendHenvendelseRequestCaptor.getValue().getAny();
        assertThat(xmlHenvendelse.getBrukersEnhet(), is(brukersEnhet));
    }

    @Test
    void sjekkerTilgangPaaOkonomiskSosialhjelp() {
        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(new WSHentHenvendelseListeResponse().withAny(
                new XMLHenvendelse()
                        .withBehandlingsId(TRAAD_ID)
                        .withBehandlingskjedeId(TRAAD_ID)
                        .withAvsluttetDato(now())
                        .withHenvendelseType(XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name())
                        .withBrukersEnhet("5678")
                        .withGjeldendeTemagruppe("OKSOS")
                        .withMetadataListe(new XMLMetadataListe().withMetadata(
                                new XMLMeldingFraBruker().withFritekst(FRITEKST).withTemagruppe(TEMAGRUPPE)))
        ));
        when(tilgangskontrollContext.harSaksbehandlerRolle(ArgumentMatchers.anyString())).thenReturn(true);

        henvendelseUtsendingService.hentTraad(FNR, TRAAD_ID, SAKSBEHANDLERS_VALGTE_ENHET);
    }

    private Fritekst mockFritekst() {
        return new Fritekst(FRITEKST, new Saksbehandler("Jan", "Saksbehandler", "ident"), DateTime.now());
    }

    private WSHentHenvendelseListeResponse mockWSHentHenvendelseResponse() {
        return new WSHentHenvendelseListeResponse().withAny(
                new XMLHenvendelse()
                        .withBehandlingsId(TRAAD_ID)
                        .withBehandlingskjedeId(TRAAD_ID)
                        .withAvsluttetDato(now())
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
                .withAvsluttetDato(now().minusDays(2))
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
                .withAvsluttetDato(now())
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
                        .withAvsluttetDato(now().minusDays(1))
                        .withGjeldendeTemagruppe("ARBD")
                        .withMetadataListe(new XMLMetadataListe().withMetadata(
                                new XMLMeldingFraBruker().withFritekst("").withTemagruppe(""))),
                new XMLHenvendelse()
                        .withFnr("")
                        .withBehandlingsId(ELDSTE_HENVENDELSE)
                        .withBehandlingskjedeId(sporsmalId)
                        .withAvsluttetDato(now())
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
