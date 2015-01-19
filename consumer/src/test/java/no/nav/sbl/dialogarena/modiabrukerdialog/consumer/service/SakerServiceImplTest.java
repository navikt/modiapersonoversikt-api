package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;


import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Saker;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.GsakKodeverk;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.LokaltKodeverk;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.StandardKodeverk;
import no.nav.virksomhet.gjennomforing.sak.v1.WSEndringsinfo;
import no.nav.virksomhet.gjennomforing.sak.v1.WSGenerellSak;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSFinnGenerellSakListeRequest;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSFinnGenerellSakListeResponse;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Sak.GODKJENT_FAGSYSTEM_FOR_GENERELLE;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Sak.SAKSTYPE_GENERELL;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SakerServiceImplTest {

    public static final DateTime FIRE_DAGER_SIDEN = DateTime.now().minusDays(4);
    @Mock
    private no.nav.virksomhet.tjenester.sak.v1.Sak sakWS;
    @Mock
    private GsakKodeverk gsakKodeverk;
    @Mock
    private StandardKodeverk standardKodeverk;
    @Mock
    private LokaltKodeverk lokaltKodeverk;

    @InjectMocks
    private SakerServiceImpl sakerService;

    @Before
    public void setUp() {
        when(sakWS.finnGenerellSakListe(any(WSFinnGenerellSakListeRequest.class))).thenReturn(
                new WSFinnGenerellSakListeResponse().withSakListe(createSakslisteBasertPaTemaMap()));
    }

    @Test
    public void hentSakerReturnererSakerObject() {
        ArgumentCaptor<WSFinnGenerellSakListeRequest> fnrCaptor = ArgumentCaptor.forClass(WSFinnGenerellSakListeRequest.class);

        Saker saker = sakerService.hentSaker("fnr");

        verify(sakWS, times(1)).finnGenerellSakListe(fnrCaptor.capture());
        assertThat(fnrCaptor.getValue().getBrukerId(), is("fnr"));
        assertThat(saker.getSakerListeGenerelle().size(), is(2));
        assertThat(saker.getSakerListeFagsak().size(), is(1));
    }

    @Test
    public void transformererResponseTilSaksliste() {
        List<Sak> saksliste = sakerService.hentListeAvSaker("fnr");

        assertThat(saksliste.get(0).saksId, is("11111111"));
    }

    @Test
    public void transformasjonenGenerererRelevanteFelter() {
        Sak sak = SakerServiceImpl.TIL_SAK.transform(createSakslisteBasertPaTemaMap().get(0));

        assertThat(sak.saksId, is("11111111"));
        assertThat(sak.temaKode, is("FUL"));
        assertThat(sak.sakstype, is(SAKSTYPE_GENERELL));
        assertThat(sak.fagsystemKode, is(GODKJENT_FAGSYSTEM_FOR_GENERELLE));
        assertThat(sak.opprettetDato, is(FIRE_DAGER_SIDEN));
    }

    private ArrayList<WSGenerellSak> createSakslisteBasertPaTemaMap() {
        ArrayList<String> temaerForGenerelle = new ArrayList<>(asList("FUL", "SER", "SIK", "VEN"));

        return new ArrayList<>(asList(
                new WSGenerellSak()
                        .withSakId("11111111")
                        .withFagomradeKode(temaerForGenerelle.get(0))
                        .withEndringsinfo(new WSEndringsinfo().withOpprettetDato(FIRE_DAGER_SIDEN))
                        .withSakstypeKode(SAKSTYPE_GENERELL)
                        .withFagsystemKode(GODKJENT_FAGSYSTEM_FOR_GENERELLE),
                new WSGenerellSak()
                        .withSakId("22222222")
                        .withFagomradeKode(temaerForGenerelle.get(1))
                        .withEndringsinfo(new WSEndringsinfo().withOpprettetDato(DateTime.now().minusDays(3)))
                        .withSakstypeKode(SAKSTYPE_GENERELL)
                        .withFagsystemKode(GODKJENT_FAGSYSTEM_FOR_GENERELLE),
                new WSGenerellSak()
                        .withSakId("55555555")
                        .withFagomradeKode("AAP")
                        .withEndringsinfo(new WSEndringsinfo().withOpprettetDato(DateTime.now().minusDays(5)))
                        .withSakstypeKode("Fag")
                        .withFagsystemKode("AO01")
        ));
    }

}