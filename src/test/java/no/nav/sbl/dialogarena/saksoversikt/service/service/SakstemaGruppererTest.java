package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.saksoversikt.service.utils.TemagrupperHenter;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Baksystem;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Sak;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.saksoversikt.service.service.SakstemaGrupperer.OPPFOLGING;
import static no.nav.sbl.dialogarena.saksoversikt.service.service.SakstemaGrupperer.TEMAGRUPPE_RESTERENDE_TEMA;
import static no.nav.sbl.dialogarena.saksoversikt.service.utils.Konstanter.*;
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
        System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
        when(temagrupperHenter.genererTemagrupperMedTema()).thenReturn(new HashMap<String, List<String>>() {
            {
                put(TEMAGRUPPE_ARBEID, asList(DAGPENGER, OPPFOLGING, ARBEIDSAVKLARINGSPENGER));
            }
        });
    }

    @Test
    public void gruppererOppfolgingSammenMedSak() throws Exception {

        Sak sak = new Sak()
                .withSaksId("123")
                .withTemakode(DAGPENGER)
                .withAvsluttet(null);

        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(OPPFOLGING)
                .withAvsluttet(null);

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(sak, oppfolinging), asList(new DokumentMetadata()
                .withTilhorendeSakid("321")
                .withBaksystem(Baksystem.JOARK)));

        assertThat(map.get(TEMAGRUPPE_ARBEID).size(), equalTo(2));
        assertFalse(map.containsKey(TEMAGRUPPE_FAMILIE));
        assertThat(map.get(TEMAGRUPPE_RESTERENDE_TEMA).size(), equalTo(0));
    }

    @Test
    public void gruppererOppfolgingAleneOmDetManglerTilknyttedeSaker() throws Exception {

        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(OPPFOLGING)
                .withAvsluttet(null);

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(oppfolinging), asList(new DokumentMetadata()
                .withTilhorendeSakid("321")
                .withBaksystem(Baksystem.JOARK)));

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

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(oppfolinging), asList());

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

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(sak, sak1), asList());

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

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(sak, sak1), asList());

        assertTrue(map.get(TEMAGRUPPE_RESTERENDE_TEMA).contains(DAGPENGER));
        assertThat(map.get(TEMAGRUPPE_RESTERENDE_TEMA).size(), equalTo(1));
    }

    @Test
    public void oppfolingUtenTemaMedDokumenterBlirResterende() {
        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(OPPFOLGING)
                .withAvsluttet(null);

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(oppfolinging), asList(new DokumentMetadata().withTilhorendeSakid("321").withBaksystem(Baksystem.JOARK)));

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

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(sak, oppfolinging), asList(new DokumentMetadata().withTilhorendeSakid("321").withBaksystem(Baksystem.JOARK)));

        assertTrue(map.containsKey(TEMAGRUPPE_ARBEID));
        assertTrue(map.get(TEMAGRUPPE_ARBEID).contains(OPPFOLGING));
        assertFalse(map.get(TEMAGRUPPE_RESTERENDE_TEMA).contains(OPPFOLGING));
    }

    @Test
    public void oppfolingUtenTemaUtenDokumenterBlirResterende() {
        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(OPPFOLGING)
                .withAvsluttet(null);

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(oppfolinging), asList());

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

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(sak, oppfolinging), asList());

        assertFalse(map.containsKey(TEMAGRUPPE_ARBEID));
        assertTrue(map.get(TEMAGRUPPE_RESTERENDE_TEMA).contains(OPPFOLGING));
        assertTrue(map.get(TEMAGRUPPE_RESTERENDE_TEMA).contains(DAGPENGER));
    }

    @Test
    public void oppfolingMedTemaMedDokumenterIHenvendelseBlirGruppert() {
        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(OPPFOLGING)
                .withAvsluttet(null);

        Sak sak = new Sak()
                .withSaksId("321")
                .withTemakode(DAGPENGER)
                .withAvsluttet(null);

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(sak, oppfolinging), asList(new DokumentMetadata()
                .withTilhorendeSakid(null)
                .withBaksystem(Baksystem.HENVENDELSE)
                .withTemakode("OPP")));

        assertTrue(map.containsKey(TEMAGRUPPE_ARBEID));
        assertTrue(map.get(TEMAGRUPPE_ARBEID).contains(OPPFOLGING));
        assertFalse(map.get(TEMAGRUPPE_RESTERENDE_TEMA).contains(OPPFOLGING));
    }

    @Test
    public void soknadIHenvendelseGirNyttSakstema() {

        Sak oppfolinging = new Sak()
                .withSaksId("321")
                .withTemakode(OPPFOLGING)
                .withAvsluttet(null);

        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(asList(oppfolinging), asList(
                new DokumentMetadata()
                        .withTilhorendeSakid(null)
                        .withBaksystem(Baksystem.HENVENDELSE)
                        .withTemakode("DAG"),
                new DokumentMetadata()
                        .withTilhorendeSakid("321")
                        .withBaksystem(Baksystem.HENVENDELSE)
                        .withTemakode("OPP")));

        assertTrue(map.containsKey(TEMAGRUPPE_ARBEID));
        assertThat(map.get(TEMAGRUPPE_RESTERENDE_TEMA).size(),is(0));
        assertThat(map.get(TEMAGRUPPE_ARBEID).size(), is(2));
    }

    @Test
    public void soknadIHenvendelseOgOppfolingssakGirNyttSakstemaMedOppfolging() {


        Map<String, Set<String>> map = sakstemaGrupperer.grupperSakstema(new ArrayList<>(), asList(new DokumentMetadata()
                .withTilhorendeSakid(null)
                .withBaksystem(Baksystem.HENVENDELSE)
                .withTemakode("DAG")));

        assertTrue(map.containsKey(TEMAGRUPPE_RESTERENDE_TEMA));
        assertThat(map.get(TEMAGRUPPE_RESTERENDE_TEMA).size(), is(1));
    }


}