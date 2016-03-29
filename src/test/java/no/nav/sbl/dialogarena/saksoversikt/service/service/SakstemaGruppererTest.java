package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Behandlingskjede;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.saksoversikt.service.utils.TemagrupperHenter;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sak;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.saksoversikt.service.service.SakstemaGrupperer.*;
import static no.nav.sbl.dialogarena.saksoversikt.service.utils.Konstanter.*;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Baksystem.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
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
                put(TEMAGRUPPE_ARBEID, asList(DAGPENGER, OPPFOLGING, ARBEIDSAVKLARINGSPENGER));
            }
        });
    }

    @Test
    public void gruppererOppfolgingAleneOmDetManglerTilknyttedeSaker() throws Exception {

        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(OPPFOLGING)
                .withAvsluttet(null);

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(oppfolinging), asList(new DokumentMetadata()
                .withTilhorendeSakid("321")
                .withBaksystem(JOARK)), Collections.emptyMap());

        assertFalse(map.containsKey(TEMAGRUPPE_ARBEID));
        assertFalse(map.containsKey(TEMAGRUPPE_FAMILIE));

        assertTrue(map.get(TEMAGRUPPE_RESTERENDE_TEMA).contains(OPPFOLGING));
    }

    @Test
    public void gruppererIkkeSelvstendigTemaSomTilhorerTemagruppe() throws Exception {

        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(DAGPENGER)
                .withAvsluttet(null);

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(oppfolinging), asList(), Collections.emptyMap());

        assertFalse(map.containsKey(TEMAGRUPPE_ARBEID));
        assertFalse(map.containsKey(TEMAGRUPPE_FAMILIE));

        assertTrue(map.get(TEMAGRUPPE_RESTERENDE_TEMA).contains(DAGPENGER));
    }

    @Test
    public void fjernGrupperingOmDetIkkeFinnesOppfolging() throws Exception {

        Sak sak = new Sak()
                .withSaksId("321")
                .withTemakode(DAGPENGER)
                .withAvsluttet(null);

        Sak sak1 = new Sak()
                .withSaksId("122")
                .withTemakode(ARBEIDSAVKLARINGSPENGER)
                .withAvsluttet(null);

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(sak, sak1), asList(), Collections.emptyMap());

        assertFalse(map.containsKey(TEMAGRUPPE_ARBEID));
        assertFalse(map.containsKey(TEMAGRUPPE_FAMILIE));

        assertTrue(map.get(TEMAGRUPPE_RESTERENDE_TEMA).contains(DAGPENGER));
        assertTrue(map.get(TEMAGRUPPE_RESTERENDE_TEMA).contains(ARBEIDSAVKLARINGSPENGER));
    }

    @Test
    public void inneholderIkkeDuplikateTema() {

        Sak sak = new Sak()
                .withSaksId("321")
                .withTemakode(DAGPENGER)
                .withAvsluttet(null);

        Sak sak1 = new Sak()
                .withSaksId("122")
                .withTemakode(DAGPENGER)
                .withAvsluttet(null);

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(sak, sak1), asList(), Collections.emptyMap());

        assertTrue(map.get(TEMAGRUPPE_RESTERENDE_TEMA).contains(DAGPENGER));
        assertThat(map.get(TEMAGRUPPE_RESTERENDE_TEMA).size(), equalTo(1));
    }

    @Test
    public void temaMedOppfolgingHvorBeggeHarDokumenterBlirGruppert(){
        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(OPPFOLGING)
                .withAvsluttet(null);

        Sak sak = new Sak()
                .withSaksId("123")
                .withTemakode(DAGPENGER)
                .withAvsluttet(null);

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(oppfolinging, sak), asList(
                new DokumentMetadata()
                        .withTilhorendeSakid("321")
                        .withBaksystem(JOARK),
                new DokumentMetadata()
                        .withTilhorendeSakid("123")
                        .withBaksystem(JOARK)), Collections.emptyMap());

        assertTrue(map.containsKey(TEMAGRUPPE_ARBEID));
        assertFalse(map.get(TEMAGRUPPE_RESTERENDE_TEMA).contains(OPPFOLGING));

    }

    @Test
    public void temaMedOppfolgingHvorOppfolgingErTomtTemaBlirIkkeGruppert(){
        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(OPPFOLGING)
                .withAvsluttet(null);

        Sak sak = new Sak()
                .withSaksId("123")
                .withTemakode(DAGPENGER)
                .withAvsluttet(null);

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(oppfolinging, sak), asList(
                new DokumentMetadata()
                        .withTilhorendeSakid("123")
                        .withBaksystem(JOARK)), Collections.emptyMap());

        assertFalse(map.containsKey(TEMAGRUPPE_ARBEID));
        assertTrue(map.get(TEMAGRUPPE_RESTERENDE_TEMA).contains(OPPFOLGING));
        assertTrue(map.get(TEMAGRUPPE_RESTERENDE_TEMA).contains(DAGPENGER));
    }

    @Test
    public void temaMedOppfolgingDerTemaErTomtBlirIkkeGruppert(){
        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(OPPFOLGING)
                .withAvsluttet(null);

        Sak sak = new Sak()
                .withSaksId("123")
                .withTemakode(DAGPENGER)
                .withAvsluttet(null);

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(oppfolinging, sak), asList(
                new DokumentMetadata()
                        .withTilhorendeSakid("321")
                        .withBaksystem(JOARK)
        ), Collections.emptyMap());

        assertFalse(map.containsKey(TEMAGRUPPE_ARBEID));
        assertTrue(map.get(TEMAGRUPPE_RESTERENDE_TEMA).contains(OPPFOLGING));
        assertTrue(map.get(TEMAGRUPPE_RESTERENDE_TEMA).contains(DAGPENGER));
    }

    @Test
    public void temaMedOppfolgingDerIngenHarDokumenterBlirIkkeGruppert(){
        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(OPPFOLGING)
                .withAvsluttet(null);

        Sak sak = new Sak()
                .withSaksId("123")
                .withTemakode(DAGPENGER)
                .withAvsluttet(null);

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(oppfolinging, sak), new ArrayList<>(), Collections.emptyMap());

        assertFalse(map.containsKey(TEMAGRUPPE_ARBEID));
        assertTrue(map.get(TEMAGRUPPE_RESTERENDE_TEMA).contains(OPPFOLGING));
        assertTrue(map.get(TEMAGRUPPE_RESTERENDE_TEMA).contains(DAGPENGER));
    }


    @Test
    public void oppfolingUtenTemaMedDokumenterBlirResterende() {
        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(OPPFOLGING)
                .withAvsluttet(null);

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(oppfolinging), asList(new DokumentMetadata().withTilhorendeSakid("321").withBaksystem(JOARK)), Collections.emptyMap());

        assertFalse(map.containsKey(TEMAGRUPPE_ARBEID));
        assertTrue(map.get(TEMAGRUPPE_RESTERENDE_TEMA).contains(OPPFOLGING));
    }

    @Test
    public void oppfolingMedTemaMedDokumenterBlirGruppert() {
        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(OPPFOLGING)
                .withAvsluttet(null);

        Sak sak = new Sak()
                .withSaksId("321")
                .withTemakode(DAGPENGER)
                .withAvsluttet(null);

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(sak, oppfolinging), asList(new DokumentMetadata().withTilhorendeSakid("321").withBaksystem(JOARK)), Collections.emptyMap());

        assertTrue(map.containsKey(TEMAGRUPPE_ARBEID));
        assertTrue(map.get(TEMAGRUPPE_ARBEID).contains(OPPFOLGING));
        assertFalse(map.get(TEMAGRUPPE_RESTERENDE_TEMA).contains(OPPFOLGING));
    }

    @Test
    public void behandlingskjedeUtenGruppeGruppersSomResterende() {
        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(OPPFOLGING)
                .withAvsluttet(null);

        Sak sak = new Sak()
                .withSaksId("321")
                .withTemakode(DAGPENGER)
                .withAvsluttet(null);

        Map<String, List<Behandlingskjede>> behandlingskjeder = new HashMap<>();
        behandlingskjeder.put("FOR", asList(new Behandlingskjede()));

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(sak, oppfolinging), asList(
                new DokumentMetadata()
                        .withTilhorendeSakid("321")
                        .withBaksystem(JOARK),
                new DokumentMetadata()
                        .withTilhorendeSakid("123")
                        .withBaksystem(JOARK))
        ,behandlingskjeder);

        assertTrue(map.containsKey(TEMAGRUPPE_ARBEID));
        assertTrue(map.get(TEMAGRUPPE_ARBEID).contains(OPPFOLGING));
        assertTrue(map.get(TEMAGRUPPE_RESTERENDE_TEMA).contains("FOR"));
    }

    @Test
    public void behandlingskjedeMedGruppeGruppersSomResterende() {
        Sak sak = new Sak()
                .withSaksId("321")
                .withTemakode(DAGPENGER)
                .withAvsluttet(null);

        Map<String, List<Behandlingskjede>> behandlingskjeder = new HashMap<>();
        behandlingskjeder.put("AAP", asList(new Behandlingskjede()));

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(sak), asList(
                new DokumentMetadata()
                        .withTilhorendeSakid("321")
                        .withBaksystem(JOARK))
                ,behandlingskjeder);

        assertFalse(map.containsKey(TEMAGRUPPE_ARBEID));
        assertTrue(map.get(TEMAGRUPPE_RESTERENDE_TEMA).contains(ARBEIDSAVKLARINGSPENGER));
        assertTrue(map.get(TEMAGRUPPE_RESTERENDE_TEMA).contains(DAGPENGER));
    }

    @Test
    public void behandlingskjedeMedGruppeOgOppfolgingGruppersSomGruppe() {
        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(OPPFOLGING)
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
                        .withBaksystem(JOARK))
                ,behandlingskjeder);

        assertTrue(map.containsKey(TEMAGRUPPE_ARBEID));
        assertTrue(map.get(TEMAGRUPPE_ARBEID).contains(OPPFOLGING));
        assertTrue(map.get(TEMAGRUPPE_ARBEID).contains(DAGPENGER));
        assertTrue(map.get(TEMAGRUPPE_RESTERENDE_TEMA).contains("FOR"));
    }

    @Test
    public void oppfolingUtenTemaUtenDokumenterBlirResterende() {
        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(OPPFOLGING)
                .withAvsluttet(null);

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(oppfolinging), asList(), Collections.emptyMap());

        assertFalse(map.containsKey(TEMAGRUPPE_ARBEID));
        assertTrue(map.get(TEMAGRUPPE_RESTERENDE_TEMA).contains(OPPFOLGING));
    }

    @Test
    public void oppfolingMedTemaUtenDokumenterBlirResterende() {
        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(OPPFOLGING)
                .withAvsluttet(null);

        Sak sak = new Sak()
                .withSaksId("123")
                .withTemakode(DAGPENGER)
                .withAvsluttet(null);

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(sak, oppfolinging), asList(), Collections.emptyMap());

        assertFalse(map.containsKey(TEMAGRUPPE_ARBEID));
        assertTrue(map.get(TEMAGRUPPE_RESTERENDE_TEMA).contains(OPPFOLGING));
        assertTrue(map.get(TEMAGRUPPE_RESTERENDE_TEMA).contains(DAGPENGER));
    }

    @Test
    public void oppfolgingssakMedDokumenterPaaOppfolgingOgAndreTemaGirGruppertTema() {

        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(OPPFOLGING)
                .withAvsluttet(null);

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(oppfolinging), asList(
                new DokumentMetadata()
                        .withTilhorendeSakid(null)
                        .withBaksystem(HENVENDELSE)
                        .withTemakode("DAG"),
                new DokumentMetadata()
                        .withTilhorendeSakid(null)
                        .withBaksystem(HENVENDELSE)
                        .withTemakode("OPP")), Collections.emptyMap());

        assertTrue(map.containsKey(TEMAGRUPPE_ARBEID));
        assertThat(map.get(TEMAGRUPPE_RESTERENDE_TEMA).size(),is(0));
        assertThat(map.get(TEMAGRUPPE_ARBEID).size(), is(2));
    }

    @Test
    public void dokumentIHenvendelseUtenSakGirNyttSakstema() {


        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(new ArrayList<>(), asList(new DokumentMetadata()
                .withTilhorendeSakid(null)
                .withBaksystem(HENVENDELSE)
                .withTemakode("DAG")), Collections.emptyMap());

        assertTrue(map.containsKey(TEMAGRUPPE_RESTERENDE_TEMA));
        assertThat(map.get(TEMAGRUPPE_RESTERENDE_TEMA).size(), is(1));
    }


}