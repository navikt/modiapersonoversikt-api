package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service;

import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Baksystem;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Behandlingskjede;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Sak;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.utils.Konstanter;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.utils.TemagrupperHenter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SakstemaGruppererTest {

    @Mock
    private TemagrupperHenter temagrupperHenter;

    @InjectMocks
    private SakstemaGrupperer sakstemaGrupperer;

    @Before
    public void setup() {
        when(temagrupperHenter.genererTemagrupperMedTema()).thenReturn(new HashMap<String, List<String>>() {
            {
                put(Konstanter.TEMAGRUPPE_ARBEID, asList(Konstanter.DAGPENGER, SakstemaGrupperer.OPPFOLGING, Konstanter.ARBEIDSAVKLARINGSPENGER));
            }
        });
    }

    @Test
    public void gruppererOppfolgingAleneOmDetManglerTilknyttedeSaker() {

        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(SakstemaGrupperer.OPPFOLGING)
                .withAvsluttet(null);

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(oppfolinging), Arrays.asList(new DokumentMetadata()
                .withTilhorendeSakid("321")
                .withBaksystem(Baksystem.JOARK)), Collections.emptyMap());

        assertFalse(map.containsKey(Konstanter.TEMAGRUPPE_ARBEID));
        assertFalse(map.containsKey(Konstanter.TEMAGRUPPE_FAMILIE));

        assertTrue(map.get(SakstemaGrupperer.TEMAGRUPPE_RESTERENDE_TEMA).contains(SakstemaGrupperer.OPPFOLGING));
    }

    @Test
    public void gruppererIkkeSelvstendigTemaSomTilhorerTemagruppe() {

        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(Konstanter.DAGPENGER)
                .withAvsluttet(null);

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(oppfolinging), asList(), Collections.emptyMap());

        assertFalse(map.containsKey(Konstanter.TEMAGRUPPE_ARBEID));
        assertFalse(map.containsKey(Konstanter.TEMAGRUPPE_FAMILIE));

        assertTrue(map.get(SakstemaGrupperer.TEMAGRUPPE_RESTERENDE_TEMA).contains(Konstanter.DAGPENGER));
    }

    @Test
    public void fjernGrupperingOmDetIkkeFinnesOppfolging() {

        Sak sak = new Sak()
                .withSaksId("321")
                .withTemakode(Konstanter.DAGPENGER)
                .withAvsluttet(null);

        Sak sak1 = new Sak()
                .withSaksId("122")
                .withTemakode(Konstanter.ARBEIDSAVKLARINGSPENGER)
                .withAvsluttet(null);

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(sak, sak1), asList(), Collections.emptyMap());

        assertFalse(map.containsKey(Konstanter.TEMAGRUPPE_ARBEID));
        assertFalse(map.containsKey(Konstanter.TEMAGRUPPE_FAMILIE));

        assertTrue(map.get(SakstemaGrupperer.TEMAGRUPPE_RESTERENDE_TEMA).contains(Konstanter.DAGPENGER));
        assertTrue(map.get(SakstemaGrupperer.TEMAGRUPPE_RESTERENDE_TEMA).contains(Konstanter.ARBEIDSAVKLARINGSPENGER));
    }

    @Test
    public void inneholderIkkeDuplikateTema() {

        Sak sak = new Sak()
                .withSaksId("321")
                .withTemakode(Konstanter.DAGPENGER)
                .withAvsluttet(null);

        Sak sak1 = new Sak()
                .withSaksId("122")
                .withTemakode(Konstanter.DAGPENGER)
                .withAvsluttet(null);

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(sak, sak1), asList(), Collections.emptyMap());

        assertTrue(map.get(SakstemaGrupperer.TEMAGRUPPE_RESTERENDE_TEMA).contains(Konstanter.DAGPENGER));
        assertThat(map.get(SakstemaGrupperer.TEMAGRUPPE_RESTERENDE_TEMA).size(), equalTo(1));
    }

    @Test
    public void temaMedOppfolgingHvorBeggeHarDokumenterBlirGruppert(){
        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(SakstemaGrupperer.OPPFOLGING)
                .withAvsluttet(null);

        Sak sak = new Sak()
                .withSaksId("123")
                .withTemakode(Konstanter.DAGPENGER)
                .withAvsluttet(null);

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(oppfolinging, sak), asList(
                new DokumentMetadata()
                        .withTilhorendeSakid("321")
                        .withBaksystem(Baksystem.JOARK),
                new DokumentMetadata()
                        .withTilhorendeSakid("123")
                        .withBaksystem(Baksystem.JOARK)), Collections.emptyMap());

        assertTrue(map.containsKey(Konstanter.TEMAGRUPPE_ARBEID));
        assertFalse(map.get(SakstemaGrupperer.TEMAGRUPPE_RESTERENDE_TEMA).contains(SakstemaGrupperer.OPPFOLGING));

    }

    @Test
    public void temaMedOppfolgingHvorOppfolgingErTomtTemaBlirIkkeGruppert(){
        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(SakstemaGrupperer.OPPFOLGING)
                .withAvsluttet(null);

        Sak sak = new Sak()
                .withSaksId("123")
                .withTemakode(Konstanter.DAGPENGER)
                .withAvsluttet(null);

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(oppfolinging, sak), asList(
                new DokumentMetadata()
                        .withTilhorendeSakid("123")
                        .withBaksystem(Baksystem.JOARK)), Collections.emptyMap());

        assertFalse(map.containsKey(Konstanter.TEMAGRUPPE_ARBEID));
        assertTrue(map.get(SakstemaGrupperer.TEMAGRUPPE_RESTERENDE_TEMA).contains(SakstemaGrupperer.OPPFOLGING));
        assertTrue(map.get(SakstemaGrupperer.TEMAGRUPPE_RESTERENDE_TEMA).contains(Konstanter.DAGPENGER));
    }

    @Test
    public void temaMedOppfolgingDerTemaErTomtBlirIkkeGruppert(){
        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(SakstemaGrupperer.OPPFOLGING)
                .withAvsluttet(null);

        Sak sak = new Sak()
                .withSaksId("123")
                .withTemakode(Konstanter.DAGPENGER)
                .withAvsluttet(null);

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(oppfolinging, sak), asList(
                new DokumentMetadata()
                        .withTilhorendeSakid("321")
                        .withBaksystem(Baksystem.JOARK)
        ), Collections.emptyMap());

        assertFalse(map.containsKey(Konstanter.TEMAGRUPPE_ARBEID));
        assertTrue(map.get(SakstemaGrupperer.TEMAGRUPPE_RESTERENDE_TEMA).contains(SakstemaGrupperer.OPPFOLGING));
        assertTrue(map.get(SakstemaGrupperer.TEMAGRUPPE_RESTERENDE_TEMA).contains(Konstanter.DAGPENGER));
    }

    @Test
    public void temaMedOppfolgingDerIngenHarDokumenterBlirIkkeGruppert(){
        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(SakstemaGrupperer.OPPFOLGING)
                .withAvsluttet(null);

        Sak sak = new Sak()
                .withSaksId("123")
                .withTemakode(Konstanter.DAGPENGER)
                .withAvsluttet(null);

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(oppfolinging, sak), new ArrayList<>(), Collections.emptyMap());

        assertFalse(map.containsKey(Konstanter.TEMAGRUPPE_ARBEID));
        assertTrue(map.get(SakstemaGrupperer.TEMAGRUPPE_RESTERENDE_TEMA).contains(SakstemaGrupperer.OPPFOLGING));
        assertTrue(map.get(SakstemaGrupperer.TEMAGRUPPE_RESTERENDE_TEMA).contains(Konstanter.DAGPENGER));
    }


    @Test
    public void oppfolingUtenTemaMedDokumenterBlirResterende() {
        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(SakstemaGrupperer.OPPFOLGING)
                .withAvsluttet(null);

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(oppfolinging), asList(new DokumentMetadata().withTilhorendeSakid("321").withBaksystem(Baksystem.JOARK)), Collections.emptyMap());

        assertFalse(map.containsKey(Konstanter.TEMAGRUPPE_ARBEID));
        assertTrue(map.get(SakstemaGrupperer.TEMAGRUPPE_RESTERENDE_TEMA).contains(SakstemaGrupperer.OPPFOLGING));
    }

    @Test
    public void oppfolingMedTemaMedDokumenterBlirGruppert() {
        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(SakstemaGrupperer.OPPFOLGING)
                .withAvsluttet(null);

        Sak sak = new Sak()
                .withSaksId("321")
                .withTemakode(Konstanter.DAGPENGER)
                .withAvsluttet(null);

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(sak, oppfolinging), asList(new DokumentMetadata().withTilhorendeSakid("321").withBaksystem(Baksystem.JOARK)), Collections.emptyMap());

        assertTrue(map.containsKey(Konstanter.TEMAGRUPPE_ARBEID));
        assertTrue(map.get(Konstanter.TEMAGRUPPE_ARBEID).contains(SakstemaGrupperer.OPPFOLGING));
        assertFalse(map.get(SakstemaGrupperer.TEMAGRUPPE_RESTERENDE_TEMA).contains(SakstemaGrupperer.OPPFOLGING));
    }

    @Test
    public void behandlingskjedeUtenGruppeGruppersSomResterende() {
        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(SakstemaGrupperer.OPPFOLGING)
                .withAvsluttet(null);

        Sak sak = new Sak()
                .withSaksId("321")
                .withTemakode(Konstanter.DAGPENGER)
                .withAvsluttet(null);

        Map<String, List<Behandlingskjede>> behandlingskjeder = new HashMap<>();
        behandlingskjeder.put("FOR", asList(new Behandlingskjede()));

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(sak, oppfolinging), asList(
                new DokumentMetadata()
                        .withTilhorendeSakid("321")
                        .withBaksystem(Baksystem.JOARK),
                new DokumentMetadata()
                        .withTilhorendeSakid("123")
                        .withBaksystem(Baksystem.JOARK))
        ,behandlingskjeder);

        assertTrue(map.containsKey(Konstanter.TEMAGRUPPE_ARBEID));
        assertTrue(map.get(Konstanter.TEMAGRUPPE_ARBEID).contains(SakstemaGrupperer.OPPFOLGING));
        assertTrue(map.get(SakstemaGrupperer.TEMAGRUPPE_RESTERENDE_TEMA).contains("FOR"));
    }

    @Test
    public void behandlingskjedeMedGruppeGruppersSomResterende() {
        Sak sak = new Sak()
                .withSaksId("321")
                .withTemakode(Konstanter.DAGPENGER)
                .withAvsluttet(null);

        Map<String, List<Behandlingskjede>> behandlingskjeder = new HashMap<>();
        behandlingskjeder.put("AAP", asList(new Behandlingskjede()));

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(sak), asList(
                new DokumentMetadata()
                        .withTilhorendeSakid("321")
                        .withBaksystem(Baksystem.JOARK))
                ,behandlingskjeder);

        assertFalse(map.containsKey(Konstanter.TEMAGRUPPE_ARBEID));
        assertTrue(map.get(SakstemaGrupperer.TEMAGRUPPE_RESTERENDE_TEMA).contains(Konstanter.ARBEIDSAVKLARINGSPENGER));
        assertTrue(map.get(SakstemaGrupperer.TEMAGRUPPE_RESTERENDE_TEMA).contains(Konstanter.DAGPENGER));
    }

    @Test
    public void behandlingskjedeMedGruppeOgOppfolgingGruppersSomGruppe() {
        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(SakstemaGrupperer.OPPFOLGING)
                .withAvsluttet(null);

        Sak sak = new Sak()
                .withSaksId("321")
                .withTemakode("FOR")
                .withAvsluttet(null);

        Map<String, List<Behandlingskjede>> behandlingskjeder = new HashMap<>();
        behandlingskjeder.put("DAG", asList(new Behandlingskjede()));

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(sak, oppfolinging), asList(
                new DokumentMetadata()
                        .withTilhorendeSakid("321")
                        .withBaksystem(Baksystem.JOARK))
                ,behandlingskjeder);

        assertTrue(map.containsKey(Konstanter.TEMAGRUPPE_ARBEID));
        assertTrue(map.get(Konstanter.TEMAGRUPPE_ARBEID).contains(SakstemaGrupperer.OPPFOLGING));
        assertTrue(map.get(Konstanter.TEMAGRUPPE_ARBEID).contains(Konstanter.DAGPENGER));
        assertTrue(map.get(SakstemaGrupperer.TEMAGRUPPE_RESTERENDE_TEMA).contains("FOR"));
    }

    @Test
    public void oppfolingUtenTemaUtenDokumenterBlirResterende() {
        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(SakstemaGrupperer.OPPFOLGING)
                .withAvsluttet(null);

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(oppfolinging), asList(), Collections.emptyMap());

        assertFalse(map.containsKey(Konstanter.TEMAGRUPPE_ARBEID));
        assertTrue(map.get(SakstemaGrupperer.TEMAGRUPPE_RESTERENDE_TEMA).contains(SakstemaGrupperer.OPPFOLGING));
    }

    @Test
    public void oppfolingMedTemaUtenDokumenterBlirResterende() {
        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(SakstemaGrupperer.OPPFOLGING)
                .withAvsluttet(null);

        Sak sak = new Sak()
                .withSaksId("123")
                .withTemakode(Konstanter.DAGPENGER)
                .withAvsluttet(null);

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(sak, oppfolinging), asList(), Collections.emptyMap());

        assertFalse(map.containsKey(Konstanter.TEMAGRUPPE_ARBEID));
        assertTrue(map.get(SakstemaGrupperer.TEMAGRUPPE_RESTERENDE_TEMA).contains(SakstemaGrupperer.OPPFOLGING));
        assertTrue(map.get(SakstemaGrupperer.TEMAGRUPPE_RESTERENDE_TEMA).contains(Konstanter.DAGPENGER));
    }

    @Test
    public void oppfolgingssakMedDokumenterPaaOppfolgingOgAndreTemaGirGruppertTema() {

        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(SakstemaGrupperer.OPPFOLGING)
                .withAvsluttet(null);

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(oppfolinging), asList(
                new DokumentMetadata()
                        .withTilhorendeSakid(null)
                        .withBaksystem(Baksystem.HENVENDELSE)
                        .withTemakode("DAG"),
                new DokumentMetadata()
                        .withTilhorendeSakid(null)
                        .withBaksystem(Baksystem.HENVENDELSE)
                        .withTemakode("OPP")), Collections.emptyMap());

        assertTrue(map.containsKey(Konstanter.TEMAGRUPPE_ARBEID));
        assertThat(map.get(SakstemaGrupperer.TEMAGRUPPE_RESTERENDE_TEMA).size(),is(0));
        assertThat(map.get(Konstanter.TEMAGRUPPE_ARBEID).size(), is(2));
    }

    @Test
    public void dokumentIHenvendelseUtenSakGirNyttSakstema() {


        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(new ArrayList<>(), asList(new DokumentMetadata()
                .withTilhorendeSakid(null)
                .withBaksystem(Baksystem.HENVENDELSE)
                .withTemakode("DAG")), Collections.emptyMap());

        assertTrue(map.containsKey(SakstemaGrupperer.TEMAGRUPPE_RESTERENDE_TEMA));
        assertThat(map.get(SakstemaGrupperer.TEMAGRUPPE_RESTERENDE_TEMA).size(), is(1));
    }


}
