package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.sbl.dialogarena.common.kodeverk.KodeverkClient;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Dokument;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Kommunikasjonsretning;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sakstema;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Baksystem;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Entitet;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sak;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad;
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
import java.util.concurrent.*;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.Optional.of;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Baksystem.*;
import static no.nav.sbl.dialogarena.saksoversikt.service.service.Filter.SEND_SOKNAD_KVITTERINGSTYPE;
import static no.nav.sbl.dialogarena.saksoversikt.service.service.SakstemaGrupperer.OPPFOLGING;
import static no.nav.sbl.dialogarena.saksoversikt.service.service.SakstemaGrupperer.TEMAGRUPPE_RESTERENDE_TEMA;
import static no.nav.sbl.dialogarena.saksoversikt.service.utils.Konstanter.*;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad.HenvendelseStatus.UNDER_ARBEID;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SaksServiceTest {

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
    private SaksService saksService = new SaksService();

    private static final String FNR = "12345678901";
    @Before
    public void setup() {
        when(request.getSession()).thenReturn(new MockHttpSession());
    }

    @Test
    public void hentPaabegynteSoknader_henterSoknader_medStatusUnderArbeid() {
        when(henvendelseService.hentHenvendelsessoknaderMedStatus(UNDER_ARBEID, "12345678901")).thenReturn(asList(
                new Soknad().withStatus(UNDER_ARBEID).withOpprettetDato(new DateTime()),
                new Soknad().withStatus(UNDER_ARBEID).withOpprettetDato(new DateTime())
        ));
        assertThat(saksService.hentPaabegynteSoknader(FNR).size(), equalTo(2));
    }

    private Sak lagSakMedAvsluttetDato(Optional<DateTime> date) {
        return new Sak().withSaksId("123")
                .withTemakode(DAGPENGER)
                .withAvsluttet(date);
    }

    @Test
    public void lagSakstemaUtenOppfolging() throws HentSakSammendragListeSakManglerEierenhet, HentSakSammendragListePersonIkkeFunnet {
        List dokumentmetadata = new ArrayList<>();

        when(kodeverk.finnesTemaKodeIKodeverk(DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(true);
        when(kodeverk.getTemanavnForTemakode(DAGPENGER, "Tema")).thenReturn("Dagpenger");

        List<Sak> saker = asList(lagSakMedAvsluttetDato(of(now().minusDays(30))));

        when(kodeverk.getTemanavnForTemakode(anyString(), anyString())).thenReturn("Dagpenger");

        when(dokumentMetadataService.hentDokumentMetadata(any(), anyString())).thenReturn(new ResultatWrapper<>(dokumentmetadata, emptySet()));

        when(sakstemaGrupperer.grupperSakstema(saker, dokumentmetadata)).thenReturn(new HashMap<String, Set<String>>() {{
            put("RESTERENDE_TEMA", new HashSet<>(asList(DAGPENGER)));
        }});

        List<Sakstema> sakstema = saksService.hentSakstema(saker, FNR, true).resultat;

        assertTrue(sakstema.size() == 1);
        assertThat(sakstema.get(0).temanavn, equalTo("Dagpenger"));
    }

    @Test
    public void lagSakstemaMedOppfoling() {
        when(kodeverk.getTemanavnForTemakode(DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn("Dagpenger");
        when(kodeverk.finnesTemaKodeIKodeverk(DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(true);

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
        ResultatWrapper<List<Sakstema>> wrapper = saksService.opprettSakstemaForEnTemagruppe(entry, Arrays.asList(sak, oppfolinging), new ArrayList<>(), emptyMap());

        assertThat(wrapper.resultat.get(0).temanavn, equalTo("Dagpenger og oppfølging"));
    }

    @Test
    public void sakstemaMedKunOppfolgingGrupperesIkke() {
        when(kodeverk.getTemanavnForTemakode(OPPFOLGING, BulletproofKodeverkService.ARKIVTEMA)).thenReturn("Oppfølging");
        when(kodeverk.finnesTemaKodeIKodeverk(OPPFOLGING, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(true);

        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(OPPFOLGING)
                .withAvsluttet(null);

        Map.Entry entry = new AbstractMap.SimpleEntry<String, Set<String>>(TEMAGRUPPE_RESTERENDE_TEMA, new HashSet<>(Arrays.asList(OPPFOLGING)));
        ResultatWrapper<List<Sakstema>> wrapper = saksService.opprettSakstemaForEnTemagruppe(entry, Arrays.asList(oppfolinging), asList(
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
        when(kodeverk.getTemanavnForTemakode(OPPFOLGING, BulletproofKodeverkService.ARKIVTEMA)).thenReturn("Oppfølging");
        when(kodeverk.getTemanavnForTemakode(DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn("Dagpenger");
        when(kodeverk.finnesTemaKodeIKodeverk(OPPFOLGING, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(true);
        when(kodeverk.finnesTemaKodeIKodeverk(DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(true);


        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(OPPFOLGING)
                .withAvsluttet(null);

        Sak sak = new Sak()
                .withSaksId("123")
                .withTemakode(DAGPENGER)
                .withAvsluttet(null);

        Map.Entry entry = new AbstractMap.SimpleEntry<String, Set<String>>(TEMAGRUPPE_ARBEID, new HashSet<>(Arrays.asList(DAGPENGER, OPPFOLGING)));
        ResultatWrapper<List<Sakstema>> wrapper = saksService.opprettSakstemaForEnTemagruppe(entry, Arrays.asList(oppfolinging, sak), asList(
                new DokumentMetadata()
                        .withTilhorendeSakid("321")
                        .withMottaker(Entitet.SLUTTBRUKER)
                        .withAvsender(Entitet.NAV)
                        .withRetning(Kommunikasjonsretning.UT)
                        .withDato(LocalDateTime.now())
                        .withBaksystem(JOARK)
                        .withHoveddokument(
                                new Dokument()
                                        .withTittel("TEST"))), emptyMap());

        assertThat(wrapper.resultat.get(0).temanavn, equalTo("Dagpenger og oppfølging"));
        assertThat(wrapper.resultat.size(), is(1));
    }

    @Test
    public void sakMedOppfolgingIHenvendelseSkalGrupperesOgFaaTilhorendeMetadata() {
        when(kodeverk.getTemanavnForTemakode(OPPFOLGING, BulletproofKodeverkService.ARKIVTEMA)).thenReturn("Oppfølging");
        when(kodeverk.getTemanavnForTemakode(DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn("Dagpenger");
        when(kodeverk.getTemanavnForTemakode(ARBEIDSAVKLARINGSPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn("Arbeidsavklaringspenger");
        when(kodeverk.finnesTemaKodeIKodeverk(OPPFOLGING, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(true);
        when(kodeverk.finnesTemaKodeIKodeverk(DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(true);
        when(kodeverk.finnesTemaKodeIKodeverk(ARBEIDSAVKLARINGSPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(true);

        Sak sak = new Sak()
                .withSaksId("123")
                .withTemakode(DAGPENGER)
                .withAvsluttet(null);

        Sak sak2 = new Sak()
                .withSaksId("1234")
                .withTemakode(ARBEIDSAVKLARINGSPENGER)
                .withAvsluttet(null);

        Map.Entry entry = new AbstractMap.SimpleEntry<String, Set<String>>(TEMAGRUPPE_ARBEID, new HashSet<>(Arrays.asList(DAGPENGER, OPPFOLGING, ARBEIDSAVKLARINGSPENGER)));
        ResultatWrapper<List<Sakstema>> wrapper = saksService.opprettSakstemaForEnTemagruppe(entry, Arrays.asList(sak2, sak), asList(
                new DokumentMetadata()
                        .withMottaker(Entitet.SLUTTBRUKER)
                        .withAvsender(Entitet.NAV)
                        .withRetning(Kommunikasjonsretning.UT)
                        .withDato(LocalDateTime.now())
                        .withBaksystem(HENVENDELSE)
                        .withTemakode("OPP")
                        .withHoveddokument(
                                new Dokument()
                                        .withTittel("Tilhorende Oppfolging"))), emptyMap());

        assertThat(wrapper.resultat.size(), is(2));
    }

    @Test
    public void sakFraSakogBehandlingUtenTilhoerendeSakstemaOppretterEgetSakstema() {
        when(kodeverk.getTemanavnForTemakode(DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn("Dagpenger");
        when(dokumentMetadataService.hentDokumentMetadata(any(), anyString())).thenReturn(new ResultatWrapper<>(emptyList()));
        when(sakstemaGrupperer.grupperSakstema(any(), any())).thenReturn(emptyMap());
        Map sakOgBehandlingResults = new HashMap<>();
        sakOgBehandlingResults.put("DAG", asList(sakFraSakOgBehandling()));
        when(sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(anyString())).thenReturn(sakOgBehandlingResults);

        ResultatWrapper<List<Sakstema>> listResultatWrapper = saksService.hentSakstema(emptyList(), FNR, false);

        assertThat(listResultatWrapper.resultat.size(), is(1));
        assertThat(listResultatWrapper.resultat.get(0).temakode, is("DAG"));
    }

    @Test
    public void sakFraSakogBehandlingMedTilhoerendeSakstemaOppretterIkkeEgetSakstema() {
        when(kodeverk.getTemanavnForTemakode(DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn("Dagpenger");
        when(dokumentMetadataService.hentDokumentMetadata(any(), anyString())).thenReturn(new ResultatWrapper<>(asList(new DokumentMetadata().withTemakode("DAG").withBaksystem(HENVENDELSE))));
        Map<String, Set<String>> gruppertTema = new HashMap<>();
        Set set = new HashSet<>();
        set.add("DAG");
        gruppertTema.put("RESTERENDE_TEMA", set);

        when(sakstemaGrupperer.grupperSakstema(any(), any())).thenReturn(gruppertTema);
        Map sakOgBehandlingResults = new HashMap<>();
        sakOgBehandlingResults.put("DAG", asList(sakFraSakOgBehandling()));
        when(sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(anyString())).thenReturn(sakOgBehandlingResults);

        ResultatWrapper<List<Sakstema>> listResultatWrapper = saksService.hentSakstema(emptyList(), FNR, false);

        assertThat(listResultatWrapper.resultat.size(), is(1));
        assertThat(listResultatWrapper.resultat.get(0).temakode, is("DAG"));
    }


    @Test
    public void forskjelligTemakodeSakOgBehandlingOgAnnet() {
        when(kodeverk.getTemanavnForTemakode(DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn("Dagpenger");
        when(kodeverk.getTemanavnForTemakode("FOR", BulletproofKodeverkService.ARKIVTEMA)).thenReturn("Foreldrepenger");
        when(dokumentMetadataService.hentDokumentMetadata(any(), anyString())).thenReturn(new ResultatWrapper<>(asList(new DokumentMetadata().withTemakode("FOR").withBaksystem(HENVENDELSE))));

        Map<String, Set<String>> gruppertTema = new HashMap<>();
        Set set = new HashSet<>();
        set.add("FOR");
        gruppertTema.put("RESTERENDE_TEMA", set);

        when(sakstemaGrupperer.grupperSakstema(any(), any())).thenReturn(gruppertTema);
        Map sakOgBehandlingResults = new HashMap<>();
        sakOgBehandlingResults.put("DAG", asList(sakFraSakOgBehandling()));
        when(sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(anyString())).thenReturn(sakOgBehandlingResults);

        ResultatWrapper<List<Sakstema>> listResultatWrapper = saksService.hentSakstema(emptyList(), FNR, false);

        assertThat(listResultatWrapper.resultat.size(), is(2));
    }

    private WSSak sakFraSakOgBehandling() {
        return new WSSak().withBehandlingskjede(new WSBehandlingskjede()
                .withSisteBehandlingAvslutningsstatus(new WSAvslutningsstatuser().withValue(Filter.OPPRETTET))
                .withSisteBehandlingstype(new WSBehandlingstyper().withValue(SEND_SOKNAD_KVITTERINGSTYPE))
                .withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue(Filter.OPPRETTET))
                .withBehandlingsListeRef("henvendelsesId")
                .withSisteBehandlingREF("henvendelsesId")
                .withStart(new DateTime().minusDays(1))
                .withSlutt(null));
    }

    @Test
    public void FlereSakstemaMedOppfolgingGirFlereSakstemaMedOppfolging() {
        when(kodeverk.getTemanavnForTemakode(OPPFOLGING, BulletproofKodeverkService.ARKIVTEMA)).thenReturn("Oppfølging");
        when(kodeverk.getTemanavnForTemakode(DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn("Dagpenger");
        when(kodeverk.getTemanavnForTemakode(ARBEIDSAVKLARINGSPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn("Arbeidsavklaringspenger");
        when(kodeverk.finnesTemaKodeIKodeverk(OPPFOLGING, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(true);
        when(kodeverk.finnesTemaKodeIKodeverk(DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(true);
        when(kodeverk.finnesTemaKodeIKodeverk(ARBEIDSAVKLARINGSPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(true);

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
        ResultatWrapper<List<Sakstema>> wrapper = saksService.opprettSakstemaForEnTemagruppe(entry, Arrays.asList(oppfolinging, sak, sak2), asList(
                new DokumentMetadata()
                        .withTilhorendeSakid("321")
                        .withMottaker(Entitet.SLUTTBRUKER)
                        .withAvsender(Entitet.NAV)
                        .withRetning(Kommunikasjonsretning.UT)
                        .withDato(LocalDateTime.now())
                        .withBaksystem(JOARK)
                        .withHoveddokument(
                                new Dokument()
                                        .withTittel("TEST"))), emptyMap());

        assertThat(wrapper.resultat.get(0).temanavn, equalTo("Arbeidsavklaringspenger og oppfølging"));
        assertThat(wrapper.resultat.get(1).temanavn, equalTo("Dagpenger og oppfølging"));
        assertThat(wrapper.resultat.size(), is(2));
    }

    private Future<Object> getFuturePesys() {
        return new Future<Object>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return false;
            }

            @Override
            public Stream<Sak> get() throws InterruptedException, ExecutionException {
                return Stream.empty();
            }

            @Override
            public Stream<Sak> get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return null;
            }
        };
    }
}
