package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service;

import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Sakstema;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Baksystem.HENVENDELSE;
import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Baksystem.SAF;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class SaksoversiktServiceTest {

    private SaksoversiktServiceImpl saksoversiktService = new SaksoversiktServiceImpl();

    private static final String PROD_SETTNINGS_DATO = "2015-01-01";

    @Before
    public void setup() {
        System.setProperty("saksoversikt.prodsettningsdato", PROD_SETTNINGS_DATO);
    }

    @Test
    public void fjernerGamleJournalpostSomKunFinnesISaf() {
        List<Sakstema> sakstema = getMockSaksteman();

        saksoversiktService.fjernGamleDokumenter(sakstema);

        assertThat(sakstema.get(0).dokumentMetadata.size(), is(2));
        assertThat(sakstema.get(1).dokumentMetadata.size(), is(3));
        assertThat(sakstema.get(2).dokumentMetadata.size(), is(0));

        assertThat(sakstema.get(0).dokumentMetadata.get(0).getJournalpostId(), is("2"));
        assertThat(sakstema.get(0).dokumentMetadata.get(1).getJournalpostId(), is("3"));

        assertThat(sakstema.get(1).dokumentMetadata.get(0).getJournalpostId(), is("4"));
        assertThat(sakstema.get(1).dokumentMetadata.get(1).getJournalpostId(), is("5"));
        assertThat(sakstema.get(1).dokumentMetadata.get(2).getJournalpostId(), is("6"));
    }

    private List<Sakstema> getMockSaksteman() {
        List<Sakstema> sakstema = new ArrayList<>();
        DokumentMetadata dokumentmetadata1 = new DokumentMetadata().withJournalpostId("1").withBaksystem(SAF).withDato(LocalDateTime.of(2013, Month.APRIL, 8, 12, 30));
        DokumentMetadata dokumentmetadata2 = new DokumentMetadata().withJournalpostId("2").withBaksystem(SAF).withBaksystem(HENVENDELSE).withDato(LocalDateTime.of(2013, Month.APRIL, 8, 12, 30));
        DokumentMetadata dokumentmetadata3 = new DokumentMetadata().withJournalpostId("3").withBaksystem(HENVENDELSE).withDato(LocalDateTime.of(2013, Month.APRIL, 8, 12, 30));
        DokumentMetadata dokumentmetadata4 = new DokumentMetadata().withJournalpostId("4").withBaksystem(SAF).withDato(LocalDateTime.of(2015, Month.APRIL, 8, 12, 30));
        DokumentMetadata dokumentmetadata5 = new DokumentMetadata().withJournalpostId("5").withBaksystem(HENVENDELSE).withDato(LocalDateTime.of(2015, Month.APRIL, 8, 12, 30));
        DokumentMetadata dokumentmetadata6 = new DokumentMetadata().withJournalpostId("6").withBaksystem(HENVENDELSE).withBaksystem(SAF).withDato(LocalDateTime.of(2015, Month.APRIL, 8, 12, 30));
        DokumentMetadata dokumentmetadata7 = new DokumentMetadata().withJournalpostId("7").withBaksystem(SAF).withDato(LocalDateTime.of(2010, Month.APRIL, 8, 12, 30));
        DokumentMetadata dokumentmetadata8 = new DokumentMetadata().withJournalpostId("8").withBaksystem(SAF).withDato(LocalDateTime.of(2011, Month.APRIL, 8, 12, 30));

        Sakstema sakstema1 = new Sakstema().withDokumentMetadata(asList(dokumentmetadata1, dokumentmetadata2, dokumentmetadata3));
        Sakstema sakstema2 = new Sakstema().withDokumentMetadata(asList(dokumentmetadata4, dokumentmetadata5, dokumentmetadata6));
        Sakstema sakstema3 = new Sakstema().withDokumentMetadata(asList(dokumentmetadata7, dokumentmetadata8));

        sakstema.addAll(asList(sakstema1, sakstema2, sakstema3));
        return sakstema;
    }
}
