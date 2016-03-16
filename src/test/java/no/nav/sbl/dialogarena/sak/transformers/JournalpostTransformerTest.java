package no.nav.sbl.dialogarena.sak.transformers;

import no.nav.sbl.dialogarena.sak.mock.JoarkMock;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Entitet;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.KategoriNotat;
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

import static no.nav.sbl.dialogarena.sak.mock.JoarkMock.PERSON_FNR;
import static no.nav.sbl.dialogarena.sak.mock.JoarkMock.brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg;
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

    @Before
    public void setup(){
        when(bulletproofKodeverkService.getTemanavnForTemakode(anyString(), anyString())).thenReturn("Temakode");
    }

    @Test(expected = RuntimeException.class)
    public void sakUtenHovedDokumentKasterRuntimeException() {
        WSJournalpost wsJournalpost = new WSJournalpost()
                .withJournalpostId(JOURNALPOSTID)
                .withJournalstatus(new WSJournalstatuser().withValue("J"))
                .withKommunikasjonsretning(new WSKommunikasjonsretninger().withValue("I"));

        journalpostTransformer.dokumentMetadataFraJournalPost(wsJournalpost, FNR);
    }

    @Test
    public void brukerMottattDokumentFraNAV() throws DatatypeConfigurationException {
        DokumentMetadata dokumentMetadata = journalpostTransformer.dokumentMetadataFraJournalPost(brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg(), FNR);
        assertThat(dokumentMetadata.getHoveddokument().getTittel(), is("Hoveddokument.tittel"));
    }

    @Test
    public void navMottattDokumentFraBruker() throws DatatypeConfigurationException {
        DokumentMetadata dokumentMetadata = journalpostTransformer.dokumentMetadataFraJournalPost(JoarkMock.navMottattDokumentFraBruker(), FNR);
        assertThat(dokumentMetadata.getHoveddokument().getTittel(), is("Hoved.tittel"));
    }

    @Test
    public void navSendtDokumentTilEksternPart() {
        DokumentMetadata dokumentMetadata = journalpostTransformer.dokumentMetadataFraJournalPost(JoarkMock.navSendtDokumentTilEksternPart(), FNR);
        assertThat(dokumentMetadata.getNavn(), is(JoarkMock.TREDJEPERSON_NAVN));
    }

    @Test
    public void navSendtDokumentFraBedrift() throws DatatypeConfigurationException {
        DokumentMetadata dokumentMetadata = journalpostTransformer.dokumentMetadataFraJournalPost(JoarkMock.navMottattDokumentFraBedrift(), FNR);
        assertThat(dokumentMetadata.getNavn(), is(JoarkMock.BEDRIFT_NAVN));
    }

    @Test
    public void navSendtDokumentUtenAktoer() throws DatatypeConfigurationException {
        DokumentMetadata dokumentMetadata = journalpostTransformer.dokumentMetadataFraJournalPost(JoarkMock.navMottattDokumentFraUkjent(), FNR);
        assertThat(dokumentMetadata.getNavn(), is("ukjent"));
    }

    @Test
    public void internKategoriNotat() throws DatatypeConfigurationException {
        DokumentMetadata dokumentMetadata = journalpostTransformer.dokumentMetadataFraJournalPost(JoarkMock.internDokumentinfoRelasjonListe(), FNR);
        assertThat(dokumentMetadata.getKategoriNotat(), is(KategoriNotat.INTERN_NOTAT));
    }

    @Test
    public void inngaaendeKategoriNotat() throws DatatypeConfigurationException {
        DokumentMetadata dokumentMetadata = journalpostTransformer.dokumentMetadataFraJournalPost(JoarkMock.eksternDokumentinfoRelasjonListe(), FNR);
        assertNull(dokumentMetadata.getKategoriNotat());
    }

    @Test
    public void sluttBrukerBlirSattTilTrue() throws DatatypeConfigurationException {
        DokumentMetadata dokumentMetadata = journalpostTransformer.dokumentMetadataFraJournalPost(JoarkMock.navMottattDokumentFraBruker(), FNR);
        assertTrue(dokumentMetadata.getAvsender().equals(Entitet.SLUTTBRUKER));
    }

    @Test
    public void sluttBrukerBlirIkkeSattTilTrue() throws DatatypeConfigurationException {
        DokumentMetadata dokumentMetadata = journalpostTransformer.dokumentMetadataFraJournalPost(JoarkMock.navMottattDokumentFraBruker(), "ikkesluttbruker");
        assertFalse(dokumentMetadata.getAvsender().equals(Entitet.SLUTTBRUKER));
    }
}
