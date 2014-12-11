package no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.*;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Person;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoernotat.*;
import org.joda.time.DateTime;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Kanal.TELEFON;

public class JournalforingNotat extends Journalforing {

    public static final String DOKUMENTTITTEL_TELEFON = "Referat fra samtale på telefon";
    public static final String DOKUMENTTITTEL_OPPMOTE = "Referat fra samtale ved oppmøte";
    public static final String KATEGORIKODE = "REFERAT";

    public static Journalpost lagJournalforingNotat(Optional<String> journalfortPostId, Sak sak, Melding melding, String journalforendeEnhetId) {
        Journalpost journalpost = new Journalpost();
        journalpost.setSignatur(lagSignatur());
        journalpost.setArkivtema(lagArkivtema(sak.temaKode));
        Person bruker = lagPerson(melding.fnrBruker);
        journalpost.getForBruker().add(bruker);
        journalpost.setInnhold(INNHOLD_BESKRIVELSE);
        journalpost.setDokumentDato(DateTimeToXmlGregorianCalendarConverter.INSTANCE.transform(DateTime.now()));
        journalpost.setGjelderSak(SakToJournalforingSak.INSTANCE.transform(sak));
        journalpost.setJournalfoerendeEnhetREF(journalforendeEnhetId);
        journalpost.setOpprettetAvNavn(getSubjectHandler().getUid());

        if (journalfortPostId.isSome()) {
            journalpost.getKryssreferanseListe().add(lagKryssreferanse(journalfortPostId.get()));
        }
        lagRelasjon(melding, journalpost);
        return journalpost;
    }

    private static void lagRelasjon(Melding melding, Journalpost journalpost) {
        DokumentinfoRelasjon dokumentinfoRelasjon = new DokumentinfoRelasjon();
        dokumentinfoRelasjon.setJournalfoertDokument(
                lagJournalfoertDokumentInfoForNotat(lagPdfInnhold(melding), melding));
        dokumentinfoRelasjon.setTillknyttetJournalpostSomKode(HOVEDDOKUMENT);
        journalpost.getDokumentinfoRelasjon().add(dokumentinfoRelasjon);
    }

    private static JournalfoertDokumentInfo lagJournalfoertDokumentInfoForNotat(byte[] pdf, Melding melding) {
        JournalfoertDokumentInfo journalfoertDokumentInfo = new JournalfoertDokumentInfo();
        String dokumenttittel;
        if (melding.kanal.equals(TELEFON.name())) {
            dokumenttittel = DOKUMENTTITTEL_TELEFON;
        } else {
            dokumenttittel = DOKUMENTTITTEL_OPPMOTE;
        }
        journalfoertDokumentInfo.setDokumentType(lagDokumenttype(BREVKODE_NOTAT));
        journalfoertDokumentInfo.setBegrensetPartsInnsyn(false);
        journalfoertDokumentInfo.setErOrganinternt(false);
        journalfoertDokumentInfo.setKategorikode(KATEGORIKODE);
        journalfoertDokumentInfo.setSensitivitet(false);
        journalfoertDokumentInfo.setTittel(dokumenttittel);
        leggBeskriverInnholdTilJournalfortDokumentInfo(
                journalfoertDokumentInfo.getBeskriverInnhold(), new Pdf(dokumenttittel, pdf));

        return journalfoertDokumentInfo;
    }

}
