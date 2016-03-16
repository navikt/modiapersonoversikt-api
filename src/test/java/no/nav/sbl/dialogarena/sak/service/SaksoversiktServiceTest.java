package no.nav.sbl.dialogarena.sak.service;

import no.nav.sbl.dialogarena.sak.service.interfaces.SaksoversiktService;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sakstema;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Baksystem.HENVENDELSE;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Baksystem.JOARK;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import no.nav.modig.core.exception.SystemException;
import no.nav.sbl.dialogarena.sak.transformers.Filter;
import no.nav.sbl.dialogarena.saksoversikt.service.service.SakOgBehandlingService;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.HentAktoerIdForIdentPersonIkkeFunnet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SaksoversiktServiceTest {

    @Mock
    private AktoerPortType fodselnummerAktorService;
    @Mock
    private SakOgBehandlingService sakOgBehandlingService;
    @Mock
    private Filter filter;

    @InjectMocks
    private SaksoversiktServiceImpl saksoversiktService = new SaksoversiktServiceImpl();


    @Test (expected = SystemException.class)
    public void kasterSystemExceptionOmAktorIdIkkeFinnes() throws HentAktoerIdForIdentPersonIkkeFunnet {
        when(fodselnummerAktorService.hentAktoerIdForIdent(any())).thenThrow(new HentAktoerIdForIdentPersonIkkeFunnet());
        saksoversiktService.hentTemaer("12345678901");
    }

    @Test (expected = RuntimeException.class)
    public void kasterRuntimeOmAktoerTjenesteErNede() throws HentAktoerIdForIdentPersonIkkeFunnet {
        when(fodselnummerAktorService.hentAktoerIdForIdent(any())).thenThrow(new RuntimeException());
        saksoversiktService.hentTemaer("12345678901");
    private static final String PROD_SETTNINGS_DATO = "2015-04-13";

    @Before
    public void setup() {
        System.setProperty("saksoversikt.prodsettningsdato", PROD_SETTNINGS_DATO);
    }

    @InjectMocks
    private SaksoversiktService saksoversiktService = new SaksoversiktServiceImpl();

    @Test
    public void fjernerGamleJournalpostSomKunFinnesIJoark() {
        List<Sakstema> saksteman = getMockSaksteman();

        saksoversiktService.fjernGamleDokumenter(saksteman);

        assertThat(saksteman.get(0).dokumentMetadata.size(), is(2));
        assertThat(saksteman.get(1).dokumentMetadata.size(), is(2));
        assertThat(saksteman.get(0).dokumentMetadata.get(0).getJournalpostId(), is("123"));
        assertThat(saksteman.get(0).dokumentMetadata.get(1).getJournalpostId(), is("456"));
        assertThat(saksteman.get(1).dokumentMetadata.get(0).getJournalpostId(), is("123"));
        assertThat(saksteman.get(1).dokumentMetadata.get(1).getJournalpostId(), is("012"));
    }

    private List<Sakstema> getMockSaksteman() {
        List<Sakstema> saksteman = new ArrayList<>();
        DokumentMetadata dokumentmetadata1 = new DokumentMetadata().withJournalpostId("123").withBaksystem(JOARK).withDato(LocalDateTime.of(2011, Month.APRIL, 8, 12, 30));
        DokumentMetadata dokumentmetadata2 = new DokumentMetadata().withJournalpostId("123").withBaksystem(HENVENDELSE).withDato(LocalDateTime.of(2011, Month.APRIL, 8, 12, 30));
        DokumentMetadata dokumentmetadata3 = new DokumentMetadata().withJournalpostId("456").withBaksystem(HENVENDELSE).withDato(LocalDateTime.of(2011, Month.APRIL, 8, 12, 30));
        DokumentMetadata dokumentmetadata4 = new DokumentMetadata().withJournalpostId("789").withBaksystem(JOARK).withDato(LocalDateTime.of(2011, Month.APRIL, 8, 12, 30));
        DokumentMetadata dokumentmetadata5 = new DokumentMetadata().withJournalpostId("012").withBaksystem(JOARK).withDato(LocalDateTime.of(2016, Month.APRIL, 8, 12, 30));

        Sakstema sakstema1 = new Sakstema().withDokumentMetadata(asList(dokumentmetadata1, dokumentmetadata3, dokumentmetadata4));
        Sakstema sakstema2 = new Sakstema().withDokumentMetadata(asList(dokumentmetadata2, dokumentmetadata5));

        saksteman.addAll(asList(sakstema1, sakstema2));
        return saksteman;
    }
}
