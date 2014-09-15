package no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing;

import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerinngaaendehenvendelse.DokumentinfoRelasjon;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerinngaaendehenvendelse.JournalfoertDokumentInfo;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerinngaaendehenvendelse.Journalpost;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static no.nav.modig.core.context.SubjectHandler.SUBJECTHANDLER_KEY;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.Journalforing.DOKUMENTTYPE_INNGAAENDE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.Journalforing.HOVEDDOKUMENT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.Journalforing.INNHOLD_BESKRIVELSE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.Journalforing.KATEGORI_ELEKTRONISK_DIALOG;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.JournalforingInngaaende.DOKUMENTTITTEL;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class JournalforingInngaaendeTest extends TestDataJournalforing {

    @Before
    public void init() {
        System.setProperty(SUBJECTHANDLER_KEY, ThreadLocalSubjectHandler.class.getName());
    }

    @Test
    public void skalLageJournalforingInngaaendeMedRiktigeFelter() {
        Journalpost journalpostInngaaende = JournalforingInngaaende.lagJournalforingSporsmal(sak, melding, JOURNALFORENDE_ENHET_ID);

        assertNotNull(journalpostInngaaende.getKanal());
        assertNotNull(journalpostInngaaende.getSignatur());
        assertNotNull(journalpostInngaaende.getArkivtema());
        assertNotNull(journalpostInngaaende.getForBruker());
        assertNotNull(journalpostInngaaende.getEksternPart());
        Assert.assertThat(journalpostInngaaende.getInnhold(), is(INNHOLD_BESKRIVELSE));
        assertNotNull(journalpostInngaaende.getDokumentDato());
        assertThat(journalpostInngaaende.getGjelderSak().getSaksId(), is(sak.saksId));
        assertThat(journalpostInngaaende.getJournalfoerendeEnhetREF(), is(JOURNALFORENDE_ENHET_ID));
    }

    @Test
    public void skalSetteRiktigDokumentInfoRelasjon() {
        Journalpost journalpostInngaaende = JournalforingInngaaende.lagJournalforingSporsmal(sak, melding, JOURNALFORENDE_ENHET_ID);

        DokumentinfoRelasjon dokumentinfoRelasjon = journalpostInngaaende.getDokumentinfoRelasjon().get(0);
        assertThat(dokumentinfoRelasjon.getTillknyttetJournalpostSomKode(), is(HOVEDDOKUMENT));

        JournalfoertDokumentInfo dokumentInfo = dokumentinfoRelasjon.getJournalfoertDokument();
        assertThat(dokumentInfo.getDokumentType().getValue(), is(DOKUMENTTYPE_INNGAAENDE));
        assertThat(dokumentInfo.isBegrensetPartsInnsyn(), is(false));
        assertThat(dokumentInfo.getKategorikode(), is(KATEGORI_ELEKTRONISK_DIALOG));
        assertThat(dokumentInfo.isSensitivitet(), is(false));
        assertThat(dokumentInfo.getTittel(), is(DOKUMENTTITTEL));
        assertNotNull(dokumentInfo.getBeskriverInnhold().get(0));
    }

}
