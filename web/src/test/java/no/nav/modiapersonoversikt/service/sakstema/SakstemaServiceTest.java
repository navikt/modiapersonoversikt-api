package no.nav.modiapersonoversikt.service.sakstema;

import kotlin.Pair;
import no.nav.modiapersonoversikt.commondomain.sak.ResultatWrapper;
import no.nav.modiapersonoversikt.commondomain.sak.Baksystem;
import no.nav.modiapersonoversikt.commondomain.sak.Entitet;
import no.nav.modiapersonoversikt.service.saf.domain.Dokument;
import no.nav.modiapersonoversikt.service.saf.domain.DokumentMetadata;
import no.nav.modiapersonoversikt.service.saf.domain.Kommunikasjonsretning;
import no.nav.modiapersonoversikt.service.sakogbehandling.SakOgBehandlingService;
import no.nav.modiapersonoversikt.service.sakstema.domain.BehandlingsStatus;
import no.nav.modiapersonoversikt.service.sakstema.domain.Sak;
import no.nav.modiapersonoversikt.service.sakstema.domain.Sakstema;
import no.nav.modiapersonoversikt.service.saf.SafService;
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk;
import no.nav.modiapersonoversikt.service.sakstema.domain.Behandlingskjede;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.*;
import static org.hamcrest.CoreMatchers.*;
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
                .withAvsluttet(Optional.empty());

        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(Konstanter.OPPFOLGING)
                .withAvsluttet(Optional.empty());

        Set<String> temakoder = new HashSet<>(List.of(Konstanter.DAGPENGER, Konstanter.OPPFOLGING));
        ResultatWrapper<List<Sakstema>> wrapper = sakstemaService.opprettSakstemaForEnTemagruppe(temakoder, List.of(sak, oppfolinging), new ArrayList<>(), emptyMap());

        assertThat(wrapper.resultat.get(0).temanavn, equalTo("Dagpenger"));
        assertThat(wrapper.resultat.get(1).temanavn, equalTo("Oppfølging"));
        assertThat(wrapper.resultat.size(), is(2));
    }

    @Test
    public void sakstemaMedKunOppfolgingGrupperesIkke() {
        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(Konstanter.OPPFOLGING)
                .withAvsluttet(Optional.empty());

        Set<String> temakoder = new HashSet<>(List.of(Konstanter.OPPFOLGING));
        ResultatWrapper<List<Sakstema>> wrapper = sakstemaService.opprettSakstemaForEnTemagruppe(temakoder, List.of(oppfolinging), List.of(
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
                .withAvsluttet(Optional.empty());

        Sak sak = new Sak()
                .withSaksId("123")
                .withTemakode(Konstanter.DAGPENGER)
                .withAvsluttet(Optional.empty());

        HashSet<String> temakoder = new HashSet<>(List.of(Konstanter.DAGPENGER, Konstanter.OPPFOLGING));
        ResultatWrapper<List<Sakstema>> wrapper = sakstemaService.opprettSakstemaForEnTemagruppe(temakoder, List.of(oppfolinging, sak), List.of(
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
                .withAvsluttet(Optional.empty());

        Sak sak2 = new Sak()
                .withSaksId("1234")
                .withTemakode(Konstanter.ARBEIDSAVKLARINGSPENGER)
                .withAvsluttet(Optional.empty());

        HashSet<String> temakoder = new HashSet<>(List.of(Konstanter.DAGPENGER, Konstanter.OPPFOLGING, Konstanter.ARBEIDSAVKLARINGSPENGER));
        ResultatWrapper<List<Sakstema>> wrapper = sakstemaService.opprettSakstemaForEnTemagruppe(temakoder, List.of(sak2, sak), List.of(
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
        when(sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(any())).thenReturn(
                new HashMap<>(){{
                    put("DAG", List.of(
                            new Behandlingskjede()
                                    .withStatus(BehandlingsStatus.FERDIG_BEHANDLET)
                                    .withSistOppdatert(LocalDateTime.now())
                    ));
                }}
        );


        ResultatWrapper<List<Sakstema>> listResultatWrapper = sakstemaService.hentSakstema(emptyList(), FNR);

        assertThat(listResultatWrapper.resultat.size(), is(1));
        assertThat(listResultatWrapper.resultat.get(0).temakode, is("DAG"));
    }

    @Test
    public void sakFraSakogBehandlingMedTilhoerendeSakstemaOppretterIkkeEgetSakstema() {
        when(safService.hentJournalposter(anyString())).thenReturn(new ResultatWrapper<>(List.of(new DokumentMetadata().withTemakode("DAG").withBaksystem(Baksystem.HENVENDELSE))));

        ResultatWrapper<List<Sakstema>> listResultatWrapper = sakstemaService.hentSakstema(emptyList(), FNR);

        assertThat(listResultatWrapper.resultat.size(), is(1));
        assertThat(listResultatWrapper.resultat.get(0).temakode, is("DAG"));
    }


    @Test
    public void forskjelligTemakodeSakOgBehandlingOgAnnet() {
        when(safService.hentJournalposter(anyString())).thenReturn(new ResultatWrapper<>(List.of(new DokumentMetadata().withTemakode("FOR").withBaksystem(Baksystem.HENVENDELSE))));
        when(sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(any())).thenReturn(
                new HashMap<>(){{
                    put("DAG", List.of(
                            new Behandlingskjede()
                                    .withStatus(BehandlingsStatus.FERDIG_BEHANDLET)
                                    .withSistOppdatert(LocalDateTime.now())
                    ));
                }}
        );

        ResultatWrapper<List<Sakstema>> listResultatWrapper = sakstemaService.hentSakstema(emptyList(), FNR);

        assertThat(listResultatWrapper.resultat.size(), is(2));
    }

    @Test
    public void FlereSakstemaMedOppfolgingGirFlereSakstemaMedOppfolging() {
        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(Konstanter.OPPFOLGING)
                .withAvsluttet(Optional.empty());

        Sak sak = new Sak()
                .withSaksId("123")
                .withTemakode(Konstanter.DAGPENGER)
                .withAvsluttet(Optional.empty());

        Sak sak2 = new Sak()
                .withSaksId("122")
                .withTemakode(Konstanter.ARBEIDSAVKLARINGSPENGER)
                .withAvsluttet(Optional.empty());

        HashSet<String> temakoder = new HashSet<>(List.of(Konstanter.DAGPENGER, Konstanter.OPPFOLGING, Konstanter.ARBEIDSAVKLARINGSPENGER));
        ResultatWrapper<List<Sakstema>> wrapper = sakstemaService.opprettSakstemaForEnTemagruppe(temakoder, List.of(oppfolinging, sak, sak2), List.of(
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
                                List.of(
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

        List<Sak> saker = List.of(
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

        ResultatWrapper<List<Sakstema>> listResultatWrapper = sakstemaService.hentSakstema(saker, FNR);

        assertThat(listResultatWrapper.resultat.size(), is(3));
        assertThat(listResultatWrapper.resultat.get(0).temanavn, is("Sykepenger"));
    }

    @Test
    public void gruppererTemaFraSakerDokumentMetadataOgBehandlingskjeder() {
        Set<String> temakoder = SakstemaService.Companion.hentAlleTema(
                lagSaker(Konstanter.DAGPENGER, Konstanter.KONTROLL),
                lagDokument(List.of(Konstanter.OPPFOLGING, Konstanter.ARBEIDSAVKLARINGSPENGER), List.of("KNA", "IND")),
                lagBehandlingskjeder(Konstanter.FORELDREPENGER, Konstanter.DAGPENGER, Konstanter.OPPFOLGING)
        );

        assertThat(temakoder, hasItems(
                Konstanter.DAGPENGER,
                Konstanter.KONTROLL,
                Konstanter.OPPFOLGING,
                Konstanter.ARBEIDSAVKLARINGSPENGER,
                Konstanter.FORELDREPENGER
        ));

        assertThat(temakoder, not(hasItems(
                "KNA",
                "IND"
        )));

        assertThat(temakoder.size(), is(5));
    }

    private static List<Sak> lagSaker(String... temakoder) {
        return Stream.of(temakoder)
                .map(temakode -> new Sak().withTemakode(temakode))
                .collect(Collectors.toList());
    }

    private static List<DokumentMetadata> lagDokument(List<String> henvendelseTemakoder, List<String> joarkTemakoder) {
        Stream<DokumentMetadata> henvendelseDokument = henvendelseTemakoder
                .stream()
                .map(temakode -> new DokumentMetadata()
                        .withTemakode(temakode)
                        .withBaksystem(Baksystem.HENVENDELSE)
                );
        Stream<DokumentMetadata> joarkDokument = joarkTemakoder
                .stream()
                .map(temakode -> new DokumentMetadata()
                        .withTemakode(temakode)
                        .withBaksystem(Baksystem.JOARK)
                );

        return Stream.concat(henvendelseDokument, joarkDokument).collect(Collectors.toList());
    }

    private static Map<String, List<Behandlingskjede>> lagBehandlingskjeder(String... temakoder) {
        return Stream.of(temakoder)
                .map(temakode -> new Pair<String, List<Behandlingskjede>>(temakode, emptyList()))
                .collect(Collectors.toMap(Pair::component1, Pair::component2));
    }

}
