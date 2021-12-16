package no.nav.modiapersonoversikt.legacy.sak.service;

import no.nav.modiapersonoversikt.legacy.sak.providerdomain.*;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.modiapersonoversikt.legacy.sak.service.interfaces.InnsynJournalV2Service;
import no.nav.modiapersonoversikt.legacy.sak.service.saf.SafService;
import no.nav.modiapersonoversikt.legacy.sak.transformers.DokumentMetadataTransformer;
import no.nav.modiapersonoversikt.legacy.sak.utils.Konstanter;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.core.Is.is;
import static org.joda.time.DateTime.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DokumentMetadataServiceTest {

    public static final String JOURNALPOST_ID = "2";
    public static final String TEMAKODE = "DAG";
    public static final String BEHANDLINGS_ID = "333";
    public static final String DOKMOT_TEMA = "BIL";
    private BulletproofKodeverkService bulletproofKodeverkService = mock(BulletproofKodeverkService.class);

    private DokumentMetadataTransformer dokumentMetadataTransformer = new DokumentMetadataTransformer(bulletproofKodeverkService);

    private HenvendelseService henvendelseService = mock(HenvendelseService.class);

    private SafService safService = mock(SafService.class);

    private InnsynJournalV2Service innsynJournalV2Service = mock(InnsynJournalV2Service.class);

    private DokumentMetadataService dokumentMetadataService = new DokumentMetadataService(innsynJournalV2Service, henvendelseService, dokumentMetadataTransformer, safService);

    @Before
    public void setup() {
        when(bulletproofKodeverkService.getKode(anyString(), any())).thenReturn("DAG");
        when(innsynJournalV2Service.identifiserJournalpost(BEHANDLINGS_ID))
                .thenReturn(new ResultatWrapper<>(new DokumentMetadata().withJournalpostId(JOURNALPOST_ID)));
    }

    @Test
    public void hvisEndretTemaFarViToDokumentMetadataOgEnMedFeilmelding() {
        mockSaf(brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg());
        when(bulletproofKodeverkService.getKode(anyString(), any())).thenReturn("FOR");

        when(bulletproofKodeverkService.getTemanavnForTemakode(anyString(), any())).thenReturn(new ResultatWrapper<>("Dagpenger"));
        when(henvendelseService.hentInnsendteSoknader(anyString())).thenReturn(singletonList(lagHenvendelse("2")));

        ResultatWrapper<List<DokumentMetadata>> wrapper = dokumentMetadataService.hentDokumentMetadata("");

        assertThat(wrapper.resultat.size(), is(2));
        assertFalse(wrapper.resultat.get(0).getFeilWrapper().getInneholderFeil());
        assertTrue(wrapper.resultat.get(1).getFeilWrapper().getInneholderFeil());
    }

    @Test
    public void hvisViFaarSammeJournalpostFraHenvendelseOgJoarkSkalViBrukeInformasjonenFraJoark() {

        mockSaf(brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg());

        when(bulletproofKodeverkService.getTemanavnForTemakode(Konstanter.DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper<>("Dagpenger"));

        when(henvendelseService.hentInnsendteSoknader(anyString())).thenReturn(singletonList(lagHenvendelse("2")));

        List<DokumentMetadata> dokumentMetadatas = dokumentMetadataService.hentDokumentMetadata("").resultat;

        assertThat(dokumentMetadatas.size(), is(1));
    }


    @Test
    public void hvisViFaarJournalpostFraHenvendelseSomIkkeFinnesIJoarkSkalDenneBrukesVidere() {
        mockSaf(brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg());

        when(bulletproofKodeverkService.getTemanavnForTemakode(Konstanter.DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper<>("Dagpenger"));

        when(henvendelseService.hentInnsendteSoknader(anyString())).thenReturn(singletonList(lagHenvendelse("En annen journalpost")));

        List<DokumentMetadata> dokumentMetadatas = dokumentMetadataService.hentDokumentMetadata("").resultat;

        assertThat(dokumentMetadatas.size(), is(2));
    }

    @Test
    public void hvisViFaarSammeJournalpostFraHenvendelseOgJoarkSkalViBrukeInformasjonenFraJoarkMenTaMedInformasjonOmEttersendelseFraHenvendelse() {
        mockSaf(brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg());

        when(bulletproofKodeverkService.getTemanavnForTemakode(Konstanter.DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper<>("Dagpenger"));

        String SAMME_JOURNALPOST = "2";
        when(henvendelseService.hentInnsendteSoknader(anyString())).thenReturn(singletonList(lagHenvendelse(SAMME_JOURNALPOST)));

        List<DokumentMetadata> dokumentMetadatas = dokumentMetadataService.hentDokumentMetadata("").resultat;

        assertThat(dokumentMetadatas.size(), is(1));
        assertTrue(dokumentMetadatas.get(0).isEttersending());
    }

    @Test
    public void hvisViBareFaarJournalpostFraJoarkSkalIkkeDokumentetSettesTilFiktivtDokument() {
        mockSaf(brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg());

        when(henvendelseService.hentInnsendteSoknader(anyString())).thenReturn(emptyList());

        ResultatWrapper<List<DokumentMetadata>> wrapper = dokumentMetadataService.hentDokumentMetadata("");

        assertFalse(wrapper.resultat.get(0).isEttersending());
    }

    @Test
    public void hvisJournalpostKommerFraBadeJoarkOgHenvendelseSkalBeggeSettesSomBaksystem() {
        mockSaf(brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg());

        when(bulletproofKodeverkService.getTemanavnForTemakode(Konstanter.DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper<>("Dagpenger"));

        when(henvendelseService.hentInnsendteSoknader(anyString())).thenReturn(singletonList(lagHenvendelse("2")));

        ResultatWrapper<List<DokumentMetadata>> wrapper = dokumentMetadataService.hentDokumentMetadata("");

        assertThat(wrapper.resultat.get(0).getBaksystem().size(), is(2));
        assertTrue(wrapper.resultat.get(0).getBaksystem().contains(Baksystem.JOARK));
        assertTrue(wrapper.resultat.get(0).getBaksystem().contains(Baksystem.HENVENDELSE));
    }

    @Test
    public void hvisJournalpostKunKommerFraJoarkSkalKunJoarkSettesSomBaksystem() {
        mockSaf(brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg());

        ResultatWrapper<List<DokumentMetadata>> wrapper = dokumentMetadataService.hentDokumentMetadata("");

        assertThat(wrapper.resultat.get(0).getBaksystem().size(), is(1));
        assertTrue(wrapper.resultat.get(0).getBaksystem().contains(Baksystem.JOARK));

    }

    @Test
    public void hvisJournalpostKunKommerFraHenvendelseSkalKunHenvendelseSettesSomBaksystem() {
        mockSaf();

        when(bulletproofKodeverkService.getTemanavnForTemakode(Konstanter.DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper<>("Dagpenger"));

        when(henvendelseService.hentInnsendteSoknader(anyString())).thenReturn(singletonList(lagHenvendelse("2")));

        ResultatWrapper<List<DokumentMetadata>> wrapper = dokumentMetadataService.hentDokumentMetadata("");

        assertThat(wrapper.resultat.get(0).getBaksystem().size(), is(1));
        assertTrue(wrapper.resultat.get(0).getBaksystem().contains(Baksystem.HENVENDELSE));

    }

    @Test
    public void hvisDokmotSoknadIkkeHarJournalpostIdBrukesBehandlingsIdForAFinneJournalpostIdOgMatche() {
        when(bulletproofKodeverkService.getKode(anyString(), any())).thenReturn(DOKMOT_TEMA);
        mockSaf(brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg().withTemakode(DOKMOT_TEMA));
        when(bulletproofKodeverkService.getTemanavnForTemakode(DOKMOT_TEMA, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper<>("Bil"));
        when(henvendelseService.hentInnsendteSoknader(anyString())).thenReturn(singletonList(lagHenvendelse(null)));

        List<DokumentMetadata> dokumenter = dokumentMetadataService.hentDokumentMetadata("").resultat;

        assertThat(dokumenter.size(), is(1));
        assertThat(dokumenter.get(0).getBaksystem(), Matchers.hasItem(Baksystem.JOARK));
        assertThat(dokumenter.get(0).getBaksystem(), Matchers.hasItem(Baksystem.HENVENDELSE));
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

    private Soknad lagHenvendelse(String journalpostId) {
        return new Soknad()
                .withJournalpostId(journalpostId)
                .withBehandlingsId(BEHANDLINGS_ID)
                .withBehandlingskjedeId("98765")
                .withStatus(Soknad.HenvendelseStatus.FERDIG)
                .withOpprettetDato(now())
                .withInnsendtDato(now())
                .withSistEndretDato(now())
                .withSkjemanummerRef("NAV---")
                .withEttersending(true)
                .withHenvendelseType(HenvendelseType.SOKNADSINNSENDING)
                .withDokumenter(singletonList(new DokumentFraHenvendelse()
                        .withErHovedskjema(true)
                        .withKodeverkRef("NAV 14-05.00")
                ));
    }


}
