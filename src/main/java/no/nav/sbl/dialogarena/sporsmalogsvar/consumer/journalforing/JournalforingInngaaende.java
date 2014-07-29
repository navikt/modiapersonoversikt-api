package no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.PdfUtils;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.DokumentInnhold;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Dokumenttyper;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Person;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerinngaaendehenvendelse.DokumentinfoRelasjon;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerinngaaendehenvendelse.JournalfoertDokumentInfo;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerinngaaendehenvendelse.Journalpost;
import org.joda.time.DateTime;

import java.util.List;

public class JournalforingInngaaende extends Journalforing {
    public static Journalpost lagJournalforingSporsmal(Sak sak, Melding melding, String journalforendeEnhetId) {
        Journalpost journalpost = new Journalpost();
        journalpost.setKanal(lagKommunikasjonskanaler());
        journalpost.setSignatur(lagSignatur());
        journalpost.setArkivtema(lagArkivtema(sak));
        Person bruker = lagPerson(melding.fnrBruker);
        journalpost.getForBruker().add(bruker);
        journalpost.setEksternPart(lagEksternPart(bruker));
        journalpost.setInnhold("Elektronisk kommunikasjon med NAV ");
        journalpost.setDokumentDato(DateTimeToXmlGregorianCalendarConverter.INSTANCE.transform(DateTime.now()));
        journalpost.setGjelderSak(SakToJournalforingSak.INSTANCE.transform(sak));
        // TODO sjekk om det er enhetsId som skal inn i journalforendeEnhetREF eller om det er navn
        journalpost.setJournalfoerendeEnhetREF(journalforendeEnhetId);

        lagRelasjon(melding, journalpost);
        return journalpost;
    }

    private static void lagRelasjon(Melding melding, Journalpost journalpost){
        DokumentinfoRelasjon dokumentinfoRelasjon = new DokumentinfoRelasjon();
        byte[] pdfInnhold = PdfUtils.genererPdf(melding);
        dokumentinfoRelasjon.setJournalfoertDokument(lagJournalfoertDokumentInfoForSporsmal(pdfInnhold));
        dokumentinfoRelasjon.setTillknyttetJournalpostSomKode(HOVEDDOKUMENT);
        journalpost.getDokumentinfoRelasjon().add(dokumentinfoRelasjon);

    }

    private static JournalfoertDokumentInfo lagJournalfoertDokumentInfoForSporsmal(byte[] pdf) {
        JournalfoertDokumentInfo journalfoertDokumentInfo = new JournalfoertDokumentInfo();

        Dokumenttyper dokumenttyper = new Dokumenttyper();
        dokumenttyper.setValue("I");

        journalfoertDokumentInfo.setDokumentType(dokumenttyper);
        journalfoertDokumentInfo.setBegrensetPartsInnsyn(false);
        journalfoertDokumentInfo.setBrevkode(BREVKODE_SPORSMAL_OG_SVAR);
        journalfoertDokumentInfo.setKategorikode(KATEGORI_ELEKTRONISK_DIALOG);
        journalfoertDokumentInfo.setSensitivitet(false);
        // TODO få inn den egentlige tittelen her
        journalfoertDokumentInfo.setTittel("Dokumenttittel");

        List<DokumentInnhold> beskriverInnhold = journalfoertDokumentInfo.getBeskriverInnhold();
        beskriverInnhold.add(PdfDokumentToUstrukturertInnholdConverter.INSTANCE.transform(pdf));

        return journalfoertDokumentInfo;
    }
}
