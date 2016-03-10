package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentFraHenvendelse;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Entitet;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.datatype.DatatypeConfigurationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singletonList;
import static junit.framework.TestCase.assertFalse;
import static no.nav.sbl.dialogarena.saksoversikt.service.service.BulletproofKodeverkService.ARKIVTEMA;
import static no.nav.sbl.dialogarena.saksoversikt.service.utils.Konstanter.DAGPENGER;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.HenvendelseType.SOKNADSINNSENDING;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentFraHenvendelse.HOVEDSKJEMA;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentFraHenvendelse.KODEVERK_REF;
import static org.hamcrest.core.Is.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DokumentMetadataServiceTest {

    @Mock
    private BulletproofKodeverkService bulletproofKodeverkService;

    @Mock
    private HenvendelseService henvendelseService;

    @Mock
    private Kodeverk kodeverk;

    @Mock
    private InnsynJournalService innsynJournalService;

    @InjectMocks
    private DokumentMetadataService dokumentMetadataService;

    @Before
    public void setup() {
        when(kodeverk.getKode(anyString(), any())).thenReturn("DAG");
    }

    @Test
    public void hvisEndretTemaFarViToDokumentMetadataOgEnMedFeilmelding() throws DatatypeConfigurationException {
        mockJoark(brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg());
        when(kodeverk.getKode(anyString(), any())).thenReturn("FOR");

        when(bulletproofKodeverkService.getTemanavnForTemakode(DAGPENGER, ARKIVTEMA)).thenReturn("Dagpenger");
        when(kodeverk.getTittel("NAV 14-05.00")).thenReturn("Soknad om foreldrepenger");
        when(henvendelseService.hentHenvendelsessoknaderMedStatus(any(), anyString())).thenReturn(singletonList(lagHenvendelse("2")));

        ResultatWrapper<List<DokumentMetadata>> wrapper = dokumentMetadataService.hentDokumentMetadata(new ArrayList<>(), "");

        assertThat(wrapper.resultat.size(), is(2));
        assertFalse(wrapper.resultat.get(0).getFeilWrapper().getInneholderFeil());
        assertTrue(wrapper.resultat.get(1).getFeilWrapper().getInneholderFeil());
    }

    @Test
    public void hvisViFaarSammeJournalpostFraHenvendelseOgJoarkSkalViBrukeInformasjonenFraJoark() throws DatatypeConfigurationException {

        mockJoark(brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg());

        when(bulletproofKodeverkService.getTemanavnForTemakode(DAGPENGER, ARKIVTEMA)).thenReturn("Dagpenger");
        when(kodeverk.getTittel("NAV 14-05.00")).thenReturn("Soknad om foreldrepenger");

        when(henvendelseService.hentHenvendelsessoknaderMedStatus(any(), anyString())).thenReturn(singletonList(lagHenvendelse("2")));

        List<DokumentMetadata> dokumentMetadatas = dokumentMetadataService.hentDokumentMetadata(new ArrayList<>(), "").resultat;

        assertThat(dokumentMetadatas.size(), is(1));
    }

    private DokumentMetadata brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg() {
        return new DokumentMetadata()
                .withJournalpostId("2")
                .withTemakode("DAG")
                .withAvsender(Entitet.NAV)
                .withMottaker(Entitet.SLUTTBRUKER)
                .withDato(LocalDateTime.now());
    }

    @Test
    public void hvisViFaarJournalpostFraHenvendelseSomIkkeFinnesIJoarkSkalDenneBrukesVidere() throws DatatypeConfigurationException {
        mockJoark(brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg());

        when(bulletproofKodeverkService.getTemanavnForTemakode(DAGPENGER, ARKIVTEMA)).thenReturn("Dagpenger");
        when(kodeverk.getTittel("NAV 14-05.00")).thenReturn("Soknad om foreldrepenger");

        when(henvendelseService.hentHenvendelsessoknaderMedStatus(any(), anyString())).thenReturn(singletonList(lagHenvendelse("En annen journalpost")));

        List<DokumentMetadata> dokumentMetadatas = dokumentMetadataService.hentDokumentMetadata(new ArrayList<>(), "").resultat;

        assertThat(dokumentMetadatas.size(), is(2));
    }

    @Test
    public void hvisViFaarSammeJournalpostFraHenvendelseOgJoarkSkalViBrukeInformasjonenFraJoarkMenTaMedInformasjonOmEttersendelseFraHenvendelse() throws DatatypeConfigurationException {
        mockJoark(brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg());

        when(bulletproofKodeverkService.getTemanavnForTemakode(DAGPENGER, ARKIVTEMA)).thenReturn("Dagpenger");
        when(kodeverk.getTittel("NAV 14-05.00")).thenReturn("Soknad om foreldrepenger");

        String SAMME_JOURNALPOST = "2";
        when(henvendelseService.hentHenvendelsessoknaderMedStatus(any(), anyString())).thenReturn(singletonList(lagHenvendelse(SAMME_JOURNALPOST)));

        List<DokumentMetadata> dokumentMetadatas = dokumentMetadataService.hentDokumentMetadata(new ArrayList<>(), "").resultat;

        assertThat(dokumentMetadatas.size(), is(1));
        assertTrue(dokumentMetadatas.get(0).isEttersending());
    }

    @Test
    public void hvisViBareFaarJournalpostFraJoarkSkalIkkeDokumentetSettesTilFiktivtDokument() throws DatatypeConfigurationException {
        mockJoark(brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg());

        when(bulletproofKodeverkService.getTemanavnForTemakode(DAGPENGER, ARKIVTEMA)).thenReturn("Dagpenger");
        when(kodeverk.getTittel("NAV 14-05.00")).thenReturn("Soknad om foreldrepenger");

        when(henvendelseService.hentHenvendelsessoknaderMedStatus(any(), anyString())).thenReturn(emptyList());

        ResultatWrapper<List<DokumentMetadata>> wrapper = dokumentMetadataService.hentDokumentMetadata(new ArrayList<>(), "");

        assertFalse(wrapper.resultat.get(0).isEttersending());
    }

    private void mockJoark(DokumentMetadata... joarkDokumentMetadata){
        when(innsynJournalService.joarkSakhentTilgjengeligeJournalposter(any(), anyString()))
                .thenReturn(new ResultatWrapper<>(asList(joarkDokumentMetadata),emptySet()));
    }

    private Record<Soknad> lagHenvendelse(String journalpostId) {
        return new Record<Soknad>()
                .with(Soknad.JOURNALPOST_ID, journalpostId)
                .with(Soknad.BEHANDLINGS_ID, "12345")
                .with(Soknad.BEHANDLINGSKJEDE_ID, "98765")
                .with(Soknad.STATUS, Soknad.HenvendelseStatus.FERDIG)
                .with(Soknad.OPPRETTET_DATO, now())
                .with(Soknad.INNSENDT_DATO, now())
                .with(Soknad.SISTENDRET_DATO, now())
                .with(Soknad.SKJEMANUMMER_REF, "NAV---")
                .with(Soknad.ETTERSENDING, true)
                .with(Soknad.TYPE, SOKNADSINNSENDING)
                .with(Soknad.DOKUMENTER, singletonList(new Record<DokumentFraHenvendelse>()
                        .with(HOVEDSKJEMA, true)
                        .with(KODEVERK_REF, "NAV 14-05.00")
                ));
    }


}
