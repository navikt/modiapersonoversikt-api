package no.nav.modiapersonoversikt.legacy.sak.service;

import no.nav.modiapersonoversikt.legacy.sak.BehandlingskjedeBuilder;
import no.nav.modiapersonoversikt.legacy.sak.SakBuilder;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.*;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.modiapersonoversikt.legacy.sak.service.filter.FilterUtils;
import no.nav.modiapersonoversikt.legacy.sak.utils.Konstanter;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.joda.time.DateTime.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SakstemaServiceTest {

    @Mock
    private SakOgBehandlingService sakOgBehandlingService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private BulletproofKodeverkService kodeverk;

    @Mock
    private DokumentMetadataService dokumentMetadataService;

    @Mock
    private SakstemaGrupperer sakstemaGrupperer;

    @InjectMocks
    private SakstemaService sakstemaService = new SakstemaService();

    private static final String FNR = "11111111111";

    private Sak lagSakMedAvsluttetDato(Optional<DateTime> date) {
        return new Sak().withSaksId("123")
                .withTemakode(Konstanter.DAGPENGER)
                .withAvsluttet(date);
    }

    @Test
    public void lagSakstemaUtenOppfolging() {
        List dokumentmetadata = new ArrayList<>();

        List<Sak> saker = asList(lagSakMedAvsluttetDato(of(now().minusDays(30))));

        when(kodeverk.getTemanavnForTemakode(anyString(), any())).thenReturn(new ResultatWrapper<>("Dagpenger"));

        when(dokumentMetadataService.hentDokumentMetadata(anyString())).thenReturn(new ResultatWrapper<>(dokumentmetadata, emptySet()));

        Map<String, List<Behandlingskjede>> emptyMap = new HashMap();
        when(sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(FNR)).thenReturn(emptyMap);

        when(sakstemaGrupperer.grupperSakstema(saker, dokumentmetadata, emptyMap)).thenReturn(new HashMap<String, Set<String>>() {{
            put("RESTERENDE_TEMA", new HashSet<>(asList(Konstanter.DAGPENGER)));
        }});

        List<Sakstema> sakstema = sakstemaService.hentSakstema(saker, FNR, true).resultat;

        assertEquals(1, sakstema.size());
        assertThat(sakstema.get(0).temanavn, equalTo("Dagpenger"));
    }

    @Test
    public void lagSakstemaMedOppfoling() {
        when(kodeverk.getTemanavnForTemakode(Konstanter.DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper<>("Dagpenger"));

        Sak sak = new Sak()
                .withSaksId("123")
                .withTemakode(Konstanter.DAGPENGER)
                .withAvsluttet(null);

        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(SakstemaGrupperer.OPPFOLGING)
                .withAvsluttet(null);

        Map.Entry entry = new AbstractMap.SimpleEntry<String, Set<String>>("Arbeid", new HashSet<>(Arrays.asList(Konstanter.DAGPENGER, SakstemaGrupperer.OPPFOLGING)));
        ResultatWrapper<List<Sakstema>> wrapper = sakstemaService.opprettSakstemaForEnTemagruppe(entry, Arrays.asList(sak, oppfolinging), new ArrayList<>(), emptyMap(), true);

        assertThat(wrapper.resultat.get(0).temanavn, equalTo("Dagpenger og oppfølging"));
    }

    @Test
    public void sakstemaMedKunOppfolgingGrupperesIkke() {
        when(kodeverk.getTemanavnForTemakode(SakstemaGrupperer.OPPFOLGING, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper<>("Oppfølging"));

        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(SakstemaGrupperer.OPPFOLGING)
                .withAvsluttet(null);

        Map.Entry entry = new AbstractMap.SimpleEntry<String, Set<String>>(SakstemaGrupperer.TEMAGRUPPE_RESTERENDE_TEMA, new HashSet<>(Arrays.asList(SakstemaGrupperer.OPPFOLGING)));
        ResultatWrapper<List<Sakstema>> wrapper = sakstemaService.opprettSakstemaForEnTemagruppe(entry, Arrays.asList(oppfolinging), Arrays.asList(
                new DokumentMetadata()
                        .withTilhorendeSakid("321")
                        .withMottaker(Entitet.SLUTTBRUKER)
                        .withAvsender(Entitet.NAV)
                        .withRetning(Kommunikasjonsretning.UT)
                        .withDato(LocalDateTime.now())
                        .withHoveddokument(
                                new Dokument()
                                        .withTittel("TEST"))), emptyMap(), true);

        assertThat(wrapper.resultat.get(0).temanavn, equalTo("Oppfølging"));
        assertThat(wrapper.resultat.size(), is(1));
    }

    @Test
    public void etSakstemaMedOppfolgingGirEtSakstema() {
        when(kodeverk.getTemanavnForTemakode(Konstanter.DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper<>("Dagpenger"));

        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(SakstemaGrupperer.OPPFOLGING)
                .withAvsluttet(null);

        Sak sak = new Sak()
                .withSaksId("123")
                .withTemakode(Konstanter.DAGPENGER)
                .withAvsluttet(null);

        Map.Entry entry = new AbstractMap.SimpleEntry<String, Set<String>>(Konstanter.TEMAGRUPPE_ARBEID, new HashSet<>(Arrays.asList(Konstanter.DAGPENGER, SakstemaGrupperer.OPPFOLGING)));
        ResultatWrapper<List<Sakstema>> wrapper = sakstemaService.opprettSakstemaForEnTemagruppe(entry, Arrays.asList(oppfolinging, sak), asList(
                new DokumentMetadata()
                        .withTilhorendeSakid("321")
                        .withMottaker(Entitet.SLUTTBRUKER)
                        .withAvsender(Entitet.NAV)
                        .withRetning(Kommunikasjonsretning.UT)
                        .withDato(LocalDateTime.now())
                        .withBaksystem(Baksystem.JOARK)
                        .withHoveddokument(
                                new Dokument()
                                        .withTittel("TEST"))), emptyMap(), true);

        assertThat(wrapper.resultat.get(0).temanavn, equalTo("Dagpenger og oppfølging"));
        assertThat(wrapper.resultat.size(), is(1));
    }

    @Test
    public void sakMedOppfolgingIHenvendelseSkalGrupperesOgFaaTilhorendeMetadata() {
        when(kodeverk.getTemanavnForTemakode(Konstanter.DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper<>("Dagpenger"));
        when(kodeverk.getTemanavnForTemakode(Konstanter.ARBEIDSAVKLARINGSPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper<>("Arbeidsavklaringspenger"));

        Sak sak = new Sak()
                .withSaksId("123")
                .withTemakode(Konstanter.DAGPENGER)
                .withAvsluttet(null);

        Sak sak2 = new Sak()
                .withSaksId("1234")
                .withTemakode(Konstanter.ARBEIDSAVKLARINGSPENGER)
                .withAvsluttet(null);

        Map.Entry entry = new AbstractMap.SimpleEntry<String, Set<String>>(Konstanter.TEMAGRUPPE_ARBEID, new HashSet<>(Arrays.asList(Konstanter.DAGPENGER, SakstemaGrupperer.OPPFOLGING, Konstanter.ARBEIDSAVKLARINGSPENGER)));
        ResultatWrapper<List<Sakstema>> wrapper = sakstemaService.opprettSakstemaForEnTemagruppe(entry, Arrays.asList(sak2, sak), asList(
                new DokumentMetadata()
                        .withMottaker(Entitet.SLUTTBRUKER)
                        .withAvsender(Entitet.NAV)
                        .withRetning(Kommunikasjonsretning.UT)
                        .withDato(LocalDateTime.now())
                        .withBaksystem(Baksystem.HENVENDELSE)
                        .withTemakode("OPP")
                        .withHoveddokument(
                                new Dokument()
                                        .withTittel("Tilhorende Oppfolging"))), emptyMap(), true);

        assertThat(wrapper.resultat.size(), is(2));
    }

    @Test
    public void sakFraSakogBehandlingUtenTilhoerendeSakstemaOppretterEgetSakstema() {
        when(kodeverk.getTemanavnForTemakode(Konstanter.DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper<>("Dagpenger"));
        when(dokumentMetadataService.hentDokumentMetadata(anyString())).thenReturn(new ResultatWrapper<>(emptyList()));

        Map<String, Set<String>> gruppertTema = new HashMap<String, Set<String>>() {{
            put("RESTERENDE_TEMA", new HashSet(asList("DAG")));
        }};

        when(sakstemaGrupperer.grupperSakstema(any(), any(), any())).thenReturn(gruppertTema);

        Map sakOgBehandlingResults = new HashMap<>();
        sakOgBehandlingResults.put("DAG", asList(sakFraSakOgBehandling()));
        when(sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(anyString())).thenReturn(sakOgBehandlingResults);

        ResultatWrapper<List<Sakstema>> listResultatWrapper = sakstemaService.hentSakstema(emptyList(), FNR, false);

        assertThat(listResultatWrapper.resultat.size(), is(1));
        assertThat(listResultatWrapper.resultat.get(0).temakode, is("DAG"));
    }

    @Test
    public void sakFraSakogBehandlingMedTilhoerendeSakstemaOppretterIkkeEgetSakstema() {
        when(kodeverk.getTemanavnForTemakode(Konstanter.DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper<>("Dagpenger"));
        when(dokumentMetadataService.hentDokumentMetadata(anyString())).thenReturn(new ResultatWrapper<>(asList(new DokumentMetadata().withTemakode("DAG").withBaksystem(Baksystem.HENVENDELSE))));
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
        when(kodeverk.getTemanavnForTemakode(Konstanter.DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper<>("Dagpenger"));
        when(kodeverk.getTemanavnForTemakode("FOR", BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper<>("Foreldrepenger"));
        when(dokumentMetadataService.hentDokumentMetadata(anyString())).thenReturn(new ResultatWrapper<>(asList(new DokumentMetadata().withTemakode("FOR").withBaksystem(Baksystem.HENVENDELSE))));

        Map<String, Set<String>> gruppertTema = new HashMap<String, Set<String>>() {{
            put("RESTERENDE_TEMA", new HashSet(asList("FOR", "DAG")));
        }};

        when(sakstemaGrupperer.grupperSakstema(any(), any(), any())).thenReturn(gruppertTema);
        Map sakOgBehandlingResults = new HashMap<>();
        sakOgBehandlingResults.put("DAG", asList(sakFraSakOgBehandling()));
        when(sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(anyString())).thenReturn(sakOgBehandlingResults);

        ResultatWrapper<List<Sakstema>> listResultatWrapper = sakstemaService.hentSakstema(emptyList(), FNR, false);

        assertThat(listResultatWrapper.resultat.size(), is(2));
    }

    private no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak sakFraSakOgBehandling() {
        return SakBuilder.create()
                .withBehandlingskjede(BehandlingskjedeBuilder.create()
                        .withSisteBehandlingAvslutningsstatus(FilterUtils.OPPRETTET)
                        .withSisteBehandlingstype(FilterUtils.SEND_SOKNAD_KVITTERINGSTYPE)
                        .withSisteBehandlingsstatus(FilterUtils.OPPRETTET)
                        .withBehandlingsListeRef("henvendelsesId")
                        .withSisteBehandlingREF("henvendelsesId")
                        .withStart(new DateTime().minusDays(1))
                        .withSlutt(null)
                        .build())
                .build();
    }

    @Test
    public void FlereSakstemaMedOppfolgingGirFlereSakstemaMedOppfolging() {
        when(kodeverk.getTemanavnForTemakode(Konstanter.DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper<>("Dagpenger"));
        when(kodeverk.getTemanavnForTemakode(Konstanter.ARBEIDSAVKLARINGSPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper<>("Arbeidsavklaringspenger"));

        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(SakstemaGrupperer.OPPFOLGING)
                .withAvsluttet(null);

        Sak sak = new Sak()
                .withSaksId("123")
                .withTemakode(Konstanter.DAGPENGER)
                .withAvsluttet(null);

        Sak sak2 = new Sak()
                .withSaksId("122")
                .withTemakode(Konstanter.ARBEIDSAVKLARINGSPENGER)
                .withAvsluttet(null);

        Map.Entry entry = new AbstractMap.SimpleEntry<String, Set<String>>(Konstanter.TEMAGRUPPE_ARBEID, new HashSet<>(Arrays.asList(Konstanter.DAGPENGER, SakstemaGrupperer.OPPFOLGING, Konstanter.ARBEIDSAVKLARINGSPENGER)));
        ResultatWrapper<List<Sakstema>> wrapper = sakstemaService.opprettSakstemaForEnTemagruppe(entry, Arrays.asList(oppfolinging, sak, sak2), asList(
                new DokumentMetadata()
                        .withTilhorendeSakid("321")
                        .withMottaker(Entitet.SLUTTBRUKER)
                        .withAvsender(Entitet.NAV)
                        .withRetning(Kommunikasjonsretning.UT)
                        .withDato(LocalDateTime.now())
                        .withBaksystem(Baksystem.JOARK)
                        .withHoveddokument(
                                new Dokument()
                                        .withTittel("TEST"))), emptyMap(), true);

        assertThat(wrapper.resultat.get(0).temanavn, equalTo("Arbeidsavklaringspenger og oppfølging"));
        assertThat(wrapper.resultat.get(1).temanavn, equalTo("Dagpenger og oppfølging"));
        assertThat(wrapper.resultat.size(), is(2));
    }

    @Test
    public void gruppererNyttTemaFraSogBOmViHarEnOppfolgingssak() {
        when(kodeverk.getTemanavnForTemakode(Konstanter.DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper<>("Dagpenger"));
        when(kodeverk.getTemanavnForTemakode(Konstanter.ARBEIDSAVKLARINGSPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper<>("Arbeidsavklaringspenger"));

        when(dokumentMetadataService.hentDokumentMetadata(anyString()))
                .thenReturn(
                        new ResultatWrapper<>(
                                asList(
                                        new DokumentMetadata()
                                                .withTilhorendeSakid("123")
                                                .withTemakode("AAP")
                                                .withBaksystem(Baksystem.JOARK),
                                        new DokumentMetadata()
                                                .withTilhorendeSakid("456")
                                                .withTemakode("OPP")
                                                .withBaksystem(Baksystem.JOARK))
                        ));

        Map<String, Set<String>> gruppertTema = new HashMap<String, Set<String>>() {{
            put("Arbeid", new HashSet(asList("AAP", "DAG", "OPP")));
        }};

        when(sakstemaGrupperer.grupperSakstema(any(), any(), any())).thenReturn(gruppertTema);

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
    public void slaarSammenSykepengerOgSykemeldingMedOppfolging() {
        when(kodeverk.getTemanavnForTemakode("SYK", BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper<>("Sykepenger"));
        when(kodeverk.getTemanavnForTemakode("SYM", BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper<>("Sykemeldinger"));

        when(dokumentMetadataService.hentDokumentMetadata(anyString()))
                .thenReturn(
                        new ResultatWrapper<>(
                                asList(
                                        new DokumentMetadata()
                                                .withTilhorendeSakid("123")
                                                .withTemakode("SYM")
                                                .withBaksystem(Baksystem.JOARK),
                                        new DokumentMetadata()
                                                .withTilhorendeSakid("456")
                                                .withTemakode("SYK")
                                                .withBaksystem(Baksystem.JOARK),
                                        new DokumentMetadata()
                                                .withTilhorendeSakid("789")
                                                .withTemakode("OPP")
                                                .withBaksystem(Baksystem.JOARK))
                        ));

        Map<String, Set<String>> gruppertTema = new HashMap<String, Set<String>>() {{
            put("Arbeid", new HashSet(asList("SYK", "SYM", "OPP")));
        }};

        when(sakstemaGrupperer.grupperSakstema(any(), any(), any())).thenReturn(gruppertTema);

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
    public void slaarSammenSykepengerOgSykemeldingUtenOppfolging() {
        when(kodeverk.getTemanavnForTemakode("SYK", BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper<>("Sykepenger"));
        when(kodeverk.getTemanavnForTemakode("SYM", BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper<>("Sykemeldinger"));

        when(dokumentMetadataService.hentDokumentMetadata(anyString()))
                .thenReturn(
                        new ResultatWrapper<>(
                                asList(
                                        new DokumentMetadata()
                                                .withTilhorendeSakid("123")
                                                .withTemakode("SYM")
                                                .withBaksystem(Baksystem.JOARK),
                                        new DokumentMetadata()
                                                .withTilhorendeSakid("456")
                                                .withTemakode("SYK")
                                                .withBaksystem(Baksystem.JOARK))
                        ));

        Map<String, Set<String>> gruppertTema = new HashMap<String, Set<String>>() {{
            put(SakstemaGrupperer.TEMAGRUPPE_RESTERENDE_TEMA, new HashSet(asList("SYK", "SYM")));
        }};

        when(sakstemaGrupperer.grupperSakstema(any(), any(), any())).thenReturn(gruppertTema);

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
    public void slaarIkkeSammenSykepengerOgSykemeldingHvisEnErTomtTema() {
        when(kodeverk.getTemanavnForTemakode("SYM", BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper<>("Sykemeldinger"));

        when(dokumentMetadataService.hentDokumentMetadata(anyString()))
                .thenReturn(
                        new ResultatWrapper<>(
                                asList(
                                        new DokumentMetadata()
                                                .withTilhorendeSakid("123")
                                                .withTemakode("SYM")
                                                .withBaksystem(Baksystem.JOARK))
                        ));

        Map<String, Set<String>> gruppertTema = new HashMap<String, Set<String>>() {{
            put(SakstemaGrupperer.TEMAGRUPPE_RESTERENDE_TEMA, new HashSet(asList("SYM")));
        }};

        when(sakstemaGrupperer.grupperSakstema(any(), any(), any())).thenReturn(gruppertTema);

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
    public void slaarIkkeSammenSykepengerOgSykemeldingForModia() {
        when(kodeverk.getTemanavnForTemakode("SYK", BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper<>("Sykepenger"));
        when(kodeverk.getTemanavnForTemakode("SYM", BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper<>("Sykemeldinger"));
        when(kodeverk.getTemanavnForTemakode(SakstemaGrupperer.OPPFOLGING, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper<>("Oppfølging"));

        when(dokumentMetadataService.hentDokumentMetadata(anyString()))
                .thenReturn(
                        new ResultatWrapper<>(
                                asList(
                                        new DokumentMetadata()
                                                .withTilhorendeSakid("123")
                                                .withTemakode("SYK")
                                                .withBaksystem(Baksystem.JOARK),
                                        new DokumentMetadata()
                                                .withTilhorendeSakid("456")
                                                .withTemakode("SYM")
                                                .withBaksystem(Baksystem.JOARK),
                                        new DokumentMetadata()
                                                .withTilhorendeSakid("789")
                                                .withTemakode("OPP")
                                                .withBaksystem(Baksystem.JOARK))
                        ));

        Map<String, Set<String>> gruppertTema = new HashMap<String, Set<String>>() {{
            put(Konstanter.TEMAGRUPPE_ARBEID, new HashSet(asList("SYK", "SYM", "OPP")));
        }};

        when(sakstemaGrupperer.grupperSakstema(any(), any(), any())).thenReturn(gruppertTema);

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
