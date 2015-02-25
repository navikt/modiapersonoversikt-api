package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;


import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.*;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.*;
import no.nav.virksomhet.gjennomforing.sak.v1.WSEndringsinfo;
import no.nav.virksomhet.gjennomforing.sak.v1.WSGenerellSak;
import no.nav.virksomhet.tjenester.sak.arbeidogaktivitet.v1.ArbeidOgAktivitet;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.*;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Sak.*;
import static org.hamcrest.core.Is.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SakerServiceImplTest {

    private static final DateTime FIRE_DAGER_SIDEN = now().minusDays(4);
    private static final String FNR = "fnr";

    @Mock
    private no.nav.virksomhet.tjenester.sak.v1.Sak sakWS;
    @Mock
    private GsakKodeverk gsakKodeverk;
    @Mock
    private StandardKodeverk standardKodeverk;
    @Mock
    private LokaltKodeverk lokaltKodeverk;
    @Mock
    private ArbeidOgAktivitet arbeidOgAktivitet;

    @InjectMocks
    private SakerServiceImpl sakerService;

    private List<WSGenerellSak> sakerListe;

    @Before
    public void setUp() {
        sakerListe = createSaksliste();
        when(sakWS.finnGenerellSakListe(any(WSFinnGenerellSakListeRequest.class))).thenReturn(
                new WSFinnGenerellSakListeResponse().withSakListe(createSaksliste()));

        when(arbeidOgAktivitet.hentSakListe(any(WSHentSakListeRequest.class))).thenReturn(new WSHentSakListeResponse());
    }

    @Test
    public void transformererResponseTilSaksliste() {
        List<Sak> saksliste = sakerService.hentListeAvSaker(FNR);

        assertThat(saksliste.get(0).saksId, is("1"));
    }

    @Test
    public void transformasjonenGenerererRelevanteFelter() {
        Sak sak = SakerServiceImpl.TIL_SAK.transform(sakerListe.get(0));

        assertThat(sak.saksId, is("1"));
        assertThat(sak.temaKode, is(GODKJENTE_TEMA_FOR_GENERELLE.get(0)));
        assertThat(sak.sakstype, is(SAKSTYPE_GENERELL));
        assertThat(sak.fagsystemKode, is(GODKJENT_FAGSYSTEM_FOR_GENERELLE));
        assertThat(sak.opprettetDato, is(FIRE_DAGER_SIDEN));
        assertThat(sak.finnesIGsak, is(true));
    }

    @Test
    public void hentSakerReturnererSakerObject() {
        ArgumentCaptor<WSFinnGenerellSakListeRequest> fnrCaptor = ArgumentCaptor.forClass(WSFinnGenerellSakListeRequest.class);

        Saker saker = sakerService.hentSaker(FNR);

        verify(sakWS, times(1)).finnGenerellSakListe(fnrCaptor.capture());
        assertThat(fnrCaptor.getValue().getBrukerId(), is(FNR));
        assertThat(saker.getSakerListeGenerelle().size() > 0, is(true));
        assertThat(saker.getSakerListeFagsak().size() > 0, is(true));
    }

    @Test
    public void returnererRiktigAntallFagsaker() {
        Saker saker = sakerService.hentSaker(FNR);

        assertThat(saker.getSakerListeFagsak().size(), is(1));
    }

    @Test
    public void leggerTilAlleManglendeGenerelleSakerInkludertOppfolgingssakDersomIngenOppfolgingssakerFinnes() {
        Saker saker = sakerService.hentSaker(FNR);

        assertThat(saker.getSakerListeGenerelle().size(), is(12));
    }

    @Test
    public void oppretterIkkeGenerellOppfolgingssakDersomFagsakerInneholderOppfolgingssak() {
        String oppfolgingssakId = "4";
        sakerListe.add(createWSGenerellSak(oppfolgingssakId, TEMAKODE_OPPFOLGING, now(), "Fag", "AO01"));
        when(sakWS.finnGenerellSakListe(any(WSFinnGenerellSakListeRequest.class))).thenReturn(
                new WSFinnGenerellSakListeResponse().withSakListe(sakerListe));

        Saker saker = sakerService.hentSaker(FNR);

        assertThat(saker.getSakerListeGenerelle().size(), is(11));
        for (SakerForTema sakerForTema : saker.getSakerListeGenerelle()) {
            assertThat(sakerForTema.temaKode.equals(TEMAKODE_OPPFOLGING), is(false));
        }
    }

    @Test
    public void oppretterIkkeGenerellOppfolgingssakDersomDenneFinnesAlleredeSelvOmFagsakerIkkeInneholderOppfolgingssak() {
        String oppfolgingssakId = "4";
        sakerListe.add(createWSGenerellSak(oppfolgingssakId, TEMAKODE_OPPFOLGING, now(), SAKSTYPE_GENERELL, GODKJENT_FAGSYSTEM_FOR_GENERELLE));
        when(sakWS.finnGenerellSakListe(any(WSFinnGenerellSakListeRequest.class))).thenReturn(
                new WSFinnGenerellSakListeResponse().withSakListe(sakerListe));

        Saker saker = sakerService.hentSaker(FNR);

        assertThat(saker.getSakerListeGenerelle().size(), is(12));
        for (SakerForTema sakerForTema : saker.getSakerListeGenerelle()) {
            if (sakerForTema.temaKode.equals(TEMAKODE_OPPFOLGING)) {
                assertThat(sakerForTema.saksliste.get(0).saksId, is(oppfolgingssakId));
            }
        }
    }

    @Test
    public void fjernerGenerellOppfolgingssakDersomDenneFinnesOgOppfolgingssakFinnesIFagsaker() {
        String oppfolgingssakGenerellId = "4";
        String oppfolgingssakFagsakId = "5";
        sakerListe.add(createWSGenerellSak(oppfolgingssakGenerellId, TEMAKODE_OPPFOLGING, now(), SAKSTYPE_GENERELL, GODKJENT_FAGSYSTEM_FOR_GENERELLE));
        sakerListe.add(createWSGenerellSak(oppfolgingssakFagsakId, TEMAKODE_OPPFOLGING, now(), "Fag", "AO01"));
        when(sakWS.finnGenerellSakListe(any(WSFinnGenerellSakListeRequest.class))).thenReturn(
                new WSFinnGenerellSakListeResponse().withSakListe(sakerListe));

        Saker saker = sakerService.hentSaker(FNR);

        assertThat(saker.getSakerListeGenerelle().size(), is(11));
        for (SakerForTema sakerForTema : saker.getSakerListeGenerelle()) {
            assertThat(sakerForTema.temaKode.equals(TEMAKODE_OPPFOLGING), is(false));
        }
    }

    private ArrayList<WSGenerellSak> createSaksliste() {
        return new ArrayList<>(asList(
                createWSGenerellSak("1", GODKJENTE_TEMA_FOR_GENERELLE.get(0), FIRE_DAGER_SIDEN, SAKSTYPE_GENERELL, GODKJENT_FAGSYSTEM_FOR_GENERELLE),
                createWSGenerellSak("2", GODKJENTE_TEMA_FOR_GENERELLE.get(1), now().minusDays(3), SAKSTYPE_GENERELL, GODKJENT_FAGSYSTEM_FOR_GENERELLE),
                createWSGenerellSak("3", "AAP", now().minusDays(5), "Fag", GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER.get(0))
        ));
    }

    private static WSGenerellSak createWSGenerellSak(String id, String tema, DateTime opprettet, String sakstype, String fagsystem) {
        return new WSGenerellSak()
                .withSakId(id)
                .withFagomradeKode(tema)
                .withEndringsinfo(new WSEndringsinfo().withOpprettetDato(opprettet))
                .withSakstypeKode(sakstype)
                .withFagsystemKode(fagsystem);
    }

}