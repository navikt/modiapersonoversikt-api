package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.kjerneinfo.domain.person.Person;
import no.nav.kjerneinfo.domain.person.Personfakta;
import no.nav.kjerneinfo.domain.person.fakta.AnsvarligEnhet;
import no.nav.kjerneinfo.domain.person.fakta.Organisasjonsenhet;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLJournalfortInformasjon;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.SaksbehandlerInnstillingerService;
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
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SPORSMAL;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR;
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

    @InjectMocks
    private HenvendelseBehandlingService henvendelseBehandlingService;

    private Sak sak;
    private Melding melding;

    @Before
    public void setUp() {
        XMLMeldingFraBruker xmlMeldingFraBruker = new XMLMeldingFraBruker()
                .withFritekst("fritekst")
                .withTemagruppe(TEMAGRUPPE);

        when(pep.hasAccess(any(PolicyRequest.class))).thenReturn(true);

        List<Object> xmlHenvendelseListe = new ArrayList<>();
        xmlHenvendelseListe.add(lagXMLHenvendelse(BEHANDLINGS_ID, DateTime.now(), null, XMLHenvendelseType.SPORSMAL.name(), xmlMeldingFraBruker));

        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(
                new WSHentHenvendelseListeResponse().withAny(xmlHenvendelseListe));

        when(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()).thenReturn("1231");

        sak = createSak(SAKS_ID, SAKSTEMA, SAKSTYPE, SAKSTYPE_GENERELL, DateTime.now().minusDays(4));
        melding = createMelding(BEHANDLINGS_ID, Meldingstype.SPORSMAL, DateTime.now(), TEMAGRUPPE, BEHANDLINGS_ID);
    }

    @Test
    public void skalHenteMeldingerMedRiktigType() {
        henvendelseBehandlingService.hentMeldinger(FNR);

        verify(henvendelsePortType).hentHenvendelseListe(wsHentHenvendelseListeRequestArgumentCaptor.capture());
        WSHentHenvendelseListeRequest request = wsHentHenvendelseListeRequestArgumentCaptor.getValue();

        assertThat(request.getFodselsnummer(), is(FNR));
        assertTrue(request.getTyper().contains(SPORSMAL.name()));
        assertTrue(request.getTyper().contains(SVAR.name()));
        assertTrue(request.getTyper().contains(REFERAT.name()));
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
    public void skalAlltidHenteMeldingerSomIkkeErKontorSperretEllerJournalfort() {
        List<Object> xmlHenvendelsesListe = new ArrayList<>();
        xmlHenvendelsesListe.add(lagXMLHenvendelse("id1", DateTime.now(), null, XMLHenvendelseType.SPORSMAL.name(), new XMLMeldingFraBruker("fritekst", TEMAGRUPPE))
                .withJournalfortInformasjon(null)
                .withKontorsperreEnhet(null));
        xmlHenvendelsesListe.add(lagXMLHenvendelse("id2", DateTime.now(), null, XMLHenvendelseType.SPORSMAL.name(), new XMLMeldingFraBruker("fritekst", TEMAGRUPPE))
                .withJournalfortInformasjon(null)
                .withKontorsperreEnhet(null));
        xmlHenvendelsesListe.add(lagXMLHenvendelse("id3", DateTime.now(), null, XMLHenvendelseType.SPORSMAL.name(), new XMLMeldingFraBruker("fritekst", TEMAGRUPPE))
                .withKontorsperreEnhet(null));
        xmlHenvendelsesListe.add(lagXMLHenvendelse("id4", DateTime.now(), null, XMLHenvendelseType.SPORSMAL.name(), new XMLMeldingFraBruker("fritekst", TEMAGRUPPE))
                .withKontorsperreEnhet("1111"));

        when(pep.hasAccess(any(PolicyRequest.class))).thenReturn(false);
        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(new WSHentHenvendelseListeResponse().withAny(xmlHenvendelsesListe));
        List<Melding> meldinger = henvendelseBehandlingService.hentMeldinger(FNR);

        assertThat(meldinger.size(), is(2));
        assertThat(meldinger.get(0).id, is(equalTo("id1")));
        assertThat(meldinger.get(1).id, is(equalTo("id2")));
    }
}