package no.nav.modiapersonoversikt.legacy.sak.service;

import no.nav.modiapersonoversikt.legacy.sak.BehandlingskjedeBuilder;
import no.nav.modiapersonoversikt.legacy.sak.SakBuilder;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.*;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.modiapersonoversikt.legacy.sak.service.filter.FilterUtils;
import no.nav.modiapersonoversikt.legacy.sak.service.saf.SafService;
import no.nav.modiapersonoversikt.legacy.sak.utils.Konstanter;
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk;
import org.joda.time.DateTime;
import org.junit.Before;
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
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
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
    private EnhetligKodeverk.Service kodeverk;

    @Mock
    private SafService safService;

    @InjectMocks
    private SakstemaService sakstemaService = new SakstemaService();

    private static final String FNR = "11111111111";

    @Before
    public void setup() {
        when(kodeverk.hentKodeverk(any())).thenReturn(new EnhetligKodeverk.Kodeverk<>("DUMMY", new HashMap<>(){{
            put("DAG", "Dagpenger");
            put("AAP", "Arbeidsavklaringspenger");
            put("OPP", "Oppfølging");
            put("FOR", "Foreldrepenger");
            put("SYK", "Sykepenger");
            put("SYM", "Sykemeldinger");
        }}));
    }

    @Test
    public void lagSakstemaMedOppfoling() {
        Sak sak = new Sak()
                .withSaksId("123")
                .withTemakode(Konstanter.DAGPENGER)
                .withAvsluttet(null);

        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(Konstanter.OPPFOLGING)
                .withAvsluttet(null);

        Set<String> temakoder = new HashSet<>(Arrays.asList(Konstanter.DAGPENGER, Konstanter.OPPFOLGING));
        ResultatWrapper<List<Sakstema>> wrapper = sakstemaService.opprettSakstemaForEnTemagruppe(temakoder, Arrays.asList(sak, oppfolinging), new ArrayList<>(), emptyMap());

        assertThat(wrapper.resultat.get(0).temanavn, equalTo("Dagpenger"));
        assertThat(wrapper.resultat.get(1).temanavn, equalTo("Oppfølging"));
        assertThat(wrapper.resultat.size(), is(2));
    }

    @Test
    public void sakstemaMedKunOppfolgingGrupperesIkke() {
        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(Konstanter.OPPFOLGING)
                .withAvsluttet(null);

        Set<String> temakoder = new HashSet<>(Arrays.asList(Konstanter.OPPFOLGING));
        ResultatWrapper<List<Sakstema>> wrapper = sakstemaService.opprettSakstemaForEnTemagruppe(temakoder, Arrays.asList(oppfolinging), Arrays.asList(
                new DokumentMetadata()
                        .withTilhorendeSakid("321")
                        .withMottaker(Entitet.SLUTTBRUKER)
                        .withAvsender(Entitet.NAV)
                        .withRetning(Kommunikasjonsretning.UT)
                        .withDato(LocalDateTime.now())
                        .withHoveddokument(
                                new Dokument()
                                        .withTittel("TEST"))), emptyMap());

        assertThat(wrapper.resultat.get(0).temanavn, equalTo("Oppfølging"));
        assertThat(wrapper.resultat.size(), is(1));
    }

    @Test
    public void etSakstemaMedOppfolgingGirEtSakstema() {
        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(Konstanter.OPPFOLGING)
                .withAvsluttet(null);

        Sak sak = new Sak()
                .withSaksId("123")
                .withTemakode(Konstanter.DAGPENGER)
                .withAvsluttet(null);

        HashSet<String> temakoder = new HashSet<>(asList(Konstanter.DAGPENGER, Konstanter.OPPFOLGING));
        ResultatWrapper<List<Sakstema>> wrapper = sakstemaService.opprettSakstemaForEnTemagruppe(temakoder, Arrays.asList(oppfolinging, sak), asList(
                new DokumentMetadata()
                        .withTilhorendeSakid("321")
                        .withMottaker(Entitet.SLUTTBRUKER)
                        .withAvsender(Entitet.NAV)
                        .withRetning(Kommunikasjonsretning.UT)
                        .withDato(LocalDateTime.now())
                        .withBaksystem(Baksystem.JOARK)
                        .withHoveddokument(
                                new Dokument()
                                        .withTittel("TEST"))), emptyMap());

        assertThat(wrapper.resultat.get(0).temanavn, equalTo("Dagpenger"));
        assertThat(wrapper.resultat.get(1).temanavn, equalTo("Oppfølging"));
        assertThat(wrapper.resultat.size(), is(2));
    }

    @Test
    public void sakMedOppfolgingIHenvendelseSkalGrupperesOgFaaTilhorendeMetadata() {
        Sak sak = new Sak()
                .withSaksId("123")
                .withTemakode(Konstanter.DAGPENGER)
                .withAvsluttet(null);

        Sak sak2 = new Sak()
                .withSaksId("1234")
                .withTemakode(Konstanter.ARBEIDSAVKLARINGSPENGER)
                .withAvsluttet(null);

        HashSet<String> temakoder = new HashSet<>(asList(Konstanter.DAGPENGER, Konstanter.OPPFOLGING, Konstanter.ARBEIDSAVKLARINGSPENGER));
        ResultatWrapper<List<Sakstema>> wrapper = sakstemaService.opprettSakstemaForEnTemagruppe(temakoder, Arrays.asList(sak2, sak), asList(
                new DokumentMetadata()
                        .withMottaker(Entitet.SLUTTBRUKER)
                        .withAvsender(Entitet.NAV)
                        .withRetning(Kommunikasjonsretning.UT)
                        .withDato(LocalDateTime.now())
                        .withBaksystem(Baksystem.HENVENDELSE)
                        .withTemakode("OPP")
                        .withHoveddokument(
                                new Dokument()
                                        .withTittel("Tilhorende Oppfolging"))), emptyMap());

        assertThat(wrapper.resultat.get(0).temanavn, equalTo("Arbeidsavklaringspenger"));
        assertThat(wrapper.resultat.get(1).temanavn, equalTo("Dagpenger"));
        assertThat(wrapper.resultat.get(2).temanavn, equalTo("Oppfølging"));
        assertThat(wrapper.resultat.size(), is(3));
    }

    @Test
    public void sakFraSakogBehandlingUtenTilhoerendeSakstemaOppretterEgetSakstema() {
        when(safService.hentJournalposter(anyString())).thenReturn(new ResultatWrapper<>(emptyList()));

        Map<String, Set<String>> gruppertTema = new HashMap<String, Set<String>>() {{
            put("RESTERENDE_TEMA", new HashSet(asList("DAG")));
        }};

        Map sakOgBehandlingResults = new HashMap<>();
        sakOgBehandlingResults.put("DAG", asList(sakFraSakOgBehandling()));
        when(sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(anyString())).thenReturn(sakOgBehandlingResults);

        ResultatWrapper<List<Sakstema>> listResultatWrapper = sakstemaService.hentSakstema(emptyList(), FNR);

        assertThat(listResultatWrapper.resultat.size(), is(1));
        assertThat(listResultatWrapper.resultat.get(0).temakode, is("DAG"));
    }

    @Test
    public void sakFraSakogBehandlingMedTilhoerendeSakstemaOppretterIkkeEgetSakstema() {
        when(safService.hentJournalposter(anyString())).thenReturn(new ResultatWrapper<>(asList(new DokumentMetadata().withTemakode("DAG").withBaksystem(Baksystem.HENVENDELSE))));
        Map<String, Set<String>> gruppertTema = new HashMap<>();
        Set set = new HashSet<>();
        set.add("DAG");
        gruppertTema.put("RESTERENDE_TEMA", set);

        Map sakOgBehandlingResults = new HashMap<>();
        sakOgBehandlingResults.put("DAG", asList(sakFraSakOgBehandling()));
        when(sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(anyString())).thenReturn(sakOgBehandlingResults);

        ResultatWrapper<List<Sakstema>> listResultatWrapper = sakstemaService.hentSakstema(emptyList(), FNR);

        assertThat(listResultatWrapper.resultat.size(), is(1));
        assertThat(listResultatWrapper.resultat.get(0).temakode, is("DAG"));
    }


    @Test
    public void forskjelligTemakodeSakOgBehandlingOgAnnet() {
        when(safService.hentJournalposter(anyString())).thenReturn(new ResultatWrapper<>(asList(new DokumentMetadata().withTemakode("FOR").withBaksystem(Baksystem.HENVENDELSE))));

        Map<String, Set<String>> gruppertTema = new HashMap<String, Set<String>>() {{
            put("RESTERENDE_TEMA", new HashSet(asList("FOR", "DAG")));
        }};

        Map sakOgBehandlingResults = new HashMap<>();
        sakOgBehandlingResults.put("DAG", asList(sakFraSakOgBehandling()));
        when(sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(anyString())).thenReturn(sakOgBehandlingResults);

        ResultatWrapper<List<Sakstema>> listResultatWrapper = sakstemaService.hentSakstema(emptyList(), FNR);

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
        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(Konstanter.OPPFOLGING)
                .withAvsluttet(null);

        Sak sak = new Sak()
                .withSaksId("123")
                .withTemakode(Konstanter.DAGPENGER)
                .withAvsluttet(null);

        Sak sak2 = new Sak()
                .withSaksId("122")
                .withTemakode(Konstanter.ARBEIDSAVKLARINGSPENGER)
                .withAvsluttet(null);

        HashSet<String> temakoder = new HashSet<>(asList(Konstanter.DAGPENGER, Konstanter.OPPFOLGING, Konstanter.ARBEIDSAVKLARINGSPENGER));
        ResultatWrapper<List<Sakstema>> wrapper = sakstemaService.opprettSakstemaForEnTemagruppe(temakoder, Arrays.asList(oppfolinging, sak, sak2), asList(
                new DokumentMetadata()
                        .withTilhorendeSakid("321")
                        .withMottaker(Entitet.SLUTTBRUKER)
                        .withAvsender(Entitet.NAV)
                        .withRetning(Kommunikasjonsretning.UT)
                        .withDato(LocalDateTime.now())
                        .withBaksystem(Baksystem.JOARK)
                        .withHoveddokument(
                                new Dokument()
                                        .withTittel("TEST"))), emptyMap());

        assertThat(wrapper.resultat.get(0).temanavn, equalTo("Arbeidsavklaringspenger"));
        assertThat(wrapper.resultat.get(1).temanavn, equalTo("Dagpenger"));
        assertThat(wrapper.resultat.get(2).temanavn, equalTo("Oppfølging"));
        assertThat(wrapper.resultat.size(), is(3));
    }

    @Test
    public void slaarIkkeSammenSykepengerOgSykemeldingForModia() {
        when(safService.hentJournalposter(anyString()))
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

        ResultatWrapper<List<Sakstema>> listResultatWrapper = sakstemaService.hentSakstema(saker, FNR);

        assertThat(listResultatWrapper.resultat.size(), is(3));
        assertThat(listResultatWrapper.resultat.get(0).temanavn, is("Sykepenger"));
    }

}
