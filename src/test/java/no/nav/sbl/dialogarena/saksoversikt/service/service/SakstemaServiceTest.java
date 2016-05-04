package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.sbl.dialogarena.common.kodeverk.KodeverkClient;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.*;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.sbl.dialogarena.saksoversikt.service.service.filter.FilterUtils;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.HentSakSammendragListePersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.pensjonsak.v1.HentSakSammendragListeSakManglerEierenhet;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSAvslutningsstatuser;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingsstatuser;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingstyper;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static java.util.Optional.of;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Baksystem.HENVENDELSE;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Baksystem.JOARK;
import static no.nav.sbl.dialogarena.saksoversikt.service.service.filter.FilterUtils.SEND_SOKNAD_KVITTERINGSTYPE;
import static no.nav.sbl.dialogarena.saksoversikt.service.service.SakstemaGrupperer.OPPFOLGING;
import static no.nav.sbl.dialogarena.saksoversikt.service.service.SakstemaGrupperer.TEMAGRUPPE_RESTERENDE_TEMA;
import static no.nav.sbl.dialogarena.saksoversikt.service.utils.Konstanter.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SakstemaServiceTest {

    @Mock
    private SakOgBehandlingService sakOgBehandlingService;

    @Mock
    private HenvendelseService henvendelseService;

    @Mock
    private GsakSakerService gsakSakerService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private PesysService pesysService;

    @Mock
    private KodeverkClient kodeverkClient;

    @Mock
    private BulletproofKodeverkService kodeverk;

    @Mock
    private DokumentMetadataService dokumentMetadataService;

    @Mock
    private SakstemaGrupperer sakstemaGrupperer;

    @InjectMocks
    private SakstemaService sakstemaService = new SakstemaService();

    private static final String FNR = "12345678901";

    @Before
    public void setup() {
        when(request.getSession()).thenReturn(new MockHttpSession());
    }

    private Sak lagSakMedAvsluttetDato(Optional<DateTime> date) {
        return new Sak().withSaksId("123")
                .withTemakode(DAGPENGER)
                .withAvsluttet(date);
    }

    @Test
    public void lagSakstemaUtenOppfolging() throws HentSakSammendragListeSakManglerEierenhet, HentSakSammendragListePersonIkkeFunnet {
        List dokumentmetadata = new ArrayList<>();

        when(kodeverk.getTemanavnForTemakode(DAGPENGER, "Tema")).thenReturn(new ResultatWrapper("Dagpenger"));

        List<Sak> saker = asList(lagSakMedAvsluttetDato(of(now().minusDays(30))));

        when(kodeverk.getTemanavnForTemakode(anyString(), anyString())).thenReturn(new ResultatWrapper("Dagpenger"));

        when(dokumentMetadataService.hentDokumentMetadata(any(), anyString())).thenReturn(new ResultatWrapper<>(dokumentmetadata, emptySet()));

        Map<String, List<Behandlingskjede>> emptyMap = new HashMap();
        when(sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(FNR)).thenReturn(emptyMap);

        when(sakstemaGrupperer.grupperSakstema(saker, dokumentmetadata, emptyMap)).thenReturn(new HashMap<String, Set<String>>() {{
            put("RESTERENDE_TEMA", new HashSet<>(asList(DAGPENGER)));
        }});

        List<Sakstema> sakstema = sakstemaService.hentSakstema(saker, FNR, true).resultat;

        assertTrue(sakstema.size() == 1);
        assertThat(sakstema.get(0).temanavn, equalTo("Dagpenger"));
    }

    @Test
    public void lagSakstemaMedOppfoling() {
        when(kodeverk.getTemanavnForTemakode(DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Dagpenger"));

        when(dokumentMetadataService.hentDokumentMetadata(any(), anyString())).thenReturn(new ResultatWrapper<>(emptyList(), emptySet()));

        Sak sak = new Sak()
                .withSaksId("123")
                .withTemakode(DAGPENGER)
                .withAvsluttet(null);

        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(OPPFOLGING)
                .withAvsluttet(null);

        Map.Entry entry = new AbstractMap.SimpleEntry<String, Set<String>>("Arbeid", new HashSet<>(Arrays.asList(DAGPENGER, OPPFOLGING)));
        ResultatWrapper<List<Sakstema>> wrapper = sakstemaService.opprettSakstemaForEnTemagruppe(entry, Arrays.asList(sak, oppfolinging), new ArrayList<>(), emptyMap(),true);

        assertThat(wrapper.resultat.get(0).temanavn, equalTo("Dagpenger og oppfølging"));
    }

    @Test
    public void sakstemaMedKunOppfolgingGrupperesIkke() {
        when(kodeverk.getTemanavnForTemakode(OPPFOLGING, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Oppfølging"));

        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(OPPFOLGING)
                .withAvsluttet(null);

        Map.Entry entry = new AbstractMap.SimpleEntry<String, Set<String>>(TEMAGRUPPE_RESTERENDE_TEMA, new HashSet<>(Arrays.asList(OPPFOLGING)));
        ResultatWrapper<List<Sakstema>> wrapper = sakstemaService.opprettSakstemaForEnTemagruppe(entry, Arrays.asList(oppfolinging), asList(
                new DokumentMetadata()
                        .withTilhorendeSakid("321")
                        .withMottaker(Entitet.SLUTTBRUKER)
                        .withAvsender(Entitet.NAV)
                        .withRetning(Kommunikasjonsretning.UT)
                        .withDato(LocalDateTime.now())
                        .withHoveddokument(
                                new Dokument()
                                        .withTittel("TEST"))), emptyMap(),true);

        assertThat(wrapper.resultat.get(0).temanavn, equalTo("Oppfølging"));
        assertThat(wrapper.resultat.size(), is(1));
    }

    @Test
    public void etSakstemaMedOppfolgingGirEtSakstema() {
        when(kodeverk.getTemanavnForTemakode(OPPFOLGING, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Oppfølging"));
        when(kodeverk.getTemanavnForTemakode(DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Dagpenger"));

        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(OPPFOLGING)
                .withAvsluttet(null);

        Sak sak = new Sak()
                .withSaksId("123")
                .withTemakode(DAGPENGER)
                .withAvsluttet(null);

        Map.Entry entry = new AbstractMap.SimpleEntry<String, Set<String>>(TEMAGRUPPE_ARBEID, new HashSet<>(Arrays.asList(DAGPENGER, OPPFOLGING)));
        ResultatWrapper<List<Sakstema>> wrapper = sakstemaService.opprettSakstemaForEnTemagruppe(entry, Arrays.asList(oppfolinging, sak), asList(
                new DokumentMetadata()
                        .withTilhorendeSakid("321")
                        .withMottaker(Entitet.SLUTTBRUKER)
                        .withAvsender(Entitet.NAV)
                        .withRetning(Kommunikasjonsretning.UT)
                        .withDato(LocalDateTime.now())
                        .withBaksystem(JOARK)
                        .withHoveddokument(
                                new Dokument()
                                        .withTittel("TEST"))), emptyMap(),true);

        assertThat(wrapper.resultat.get(0).temanavn, equalTo("Dagpenger og oppfølging"));
        assertThat(wrapper.resultat.size(), is(1));
    }

    @Test
    public void sakMedOppfolgingIHenvendelseSkalGrupperesOgFaaTilhorendeMetadata() {
        when(kodeverk.getTemanavnForTemakode(OPPFOLGING, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Oppfølging"));
        when(kodeverk.getTemanavnForTemakode(DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Dagpenger"));
        when(kodeverk.getTemanavnForTemakode(ARBEIDSAVKLARINGSPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Arbeidsavklaringspenger"));

        Sak sak = new Sak()
                .withSaksId("123")
                .withTemakode(DAGPENGER)
                .withAvsluttet(null);

        Sak sak2 = new Sak()
                .withSaksId("1234")
                .withTemakode(ARBEIDSAVKLARINGSPENGER)
                .withAvsluttet(null);

        Map.Entry entry = new AbstractMap.SimpleEntry<String, Set<String>>(TEMAGRUPPE_ARBEID, new HashSet<>(Arrays.asList(DAGPENGER, OPPFOLGING, ARBEIDSAVKLARINGSPENGER)));
        ResultatWrapper<List<Sakstema>> wrapper = sakstemaService.opprettSakstemaForEnTemagruppe(entry, Arrays.asList(sak2, sak), asList(
                new DokumentMetadata()
                        .withMottaker(Entitet.SLUTTBRUKER)
                        .withAvsender(Entitet.NAV)
                        .withRetning(Kommunikasjonsretning.UT)
                        .withDato(LocalDateTime.now())
                        .withBaksystem(HENVENDELSE)
                        .withTemakode("OPP")
                        .withHoveddokument(
                                new Dokument()
                                        .withTittel("Tilhorende Oppfolging"))), emptyMap(),true);

        assertThat(wrapper.resultat.size(), is(2));
    }

    @Test
    public void sakFraSakogBehandlingUtenTilhoerendeSakstemaOppretterEgetSakstema() {
        when(kodeverk.getTemanavnForTemakode(DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Dagpenger"));
        when(dokumentMetadataService.hentDokumentMetadata(any(), anyString())).thenReturn(new ResultatWrapper<>(emptyList()));

        Map<String, Set<String>> gruppertTema = new HashMap<String, Set<String>>(){{
            put("RESTERENDE_TEMA", new HashSet(asList("DAG")));
        }};

        when(sakstemaGrupperer.grupperSakstema(any(), any(),any())).thenReturn(gruppertTema);

        Map sakOgBehandlingResults = new HashMap<>();
        sakOgBehandlingResults.put("DAG", asList(sakFraSakOgBehandling()));
        when(sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(anyString())).thenReturn(sakOgBehandlingResults);

        ResultatWrapper<List<Sakstema>> listResultatWrapper = sakstemaService.hentSakstema(emptyList(), FNR, false);

        assertThat(listResultatWrapper.resultat.size(), is(1));
        assertThat(listResultatWrapper.resultat.get(0).temakode, is("DAG"));
    }

    @Test
    public void sakFraSakogBehandlingMedTilhoerendeSakstemaOppretterIkkeEgetSakstema() {
        when(kodeverk.getTemanavnForTemakode(DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Dagpenger"));
        when(dokumentMetadataService.hentDokumentMetadata(any(), anyString())).thenReturn(new ResultatWrapper<>(asList(new DokumentMetadata().withTemakode("DAG").withBaksystem(HENVENDELSE))));
        Map<String, Set<String>> gruppertTema = new HashMap<>();
        Set set = new HashSet<>();
        set.add("DAG");
        gruppertTema.put("RESTERENDE_TEMA", set);

        when(sakstemaGrupperer.grupperSakstema(any(), any(), any())).thenReturn(gruppertTema);
        Map sakOgBehandlingResults = new HashMap<>();
        sakOgBehandlingResults.put("DAG", asList(sakFraSakOgBehandling()));
        when(sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(anyString())).thenReturn(sakOgBehandlingResults);

        ResultatWrapper<List<Sakstema>> listResultatWrapper = sakstemaService.hentSakstema(emptyList(), FNR, false);

        assertThat(listResultatWrapper.resultat.size(), is(1));
        assertThat(listResultatWrapper.resultat.get(0).temakode, is("DAG"));
    }


    @Test
    public void forskjelligTemakodeSakOgBehandlingOgAnnet() {
        when(kodeverk.getTemanavnForTemakode(DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Dagpenger"));
        when(kodeverk.getTemanavnForTemakode("FOR", BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Foreldrepenger"));
        when(dokumentMetadataService.hentDokumentMetadata(any(), anyString())).thenReturn(new ResultatWrapper<>(asList(new DokumentMetadata().withTemakode("FOR").withBaksystem(HENVENDELSE))));

        Map<String, Set<String>> gruppertTema = new HashMap<String, Set<String>>(){{
            put("RESTERENDE_TEMA", new HashSet(asList("FOR", "DAG")));
        }};

        when(sakstemaGrupperer.grupperSakstema(any(), any(),any())).thenReturn(gruppertTema);
        Map sakOgBehandlingResults = new HashMap<>();
        sakOgBehandlingResults.put("DAG", asList(sakFraSakOgBehandling()));
        when(sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(anyString())).thenReturn(sakOgBehandlingResults);

        ResultatWrapper<List<Sakstema>> listResultatWrapper = sakstemaService.hentSakstema(emptyList(), FNR, false);

        assertThat(listResultatWrapper.resultat.size(), is(2));
    }

    private WSSak sakFraSakOgBehandling() {
        return new WSSak().withBehandlingskjede(new WSBehandlingskjede()
                .withSisteBehandlingAvslutningsstatus(new WSAvslutningsstatuser().withValue(FilterUtils.OPPRETTET))
                .withSisteBehandlingstype(new WSBehandlingstyper().withValue(SEND_SOKNAD_KVITTERINGSTYPE))
                .withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue(FilterUtils.OPPRETTET))
                .withBehandlingsListeRef("henvendelsesId")
                .withSisteBehandlingREF("henvendelsesId")
                .withStart(new DateTime().minusDays(1))
                .withSlutt(null));
    }

    @Test
    public void FlereSakstemaMedOppfolgingGirFlereSakstemaMedOppfolging() {
        when(kodeverk.getTemanavnForTemakode(OPPFOLGING, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Oppfølging"));
        when(kodeverk.getTemanavnForTemakode(DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Dagpenger"));
        when(kodeverk.getTemanavnForTemakode(ARBEIDSAVKLARINGSPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Arbeidsavklaringspenger"));

        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(OPPFOLGING)
                .withAvsluttet(null);

        Sak sak = new Sak()
                .withSaksId("123")
                .withTemakode(DAGPENGER)
                .withAvsluttet(null);

        Sak sak2 = new Sak()
                .withSaksId("122")
                .withTemakode(ARBEIDSAVKLARINGSPENGER)
                .withAvsluttet(null);

        Map.Entry entry = new AbstractMap.SimpleEntry<String, Set<String>>(TEMAGRUPPE_ARBEID, new HashSet<>(Arrays.asList(DAGPENGER, OPPFOLGING, ARBEIDSAVKLARINGSPENGER)));
        ResultatWrapper<List<Sakstema>> wrapper = sakstemaService.opprettSakstemaForEnTemagruppe(entry, Arrays.asList(oppfolinging, sak, sak2), asList(
                new DokumentMetadata()
                        .withTilhorendeSakid("321")
                        .withMottaker(Entitet.SLUTTBRUKER)
                        .withAvsender(Entitet.NAV)
                        .withRetning(Kommunikasjonsretning.UT)
                        .withDato(LocalDateTime.now())
                        .withBaksystem(JOARK)
                        .withHoveddokument(
                                new Dokument()
                                        .withTittel("TEST"))), emptyMap(),true);

        assertThat(wrapper.resultat.get(0).temanavn, equalTo("Arbeidsavklaringspenger og oppfølging"));
        assertThat(wrapper.resultat.get(1).temanavn, equalTo("Dagpenger og oppfølging"));
        assertThat(wrapper.resultat.size(), is(2));
    }

    @Test
    public void gruppererNyttTemaFraSogBOmViHarEnOppfolgingssak() {
        when(kodeverk.getTemanavnForTemakode(DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Dagpenger"));
        when(kodeverk.getTemanavnForTemakode(ARBEIDSAVKLARINGSPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Arbeidsavklaringspenger"));
        when(kodeverk.getTemanavnForTemakode(OPPFOLGING, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Oppfølging"));

        when(dokumentMetadataService.hentDokumentMetadata(any(), anyString()))
                .thenReturn(
                        new ResultatWrapper<>(
                                asList(
                                        new DokumentMetadata()
                                                .withTilhorendeSakid("123")
                                                .withTemakode("AAP")
                                                .withBaksystem(JOARK),
                                        new DokumentMetadata()
                                                .withTilhorendeSakid("456")
                                                .withTemakode("OPP")
                                                .withBaksystem(JOARK))
                        ));

        Map<String, Set<String>> gruppertTema = new HashMap<String, Set<String>>(){{
            put("Arbeid", new HashSet(asList("AAP", "DAG", "OPP")));
        }};

        when(sakstemaGrupperer.grupperSakstema(any(), any(),any())).thenReturn(gruppertTema);

        List<Sak> saker = asList(new Sak().withSaksId("123").withTemakode("AAP"), new Sak().withSaksId("456").withTemakode("OPP"));

        Map sakOgBehandlingResults = new HashMap<>();
        sakOgBehandlingResults.put("DAG", asList(sakFraSakOgBehandling()));

        when(sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(anyString())).thenReturn(sakOgBehandlingResults);

        ResultatWrapper<List<Sakstema>> listResultatWrapper = sakstemaService.hentSakstema(saker, FNR, true);

        assertThat(listResultatWrapper.resultat.size(), is(2));
        assertThat(listResultatWrapper.resultat.get(0).temanavn, is("Arbeidsavklaringspenger og oppfølging"));
        assertThat(listResultatWrapper.resultat.get(1).temanavn, is("Dagpenger og oppfølging"));
    }

    @Test
    public void slaarSammenSykepengerOgSykemeldingMedOppfolging (){
        when(kodeverk.getTemanavnForTemakode("SYK", BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Sykepenger"));
        when(kodeverk.getTemanavnForTemakode("SYM", BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Sykemeldinger"));
        when(kodeverk.getTemanavnForTemakode(OPPFOLGING, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Oppfølging"));

        when(dokumentMetadataService.hentDokumentMetadata(any(), anyString()))
                .thenReturn(
                        new ResultatWrapper<>(
                                asList(
                                        new DokumentMetadata()
                                                .withTilhorendeSakid("123")
                                                .withTemakode("SYM")
                                                .withBaksystem(JOARK),
                                        new DokumentMetadata()
                                                .withTilhorendeSakid("456")
                                                .withTemakode("SYK")
                                                .withBaksystem(JOARK),
                                        new DokumentMetadata()
                                                .withTilhorendeSakid("789")
                                                .withTemakode("OPP")
                                                .withBaksystem(JOARK))
                        ));

        Map<String, Set<String>> gruppertTema = new HashMap<String, Set<String>>(){{
            put("Arbeid", new HashSet(asList("SYK", "SYM", "OPP")));
        }};

        when(sakstemaGrupperer.grupperSakstema(any(), any(),any())).thenReturn(gruppertTema);

        List<Sak> saker = asList(
                new Sak()
                        .withSaksId("123")
                        .withTemakode("SYM"),
                new Sak()
                        .withSaksId("456")
                        .withTemakode("SYK"),
                new Sak()
                        .withSaksId("789")
                        .withTemakode("OPP")
        );

        Map sakOgBehandlingResults = new HashMap<>();
        sakOgBehandlingResults.put("DAG", asList(sakFraSakOgBehandling()));

        when(sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(anyString())).thenReturn(sakOgBehandlingResults);

        ResultatWrapper<List<Sakstema>> listResultatWrapper = sakstemaService.hentSakstema(saker, FNR, true);

        assertThat(listResultatWrapper.resultat.size(), is(1));
        assertThat(listResultatWrapper.resultat.get(0).temanavn, is("Sykmelding, sykepenger og oppfølging"));
    }

    @Test
    public void slaarSammenSykepengerOgSykemeldingUtenOppfolging (){
        when(kodeverk.getTemanavnForTemakode("SYK", BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Sykepenger"));
        when(kodeverk.getTemanavnForTemakode("SYM", BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Sykemeldinger"));
        when(kodeverk.getTemanavnForTemakode(OPPFOLGING, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Oppfølging"));

        when(dokumentMetadataService.hentDokumentMetadata(any(), anyString()))
                .thenReturn(
                        new ResultatWrapper<>(
                                asList(
                                        new DokumentMetadata()
                                                .withTilhorendeSakid("123")
                                                .withTemakode("SYM")
                                                .withBaksystem(JOARK),
                                        new DokumentMetadata()
                                                .withTilhorendeSakid("456")
                                                .withTemakode("SYK")
                                                .withBaksystem(JOARK))
                        ));

        Map<String, Set<String>> gruppertTema = new HashMap<String, Set<String>>(){{
            put(TEMAGRUPPE_RESTERENDE_TEMA, new HashSet(asList("SYK", "SYM")));
        }};

        when(sakstemaGrupperer.grupperSakstema(any(), any(),any())).thenReturn(gruppertTema);

        List<Sak> saker = asList(
                new Sak()
                        .withSaksId("123")
                        .withTemakode("SYM"),
                new Sak()
                        .withSaksId("456")
                        .withTemakode("SYK"),
                new Sak()
                        .withSaksId("789")
                        .withTemakode("OPP")
        );

        Map sakOgBehandlingResults = new HashMap<>();
        sakOgBehandlingResults.put("DAG", asList(sakFraSakOgBehandling()));

        when(sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(anyString())).thenReturn(sakOgBehandlingResults);

        ResultatWrapper<List<Sakstema>> listResultatWrapper = sakstemaService.hentSakstema(saker, FNR, true);

        assertThat(listResultatWrapper.resultat.size(), is(1));
        assertThat(listResultatWrapper.resultat.get(0).temanavn, is("Sykmelding og sykepenger"));

    }

    @Test
    public void slaarIkkeSammenSykepengerOgSykemeldingHvisEnErTomtTema (){
        when(kodeverk.getTemanavnForTemakode("SYK", BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Sykepenger"));
        when(kodeverk.getTemanavnForTemakode("SYM", BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Sykemeldinger"));
        when(kodeverk.getTemanavnForTemakode(OPPFOLGING, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Oppfølging"));

        when(dokumentMetadataService.hentDokumentMetadata(any(), anyString()))
                .thenReturn(
                        new ResultatWrapper<>(
                                asList(
                                        new DokumentMetadata()
                                                .withTilhorendeSakid("123")
                                                .withTemakode("SYM")
                                                .withBaksystem(JOARK))
                        ));

        Map<String, Set<String>> gruppertTema = new HashMap<String, Set<String>>(){{
            put(TEMAGRUPPE_RESTERENDE_TEMA, new HashSet(asList("SYM")));
        }};

        when(sakstemaGrupperer.grupperSakstema(any(), any(),any())).thenReturn(gruppertTema);

        List<Sak> saker = asList(
                new Sak()
                        .withSaksId("123")
                        .withTemakode("SYM"),
                new Sak()
                        .withSaksId("456")
                        .withTemakode("SYK"),
                new Sak()
                        .withSaksId("789")
                        .withTemakode("OPP")
        );

        Map sakOgBehandlingResults = new HashMap<>();
        sakOgBehandlingResults.put("DAG", asList(sakFraSakOgBehandling()));

        when(sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(anyString())).thenReturn(sakOgBehandlingResults);

        ResultatWrapper<List<Sakstema>> listResultatWrapper = sakstemaService.hentSakstema(saker, FNR, true);

        assertThat(listResultatWrapper.resultat.size(), is(1));
        assertThat(listResultatWrapper.resultat.get(0).temanavn, is("Sykemeldinger"));
    }

    @Test
    public void slaarIkkeSammenSykepengerOgSykemeldingForModia (){
        when(kodeverk.getTemanavnForTemakode("SYK", BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Sykepenger"));
        when(kodeverk.getTemanavnForTemakode("SYM", BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Sykemeldinger"));
        when(kodeverk.getTemanavnForTemakode(OPPFOLGING, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Oppfølging"));

        when(dokumentMetadataService.hentDokumentMetadata(any(), anyString()))
                .thenReturn(
                        new ResultatWrapper<>(
                                asList(
                                        new DokumentMetadata()
                                                .withTilhorendeSakid("123")
                                                .withTemakode("SYK")
                                                .withBaksystem(JOARK),
                                        new DokumentMetadata()
                                                .withTilhorendeSakid("456")
                                                .withTemakode("SYM")
                                                .withBaksystem(JOARK),
                                        new DokumentMetadata()
                                                .withTilhorendeSakid("789")
                                                .withTemakode("OPP")
                                                .withBaksystem(JOARK))
                        ));

        Map<String, Set<String>> gruppertTema = new HashMap<String, Set<String>>(){{
            put(TEMAGRUPPE_ARBEID, new HashSet(asList("SYK", "SYM", "OPP")));
        }};

        when(sakstemaGrupperer.grupperSakstema(any(), any(),any())).thenReturn(gruppertTema);

        List<Sak> saker = asList(
                new Sak()
                        .withSaksId("123")
                        .withTemakode("SYM"),
                new Sak()
                        .withSaksId("456")
                        .withTemakode("SYK"),
                new Sak()
                        .withSaksId("789")
                        .withTemakode("OPP")
        );

        Map sakOgBehandlingResults = new HashMap<>();

        when(sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(anyString())).thenReturn(sakOgBehandlingResults);

        ResultatWrapper<List<Sakstema>> listResultatWrapper = sakstemaService.hentSakstema(saker, FNR, false);

        assertThat(listResultatWrapper.resultat.size(), is(3));
        assertThat(listResultatWrapper.resultat.get(0).temanavn, is("Sykepenger"));
    }

}
