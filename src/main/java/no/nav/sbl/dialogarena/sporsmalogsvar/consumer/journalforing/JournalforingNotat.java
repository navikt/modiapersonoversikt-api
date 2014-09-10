package no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoernotat.DokumentinfoRelasjon;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoernotat.JournalfoertDokumentInfo;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoernotat.Journalpost;
import org.joda.time.DateTime;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;

public class JournalforingNotat extends Journalforing {

    public static final String KANAL_TYPE_TELEFON = "TELEFON";
    public static final String DOKUMENTTITTEL_TELEFON = "Referat fra samtale på telefon";
    public static final String DOKUMENTTITTEL_OPPMOTE = "Referat fra samtale ved oppmøte";
    public static final String KATEGORIKODE = "REFERAT";

    public static Journalpost lagJournalforingNotat(Optional<String> journalfortPostId, Sak sak, Melding melding, String journalforendeEnhetId) {
        Journalpost journalpost = new Journalpost();
        journalpost.setKanal(lagKommunikasjonskanaler());
        journalpost.setSignatur(lagSignatur());
        journalpost.setArkivtema(lagArkivtema(sak.tema));
        journalpost.getForBruker().add(lagPerson(melding.fnrBruker));
        journalpost.setInnhold(INNHOLD_BESKRIVELSE);
        journalpost.setDokumentDato(DateTimeToXmlGregorianCalendarConverter.INSTANCE.transform(DateTime.now()));
        journalpost.setGjelderSak(SakToJournalforingSak.INSTANCE.transform(sak));
        // TODO sjekk om det er enhetsId som skal inn i journalforendeEnhetREF eller om det er navn
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

        journalfoertDokumentInfo.setDokumentType(lagDokumenttype(DOKUMENTTYPE_NOTAT));
        journalfoertDokumentInfo.setBegrensetPartsInnsyn(false);
        journalfoertDokumentInfo.setBrevkode(BREVKODE_NOTAT);
        journalfoertDokumentInfo.setErOrganinternt(false);
        journalfoertDokumentInfo.setKategorikode(KATEGORIKODE);
        journalfoertDokumentInfo.setSensitivitet(false);
        leggBeskriverInnholdTilJournalfortDokumentInfo(
                journalfoertDokumentInfo.getBeskriverInnhold(), pdf);
        // TODO: Få inn kodeverk slik at vi får rikitg evaluering av hva slags kanal vi har
        if (melding.kanal.equals(KANAL_TYPE_TELEFON)) {
            journalfoertDokumentInfo.setTittel(DOKUMENTTITTEL_TELEFON);
        } else {
            journalfoertDokumentInfo.setTittel(DOKUMENTTITTEL_OPPMOTE);
        }

        return journalfoertDokumentInfo;
    }

}
