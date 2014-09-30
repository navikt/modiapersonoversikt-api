package no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Pdf;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Person;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerutgaaendehenvendelse.DokumentinfoRelasjon;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerutgaaendehenvendelse.JournalfoertDokumentInfo;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerutgaaendehenvendelse.Journalpost;
import org.joda.time.DateTime;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;

public class JournalforingUtgaaende extends Journalforing {

    public static final String DOKUMENTTITTEL = "Svar fra Ditt NAV";

    public static Journalpost lagJournalforingSvar(String journalfortPostId, Sak sak, Melding melding, String journalforendeEnhetId) {
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
        journalpost.getKryssreferanseListe().add(lagKryssreferanse(journalfortPostId));
        journalpost.setOpprettetAvNavn(getSubjectHandler().getUid());

        lagRelasjon(melding, journalpost);
        return journalpost;
    }

    private static void lagRelasjon(Melding melding, Journalpost journalpost) {
        DokumentinfoRelasjon dokumentinfoRelasjon = new DokumentinfoRelasjon();
        dokumentinfoRelasjon.setJournalfoertDokument(
                lagJournalfoertDokumentInfoForSvar(lagPdfInnhold(melding)));
        dokumentinfoRelasjon.setTillknyttetJournalpostSomKode(HOVEDDOKUMENT);
        journalpost.getDokumentinfoRelasjon().add(dokumentinfoRelasjon);
    }

    private static JournalfoertDokumentInfo lagJournalfoertDokumentInfoForSvar(byte[] pdf) {
        JournalfoertDokumentInfo journalfoertDokumentInfo = new JournalfoertDokumentInfo();

        journalfoertDokumentInfo.setDokumentType(lagDokumenttype(DOKUMENTTYPE_UTGAAENDE));
        journalfoertDokumentInfo.setBegrensetPartsInnsyn(false);
        journalfoertDokumentInfo.setKategorikode(KATEGORI_ELEKTRONISK_DIALOG);
        journalfoertDokumentInfo.setSensitivitet(false);
        journalfoertDokumentInfo.setTittel(DOKUMENTTITTEL);
        leggBeskriverInnholdTilJournalfortDokumentInfo(
                journalfoertDokumentInfo.getBeskriverInnhold(), new Pdf(DOKUMENTTITTEL, pdf));

        return journalfoertDokumentInfo;
    }

}
