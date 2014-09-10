package no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing;

import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerutgaaendehenvendelse.DokumentinfoRelasjon;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerutgaaendehenvendelse.JournalfoertDokumentInfo;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerutgaaendehenvendelse.Journalpost;
import org.junit.Before;
import org.junit.Test;

import static no.nav.modig.core.context.SubjectHandler.SUBJECTHANDLER_KEY;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.Journalforing.BREVKODE_SPORSMAL_OG_SVAR;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.Journalforing.DOKUMENTTYPE_UTGAAENDE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.Journalforing.HOVEDDOKUMENT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.Journalforing.INNHOLD_BESKRIVELSE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.Journalforing.KATEGORI_ELEKTRONISK_DIALOG;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.JournalforingUtgaaende.DOKUMENTTITTEL;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class JournalforingUtgaaendeTest extends TestDataJournalforing {

    @Before
    public void init() {
        System.setProperty(SUBJECTHANDLER_KEY, ThreadLocalSubjectHandler.class.getName());
    }

    @Test
    public void skalLageJournalforingUtgaaendeMedRiktigeFelter() {
        Journalpost journalpostUtgaaende = JournalforingUtgaaende.lagJournalforingSvar(
                journalfortPostId, sak, melding, JOURNALFORENDE_ENHET_ID);

        assertNotNull(journalpostUtgaaende.getKanal());
        assertNotNull(journalpostUtgaaende.getSignatur());
        assertNotNull(journalpostUtgaaende.getArkivtema());
        assertNotNull(journalpostUtgaaende.getForBruker());
        assertNotNull(journalpostUtgaaende.getEksternPart());
        assertThat(journalpostUtgaaende.getInnhold(), is(INNHOLD_BESKRIVELSE));
        assertNotNull(journalpostUtgaaende.getDokumentDato());
        assertThat(journalpostUtgaaende.getGjelderSak().getSaksId(), is(sak.saksId));
        assertThat(journalpostUtgaaende.getJournalfoerendeEnhetREF(), is(JOURNALFORENDE_ENHET_ID));
        assertThat(journalpostUtgaaende.getKryssreferanseListe().get(0).getReferanseId(), is(journalfortPostId));
        assertNotNull(journalpostUtgaaende.getDokumentinfoRelasjon());
    }

    @Test
    public void skalSetteRiktigDokumentInfoRelasjon() {
        Journalpost journalpostUtgaaende = JournalforingUtgaaende.lagJournalforingSvar(
                journalfortPostId, sak, melding, JOURNALFORENDE_ENHET_ID);

        DokumentinfoRelasjon dokumentinfoRelasjon = journalpostUtgaaende.getDokumentinfoRelasjon().get(0);
        assertThat(dokumentinfoRelasjon.getTillknyttetJournalpostSomKode(), is(HOVEDDOKUMENT));

        JournalfoertDokumentInfo dokumentInfo = dokumentinfoRelasjon.getJournalfoertDokument();
        assertThat(dokumentInfo.getDokumentType().getValue(), is(DOKUMENTTYPE_UTGAAENDE));
        assertThat(dokumentInfo.isBegrensetPartsInnsyn(), is(false));
        assertThat(dokumentInfo.getBrevkode(), is(BREVKODE_SPORSMAL_OG_SVAR));
        assertThat(dokumentInfo.getKategorikode(), is(KATEGORI_ELEKTRONISK_DIALOG));
        assertThat(dokumentInfo.isSensitivitet(), is(false));
        assertThat(dokumentInfo.getTittel(), is(DOKUMENTTITTEL));
        assertNotNull(dokumentInfo.getBeskriverInnhold().get(0));
    }

}
