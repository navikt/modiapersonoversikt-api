package no.nav.sbl.dialogarena.modiabrukerdialog.sak.transformers;

import no.nav.sbl.dialogarena.modiabrukerdialog.sak.mock.JoarkMock;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Entitet;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.KategoriNotat;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sak;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.sbl.dialogarena.saksoversikt.service.service.BulletproofKodeverkService;
import no.nav.tjeneste.virksomhet.journal.v2.informasjon.WSJournalpost;
import no.nav.tjeneste.virksomhet.journal.v2.informasjon.WSJournalstatuser;
import no.nav.tjeneste.virksomhet.journal.v2.informasjon.WSKommunikasjonsretninger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.datatype.DatatypeConfigurationException;

import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.mock.JoarkMock.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JournalpostTransformerTest {

    @Mock
    private BulletproofKodeverkService bulletproofKodeverkService;

    @InjectMocks
    private JournalpostTransformer journalpostTransformer;

    private static final String FNR = PERSON_FNR;
    private static final String JOURNALPOSTID = "123";
    private String fagsakId = "fagsak123";

    @Before
    public void setup(){
        when(bulletproofKodeverkService.getTemanavnForTemakode(anyString(), anyString())).thenReturn(new ResultatWrapper("Temakode"));
    }

    @Test(expected = RuntimeException.class)
    public void sakUtenHovedDokumentKasterRuntimeException() {
        WSJournalpost wsJournalpost = new WSJournalpost()
                .withJournalpostId(JOURNALPOSTID)
                .withJournalstatus(new WSJournalstatuser().withValue("J"))
                .withKommunikasjonsretning(new WSKommunikasjonsretninger().withValue("I"));

        journalpostTransformer.dokumentMetadataFraJournalPost(wsJournalpost, FNR, fagsakId);
    }

    @Test
    public void brukerMottattDokumentFraNAV() throws DatatypeConfigurationException {
        ResultatWrapper<DokumentMetadata> wrapper = journalpostTransformer.dokumentMetadataFraJournalPost(brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg(), FNR, fagsakId);
        assertThat(wrapper.resultat.getHoveddokument().getTittel(), is("Hoveddokument.tittel"));
    }

    @Test
    public void navMottattDokumentFraBruker() throws DatatypeConfigurationException {
        ResultatWrapper<DokumentMetadata> wrapper = journalpostTransformer.dokumentMetadataFraJournalPost(JoarkMock.navMottattDokumentFraBruker(), FNR, fagsakId);
        assertThat(wrapper.resultat.getHoveddokument().getTittel(), is("Hoved.tittel"));
    }

    @Test
    public void navSendtDokumentTilEksternPart() {
        ResultatWrapper<DokumentMetadata> wrapper = journalpostTransformer.dokumentMetadataFraJournalPost(dokumentUtenEksternPartMedFallbackNavn(), FNR, fagsakId);
        assertThat(wrapper.resultat.getNavn(), is(FALLBACK_NAVN));
    }

    @Test
    public void setterUkjentDersomIkkeEksternPartEllerEksternPartNavn() {
        ResultatWrapper<DokumentMetadata> wrapper = journalpostTransformer.dokumentMetadataFraJournalPost(dokumentUkjentAvsender(), FNR, fagsakId);
        assertThat(wrapper.resultat.getNavn(), is("ukjent"));
    }

    @Test
    public void brukerFallbackNavnHvisEksternpartErNull() {
        ResultatWrapper<DokumentMetadata> wrapper = journalpostTransformer.dokumentMetadataFraJournalPost(JoarkMock.navSendtDokumentTilEksternPart(), FNR, fagsakId);
        assertThat(wrapper.resultat.getNavn(), is(TREDJEPERSON_NAVN));
    }

    @Test
    public void navSendtDokumentFraBedrift() throws DatatypeConfigurationException {
        ResultatWrapper<DokumentMetadata> wrapper = journalpostTransformer.dokumentMetadataFraJournalPost(navMottattDokumentFraBedrift(), FNR, fagsakId);
        assertThat(wrapper.resultat.getNavn(), is(BEDRIFT_NAVN));
    }

    @Test
    public void navSendtDokumentUtenAktoer() throws DatatypeConfigurationException {
        ResultatWrapper<DokumentMetadata> wrapper = journalpostTransformer.dokumentMetadataFraJournalPost(navMottattDokumentFraUkjent(), FNR, fagsakId);
        assertThat(wrapper.resultat.getNavn(), is("ukjent"));
    }

    @Test
    public void internKategoriNotat() throws DatatypeConfigurationException {
        ResultatWrapper<DokumentMetadata> wrapper = journalpostTransformer.dokumentMetadataFraJournalPost(internDokumentinfoRelasjonListe(), FNR, fagsakId);
        assertThat(wrapper.resultat.getKategoriNotat(), is(KategoriNotat.INTERN_NOTAT));
    }

    @Test
    public void inngaaendeKategoriNotat() throws DatatypeConfigurationException {
        ResultatWrapper<DokumentMetadata> wrapper = journalpostTransformer.dokumentMetadataFraJournalPost(eksternDokumentinfoRelasjonListe("1"), FNR, fagsakId);
        assertNull(wrapper.resultat.getKategoriNotat());
    }

    @Test
    public void sluttBrukerBlirSattTilTrue() throws DatatypeConfigurationException {
        ResultatWrapper<DokumentMetadata> wrapper = journalpostTransformer.dokumentMetadataFraJournalPost(JoarkMock.navMottattDokumentFraBruker(), FNR, fagsakId);
        assertTrue(wrapper.resultat.getAvsender().equals(Entitet.SLUTTBRUKER));
    }

    @Test
    public void sluttBrukerBlirIkkeSattTilTrue() throws DatatypeConfigurationException {
        ResultatWrapper<DokumentMetadata> wrapper = journalpostTransformer.dokumentMetadataFraJournalPost(JoarkMock.navMottattDokumentFraBruker(), "ikkesluttbruker", fagsakId);
        assertFalse(wrapper.resultat.getAvsender().equals(Entitet.SLUTTBRUKER));
    }
}
