package no.nav.modiapersonoversikt.legacy.sporsmalogsvar.consumer;

import no.nav.common.auth.subject.IdentType;
import no.nav.common.auth.subject.SsoToken;
import no.nav.common.auth.subject.Subject;
import no.nav.common.auth.subject.SubjectHandler;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.*;
import no.nav.modiapersonoversikt.infrastructure.content.ContentRetriever;
import no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Melding;
import no.nav.modiapersonoversikt.legacy.api.service.arbeidsfordeling.ArbeidsfordelingV1Service;
import no.nav.modiapersonoversikt.legacy.api.service.kodeverk.StandardKodeverk;
import no.nav.modiapersonoversikt.legacy.api.service.ldap.LDAPService;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollContext;
import no.nav.modiapersonoversikt.legacy.sporsmalogsvar.consumer.henvendelse.HenvendelseBehandlingServiceImpl;
import no.nav.modiapersonoversikt.legacy.sporsmalogsvar.legacy.TraadVM;
import no.nav.modiapersonoversikt.rest.persondata.*;
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeResponse;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.*;
import static no.nav.modiapersonoversikt.legacy.api.domain.Temagruppe.OKSOS;
import static no.nav.modiapersonoversikt.legacy.sporsmalogsvar.legacy.TestUtils.*;
import static no.nav.modiapersonoversikt.rest.persondata.PersondataTestdataKt.gittNavKontorEnhet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class HenvendelseBehandlingServiceImplTest {
    private static final Subject TEST_SUBJECT = new Subject("Z999999", IdentType.InternBruker, SsoToken.oidcToken("token", emptyMap()));
    private static final String FNR = "11111111";
    private static final String TEMAGRUPPE = "temagruppe";
    private static final String BEHANDLINGS_ID = "id1";
    private static final List<String> IDER_I_VALGT_TRAAD = asList(ID_1, ID_2, ID_3);
    private static final String NAVBRUKERS_ENHET = "Navbrukers enhet";
    private static final String ARKIVTEMANAVN = "arkivtemanavn";
    private static final String VALGT_ENHET = "1231";

    @Captor
    private ArgumentCaptor<WSHentHenvendelseListeRequest> wsHentHenvendelseListeRequestArgumentCaptor;

    private final HenvendelsePortType henvendelsePortType = mock(HenvendelsePortType.class);
    private final BehandleHenvendelsePortType behandleHenvendelsePortType = mock(BehandleHenvendelsePortType.class);
    private PersondataService persondataService = mock(PersondataService.class);
    private final StandardKodeverk standardKodeverk = mock(StandardKodeverk.class);
    private final ContentRetriever propertyResolver = mock(ContentRetriever.class);
    private final LDAPService ldapService = mock(LDAPService.class);
    private final ArbeidsfordelingV1Service arbeidsfordelingService = mock(ArbeidsfordelingV1Service.class);
    private final TilgangskontrollContext tilgangskontrollContext = mock(TilgangskontrollContext.class);

    private final HenvendelseBehandlingServiceImpl henvendelseBehandlingService = new HenvendelseBehandlingServiceImpl(
            henvendelsePortType,
            behandleHenvendelsePortType,
            persondataService,
            new Tilgangskontroll(tilgangskontrollContext),
            standardKodeverk,
            propertyResolver,
            ldapService,
            arbeidsfordelingService
    );


    private final TraadVM VALGT_TRAAD = new TraadVM(createMeldingVMer());

    @BeforeEach
    void setUp() {
        initMocks(this);
        when(propertyResolver.hentTekst(anyString())).thenAnswer((Answer<String>) invocation -> ((String) invocation.getArguments()[0]));
        when(arbeidsfordelingService.hentGTnummerForEnhet(anyString())).thenReturn(Collections.emptyList());

        XMLMeldingFraBruker xmlMeldingFraBruker = new XMLMeldingFraBruker()
                .withFritekst("fritekst")
                .withTemagruppe(TEMAGRUPPE);

        List<Object> xmlHenvendelseListe = new ArrayList<>();
        xmlHenvendelseListe.add(lagXMLHenvendelse(BEHANDLINGS_ID, BEHANDLINGS_ID, DateTime.now(), null, XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name(), null, new XMLMetadataListe().withMetadata(xmlMeldingFraBruker)));

        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(
                new WSHentHenvendelseListeResponse().withAny(xmlHenvendelseListe));

        when(standardKodeverk.getArkivtemaNavn(anyString())).thenReturn(ARKIVTEMANAVN);
    }

    @Test
    void skalHenteMeldingerMedRiktigType() {
        SubjectHandler.withSubject(TEST_SUBJECT, () -> henvendelseBehandlingService.hentMeldinger(FNR, VALGT_ENHET));

        verify(henvendelsePortType).hentHenvendelseListe(wsHentHenvendelseListeRequestArgumentCaptor.capture());
        WSHentHenvendelseListeRequest request = wsHentHenvendelseListeRequestArgumentCaptor.getValue();

        assertThat(request.getFodselsnummer()).isEqualTo(FNR);
        assertThat(request.getTyper()).contains(SPORSMAL_SKRIFTLIG.name());
        assertThat(request.getTyper()).contains(SVAR_OPPMOTE.name());
        assertThat(request.getTyper()).contains(SVAR_SKRIFTLIG.name());
        assertThat(request.getTyper()).contains(SVAR_TELEFON.name());
        assertThat(request.getTyper()).contains(REFERAT_OPPMOTE.name());
        assertThat(request.getTyper()).contains(REFERAT_TELEFON.name());
        assertThat(request.getTyper()).contains(DELVIS_SVAR_SKRIFTLIG.name());
    }

    @Test
    void skalTransformereResponsenTilMeldingsliste() {
        List<Melding> meldinger = SubjectHandler.withSubject(TEST_SUBJECT, () -> henvendelseBehandlingService.hentMeldinger(FNR, VALGT_ENHET).getTraader()
                .stream()
                .flatMap(traader -> traader.getMeldinger().stream())
                .collect(Collectors.toList())
        );

        assertThat(meldinger).hasSize(1);
        assertThat(meldinger.get(0).id).isEqualTo(BEHANDLINGS_ID);
    }

    @Test
    void skalMerkeSomKontorsperret() {
        //TODO
        EnhetligKodeverk.Service kodeverk = mock(EnhetligKodeverk.Service.class);
        when(kodeverk.hentKodeverk(any())).thenReturn(PersondataTestdataKt.gittKodeverk());

        PersondataFletter fletter = new PersondataFletter(kodeverk);
        when(mock(persondataService.getClass()).hentPerson(any()))
            .thenReturn(fletter.flettSammenData(PersondataTestdataKt.gittData(
                PersondataTestdataKt.gittPerson(),
                PersondataResult.runCatching("gt", () -> null),
                PersondataResult.runCatching("egenAnsatt", () -> false),
                PersondataResult.runCatching("navEnhet", () -> gittNavKontorEnhet(VALGT_ENHET, NAVBRUKERS_ENHET))
            )));

        henvendelseBehandlingService.merkSomKontorsperret("navbrukers fnr", VALGT_TRAAD);

        verify(behandleHenvendelsePortType).oppdaterKontorsperre(NAVBRUKERS_ENHET, IDER_I_VALGT_TRAAD);
    }

    @Test
    void skalMerkeSomFeilsendt() {
        henvendelseBehandlingService.merkSomFeilsendt(VALGT_TRAAD);

        verify(behandleHenvendelsePortType).oppdaterTilKassering(IDER_I_VALGT_TRAAD);
    }

    @Test
    void skalMerkeForHastekassering() {
        henvendelseBehandlingService.merkForHastekassering(VALGT_TRAAD);

        verify(behandleHenvendelsePortType).markerTraadForHasteKassering(IDER_I_VALGT_TRAAD);
    }

    @Test
    void skalAlltidHenteMeldingerSomIkkeErKontorsperret() {
        List<Object> xmlHenvendelsesListe = new ArrayList<>();
        xmlHenvendelsesListe.add(lagXMLHenvendelse("id1", "id1", DateTime.now().minusDays(1), null, XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name(), null, new XMLMetadataListe().withMetadata(new XMLMeldingFraBruker(TEMAGRUPPE, "fritekst")))
                .withJournalfortInformasjon(null));
        final XMLMeldingFraBruker fritekst1 = new XMLMeldingFraBruker("fritekst", TEMAGRUPPE);
        xmlHenvendelsesListe.add(lagXMLHenvendelse("id2", "id2", DateTime.now(), null, XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name(), null, new XMLMetadataListe().withMetadata(fritekst1))
                .withJournalfortInformasjon(null));
        final XMLMeldingFraBruker fritekst3 = new XMLMeldingFraBruker("fritekst", TEMAGRUPPE);
        xmlHenvendelsesListe.add(lagXMLHenvendelse("id4", "id4", DateTime.now().plusDays(1), null, XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name(), null, new XMLMetadataListe().withMetadata(fritekst3))
                .withMarkeringer(new XMLMarkeringer().withKontorsperre(new XMLKontorsperre().withEnhet("1111"))));

        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(new WSHentHenvendelseListeResponse().withAny(xmlHenvendelsesListe));

        List<Melding> meldinger = SubjectHandler.withSubject(TEST_SUBJECT, () -> henvendelseBehandlingService.hentMeldinger(FNR, VALGT_ENHET).getTraader().stream()
                .flatMap(traad -> traad.getMeldinger().stream())
                .sorted(Comparator.comparing(melding -> melding.opprettetDato))
                .collect(Collectors.toList())
        );

        assertThat(meldinger).hasSize(2);
        assertThat(meldinger.get(0).id).isEqualTo("id1");
        assertThat(meldinger.get(1).id).isEqualTo("id2");
    }

    @Test
    void skalFjerneFritekstFraJournalfortMeldingManIkkeHarTilgangTil() {
        List<Object> xmlHenvendelsesListe = new ArrayList<>();
        xmlHenvendelsesListe.add(lagXMLHenvendelse("id1", "id1", DateTime.now(), null, XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name(), null, new XMLMetadataListe().withMetadata(new XMLMeldingFraBruker(TEMAGRUPPE, "fritekst")))
                .withJournalfortInformasjon(null));
        xmlHenvendelsesListe.add(lagXMLHenvendelse("id2", "id2", DateTime.now().plusDays(1), null, XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name(), null, new XMLMetadataListe().withMetadata(new XMLMeldingFraBruker(TEMAGRUPPE, "fritekst")))
                .withJournalfortInformasjon(new XMLJournalfortInformasjon().withJournalfortTema("tema")));

        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(new WSHentHenvendelseListeResponse().withAny(xmlHenvendelsesListe));
        List<Melding> meldinger = SubjectHandler.withSubject(TEST_SUBJECT, () -> henvendelseBehandlingService.hentMeldinger(FNR, VALGT_ENHET).getTraader().stream()
                .flatMap(traad -> traad.getMeldinger().stream())
                .sorted(Comparator.comparing(melding -> melding.opprettetDato))
                .collect(Collectors.toList())
        );

        assertThat(meldinger).hasSize(2);
        assertThat(meldinger.get(0).getFritekst()).isEqualTo("fritekst");
        assertThat(meldinger.get(1).getFritekst()).isEqualTo("tilgang.journalfort");
    }

    @Test
    void oversetterFraJournalfortTemaTilTemanavn() {
        List<Object> xmlHenvendelsesListe = new ArrayList<>();
        xmlHenvendelsesListe.add(lagXMLHenvendelse("id1", "id1", DateTime.now().minusDays(1), null, XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name(), null, new XMLMetadataListe().withMetadata(new XMLMeldingFraBruker(TEMAGRUPPE, "fritekst")))
                .withJournalfortInformasjon(new XMLJournalfortInformasjon()
                        .withJournalfortSaksId("saksid")
                        .withJournalforerNavIdent("journalfort av navident")
                        .withJournalfortDato(DateTime.now())
                        .withJournalfortTema("journalfort tema")));

        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(new WSHentHenvendelseListeResponse().withAny(xmlHenvendelsesListe));

        List<Melding> meldinger = SubjectHandler.withSubject(TEST_SUBJECT, () -> henvendelseBehandlingService.hentMeldinger(FNR, VALGT_ENHET).getTraader().stream()
                .flatMap(traad -> traad.getMeldinger().stream())
                .collect(Collectors.toList())
        );

        assertThat(meldinger.get(0).journalfortTemanavn).isEqualTo(ARKIVTEMANAVN);
    }

    @Test
    void oversetterIkkeFraJournalfortTemaTilTemanavnDersomJournalfortTemaErNull() {
        List<Object> xmlHenvendelsesListe = new ArrayList<>();
        xmlHenvendelsesListe.add(lagXMLHenvendelse("id1", BEHANDLINGS_ID, DateTime.now().minusDays(1), null, XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name(), null, new XMLMetadataListe().withMetadata(new XMLMeldingFraBruker(TEMAGRUPPE, "fritekst")))
                .withJournalfortInformasjon(null));

        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(new WSHentHenvendelseListeResponse().withAny(xmlHenvendelsesListe));

        List<Melding> meldinger = SubjectHandler.withSubject(TEST_SUBJECT, () -> henvendelseBehandlingService.hentMeldinger(FNR, VALGT_ENHET).getTraader().stream()
                .flatMap(traad -> traad.getMeldinger().stream())
                .collect(Collectors.toList())
        );

        assertThat(meldinger.get(0).journalfortTemanavn).isNull();
    }

    @Test
    void skalFjerneFritekstForOkonomiskSosialhjelpDersomManIkkeHarTilgang() {
        String fritekst = "fritekst";

        XMLHenvendelse okonomiskSosialhjelp = lagXMLHenvendelse("1234", "1234", DateTime.now(), DateTime.now(), SPORSMAL_SKRIFTLIG.toString(), null,
                new XMLMetadataListe().withMetadata(new XMLMeldingFraBruker().withFritekst(fritekst).withTemagruppe(OKSOS.toString())))
                .withTilknyttetEnhet("9999")
                .withGjeldendeTemagruppe(OKSOS.toString());

        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(new WSHentHenvendelseListeResponse().withAny(okonomiskSosialhjelp));

        List<Melding> meldinger = SubjectHandler.withSubject(TEST_SUBJECT, () -> henvendelseBehandlingService.hentMeldinger(FNR, VALGT_ENHET).getTraader().stream()
                .flatMap(traad -> traad.getMeldinger().stream())
                .collect(Collectors.toList())
        );

        assertThat(meldinger.get(0).getFritekst()).isNotEqualTo(fritekst);
    }

}
