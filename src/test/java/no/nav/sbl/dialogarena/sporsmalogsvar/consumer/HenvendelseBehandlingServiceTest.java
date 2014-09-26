package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.kjerneinfo.domain.person.Person;
import no.nav.kjerneinfo.domain.person.Personfakta;
import no.nav.kjerneinfo.domain.person.fakta.AnsvarligEnhet;
import no.nav.kjerneinfo.domain.person.fakta.Organisasjonsenhet;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLJournalfortInformasjon;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.SaksbehandlerInnstillingerService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.StandardKodeverk;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeResponse;
import org.joda.time.DateTime;
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
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT_OPPMOTE;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT_TELEFON;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SPORSMAL_SKRIFTLIG;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR_OPPMOTE;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR_SKRIFTLIG;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR_TELEFON;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak.SAKSTYPE_GENERELL;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.ID_1;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.ID_2;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.ID_3;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.createMelding;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.createMeldingVMer;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.createSak;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.innloggetBrukerEr;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.lagXMLHenvendelse;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.text.IsEmptyString.isEmptyString;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HenvendelseBehandlingServiceTest {

    private static final String FNR = "11111111";
    private static final String TEMAGRUPPE = "temagruppe";
    private static final String BEHANDLINGS_ID = "id1";
    private static final String SAKS_ID = "111111111";
    private static final String SAKSTEMA = "tema";
    private static final String SAKSTYPE = "Fagsystem1";
    private static final String JOURNALPOST_ID = "journalpostId";
    private static final String NAVIDENT = "navident";
    private static final TraadVM VALGT_TRAAD = new TraadVM(createMeldingVMer());
    private static final List<String> IDER_I_VALGT_TRAAD = asList(ID_1, ID_2, ID_3);
    private static final String NAVBRUKERS_ENHET = "Navbrukers enhet";
    public static final String ARKIVTEMANAVN = "arkivtemanavn";

    @Captor
    private ArgumentCaptor<WSHentHenvendelseListeRequest> wsHentHenvendelseListeRequestArgumentCaptor;
    @Captor
    private ArgumentCaptor<XMLJournalfortInformasjon> xmlJournalfortInformasjonArgumentCaptor;

    @Mock
    private HenvendelsePortType henvendelsePortType;
    @Mock
    protected BehandleHenvendelsePortType behandleHenvendelsePortType;
    @Mock
    private EnforcementPoint pep;
    @Mock
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Mock
    private PersonKjerneinfoServiceBi kjerneinfo;
    @Mock
    private StandardKodeverk standardKodeverk;

    @InjectMocks
    private HenvendelseBehandlingService henvendelseBehandlingService;

    private Sak sak;
    private Melding melding;
    private List<Object> xmlHenvendelsesListe;

    @Before
    public void setUp() {
        XMLMeldingFraBruker xmlMeldingFraBruker = new XMLMeldingFraBruker()
                .withFritekst("fritekst")
                .withTemagruppe(TEMAGRUPPE);

        when(pep.hasAccess(any(PolicyRequest.class))).thenReturn(true);

        List<Object> xmlHenvendelseListe = new ArrayList<>();
        xmlHenvendelseListe.add(lagXMLHenvendelse(BEHANDLINGS_ID, BEHANDLINGS_ID, DateTime.now(), null, XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name(), new XMLMetadataListe().withMetadata(xmlMeldingFraBruker)));

        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(
                new WSHentHenvendelseListeResponse().withAny(xmlHenvendelseListe));

        when(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()).thenReturn("1231");
        when(standardKodeverk.getArkivtemaNavn(anyString())).thenReturn(ARKIVTEMANAVN);

        sak = createSak(SAKS_ID, SAKSTEMA, SAKSTYPE, SAKSTYPE_GENERELL, DateTime.now().minusDays(4));
        melding = createMelding(BEHANDLINGS_ID, Meldingstype.SPORSMAL_SKRIFTLIG, DateTime.now(), TEMAGRUPPE, BEHANDLINGS_ID);
    }

    @Test
    public void skalHenteMeldingerMedRiktigType() {
        henvendelseBehandlingService.hentMeldinger(FNR);

        verify(henvendelsePortType).hentHenvendelseListe(wsHentHenvendelseListeRequestArgumentCaptor.capture());
        WSHentHenvendelseListeRequest request = wsHentHenvendelseListeRequestArgumentCaptor.getValue();

        assertThat(request.getFodselsnummer(), is(FNR));
        assertTrue(request.getTyper().contains(SPORSMAL_SKRIFTLIG.name()));
        assertTrue(request.getTyper().contains(SVAR_SKRIFTLIG.name()));
        assertTrue(request.getTyper().contains(SVAR_OPPMOTE.name()));
        assertTrue(request.getTyper().contains(SVAR_TELEFON.name()));
        assertTrue(request.getTyper().contains(REFERAT_OPPMOTE.name()));
        assertTrue(request.getTyper().contains(REFERAT_TELEFON.name()));
    }

    @Test
    public void skalTransformereResponsenTilMeldingsliste() {
        List<Melding> meldinger = henvendelseBehandlingService.hentMeldinger(FNR);

        assertThat(meldinger.size(), is(1));
        assertThat(meldinger.get(0).id, is(BEHANDLINGS_ID));
    }

    @Test
    public void skalSendeJournalfortInformasjonTilBehandleHenvendelse() {
        innloggetBrukerEr(NAVIDENT);
        henvendelseBehandlingService.oppdaterJournalfortInformasjonIHenvendelse(sak, JOURNALPOST_ID, melding);

        verify(behandleHenvendelsePortType).oppdaterJournalfortInformasjon(anyString(), xmlJournalfortInformasjonArgumentCaptor.capture());
        XMLJournalfortInformasjon journalfortInformasjon = xmlJournalfortInformasjonArgumentCaptor.getValue();

        assertThat(journalfortInformasjon.getJournalfortTema(), is(SAKSTEMA));
        assertThat(journalfortInformasjon.getJournalpostId(), is(JOURNALPOST_ID));
        assertThat(journalfortInformasjon.getJournalfortSaksId(), is(SAKS_ID));
        assertThat(journalfortInformasjon.getJournalforerNavIdent(), is(NAVIDENT));
    }

    @Test
    public void skalMerkeSomKontorsperret() {
        HentKjerneinformasjonResponse hentKjerneinformasjonResponse = new HentKjerneinformasjonResponse();
        hentKjerneinformasjonResponse.setPerson(
                new Person.With().personfakta(
                        new Personfakta.With().harAnsvarligEnhet(
                                new AnsvarligEnhet.With().organisasjonsenhet(
                                        new Organisasjonsenhet.With().organisasjonselementId(NAVBRUKERS_ENHET)
                                                .done()
                                ).done()
                        ).done()
                ).done()
        );

        when(kjerneinfo.hentKjerneinformasjon(any(HentKjerneinformasjonRequest.class))).thenReturn(hentKjerneinformasjonResponse);
        henvendelseBehandlingService.merkSomKontorsperret("navbrukers fnr", VALGT_TRAAD);

        verify(behandleHenvendelsePortType).oppdaterKontorsperre(NAVBRUKERS_ENHET, IDER_I_VALGT_TRAAD);
    }

    @Test
    public void skalMerkeSomFeilsendt() {
        henvendelseBehandlingService.merkSomFeilsendt(VALGT_TRAAD);

        verify(behandleHenvendelsePortType).oppdaterTilKassering(IDER_I_VALGT_TRAAD);
    }

    @Test
    public void skalAlltidHenteMeldingerSomIkkeErKontorsperret() {
        List<Object> xmlHenvendelsesListe = new ArrayList<>();
        xmlHenvendelsesListe.add(lagXMLHenvendelse("id1", DateTime.now(), null, XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name(), new XMLMeldingFraBruker(TEMAGRUPPE, "fritekst"))
                .withJournalfortInformasjon(null)
                .withKontorsperreEnhet(null));
        final XMLMeldingFraBruker fritekst1 = new XMLMeldingFraBruker("fritekst", TEMAGRUPPE);
        xmlHenvendelsesListe.add(lagXMLHenvendelse("id2", "id2", DateTime.now(), null, XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name(), new XMLMetadataListe().withMetadata(fritekst1))
                .withJournalfortInformasjon(null)
                .withKontorsperreEnhet(null));
        final XMLMeldingFraBruker fritekst3 = new XMLMeldingFraBruker("fritekst", TEMAGRUPPE);
        xmlHenvendelsesListe.add(lagXMLHenvendelse("id4", "id4", DateTime.now(), null, XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name(), new XMLMetadataListe().withMetadata(fritekst3))
                .withKontorsperreEnhet("1111"));

        when(pep.hasAccess(any(PolicyRequest.class))).thenReturn(false);
        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(new WSHentHenvendelseListeResponse().withAny(xmlHenvendelsesListe));
        List<Melding> meldinger = henvendelseBehandlingService.hentMeldinger(FNR);

        assertThat(meldinger.size(), is(2));
        assertThat(meldinger.get(0).id, is(equalTo("id1")));
        assertThat(meldinger.get(1).id, is(equalTo("id2")));
    }

    @Test
    public void skalFjerneFritekstFraJournalfortMeldingManIkkeHarTilgangTil() {
        List<Object> xmlHenvendelsesListe = new ArrayList<>();
        xmlHenvendelsesListe.add(lagXMLHenvendelse("id1", DateTime.now(), null, XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name(), new XMLMeldingFraBruker(TEMAGRUPPE, "fritekst"))
                .withJournalfortInformasjon(null));
        xmlHenvendelsesListe.add(lagXMLHenvendelse("id2", "id2", DateTime.now(), null, XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name(), new XMLMetadataListe().withMetadata(new XMLMeldingFraBruker(TEMAGRUPPE, "fritekst")))
                .withJournalfortInformasjon(new XMLJournalfortInformasjon().withJournalfortTema("tema")));

        when(pep.hasAccess(any(PolicyRequest.class))).thenReturn(false);
        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(new WSHentHenvendelseListeResponse().withAny(xmlHenvendelsesListe));
        List<Melding> meldinger = henvendelseBehandlingService.hentMeldinger(FNR);

        assertThat(meldinger.size(), is(2));
        assertThat(meldinger.get(0).fritekst, is(equalTo("fritekst")));
        assertThat(meldinger.get(1).fritekst, isEmptyString());
    }

    @Test
    public void oversetterFraJournalfortTemaTilTemanavn() {
        xmlHenvendelsesListe.clear();
        xmlHenvendelsesListe.add(lagXMLHenvendelse("id1", DateTime.now().minusDays(1), null, XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name(), new XMLMeldingFraBruker(TEMAGRUPPE, "fritekst"))
                .withJournalfortInformasjon(new XMLJournalfortInformasjon()
                        .withJournalfortSaksId("saksid")
                        .withJournalforerNavIdent("journalfort av navident")
                        .withJournalfortDato(DateTime.now())
                        .withJournalfortTema("journalfort tema"))

        );
        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(new WSHentHenvendelseListeResponse().withAny(xmlHenvendelsesListe));

        List<Melding> meldinger = henvendelseBehandlingService.hentMeldinger(FNR);

        assertThat(meldinger.get(0).journalfortTemanavn, is(ARKIVTEMANAVN));
    }

    @Test
    public void oversetterIkkeFraJournalfortTemaTilTemanavnDersomJournalfortTemaErNull() {
        xmlHenvendelsesListe.clear();
        xmlHenvendelsesListe.add(lagXMLHenvendelse("id1", DateTime.now().minusDays(1), null, XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name(), new XMLMeldingFraBruker(TEMAGRUPPE, "fritekst"))
                .withJournalfortInformasjon(null));
        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(new WSHentHenvendelseListeResponse().withAny(xmlHenvendelsesListe));

        List<Melding> meldinger = henvendelseBehandlingService.hentMeldinger(FNR);

        assertNull(meldinger.get(0).journalfortTemanavn);
    }

    private XMLHenvendelse createStandardXMLHenvendelse() {
        return lagXMLHenvendelse(BEHANDLINGS_ID, DateTime.now(), null, XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name(),
                new XMLMeldingFraBruker()
                        .withFritekst("fritekst")
                        .withTemagruppe(TEMAGRUPPE));
    }

}