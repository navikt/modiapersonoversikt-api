package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service;

import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.*;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.transformers.DokumentMetadataTransformer;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.utils.Konstanter;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.*;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.datatype.DatatypeConfigurationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.Is.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
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

    private Kodeverk kodeverk = mock(Kodeverk.class);

    private JoarkJournalService joarkJournalService = mock(JoarkJournalService.class);

    private DokumentMetadataService dokumentMetadataService = new DokumentMetadataService(joarkJournalService, henvendelseService, dokumentMetadataTransformer);

    @Before
    public void setup() {
        when(kodeverk.getKode(anyString(), any())).thenReturn("DAG");
        when(bulletproofKodeverkService.getKode(anyString(), any())).thenReturn("DAG");
        when(joarkJournalService.identifiserJournalpost(BEHANDLINGS_ID))
                .thenReturn(new ResultatWrapper<>(new DokumentMetadata().withJournalpostId(JOURNALPOST_ID)));
    }

    @Test
    public void hvisEndretTemaFarViToDokumentMetadataOgEnMedFeilmelding() throws DatatypeConfigurationException {
        mockJoark(brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg());
        when(bulletproofKodeverkService.getKode(anyString(), any())).thenReturn("FOR");

        when(bulletproofKodeverkService.getTemanavnForTemakode(anyString(), anyString())).thenReturn(new ResultatWrapper("Dagpenger"));
        when(kodeverk.getTittel("NAV 14-05.00")).thenReturn("Soknad om foreldrepenger");
        when(henvendelseService.hentInnsendteSoknader(anyString())).thenReturn(singletonList(lagHenvendelse("2")));

        ResultatWrapper<List<DokumentMetadata>> wrapper = dokumentMetadataService.hentDokumentMetadata(new ArrayList<>(), "");

        assertThat(wrapper.resultat.size(), is(2));
        assertFalse(wrapper.resultat.get(0).getFeilWrapper().getInneholderFeil());
        assertTrue(wrapper.resultat.get(1).getFeilWrapper().getInneholderFeil());
    }

    @Test
    public void hvisViFaarSammeJournalpostFraHenvendelseOgJoarkSkalViBrukeInformasjonenFraJoark() throws DatatypeConfigurationException {

        mockJoark(brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg());

        when(bulletproofKodeverkService.getTemanavnForTemakode(Konstanter.DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Dagpenger"));
        when(kodeverk.getTittel("NAV 14-05.00")).thenReturn("Soknad om foreldrepenger");

        when(henvendelseService.hentInnsendteSoknader(anyString())).thenReturn(singletonList(lagHenvendelse("2")));

        List<DokumentMetadata> dokumentMetadatas = dokumentMetadataService.hentDokumentMetadata(new ArrayList<>(), "").resultat;

        assertThat(dokumentMetadatas.size(), is(1));
    }


    @Test
    public void hvisViFaarJournalpostFraHenvendelseSomIkkeFinnesIJoarkSkalDenneBrukesVidere() throws DatatypeConfigurationException {
        mockJoark(brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg());

        when(bulletproofKodeverkService.getTemanavnForTemakode(Konstanter.DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Dagpenger"));
        when(kodeverk.getTittel("NAV 14-05.00")).thenReturn("Soknad om foreldrepenger");

        when(henvendelseService.hentInnsendteSoknader(anyString())).thenReturn(singletonList(lagHenvendelse("En annen journalpost")));

        List<DokumentMetadata> dokumentMetadatas = dokumentMetadataService.hentDokumentMetadata(new ArrayList<>(), "").resultat;

        assertThat(dokumentMetadatas.size(), is(2));
    }

    @Test
    public void hvisViFaarSammeJournalpostFraHenvendelseOgJoarkSkalViBrukeInformasjonenFraJoarkMenTaMedInformasjonOmEttersendelseFraHenvendelse() throws DatatypeConfigurationException {
        mockJoark(brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg());

        when(bulletproofKodeverkService.getTemanavnForTemakode(Konstanter.DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Dagpenger"));
        when(kodeverk.getTittel("NAV 14-05.00")).thenReturn("Soknad om foreldrepenger");

        String SAMME_JOURNALPOST = "2";
        when(henvendelseService.hentInnsendteSoknader(anyString())).thenReturn(singletonList(lagHenvendelse(SAMME_JOURNALPOST)));

        List<DokumentMetadata> dokumentMetadatas = dokumentMetadataService.hentDokumentMetadata(new ArrayList<>(), "").resultat;

        assertThat(dokumentMetadatas.size(), is(1));
        assertTrue(dokumentMetadatas.get(0).isEttersending());
    }

    @Test
    public void hvisViBareFaarJournalpostFraJoarkSkalIkkeDokumentetSettesTilFiktivtDokument() throws DatatypeConfigurationException {
        mockJoark(brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg());

        when(bulletproofKodeverkService.getTemanavnForTemakode(Konstanter.DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Dagpenger"));
        when(kodeverk.getTittel("NAV 14-05.00")).thenReturn("Soknad om foreldrepenger");

        when(henvendelseService.hentInnsendteSoknader(anyString())).thenReturn(emptyList());

        ResultatWrapper<List<DokumentMetadata>> wrapper = dokumentMetadataService.hentDokumentMetadata(new ArrayList<>(), "");

        assertFalse(wrapper.resultat.get(0).isEttersending());
    }

    @Test
    public void hvisJournalpostKommerFraBadeJoarkOgHenvendelseSkalBeggeSettesSomBaksystem() {
        mockJoark(brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg());

        when(bulletproofKodeverkService.getTemanavnForTemakode(Konstanter.DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Dagpenger"));
        when(kodeverk.getTittel("NAV 14-05.00")).thenReturn("Soknad om foreldrepenger");

        when(henvendelseService.hentInnsendteSoknader(anyString())).thenReturn(singletonList(lagHenvendelse("2")));

        ResultatWrapper<List<DokumentMetadata>> wrapper = dokumentMetadataService.hentDokumentMetadata(new ArrayList<>(), "");

        assertThat(wrapper.resultat.get(0).getBaksystem().size(), is(2));
        assertTrue(wrapper.resultat.get(0).getBaksystem().contains(Baksystem.JOARK));
        assertTrue(wrapper.resultat.get(0).getBaksystem().contains(Baksystem.HENVENDELSE));
    }

    @Test
    public void hvisJournalpostKunKommerFraJoarkSkalKunJoarkSettesSomBaksystem() {
        mockJoark(brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg());

        when(bulletproofKodeverkService.getTemanavnForTemakode(Konstanter.DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Dagpenger"));
        when(kodeverk.getTittel("NAV 14-05.00")).thenReturn("Soknad om foreldrepenger");

        when(henvendelseService.hentInnsendteSoknader(anyString())).thenReturn(emptyList());

        ResultatWrapper<List<DokumentMetadata>> wrapper = dokumentMetadataService.hentDokumentMetadata(new ArrayList<>(), "");

        assertThat(wrapper.resultat.get(0).getBaksystem().size(), is(1));
        assertTrue(wrapper.resultat.get(0).getBaksystem().contains(Baksystem.JOARK));

    }

    @Test
    public void hvisJournalpostKunKommerFraHenvendelseSkalKunHenvendelseSettesSomBaksystem() {
        mockJoark();

        when(bulletproofKodeverkService.getTemanavnForTemakode(Konstanter.DAGPENGER, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Dagpenger"));
        when(kodeverk.getTittel("NAV 14-05.00")).thenReturn("Soknad om foreldrepenger");

        when(henvendelseService.hentInnsendteSoknader(anyString())).thenReturn(singletonList(lagHenvendelse("2")));

        ResultatWrapper<List<DokumentMetadata>> wrapper = dokumentMetadataService.hentDokumentMetadata(new ArrayList<>(), "");

        assertThat(wrapper.resultat.get(0).getBaksystem().size(), is(1));
        assertTrue(wrapper.resultat.get(0).getBaksystem().contains(Baksystem.HENVENDELSE));

    }

    @Test
    public void hvisDokmotSoknadIkkeHarJournalpostIdBrukesBehandlingsIdForAFinneJournalpostIdOgMatche() {
        when(kodeverk.getKode(anyString(), any())).thenReturn(DOKMOT_TEMA);
        when(bulletproofKodeverkService.getKode(anyString(), any())).thenReturn(DOKMOT_TEMA);
        mockJoark(brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg().withTemakode(DOKMOT_TEMA));
        when(bulletproofKodeverkService.getTemanavnForTemakode(DOKMOT_TEMA, BulletproofKodeverkService.ARKIVTEMA)).thenReturn(new ResultatWrapper("Bil"));
        when(kodeverk.getTittel("NAV 14-05.00")).thenReturn("Soknad om bil");
        when(henvendelseService.hentInnsendteSoknader(anyString())).thenReturn(singletonList(lagHenvendelse(null)));

        List<DokumentMetadata> dokumenter = dokumentMetadataService.hentDokumentMetadata(new ArrayList<>(), "").resultat;

        assertThat(dokumenter.size(), is(1));
        assertThat(dokumenter.get(0).getBaksystem(), Matchers.hasItem(Baksystem.JOARK));
        assertThat(dokumenter.get(0).getBaksystem(), Matchers.hasItem(Baksystem.HENVENDELSE));
        assertThat(dokumenter.get(0).getJournalpostId(), is(JOURNALPOST_ID));
    }

    private void mockJoark(DokumentMetadata... joarkDokumentMetadata){
        when(joarkJournalService.hentTilgjengeligeJournalposter(any(), anyString()))
                .thenReturn(new ResultatWrapper<>(asList(joarkDokumentMetadata),emptySet()));
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
