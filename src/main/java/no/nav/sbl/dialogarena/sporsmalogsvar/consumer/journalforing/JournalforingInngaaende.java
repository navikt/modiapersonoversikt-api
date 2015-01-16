package no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing;

import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Pdf;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Person;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerinngaaendehenvendelse.*;
import org.joda.time.DateTime;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;

public class JournalforingInngaaende extends Journalforing {

    public static final String DOKUMENTTITTEL = "Henvendelse fra Ditt NAV";

    public static Journalpost lagJournalforingInngaaende(Optional<String> journalfortPostId, Sak sak, Melding melding, String journalforendeEnhetId) {
        Journalpost journalpost = new Journalpost();
        journalpost.setKanal(lagKommunikasjonskanaler());
        journalpost.setSignatur(lagSignatur());
        journalpost.setArkivtema(lagArkivtema(sak.temaKode));
        Person bruker = lagPerson(melding.fnrBruker);
        journalpost.getForBruker().add(bruker);
        journalpost.setEksternPart(lagEksternPart(bruker));
        journalpost.setInnhold(INNHOLD_BESKRIVELSE);
        journalpost.setDokumentDato(DateTimeToXmlGregorianCalendarConverter.INSTANCE.transform(DateTime.now()));
        journalpost.setGjelderSak(SakToJournalforingSak.INSTANCE.transform(sak));
        journalpost.setJournalfoerendeEnhetREF(journalforendeEnhetId);
        journalpost.setOpprettetAvNavn(getSubjectHandler().getUid());
        journalpost.setMottattDato(DateTimeToXmlGregorianCalendarConverter.INSTANCE.transform(melding.opprettetDato));

        if (journalfortPostId.isSome()) {
            journalpost.getKryssreferanseListe().add(lagKryssreferanse(journalfortPostId.get()));
        }

        lagRelasjon(melding, journalpost);
        return journalpost;
    }

    private static void lagRelasjon(Melding melding, Journalpost journalpost) {
        DokumentinfoRelasjon dokumentinfoRelasjon = new DokumentinfoRelasjon();
        dokumentinfoRelasjon.setJournalfoertDokument(
                lagJournalfoertDokumentInfoForInngaaende(lagPdfInnhold(melding)));
        dokumentinfoRelasjon.setTillknyttetJournalpostSomKode(HOVEDDOKUMENT);
        journalpost.getDokumentinfoRelasjon().add(dokumentinfoRelasjon);
    }

    private static JournalfoertDokumentInfo lagJournalfoertDokumentInfoForInngaaende(byte[] pdf) {
        JournalfoertDokumentInfo journalfoertDokumentInfo = new JournalfoertDokumentInfo();

        journalfoertDokumentInfo.setDokumentType(lagDokumenttype(BREVKODE_SPORSMAL));
        journalfoertDokumentInfo.setBegrensetPartsInnsyn(false);
        journalfoertDokumentInfo.setKategorikode(KATEGORI_ELEKTRONISK_DIALOG);
        journalfoertDokumentInfo.setSensitivitet(false);
        journalfoertDokumentInfo.setTittel(DOKUMENTTITTEL);
        leggBeskriverInnholdTilJournalfortDokumentInfo(
                journalfoertDokumentInfo.getBeskriverInnhold(), new Pdf(DOKUMENTTITTEL, pdf));

        return journalfoertDokumentInfo;
    }

}
