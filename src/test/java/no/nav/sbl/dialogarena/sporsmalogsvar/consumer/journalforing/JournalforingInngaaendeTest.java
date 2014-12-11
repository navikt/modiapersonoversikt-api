package no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing;

import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.modig.lang.option.Optional;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerinngaaendehenvendelse.*;
import org.junit.Before;
import org.junit.Test;

import static no.nav.modig.core.context.SubjectHandler.SUBJECTHANDLER_KEY;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.Journalforing.BREVKODE_SPORSMAL;
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
    public void lagerJournalforingInngaaendeMedRiktigeFelter() {
        Journalpost journalpostInngaaende = JournalforingInngaaende.lagJournalforingInngaaende(
                optional(journalfortPostId), sak, melding, JOURNALFORENDE_ENHET_ID);

        assertNotNull(journalpostInngaaende.getKanal());
        assertNotNull(journalpostInngaaende.getSignatur());
        assertNotNull(journalpostInngaaende.getArkivtema());
        assertNotNull(journalpostInngaaende.getForBruker());
        assertNotNull(journalpostInngaaende.getEksternPart());
        assertThat(journalpostInngaaende.getInnhold(), is(INNHOLD_BESKRIVELSE));
        assertNotNull(journalpostInngaaende.getDokumentDato());
        assertThat(journalpostInngaaende.getGjelderSak().getSaksId(), is(sak.saksId));
        assertThat(journalpostInngaaende.getJournalfoerendeEnhetREF(), is(JOURNALFORENDE_ENHET_ID));
        assertThat(journalpostInngaaende.getKryssreferanseListe().get(0).getReferanseId(), is(journalfortPostId));
        assertNotNull(journalpostInngaaende.getDokumentinfoRelasjon());
    }

    @Test
    public void lagerJournalforingInngaaendeUtenKryssreferanseDersomJournalfortPostIdErTom() {
        Journalpost journalpostInngaaende = JournalforingInngaaende.lagJournalforingInngaaende(
                Optional.<String>none(), sak, melding, JOURNALFORENDE_ENHET_ID);

        assertThat(journalpostInngaaende.getKryssreferanseListe().isEmpty(), is(true));
    }

    @Test
    public void setterRiktigDokumentInfoRelasjon() {
        Journalpost journalpostInngaaende = JournalforingInngaaende.lagJournalforingInngaaende(
                Optional.<String>none(), sak, melding, JOURNALFORENDE_ENHET_ID);

        DokumentinfoRelasjon dokumentinfoRelasjon = journalpostInngaaende.getDokumentinfoRelasjon().get(0);
        assertThat(dokumentinfoRelasjon.getTillknyttetJournalpostSomKode(), is(HOVEDDOKUMENT));

        JournalfoertDokumentInfo dokumentInfo = dokumentinfoRelasjon.getJournalfoertDokument();
        assertThat(dokumentInfo.getDokumentType().getValue(), is(BREVKODE_SPORSMAL));
        assertThat(dokumentInfo.isBegrensetPartsInnsyn(), is(false));
        assertThat(dokumentInfo.getKategorikode(), is(KATEGORI_ELEKTRONISK_DIALOG));
        assertThat(dokumentInfo.isSensitivitet(), is(false));
        assertThat(dokumentInfo.getTittel(), is(DOKUMENTTITTEL));
        assertNotNull(dokumentInfo.getBeskriverInnhold().get(0));
        assertThat(dokumentInfo.getBeskriverInnhold().get(0).getFilnavn(), is(DOKUMENTTITTEL));
    }

}
