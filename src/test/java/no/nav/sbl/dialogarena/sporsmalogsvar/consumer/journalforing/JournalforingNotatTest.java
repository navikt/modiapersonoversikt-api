package no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing;

import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.modig.lang.option.Optional;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoernotat.DokumentinfoRelasjon;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoernotat.JournalfoertDokumentInfo;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoernotat.Journalpost;
import org.junit.Before;
import org.junit.Test;

import static no.nav.modig.core.context.SubjectHandler.SUBJECTHANDLER_KEY;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.Journalforing.DOKUMENTTYPE_NOTAT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.Journalforing.HOVEDDOKUMENT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.Journalforing.INNHOLD_BESKRIVELSE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.JournalforingNotat.DOKUMENTTITTEL_OPPMOTE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.JournalforingNotat.DOKUMENTTITTEL_TELEFON;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing.JournalforingNotat.KATEGORIKODE;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class JournalforingNotatTest extends TestDataJournalforing {

    private static final Optional<String> journalfortPostIdOptional = Optional.optional(journalfortPostId);

    @Before
    public void init() {
        System.setProperty(SUBJECTHANDLER_KEY, ThreadLocalSubjectHandler.class.getName());
    }

    @Test
    public void skalLageJournalforingNotatMedRiktigeFelter() {
        Journalpost journalpostNotat = JournalforingNotat.lagJournalforingNotat(
                journalfortPostIdOptional, sak, melding, JOURNALFORENDE_ENHET_ID);

        assertNotNull(journalpostNotat.getSignatur());
        assertNotNull(journalpostNotat.getArkivtema());
        assertNotNull(journalpostNotat.getForBruker());
        assertThat(journalpostNotat.getInnhold(), is(INNHOLD_BESKRIVELSE));
        assertNotNull(journalpostNotat.getDokumentDato());
        assertThat(journalpostNotat.getGjelderSak().getSaksId(), is(sak.saksId));
        assertThat(journalpostNotat.getJournalfoerendeEnhetREF(), is(JOURNALFORENDE_ENHET_ID));
        assertThat(journalpostNotat.getKryssreferanseListe().get(0).getReferanseId(), is(journalfortPostId));
        assertNotNull(journalpostNotat.getDokumentinfoRelasjon());
    }

    @Test
    public void skalSetteRiktigDokumentInfoRelasjon() {
        Journalpost journalpostNotat = JournalforingNotat.lagJournalforingNotat(Optional.optional(journalfortPostId), sak, melding, JOURNALFORENDE_ENHET_ID);

        DokumentinfoRelasjon dokumentinfoRelasjon = journalpostNotat.getDokumentinfoRelasjon().get(0);
        assertThat(dokumentinfoRelasjon.getTillknyttetJournalpostSomKode(), is(HOVEDDOKUMENT));

        JournalfoertDokumentInfo dokumentInfo = dokumentinfoRelasjon.getJournalfoertDokument();
        assertThat(dokumentInfo.getDokumentType().getValue(), is(DOKUMENTTYPE_NOTAT));
        assertThat(dokumentInfo.isBegrensetPartsInnsyn(), is(false));
        assertThat(dokumentInfo.isErOrganinternt(), is(false));
        assertThat(dokumentInfo.getKategorikode(), is(KATEGORIKODE));
        assertThat(dokumentInfo.isSensitivitet(), is(false));
        assertNotNull(dokumentInfo.getBeskriverInnhold().get(0));
    }

    @Test
    public void skalSetteRiktigTittelNaarMeldingskanalErTelefon() {
        melding.kanal = JournalforingNotat.KANAL_TYPE_TELEFON;

        Journalpost journalpostNotat = JournalforingNotat.lagJournalforingNotat(journalfortPostIdOptional, sak, melding, JOURNALFORENDE_ENHET_ID);

        JournalfoertDokumentInfo dokumentInfo = journalpostNotat.getDokumentinfoRelasjon().get(0).getJournalfoertDokument();
        assertThat(dokumentInfo.getTittel(), is(DOKUMENTTITTEL_TELEFON));
    }

    @Test
    public void skalSetteRiktigTittelNaarMeldingskanalIkkeErTelefon() {
        melding.kanal = "annet";

        Journalpost journalpostNotat = JournalforingNotat.lagJournalforingNotat(journalfortPostIdOptional, sak, melding, JOURNALFORENDE_ENHET_ID);

        JournalfoertDokumentInfo dokumentInfo = journalpostNotat.getDokumentinfoRelasjon().get(0).getJournalfoertDokument();
        assertThat(dokumentInfo.getTittel(), is(DOKUMENTTITTEL_OPPMOTE));
    }

}
