package no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforinghelpers;

import no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.PdfUtils;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.DokumentInnhold;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Dokumenttyper;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Person;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerutgaaendehenvendelse.DokumentinfoRelasjon;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerutgaaendehenvendelse.JournalfoertDokumentInfo;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerutgaaendehenvendelse.Journalpost;
import org.joda.time.DateTime;

import java.util.List;

public class JournalforingSvar extends Journalforing {
    public static Journalpost lagJournalforingSvar(String journalfortPostId, Sak sak, Melding melding) {
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
        journalpost.getKryssreferanseListe().add(lagKryssreferanse(journalfortPostId));

        lagRelasjon(melding, journalpost);
        return journalpost;
    }

    private static void lagRelasjon(Melding melding, Journalpost journalpost){
        DokumentinfoRelasjon dokumentinfoRelasjon = new DokumentinfoRelasjon();
        byte[] pdfInnhold = PdfUtils.genererPdf(melding);
        dokumentinfoRelasjon.setJournalfoertDokument(lagJournalfoertDokumentInfoForSvar(pdfInnhold));
        dokumentinfoRelasjon.setTillknyttetJournalpostSomKode(HOVEDDOKUMENT);
        journalpost.getDokumentinfoRelasjon().add(dokumentinfoRelasjon);
    }

    private static JournalfoertDokumentInfo lagJournalfoertDokumentInfoForSvar(byte[] pdf) {
        JournalfoertDokumentInfo
                journalfoertDokumentInfo = new JournalfoertDokumentInfo();

        // TODO hent inn kodeverk for feletene setKodevrksRef() og setKodeRef() som tilhører dokumenttyper-objektet
        Dokumenttyper dokumenttyper = new Dokumenttyper();
        dokumenttyper.setValue("U");

        journalfoertDokumentInfo.setDokumentType(dokumenttyper);
        journalfoertDokumentInfo.setBegrensetPartsInnsyn(false);
        journalfoertDokumentInfo.setBrevkode(SPORSMAL_OG_SVAR);
        journalfoertDokumentInfo.setKategorikode("KU");
        journalfoertDokumentInfo.setSensitivitet(false);
        // TODO få inn den egentlige tittelen her
        journalfoertDokumentInfo.setTittel("Dokumenttittel");

        List<DokumentInnhold> beskriverInnhold = journalfoertDokumentInfo.getBeskriverInnhold();
        beskriverInnhold.add(PdfDokumentToUstrukturertInnholdConverter.INSTANCE.transform(pdf));

        return journalfoertDokumentInfo;
    }
}
