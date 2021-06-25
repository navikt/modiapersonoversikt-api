package no.nav.modiapersonoversikt.legacy.sak.service;

import no.nav.modiapersonoversikt.legacy.sak.providerdomain.Baksystem;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.DokumentMetadata;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.Entitet;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.modiapersonoversikt.legacy.sak.service.interfaces.InnsynJournalV2Service;
import no.nav.modiapersonoversikt.legacy.sak.service.saf.SafService;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DokumentMetadataServiceTest {

    public static final String JOURNALPOST_ID = "2";
    public static final String TEMAKODE = "DAG";
    public static final String BEHANDLINGS_ID = "333";
    public static final String DOKMOT_TEMA = "BIL";

    private SafService safService = mock(SafService.class);

    private InnsynJournalV2Service innsynJournalV2Service = mock(InnsynJournalV2Service.class);

    private DokumentMetadataService dokumentMetadataService = new DokumentMetadataService(innsynJournalV2Service, safService);

    @Test
    public void hvisEndretTemaFarViToDokumentMetadataOgEnMedFeilmelding() {
        mockSaf(brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg());
        ResultatWrapper<List<DokumentMetadata>> wrapper = dokumentMetadataService.hentDokumentMetadata("");

        assertThat(wrapper.resultat.size(), is(1));
        assertFalse(wrapper.resultat.get(0).getFeilWrapper().getInneholderFeil());
    }

    @Test
    public void hvisViBareFaarJournalpostFraJoarkSkalIkkeDokumentetSettesTilFiktivtDokument() {
        mockSaf(brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg());

        ResultatWrapper<List<DokumentMetadata>> wrapper = dokumentMetadataService.hentDokumentMetadata("");

        assertFalse(wrapper.resultat.get(0).isEttersending());
    }

    @Test
    public void hvisJournalpostKunKommerFraJoarkSkalKunJoarkSettesSomBaksystem() {
        mockSaf(brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg());

        ResultatWrapper<List<DokumentMetadata>> wrapper = dokumentMetadataService.hentDokumentMetadata("");

        assertThat(wrapper.resultat.get(0).getBaksystem().size(), is(1));
        assertTrue(wrapper.resultat.get(0).getBaksystem().contains(Baksystem.JOARK));

    }

    @Test
    public void hvisDokmotSoknadIkkeHarJournalpostIdBrukesBehandlingsIdForAFinneJournalpostIdOgMatche() {
        mockSaf(brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg().withTemakode(DOKMOT_TEMA));

        List<DokumentMetadata> dokumenter = dokumentMetadataService.hentDokumentMetadata("").resultat;

        assertThat(dokumenter.size(), is(1));
        assertThat(dokumenter.get(0).getBaksystem(), Matchers.hasItem(Baksystem.JOARK));
        assertThat(dokumenter.get(0).getJournalpostId(), is(JOURNALPOST_ID));
    }

    private void mockSaf(DokumentMetadata... safDokumentMetadata) {
        when(safService.hentJournalposter(anyString()))
                .thenReturn(new ResultatWrapper<>((asList(safDokumentMetadata))));
    }

    private DokumentMetadata brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg() {
        return new DokumentMetadata()
                .withJournalpostId(JOURNALPOST_ID)
                .withTemakode(TEMAKODE)
                .withAvsender(Entitet.NAV)
                .withMottaker(Entitet.SLUTTBRUKER)
                .withBaksystem(Baksystem.JOARK)
                .withDato(LocalDateTime.now());
    }
}
